import windows.ProgramWindow;

import javax.swing.*;

public class Main
{
    public static void main (String [] args) {
        JFrame window = new ProgramWindow();
        window.setLocationRelativeTo(null); //������������� ���� �� ������ ������
        window.setVisible(true);
    }
}
