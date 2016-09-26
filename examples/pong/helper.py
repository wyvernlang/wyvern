import pygame
import sys

def make2Tuple(a, b):
    return (a, b)

def fst(pair):
    return pair[0]

def snd(pair):
    return pair[1]

def makeRect(left, top, width, height):
    return pygame.Rect(left, top, width, height)

white = pygame.Color("white")
black = pygame.Color("black")

def makeClock():
    return pygame.time.Clock()

def debug(msg):
    print(msg)
    sys.stdout.flush()
