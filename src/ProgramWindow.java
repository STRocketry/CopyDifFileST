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
    private JLabel copyFileLabel;
    private JLabel processLabel;
    private JButton copy;
    private JButton cancel;
    private JProgressBar progressBar1;
    private int processed;
    private boolean flag = false;

    ProgramWindow() {
        super("Copy different files ST");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Настраиваем первую горизонтальную панель (для ввода расширения файла)
        Box box1 = Box.createHorizontalBox();
        JLabel extensionLabel = new JLabel("Extension:");
        JTextField extensionField = new JTextField(15);
        box1.add(extensionLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(extensionField);
        box1.add(Box.createHorizontalStrut(200));

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

        // Настраиваем четвертую горизонтальную панель
        Box box4 = Box.createHorizontalBox();
        copyFileLabel = new JLabel("Copy:");
        Font font = new Font("Verdana", Font.ITALIC, 9); //фонты для label
        copyFileLabel.setFont(font);
        copyFileLabel.setPreferredSize(new Dimension(300, 10));
        box4.add(copyFileLabel);
        box4.add(Box.createHorizontalGlue());

        Box box5 = Box.createHorizontalBox();
        progressBar1 = new JProgressBar(0, 100);
        progressBar1.setStringPainted(true);
        progressBar1.setPreferredSize(new Dimension(318, 20));

        box5.add(progressBar1);
        box5.add(Box.createHorizontalGlue());

        Box box6 = Box.createHorizontalBox();

        processLabel = new JLabel("Files (total/processed): 0/0");
        box6.add(processLabel);
        Font font2 = new Font("Verdana", Font.PLAIN, 9);
        processLabel.setFont(font2);
        processLabel.setPreferredSize(new Dimension(240, 10));
        box6.add(Box.createHorizontalStrut(5));
        copy = new JButton("Copy");
        box6.add(copy);
        box6.add(Box.createHorizontalStrut(10));
        cancel = new JButton("Cancel");
        box6.add(cancel);
        box6.add(Box.createHorizontalGlue());
        copy.setEnabled(false); //Сразу сделали кнопку не активной, пока не ввели все данные
        cancel.setEnabled(false);

        // Размещаем четыре горизонтальные панели на одной вертикальной
        final Box mainBox = Box.createVerticalBox();
        mainBox.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box3);
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(box4);
        mainBox.add(Box.createVerticalStrut(5));
        mainBox.add(box5);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box6);
        setContentPane(mainBox);
        setSize(350, 220); //Ручная установка размера окна
        // pack(); //Автоматически устанавливает предпочтительный размер
        setResizable(false); //запретить окну изменять свои размеры


        extField = new StringField(extensionField, this);
        fromDir = new StringField(dirOldField, this);
        toDir = new StringField(dirNewField, this);
        fromDir.addListener();
        extField.addListener();
        toDir.addListener();

        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                flag = true;
            }
        });

        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                Thread thread = new Thread() {
                    public void run() {
                        copyStart();
                    }
                };
                thread.start();
            }
        });
    }

    private void copyStart() {
        copy.setEnabled(false);

        Path startPath = Paths.get(fromDir.getValue());
        MyFileFindVisitor myFileFindVisitor = new MyFileFindVisitor("glob:*." + extField.getValue());
        try {
            Files.walkFileTree(startPath, myFileFindVisitor);
            System.out.println("File search completed!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        processLabel.setText("Files (total/processed): " + myFileFindVisitor.getArrayFilesCopySize() + "/0");
        progressBar1.setMaximum(myFileFindVisitor.getArrayFilesCopySize());

        cancel.setEnabled(true); //Кнопку отмены делаем активной

        for (Path p : myFileFindVisitor.getArray())//копируем каждый файл
        {
            System.out.println(p.getFileName()); //выводим копируемый файл в консоль для отладки
            copyFileLabel.setText("Copy: " + p.toAbsolutePath().toString());

            new CopyFiles(p.toAbsolutePath().toString(), toDir.getValue() + "/Copy-" + p.getFileName());
            processed++;
            processLabel.setText("Files (total/processed): " + myFileFindVisitor.getArrayFilesCopySize() + "/" + processed);
            progressBar1.setValue(processed);

            if (flag){
                flag = false;
                break;
            }

        }

        processed = 0;
        progressBar1.setValue(processed);
        copyFileLabel.setText("Ready");
        processLabel.setText("Files (total/processed): 0/0");
        cancel.setEnabled(false);
        //Очитска полей для нового ввода
        extField.clear();
        fromDir.clear();
        toDir.clear();
        setCopyButtonEnabled();
    }

    public void setCopyButtonEnabled() {
        boolean anyUnread = !extField.isRead() || !fromDir.isRead() || !toDir.isRead();
        copy.setEnabled(!anyUnread); //Когда ввели все данные делаем кнопу активной
    }
}

