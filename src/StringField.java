import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Admin on 10.05.15.
 */
public class StringField {

    private final JTextField jTextField;
    private final ProgramWindow programWindow;
    String value;

    public StringField(JTextField extensionField, ProgramWindow programWindow) {
        this.jTextField = extensionField;
        this.programWindow = programWindow;
    }

    void addListener() {
        jTextField.addActionListener(
                new ActionListener() //добавил лисинер для поля
                {
                    public void actionPerformed(ActionEvent evt) //обработчик лисинера
                    {
                        value = jTextField.getText();
                        jTextField.setBackground(new Color(0x87AEC8)); //меняем цвет если внесли данные
                        System.out.println(value);
                        programWindow.setCopyButtonEnabled();
                    }
                }
        );
    }

    public String getValue() {
        return value;
    }

    public boolean isRead() {
        return jTextField.getText() != null && jTextField.getText().length() > 0;
    }

    public void clear() {
        jTextField.setText(null);
        jTextField.setBackground(new Color(0xFFFFFF)); //меняем цвет когда очистили поле
    }
}
