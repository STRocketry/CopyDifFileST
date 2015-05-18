import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Admin on 16.04.15.
 */
public class ProgramWindow extends JFrame
{

    private StringField extField;
    private StringField fromDir;
    private StringField toDir;
    private MyFileFindVisitor myFileFindVisitor;
    private JLabel copyFileLabel;
    private JButton copy;

    ProgramWindow()
    {
        super("Copy different files ST");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Настраиваем первую горизонтальную панель (для ввода расширения файла)
        Box box1 = Box.createHorizontalBox();
        JLabel extensionLabel = new JLabel("Extension:");
        JTextField extensionField = new JTextField(15);
        box1.add(extensionLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(extensionField);
        // Настраиваем вторую горизонтальную панель (для ввода пути каталога нахождения файлов)
        Box box2 = Box.createHorizontalBox();
        JLabel sourceLabel = new JLabel("From:");
        JTextField dirOldField = new JTextField(15);
        box2.add(sourceLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(dirOldField);
        // Настраиваем третью горизонтальную панель (для ввода пути каталога копирования файлов)
        Box box3 = Box.createHorizontalBox();
        JLabel destLabel = new JLabel("To:");
        JTextField dirNewField = new JTextField(15);
        box3.add(destLabel);
        box3.add(Box.createHorizontalStrut(6));
        box3.add(dirNewField);

        // Настраиваем четвертую горизонтальную панель (с кнопками)
        Box box4 = Box.createHorizontalBox();
        copyFileLabel = new JLabel("Copy:");
        Font font = new Font("Verdana", Font.ITALIC, 9); //фонты для label
        copyFileLabel.setFont(font);
        box4.add(copyFileLabel);
        copy = new JButton("Copy");
        box4.add(Box.createHorizontalGlue());
        box4.add(copy);
        copy.setEnabled(false); //Сразу сделали кнопку не активной, пока не ввели все данные
        // Уточняем размеры компонентов
        extensionLabel.setPreferredSize(sourceLabel.getPreferredSize());
        // Размещаем четыре горизонтальные панели на одной вертикальной
        final Box mainBox = Box.createVerticalBox();
        mainBox.setBorder(new EmptyBorder(12,12,12,12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(box3);
        mainBox.add(Box.createVerticalStrut(22));
        mainBox.add(box4);
        setContentPane(mainBox);
       // setSize(250, 100); //Ручная установка размера окна
        pack(); //Автоматически устанавливает предпочтительный размер
        setResizable(true); //запретить окну изменять свои размеры


        extField = new StringField(extensionField, this);
        fromDir = new StringField(dirOldField, this);
        toDir = new StringField(dirNewField, this);
        fromDir.addListener();
        extField.addListener();
        toDir.addListener();

        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                Path startPath = Paths.get(fromDir.getValue());
                myFileFindVisitor = new MyFileFindVisitor("glob:*." + extField.getValue());
                try {
                    Files.walkFileTree(startPath, myFileFindVisitor);
                    System.out.println("File search completed!");
                    copyFileLabel.setText("File search completed!"); //НЕ РАБОТАЕ!!!! ???
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (Path p : myFileFindVisitor.getArray())//копируем каждый файл
                {
                    System.out.println(p.getFileName()); //выводим копируемый файл в консоль для отладки
                    copyFileLabel.setText("Copy: " + p.getFileName()); //НЕ РАБОТАЕ!!!! ???
                    copyFileLabel.paintImmediately(copyFileLabel.getVisibleRect());
                    new CopyFiles(p.toAbsolutePath().toString(), toDir.getValue() + "/Копия-" + p.getFileName());
                    copyFileLabel.setText("Ready");
                }

                //Очитска полей для нового ввода
                extField.clear();
                fromDir.clear();
                toDir.clear();
                setCopyButtonEnabled();
            }
        });
    }

   public void setCopyButtonEnabled() {
       boolean anyUnread = !extField.isRead() || !fromDir.isRead() || !toDir.isRead();
       copy.setEnabled(!anyUnread); //Когда ввели все данные делаем кнопу активной
    }
}

