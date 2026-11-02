# Projects

A small collection of beginner-friendly terminal programs in Java and Python.

## Contents

| Project | Language | Description |
|---|---|---|
| [Quiz App](#quiz-app) | Java | Command-line quiz with difficulty levels, a timer, and high scores |
| [Lyrics Player](#lyrics-player) | Python | Plays any song from a file: line-by-line, typing, or karaoke style |
| [Original lyrics scripts](#original-lyrics-scripts) | Python | The first versions that the Lyrics Player grew out of |

## Quiz App

Folder: [`quiz-app/`](quiz-app/)

**Features**
- Questions loaded from `questions.txt`, so you can add your own without touching the code
- Pick a difficulty: easy, medium, hard, or all
- Questions and answer choices are shuffled every game
- 15 seconds per question (a slow answer counts as wrong)
- Score at the end plus a saved top-5 high score list (`highscores.txt`)

**Requirements:** Java JDK 11 or newer

```bash
cd quiz-app
javac QuizApp.java
java QuizApp                 # uses questions.txt
java QuizApp mystuff.txt     # or use your own question file
```

**Question file format** (one question per line, `#` starts a comment):

```
difficulty|question|choice A;choice B;choice C;choice D|number of the correct choice
easy|Which planet is known as the Red Planet?|Venus;Mars;Jupiter;Mercury|2
```

> The root-level `QuizApp.jar` is the original version and is kept for reference.

## Lyrics Player

Folder: [`lyrics/`](lyrics/)

One script that plays any song file, with three display modes:

| Mode | What it does |
|---|---|
| `line` | Prints each line, then waits (default) |
| `typing` | Types each line letter by letter |
| `karaoke` | Shows the whole song, highlights the current line, dims the rest |

**Requirements:** Python 3.8+ (no extra packages)

```bash
cd lyrics
python lyrics_player.py songs/sample.json
python lyrics_player.py songs/sample.txt --mode typing --speed 1.5
python lyrics_player.py songs/sample.json --mode karaoke --color magenta
```

**Options:** `--mode`, `--speed` (2 = twice as fast), `--color`, `--no-color`

**Song file formats**

`.json`:
```json
{
  "title": "My Song",
  "lines": [
    {"text": "First line", "delay": 2.0},
    "A plain string uses the default delay"
  ]
}
```

`.txt` (one line per lyric, optional `seconds|` prefix for the pause):
```
2.5|First line
A line without a number uses the default delay
```

The sample songs are original text written for this repo. Add your own song files to `lyrics/songs/`.

## Original lyrics scripts

The two first versions are still in the repo root:
- `Basic Line-by-Line Delay lyrics.py`
- `Letter-by-Letter Typing Animation lyrics.py`

## Author

Made by [@Kaito857](https://github.com/Kaito857)
