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
import java.util.Random;

public class PoemGenerator {

    private static final String API_URL = "https://api.datamuse.com/words?rel_rhy=";
    private static final String TWAIN_TEXT_URL = "https://raw.githubusercontent.com/gmamaladze/trienet/master/DemoApp/texts/Adventures%20of%20Huckleberry%20Finn%20by%20Mark%20Twain.txt"; // Raw Twain text URL

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
                    // Split by sentence end markers, you might need more sophisticated sentence parsing
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

    // Generate a short poem by finding rhyming words in Mark Twain's sentences
    public static String generatePoem(String word) {
        // Get rhymes for the input word
        List<String> rhymes = getRhymes(word);

        // Get sentences from Mark Twain text
        List<String> sentences = getMarkTwainSentences();

        // Build the poem by matching rhyming words with sentences
        StringBuilder poem = new StringBuilder("Here is your poem based on the word '");
        poem.append(word).append("':\n");

        Random rand = new Random();
        for (String rhyme : rhymes) {
            // Find a random sentence that contains a rhyme
            for (String sentence : sentences) {
                if (sentence.contains(rhyme)) {
                    poem.append(sentence).append("\n");
                    break;
                }
            }
        }

        return poem.toString();
    }

    // Method to create and show the GUI
    public static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Rhyme Poem Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a panel for the components
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a label for instructions
        JLabel label = new JLabel("Enter a monosyllabic word:");
        panel.add(label, BorderLayout.NORTH);

        // Create a text field for the word input
        JTextField wordField = new JTextField();
        panel.add(wordField, BorderLayout.CENTER);

        // Create a button to generate the poem
        JButton generateButton = new JButton("Generate Poem");
        panel.add(generateButton, BorderLayout.SOUTH);

        // Create a text area to display the poem
        JTextArea poemArea = new JTextArea();
        poemArea.setEditable(false);
        poemArea.setLineWrap(true);
        poemArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(poemArea);
        panel.add(scrollPane, BorderLayout.EAST);

        // Add the panel to the frame
        frame.getContentPane().add(panel);

        // Define the button action
        generateButton.addActionListener(e -> {
            String word = wordField.getText().trim();

            // Ensure the word is monosyllabic (optional check)
            if (isMonosyllabic(word)) {
                // Generate the poem and display it
                String poem = generatePoem(word);
                poemArea.setText(poem);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a monosyllabic word.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    // Check if the word is monosyllabic (simple check, can be improved)
    public static boolean isMonosyllabic(String word) {
        return word.split("[aeiou]+").length == 2; // Simplistic check for syllables
    }

    public static void main(String[] args) {
        // Run the GUI on the Swing event dispatch thread
        SwingUtilities.invokeLater(RhymePoemGeneratorGUI::createAndShowGUI);
    }
}
