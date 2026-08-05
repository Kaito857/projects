import sys
import time

lyrics = [
    ("Hello darkness, my old friend", 2.0),
    ("I've come to talk with you again", 2.5),
    ("Because a vision softly creeping", 2.0),
    ("Left its seeds while I was sleeping", 3.0),
]

for line, delay in lyrics:
  print(line)
  time.sleep(delay)  # Wait for the specified number of seconds
