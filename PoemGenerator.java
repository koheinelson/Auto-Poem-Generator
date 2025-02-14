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
    private static final String TWAIN_TEXT_URL = "https://raw.githubusercontent.com/koheinelson/auto-poem/refs/heads/master/mark_twain.txt";

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
       
    	List<String> rhymes = getRhymes(word); // Get rhyming words
        List<String> sentences = getMarkTwainSentences(); // Get Twain sentences
        StringBuilder poem = new StringBuilder();
        int linesAdded = 0;

        for (String rhyme : rhymes) { // Go through each rhyme in order
            if (linesAdded >= 10) break; // Stop after 10 lines

            for (String sentence : sentences) { // Check each sentence
                int index = sentence.toLowerCase().indexOf(rhyme.toLowerCase()); // Find the index of the rhyme
                if (index != -1) { // If rhyme exists in the sentence
                    String extracted = sentence.substring(0, index + rhyme.length()).trim(); // Extract up to the rhyme
                    
                    // Normalize spaces (remove extra spaces between words)
                    extracted = extracted.replaceAll("\\s+", " ");

                    // Ensure the extracted text contains at least two words
                    if (extracted.split("\\s+").length >= 2) {
                        poem.append(extracted).append("\n");
                        linesAdded++;
                        break; // Move to the next rhyme after finding a match
                    }
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
    word = word.toLowerCase().replaceAll("[^a-z]", ""); // Remove non-letter characters
    int syllableCount = 0;
    boolean prevVowel = false;

    for (char c : word.toCharArray()) {
        if ("aeiouy".indexOf(c) != -1) { // Check if the character is a vowel
            if (!prevVowel) { // Count only new vowel groups
                syllableCount++;
            }
            prevVowel = true;
        } else {
            prevVowel = false;
        }
    }

    // Adjust for common edge cases
    if (word.endsWith("e") && syllableCount > 1) {
        syllableCount--;
    }

    return syllableCount == 1;
}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PoemGenerator::createAndShowGUI);
    }
}
