package com.chaquopy.python.demo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatView: TextView
    private var conversationHistory = StringBuilder()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize Chaquopy Python runtime
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        
        // Bind UI elements
        editText = findViewById(R.id.input_text)
        sendButton = findViewById(R.id.send_button)
        chatView = findViewById(R.id.chat_output)
        
        // Send button click handler
        sendButton.setOnClickListener {
            val userQuery = editText.text.toString().trim()
            
            if (userQuery.isNotEmpty()) {
                // Show user message
                appendToChatView("YOU: $userQuery")
                editText.text.clear()
                
                // Process through Numogram in background (non-blocking)
                processQueryAsync(userQuery)
            }
        }
    }
    
    private fun processQueryAsync(query: String) {
        lifecycleScope.launch {
            try {
                // Run Python call in background thread
                val result = withContext(Dispatchers.Default) {
                    callNumogramPython(query)
                }
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    displayNumogramOutput(result)
                }
            } catch (e: Exception) {
                appendToChatView("ERROR: ${e.message}")
            }
        }
    }
    
    private fun callNumogramPython(query: String): JSONObject {
        """
        Invokes numogram.core.process_question(query) → JSON
        """
        
        try {
            val py = Python.getInstance()
            val numogramModule = py.getModule("numogram.core")
            
            // Call Python entry point
            val pyResult = numogramModule.callAttr("process_question", query)
            
            // Convert PyObject to JSON
            val jsonString = pyResult.toString()
            return JSONObject(jsonString)
        
        } catch (e: Exception) {
            // Return error JSON
            return JSONObject().apply {
                put("output_text", "Numogram processing failed: ${e.message}")
                put("modules_used", JSONArray())
                put("debug", JSONObject().put("error", e.message))
            }
        }
    }
    
    private fun displayNumogramOutput(result: JSONObject) {
        """
        Parse Numogram JSON output and display in chat.
        """
        
        try {
            val outputText = result.getString("output_text")
            val modulesUsed = result.getJSONArray("modules_used")
            val debugInfo = result.optJSONObject("debug") ?: JSONObject()
            
            // Display main output
            appendToChatView("NUMOGRAM: $outputText")
            
            // Display metadata (optional)
            if (modulesUsed.length() > 0) {
                val modules = (0 until modulesUsed.length())
                    .map { modulesUsed.getString(it) }
                    .joinToString(", ")
                appendToChatView("[Modules: $modules]")
            }
        
        } catch (e: Exception) {
            appendToChatView("ERROR parsing output: ${e.message}")
        }
    }
    
    private fun appendToChatView(message: String) {
        conversationHistory.append(message).append("\n\n")
        chatView.text = conversationHistory.toString()
        
        // Auto-scroll to bottom
        val scrollView = findViewById<ScrollView>(R.id.scroll_view)
        scrollView?.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
