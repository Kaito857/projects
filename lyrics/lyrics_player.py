#!/usr/bin/env python3
"""Lyrics player: plays any song from a .json or .txt file in the terminal.

Modes:
  line     - print each line, then wait (like "Basic Line-by-Line Delay")
  typing   - type each line letter by letter (like "Letter-by-Letter Typing")
  karaoke  - show the whole song, highlight the current line, dim the rest

Examples:
  python lyrics_player.py songs/sample.json
  python lyrics_player.py songs/sample.txt --mode typing --speed 1.5
  python lyrics_player.py songs/sample.json --mode karaoke --color magenta
"""

import argparse
import json
import sys
import time
from pathlib import Path

DEFAULT_DELAY = 2.0

COLORS = {
    "white": "37",
    "red": "31",
    "green": "32",
    "yellow": "33",
    "blue": "34",
    "magenta": "35",
    "cyan": "36",
}
RESET = "\033[0m"
DIM = "\033[2m"
BOLD = "\033[1m"
CLEAR = "\033[2J\033[H"


def load_song(path):
    """Return (title, lines) where lines is a list of (text, delay) tuples.

    JSON format:
        {"title": "Name", "lines": [{"text": "Hello", "delay": 2.0}, "Plain line"]}

    TXT format (one line per lyric, optional delay before a '|'):
        2.5|Hello there
        Plain line uses the default delay
    """
    path = Path(path)
    if not path.exists():
        sys.exit(f"File not found: {path}")

    if path.suffix.lower() == ".json":
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        title = data.get("title", path.stem)
        lines = []
        for item in data["lines"]:
            if isinstance(item, str):
                lines.append((item, DEFAULT_DELAY))
            else:
                lines.append((item["text"], float(item.get("delay", DEFAULT_DELAY))))
        return title, lines

    title = path.stem
    lines = []
    with open(path, encoding="utf-8") as f:
        for raw in f:
            raw = raw.rstrip("\n")
            if not raw.strip():
                continue
            head, sep, rest = raw.partition("|")
            try:
                if sep:
                    lines.append((rest, float(head)))
                    continue
            except ValueError:
                pass
            lines.append((raw, DEFAULT_DELAY))
    return title, lines


def paint(text, color, use_color):
    if not use_color:
        return text
    return f"\033[{COLORS[color]}m{text}{RESET}"


def play_line(lines, speed, color, use_color):
    for text, delay in lines:
        print(paint(text, color, use_color))
        time.sleep(delay / speed)


def play_typing(lines, speed, color, use_color, char_delay=0.08):
    for text, delay in lines:
        sys.stdout.write(f"\033[{COLORS[color]}m" if use_color else "")
        for char in text:
            sys.stdout.write(char)
            sys.stdout.flush()
            time.sleep(char_delay / speed)
        sys.stdout.write(RESET if use_color else "")
        print()
        time.sleep(delay / speed)


def play_karaoke(title, lines, speed, color, use_color):
    for current in range(len(lines)):
        out = [CLEAR if use_color else "\n" * 2]
        out.append((BOLD if use_color else "") + title + (RESET if use_color else ""))
        out.append("")
        for i, (text, _) in enumerate(lines):
            if i == current:
                out.append(paint(("> " + text), color, use_color))
            elif use_color:
                out.append(f"{DIM}  {text}{RESET}")
            else:
                out.append(f"  {text}")
        print("\n".join(out))
        time.sleep(lines[current][1] / speed)


def parse_args():
    p = argparse.ArgumentParser(description="Play lyrics in the terminal.")
    p.add_argument("file", help="song file (.json or .txt)")
    p.add_argument(
        "--mode",
        choices=["line", "typing", "karaoke"],
        default="line",
        help="how to display the lyrics (default: line)",
    )
    p.add_argument(
        "--speed",
        type=float,
        default=1.0,
        help="speed multiplier, e.g. 2 = twice as fast (default: 1.0)",
    )
    p.add_argument(
        "--color",
        choices=sorted(COLORS),
        default="cyan",
        help="highlight color (default: cyan)",
    )
    p.add_argument("--no-color", action="store_true", help="turn colors off")
    args = p.parse_args()
    if args.speed <= 0:
        p.error("--speed must be greater than 0")
    return args


def main():
    args = parse_args()
    title, lines = load_song(args.file)
    if not lines:
        sys.exit("That song file has no lyrics in it.")

    use_color = not args.no_color and sys.stdout.isatty()

    try:
        if args.mode == "line":
            play_line(lines, args.speed, args.color, use_color)
        elif args.mode == "typing":
            play_typing(lines, args.speed, args.color, use_color)
        else:
            play_karaoke(title, lines, args.speed, args.color, use_color)
    except KeyboardInterrupt:
        print(RESET if use_color else "")
        print("Stopped.")


if __name__ == "__main__":
    main()
