package PoetryGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class PoemGenerator {

    private static final String API_URL = "https://api.datamuse.com/words?rel_rhy=";
    private static final String TWAIN_TEXT_URL = "https://raw.githubusercontent.com/gmamaladze/trienet/master/DemoApp/texts/Adventures%20of%20Huckleberry%20Finn%20by%20Mark%20Twain.txt";

    public static List<String> getRhymes(String word) {
        List<String> rhymes = new ArrayList<>();
        try {
            URL url = new URL(API_URL + word);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    rhymes.add(obj.getString("word"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching rhymes: " + e.getMessage());
        }
        return rhymes;
    }

    public static List<String> getMarkTwainSentences() {
        List<String> sentences = new ArrayList<>();
        try {
            URL url = new URL(TWAIN_TEXT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] sentenceArray = line.split("\\. ");
                    for (String sentence : sentenceArray) {
                        sentences.add(sentence.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading Mark Twain text: " + e.getMessage());
        }
        return sentences;
    }

    public static String generatePoem(String word) {
        List<String> rhymes = getRhymes(word);
        List<String> sentences = getMarkTwainSentences();
        Set<String> usedRhymes = new HashSet<>();
        Set<String> usedLines = new HashSet<>();
        StringBuilder poem = new StringBuilder();
        int linesAdded = 0;

        for (String rhyme : rhymes) {
            if (linesAdded >= 10) break;
            if (usedRhymes.contains(rhyme)) continue;

            for (String sentence : sentences) {
                if (sentence.contains(rhyme)) {
                    String[] words = sentence.split("\\s+");
                    StringBuilder line = new StringBuilder();

                    int start = Math.max(0, Arrays.asList(words).indexOf(rhyme) - 9);
                    for (int i = start; i < words.length && line.length() < 50; i++) {
                        line.append(words[i]).append(" ");
                    }

                    // Ensure the line has at least 3 words
                    String lineText = line.toString().trim();
                    if (lineText.split("\\s+").length < 3) {
                        continue; // Skip lines with fewer than 3 words
                    }

                    // Check if the line ends with a rhyme
                    String lastWord = lineText.split("\\s+")[lineText.split("\\s+").length - 1];

                    // If the line doesn't end with a rhyme, skip this sentence
                    if (!rhymes.contains(lastWord)) {
                        continue;
                    }

                    // Skip if the line has already been used
                    if (usedLines.contains(lineText)) {
                        continue;
                    }

                    poem.append(lineText).append("\n");
                    usedRhymes.add(rhyme);
                    usedLines.add(lineText); // Mark this line as used
                    linesAdded++;
                    break;
                }
            }
        }

        return poem.append("\n-by Mark Twain").toString();
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Rhyme Poem Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel label = new JLabel("Input a monosyllabic word:");
        JTextField wordField = new JTextField(10);
        JButton generateButton = new JButton("Generate Poem");
        topPanel.add(label);
        topPanel.add(wordField);
        topPanel.add(generateButton);

        JTextArea poemArea = new JTextArea();
        poemArea.setEditable(false);
        poemArea.setLineWrap(true);
        poemArea.setWrapStyleWord(true);
        poemArea.setFont(new Font("Serif", Font.PLAIN, 18));
        poemArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(poemArea);
        scrollPane.setPreferredSize(new Dimension(580, 400));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> {
            String word = wordField.getText().trim();
            if (isMonosyllabic(word)) {
                poemArea.setText("");
                poemArea.setText(generatePoem(word));
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a monosyllabic word.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static boolean isMonosyllabic(String word) {
        return word.split("[aeiouy]+").length == 2;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PoemGenerator::createAndShowGUI);
    }
}


