import importlib


#
# Replace these names with your actual Python module filenames.
#
# The Numogram selects. The language device never selects.
#
ROUTES = {
    0: ["difference_engine"],
    1: ["difference_engine"],
    2: ["difference_engine"],
    3: ["difference_engine"],
    4: ["difference_engine"],
    5: ["difference_engine"],
    6: ["difference_engine"],
    7: ["difference_engine"],
    8: ["difference_engine"],
    9: ["difference_engine"],
}


def execute_one(module_name, packet):
    full_name = "schizoid.modules." + module_name

    try:
        module = importlib.import_module(full_name)

        if not hasattr(module, "process"):
            packet["warnings"].append(
                module_name + " has no process(packet) function"
            )
            return packet

        return module.process(packet)

    except Exception as exc:
        packet["warnings"].append(
            module_name + ": " + repr(exc)
        )
        return packet


def execute_modules(packet):
    zone = packet["numogram"]["zone"]

    selected = ROUTES.get(zone, [])

    packet["modules"] = list(selected)

    for module_name in selected:
        packet = execute_one(module_name, packet)

    return packet
