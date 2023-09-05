package Gui;

import javax.swing.*;
import java.awt.*;

// Class that represent the gui for the new game settings gui
public class GameSettingsGui {

    private static final int WHITE_COLOR_INDEX = 0;

    // Create the gui for the new game settings
    public static void createPopupWindow(GameSettings gameSettings, ChessBoardGui gui) {
        JFrame frame = new JFrame("Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Create all the labels and text for all the different settings
        JLabel colorLabel = new JLabel("Engine color:");
        JComboBox<String> colorComboBox = new JComboBox<>(new String[]{"White", "Black"});

        JLabel fenLabel = new JLabel("Enter FEN Position (empty for default):");
        JTextField fenTextField = new JTextField(20); // Increase text field size

        JLabel timeLabel = new JLabel("Time to Think (easy 2, medium 6):");
        JTextField timeTextField = new JTextField(20); // Increase text field size
        timeTextField.setText("2");

        JLabel depthLabel = new JLabel("Starting depth (recommended 2):");
        JTextField depthTextField = new JTextField(20); // Increase text field size
        depthTextField.setText("2");

        // Button for the new game settings
        JButton newGameButton = new JButton("Start new game");
        newGameButton.addActionListener(e -> {
            // Put all the settings that the user entered into the gameSettings object
            gameSettings.engineColorToPlay = colorComboBox.getSelectedIndex() == WHITE_COLOR_INDEX;
            gameSettings.fenStartingPosition = fenTextField.getText();
            gameSettings.engineTimeToThink = Integer.parseInt(timeTextField.getText());
            gameSettings.startingDepthForSearch = Integer.parseInt(depthTextField.getText());

            // Start a game
            gui.startGame();
            // Close the popup window
            frame.dispose();
        });

        // Add all the labels and text in the right order
        panel.add(colorLabel, gbc);
        gbc.gridx++;
        panel.add(colorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(fenLabel, gbc);
        gbc.gridx++;
        panel.add(fenTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(timeLabel, gbc);
        gbc.gridx++;
        panel.add(timeTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(depthLabel, gbc);
        gbc.gridx++;
        panel.add(depthTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // Span two columns for the submit button
        panel.add(newGameButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);
    }
}
