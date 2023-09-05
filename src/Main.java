import Gui.ChessBoardGui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessBoardGui::new);
    }
}
