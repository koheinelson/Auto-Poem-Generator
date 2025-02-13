package PoetryGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PoemGenerator {

    private static final String API_URL = "https://api.datamuse.com/words?rel_rhy=";
    private static final String TWAIN_TEXT_URL = "https://raw.githubusercontent.com/gmamaladze/trienet/master/DemoApp/texts/Adventures%20of%20Huckleberry%20Finn%20by%20Mark%20Twain.txt";

    // Get rhymes for the word from Datamuse API
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

    // Get sentences from Mark Twain text via URL
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

    // Generate a short poem with a maximum of 10 lines, ensuring each line ends with a rhyme
    public static String generatePoem(String word) {
        List<String> rhymes = getRhymes(word);
        List<String> sentences = getMarkTwainSentences();

        StringBuilder poem = new StringBuilder();
        int linesAdded = 0;

        for (String rhyme : rhymes) {
            if (linesAdded >= 10) break; // Stop after 10 lines
            for (String sentence : sentences) {
                if (sentence.contains(rhyme)) {
                    // Ensure the sentence ends with the rhyme word
                    String updatedSentence = sentence.replaceAll("(?i)\\b" + rhyme + "\\b.*", "") + rhyme + ".";
                    poem.append(updatedSentence).append("\n");
                    linesAdded++;
                    break;
                }
            }
        }
        return poem.toString();
    }

    // Method to create and show the GUI
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Rhyme Poem Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter a monosyllabic word:");
        panel.add(label, BorderLayout.NORTH);

        JTextField wordField = new JTextField();
        panel.add(wordField, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate Poem");
        panel.add(generateButton, BorderLayout.SOUTH);

        JTextArea poemArea = new JTextArea();
        poemArea.setEditable(false);
        poemArea.setLineWrap(true);
        poemArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(poemArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        panel.add(scrollPane, BorderLayout.EAST);

        generateButton.addActionListener(e -> {
            String word = wordField.getText().trim();
            if (isMonosyllabic(word)) {
                String poem = generatePoem(word);
                poemArea.setText(poem);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a monosyllabic word.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    // Check if the word is monosyllabic (simple check, can be improved)
    public static boolean isMonosyllabic(String word) {
        return word.split("[aeiou]+").length == 2;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PoemGenerator::createAndShowGUI);
    }
}

