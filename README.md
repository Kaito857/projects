# Projects

A small collection of beginner-friendly programs in Java and Python.

## Contents

| Project | Language | Description |
|---|---|---|
| [QuizApp](#quizapp) | Java | A command-line quiz application |
| [Line-by-Line Lyrics](#line-by-line-lyrics) | Python | Prints lyrics one line at a time with a custom delay per line |
| [Typing Animation Lyrics](#typing-animation-lyrics) | Python | Prints lyrics letter by letter, like a typewriter |

## QuizApp

A quiz you play in the terminal. *(Add a sentence or two here: what topics, how many questions, how scoring works.)*

**Requirements:** Java JDK 8 or newer

**Run it from the command prompt:**

```bash
cd path/to/projects
javac QuizApp.java
java QuizApp
```

Or run the prebuilt jar:

```bash
java -jar QuizApp.jar
```

## Line-by-Line Lyrics

Prints each lyric line on its own, waiting a set number of seconds before the next one. Each line has its own delay, so you can match the timing of the song.

```bash
python "Basic Line-by-Line Delay lyrics.py"
```

## Typing Animation Lyrics

Prints lyrics one character at a time using `sys.stdout.flush()` and `time.sleep()`. `sing_line()` takes two optional arguments, `char_delay` (default 0.08s) and `line_delay` (default 1.5s), so you can speed it up or slow it down.

```bash
python "Letter-by-Letter Typing Animation lyrics.py"
```

**Requirements:** Python 3.x (no extra packages needed)

## Author

Made by [@Kaito857](https://github.com/Kaito857)
