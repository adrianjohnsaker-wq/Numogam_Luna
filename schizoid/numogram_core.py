import hashlib

from schizoid.module_registry import execute_modules


# Complementary Numogram pairing.
SYZYGY = {
    0: 9,
    1: 8,
    2: 7,
    3: 6,
    4: 5,
    5: 4,
    6: 3,
    7: 2,
    8: 1,
    9: 0,
}


STATE = {
    "turn": 0,
    "zone_history": [],
}


def digital_reduction(question):
    """
    ENGINEERING PLACEHOLDER ONLY.

    Replace this function with the canonical Numogram reduction.
    Its purpose here is simply to prove that routing occurs
    before module execution and before the language model.
    """

    data = question.encode("utf-8")
    digest = hashlib.blake2b(data, digest_size=16).digest()

    zone = sum(digest) % 10
    partner = SYZYGY[zone]

    # Three additional values give us an observable circuit trace.
    circuit = [
        digest[0] % 10,
        digest[1] % 10,
        digest[2] % 10,
    ]

    intensity = sum(digest) / (len(digest) * 255.0)

    return {
        "zone": zone,
        "syzygy": partner,
        "circuit": circuit,
        "intensity": intensity,
        "digest": digest.hex(),
    }


def process(question):
    STATE["turn"] += 1

    numogram = digital_reduction(question)

    packet = {
        "question": question,
        "turn": STATE["turn"],

        "numogram": numogram,

        "modules": [],
        "transformations": [],
        "directives": [],
        "warnings": [],
    }

    STATE["zone_history"].append(numogram["zone"])

    packet = execute_modules(packet)

    return packet
