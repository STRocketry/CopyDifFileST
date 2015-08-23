import windows.ProgramWindow;

import javax.swing.*;

public class Main
{
    public static void main (String [] args) {
        JFrame window = new ProgramWindow();
        window.setLocationRelativeTo(null); //устанавливает окно по центру экрана
        window.setVisible(true);
    }
}
