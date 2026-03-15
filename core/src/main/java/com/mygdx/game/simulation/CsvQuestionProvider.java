package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

/**
 * CsvQuestionProvider - loads questions from a CSV file.
 * 
 * CSV Format:
 * question,correct_answer,wrong_answer_1,wrong_answer_2,theme_color_r,theme_color_g,theme_color_b,time_limit
 * 
 * SRP: Responsible only for loading and parsing CSV data into Question objects.
 * DIP: Implements IQuestionProvider - GameScene doesn't care where questions come from.
 */
public class CsvQuestionProvider implements IQuestionProvider {
    private Array<Question> questions;
    private static final String CSV_FILE = "questions.csv";

    public CsvQuestionProvider() {
        this.questions = new Array<>();
        loadQuestionsFromCsv();
    }

    private void loadQuestionsFromCsv() {
        try {
            FileHandle fileHandle = Gdx.files.internal(CSV_FILE);
            if (!fileHandle.exists()) {
                System.err.println("⚠️ questions.csv not found! Using default questions.");
                loadDefaultQuestions();
                return;
            }

            String[] lines = fileHandle.readString().split("\n");
            
            if (lines.length < 2) {
                System.err.println("⚠️ questions.csv is empty! Using default questions.");
                loadDefaultQuestions();
                return;
            }

            // Skip header line (line 0)
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                try {
                    Question q = parseQuestionFromCsvLine(line);
                    if (q != null) {
                        questions.add(q);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error parsing line " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (questions.size == 0) {
                System.err.println("⚠️ No valid questions loaded from CSV! Using default questions.");
                loadDefaultQuestions();
                return;
            }

            System.out.println("✓ Loaded " + questions.size + " questions from " + CSV_FILE);
            questions.shuffle();

        } catch (Exception e) {
            System.err.println("✗ Error loading CSV file: " + e.getMessage());
            loadDefaultQuestions();
        }
    }

    /**
     * Parse a single CSV line into a Question object.
     * Format: question,correct_answer,wrong_answer_1,wrong_answer_2,color_r,color_g,color_b,time_limit
     */
    private Question parseQuestionFromCsvLine(String line) {
        try {
            // Simple CSV parsing - handles quotes around fields with commas
            String[] fields = parseCSVLine(line);

            if (fields.length < 8) {
                System.err.println("✗ Insufficient fields in line: " + line);
                return null;
            }

            String questionText = fields[0].trim();
            String correctAnswer = fields[1].trim();
            String wrongAnswer1 = fields[2].trim();
            String wrongAnswer2 = fields[3].trim();
            
            float colorR = Float.parseFloat(fields[4].trim());
            float colorG = Float.parseFloat(fields[5].trim());
            float colorB = Float.parseFloat(fields[6].trim());
            float timeLimit = Float.parseFloat(fields[7].trim());

            // First element in array must be the correct answer
            String[] answers = { correctAnswer, wrongAnswer1, wrongAnswer2 };
            Color themeColor = new Color(colorR, colorG, colorB, 1f);

            return new Question(questionText, answers, themeColor, timeLimit);

        } catch (NumberFormatException e) {
            System.err.println("✗ Number parsing error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("✗ Error parsing question: " + e.getMessage());
            return null;
        }
    }

    /**
     * Simple CSV line parser that handles quoted fields containing commas.
     * Splits on commas that are not inside quotes.
     */
    private String[] parseCSVLine(String line) {
        Array<String> fields = new Array<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(String.class);
    }

    /**
     * Fallback to default questions if CSV loading fails.
     */
    private void loadDefaultQuestions() {
        questions.clear();
        questions.add(new Question(
            "What is the probability of flipping tails on a fair coin?",
            new String[]{"1/2", "1/3", "1/4"},
            new Color(0.1f, 0.1f, 0.2f, 1f),
            5f
        ));
        questions.add(new Question(
            "What is the probability of rolling a 4 on a 6-sided die?",
            new String[]{"1/6", "1/2", "1/4"},
            new Color(0.1f, 0.2f, 0.1f, 1f),
            5f
        ));
        questions.add(new Question(
            "What is the probability of drawing an Ace from a standard deck?",
            new String[]{"1/13", "1/4", "1/52"},
            new Color(0.2f, 0.1f, 0.1f, 1f),
            4f
        ));
        questions.add(new Question(
            "What is the probability of flipping two heads in a row?",
            new String[]{"1/4", "1/2", "3/4"},
            new Color(0.2f, 0.2f, 0.1f, 1f),
            4f
        ));
        questions.add(new Question(
            "What is the probability of rolling a sum of 7 with two dice?",
            new String[]{"1/6", "1/12", "1/36"},
            new Color(0.1f, 0.1f, 0.1f, 1f),
            3.5f
        ));

        System.out.println("✓ Using " + questions.size + " default questions");
        questions.shuffle();
    }

    @Override
    public Array<Question> getQuestions() {
        return questions;
    }

    @Override
    public Question getQuestion(int index) {
        if (index >= 0 && index < questions.size) {
            return questions.get(index);
        }
        return null;
    }

    @Override
    public int getTotalQuestions() {
        return questions.size;
    }

    /**
     * Reshuffle questions for a new game.
     * Call this when starting a new game to ensure different question order.
     */
    public void shuffleForNewGame() {
        questions.shuffle();
        System.out.println("✓ Questions reshuffled for new game");
    }
}
