import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Command-line quiz.
 *
 * Questions are loaded from a text file (default: questions.txt), one per line:
 *   difficulty|question|choice A;choice B;choice C;choice D|number of the correct choice (1-4)
 *
 * Features: difficulty levels, shuffled questions and choices, a time limit per
 * question, scoring, and a saved top-5 high score list (highscores.txt).
 *
 * Run:  javac QuizApp.java && java QuizApp [questionsFile]
 */
public class QuizApp {

    private static final int TIME_LIMIT_SECONDS = 15;
    private static final Path HIGH_SCORE_FILE = Paths.get("highscores.txt");

    static class Question {
        final String difficulty;
        final String text;
        final List<String> choices;
        final String correct;

        Question(String difficulty, String text, List<String> choices, String correct) {
            this.difficulty = difficulty;
            this.text = text;
            this.choices = choices;
            this.correct = correct;
        }
    }

    static class Score {
        final String name;
        final int points;
        final int total;
        final String difficulty;

        Score(String name, int points, int total, String difficulty) {
            this.name = name;
            this.points = points;
            this.total = total;
            this.difficulty = difficulty;
        }

        double percent() {
            return total == 0 ? 0 : (100.0 * points) / total;
        }
    }

    public static void main(String[] args) {
        Path file = Paths.get(args.length > 0 ? args[0] : "questions.txt");
        List<Question> all;
        try {
            all = loadQuestions(file);
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
            return;
        }
        if (all.isEmpty()) {
            System.out.println("No valid questions found in " + file);
            return;
        }

        Scanner in = new Scanner(System.in);
        System.out.println("=== QUIZ APP ===");
        System.out.print("Your name: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        String difficulty = askDifficulty(in);
        List<Question> quiz = new ArrayList<>();
        for (Question q : all) {
            if (difficulty.equals("all") || q.difficulty.equals(difficulty)) {
                quiz.add(q);
            }
        }
        if (quiz.isEmpty()) {
            System.out.println("No questions for that difficulty yet.");
            return;
        }
        Collections.shuffle(quiz);

        System.out.println();
        System.out.println(quiz.size() + " questions. You have " + TIME_LIMIT_SECONDS
                + " seconds for each one. Good luck!");

        int points = 0;
        int number = 1;
        for (Question q : quiz) {
            if (ask(in, q, number, quiz.size())) {
                points++;
            }
            number++;
        }

        Score result = new Score(name, points, quiz.size(), difficulty);
        System.out.println();
        System.out.printf(Locale.US, "Final score: %d/%d (%.0f%%)%n", points, quiz.size(), result.percent());

        saveScore(result);
        showHighScores();
    }

    private static String askDifficulty(Scanner in) {
        while (true) {
            System.out.print("Difficulty (easy / medium / hard / all): ");
            String d = in.nextLine().trim().toLowerCase(Locale.ROOT);
            if (d.equals("easy") || d.equals("medium") || d.equals("hard") || d.equals("all")) {
                return d;
            }
            System.out.println("Please type easy, medium, hard, or all.");
        }
    }

    private static boolean ask(Scanner in, Question q, int number, int total) {
        List<String> shuffled = new ArrayList<>(q.choices);
        Collections.shuffle(shuffled);

        System.out.println();
        System.out.println("Question " + number + "/" + total + " [" + q.difficulty + "]");
        System.out.println(q.text);
        for (int i = 0; i < shuffled.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + shuffled.get(i));
        }

        long start = System.nanoTime();
        int choice = readChoice(in, shuffled.size());
        long elapsed = (System.nanoTime() - start) / 1_000_000_000L;

        if (elapsed > TIME_LIMIT_SECONDS) {
            System.out.println("Too slow! (" + elapsed + "s) The answer was: " + q.correct);
            return false;
        }
        if (shuffled.get(choice - 1).equals(q.correct)) {
            System.out.println("Correct!");
            return true;
        }
        System.out.println("Wrong. The answer was: " + q.correct);
        return false;
    }

    private static int readChoice(Scanner in, int max) {
        while (true) {
            System.out.print("Your answer (1-" + max + "): ");
            String line = in.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= max) {
                    return n;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Enter a number from 1 to " + max + ".");
        }
    }

    static List<Question> loadQuestions(Path file) throws IOException {
        List<Question> questions = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\|");
            if (parts.length != 4) {
                continue;
            }
            String[] choices = parts[2].split(";");
            try {
                int answerIndex = Integer.parseInt(parts[3].trim());
                if (choices.length < 2 || answerIndex < 1 || answerIndex > choices.length) {
                    continue;
                }
                List<String> list = new ArrayList<>();
                for (String c : choices) {
                    list.add(c.trim());
                }
                questions.add(new Question(
                        parts[0].trim().toLowerCase(Locale.ROOT),
                        parts[1].trim(),
                        list,
                        list.get(answerIndex - 1)));
            } catch (NumberFormatException e) {
                // skip malformed line
            }
        }
        return questions;
    }

    private static void saveScore(Score s) {
        // Names can't contain '|' because it is our separator.
        String safeName = s.name.replace("|", "/");
        String line = safeName + "|" + s.points + "|" + s.total + "|" + s.difficulty + System.lineSeparator();
        try {
            Files.write(HIGH_SCORE_FILE, line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("(Could not save your score: " + e.getMessage() + ")");
        }
    }

    private static void showHighScores() {
        List<Score> scores = new ArrayList<>();
        if (Files.exists(HIGH_SCORE_FILE)) {
            try {
                for (String line : Files.readAllLines(HIGH_SCORE_FILE, StandardCharsets.UTF_8)) {
                    String[] p = line.split("\\|");
                    if (p.length == 4) {
                        try {
                            scores.add(new Score(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]), p[3]));
                        } catch (NumberFormatException ignored) {
                            // skip bad line
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("(Could not read high scores: " + e.getMessage() + ")");
                return;
            }
        }
        scores.sort(Comparator.comparingDouble(Score::percent).reversed()
                .thenComparing(Comparator.comparingInt((Score s) -> s.total).reversed()));

        System.out.println();
        System.out.println("--- TOP 5 HIGH SCORES ---");
        int shown = Math.min(5, scores.size());
        for (int i = 0; i < shown; i++) {
            Score s = scores.get(i);
            System.out.printf(Locale.US, "%d. %-12s %d/%d (%.0f%%) [%s]%n",
                    i + 1, s.name, s.points, s.total, s.percent(), s.difficulty);
        }
    }
}
