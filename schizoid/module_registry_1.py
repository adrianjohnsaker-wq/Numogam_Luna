import importlib


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


def execute_modules(packet):
    zone = packet["numogram"]["zone"]
    selected = ROUTES.get(zone, [])

    packet["modules"] = list(selected)

    for module_name in selected:
        try:
            module = importlib.import_module(
                "schizoid.modules." + module_name
            )

            packet = module.process(packet)

        except Exception as exc:
            packet["warnings"].append(
                module_name + ": " + repr(exc)
            )

    return packet
