import sys
import time


def sing_line(line, char_delay=0.08, line_delay=1.5):
  for char in line:
    sys.stdout.write(char)
    sys.stdout.flush()
    time.sleep(char_delay)
  print()
  time.sleep(line_delay)


song = [
    "I'm bulletproof, nothing to lose",
    "Fire away, fire away",
    "Ricochet, you take your aim",
    "Fire away, fire away",
]

for sentence in song:
  sing_line(sentence)
