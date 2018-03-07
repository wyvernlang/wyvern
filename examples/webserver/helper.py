import sys

def make2Tuple(a, b):
    return (a, b)

def fst(pair):
    return pair[0]

def snd(pair):
    return pair[1]

def debug(msg):
    print(msg)
    sys.stdout.flush()

def getType(e):
    return e.type

def floatOfString(s):
    return float(s)
