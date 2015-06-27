import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Admin on 16.04.15.
 */
public class ProgramWindow extends JFrame
{
    private JTextField fromDirField;
    private JTextField toDirField;
    private JComboBox combo;
    private JLabel copyFileLabel;
    private JLabel processLabel;
    private JButton copy;
    private JButton cancel;
    private JProgressBar progressBar;
    private int processed;
    private Thread thread;

    ProgramWindow() {
        super("Copy different files ST");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenuItem help = new JMenuItem("How to use");
        menuBar.add(help);

        // Настраиваем первую горизонтальную панель (для ввода расширения файла)
        Box box1 = Box.createHorizontalBox();
        JLabel extensionLabel = new JLabel("Extension:");
        box1.add(extensionLabel);
        box1.add(Box.createHorizontalStrut(6));
        String[] extElements = new String[] {"mp3", "pdf", "avi", "jpg", "txt"};
        combo = new JComboBox(extElements);
        combo.setMaximumSize(new Dimension(20, 20));
        combo.setEditable(true); // Позволяет ввести произвольны элемент
        box1.add(combo);
        box1.add(Box.createHorizontalStrut(190));
        box1.add(Box.createHorizontalGlue());

        // Настраиваем вторую горизонтальную панель (для ввода пути каталога нахождения файлов)
        Box box2 = Box.createHorizontalBox();
        JLabel fromLabel = new JLabel("From:");
        fromDirField = new JTextField(15);
        fromDirField.setMaximumSize(new Dimension(310, 21));
        JButton from = new JButton("...");
        from.setMaximumSize(new Dimension(20, 20));
        box2.add(fromLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(fromDirField);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(from);
        box2.add(Box.createHorizontalGlue());

        // Настраиваем третью горизонтальную панель (для ввода пути каталога копирования файлов)
        Box box3 = Box.createHorizontalBox();
        JLabel toLabel = new JLabel("To:");
        toDirField = new JTextField(15);
        toDirField.setMaximumSize(fromDirField.getMinimumSize());
        JButton to = new JButton("...");
        to.setMaximumSize(new Dimension(20, 20));
        box3.add(toLabel);
        box3.add(Box.createHorizontalStrut(6));
        box3.add(toDirField);
        box3.add(Box.createHorizontalStrut(6));
        box3.add(to);
        box3.add(Box.createHorizontalGlue());

        // Настраиваем четвертую горизонтальную панель
        Box box4 = Box.createHorizontalBox();
        copyFileLabel = new JLabel("Copy:");
        Font font = new Font("Verdana", Font.ITALIC, 9); //фонты для label
        copyFileLabel.setFont(font);
        copyFileLabel.setPreferredSize(new Dimension(300, 12));
        box4.add(copyFileLabel);
        box4.add(Box.createHorizontalGlue());

        Box box5 = Box.createHorizontalBox();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(318, 20));
        box5.add(progressBar);
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
        box6.add(Box.createHorizontalStrut(5));
        cancel = new JButton("Cancel");
        box6.add(cancel);
        box6.add(Box.createHorizontalGlue());
        copy.setEnabled(true);
        cancel.setEnabled(false);//Сразу сделали кнопку не активной, пока не запустили процесс

        // Размещаем горизонтальные панели на одной вертикальной
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
        setJMenuBar(menuBar);
        setSize(350, 262); //Ручная установка размера окна
//         pack(); //Автоматически устанавливает предпочтительный размер
        setResizable(false); //запретить окну изменять свои размеры


        from.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                getFileChooser(fromDirField);
            }
        });

        to.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                getFileChooser(toDirField);
            }
        });

        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                thread.stop(); //пока так работает, с интерраптом не работает так как надо :( 10.06.15
//                Thread.interrupted();
                JOptionPane.showMessageDialog(progressBar, processed + " files copied successfully!", "INTERRUPTED", JOptionPane.WARNING_MESSAGE);
                setInitialState();
            }
        });

        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (combo.getSelectedItem().toString().length() == 0) {
                    JOptionPane.showMessageDialog(progressBar, "<html>Field <i>Extension</i> is empty!", "ERROR", JOptionPane.ERROR_MESSAGE);

                } else   if (fromDirField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(progressBar, "<html>Field <i>FROM</i> is empty!", "ERROR", JOptionPane.ERROR_MESSAGE);

                }else if (toDirField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(progressBar, "<html>Field <i>TO</i> is empty!", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    thread = new Thread() {
                        public void run() {
                            copyStart();
                        }
                    };
                    thread.start();
                }
            }
        });


        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(progressBar, "<html>Алгоритм использования: " +
                                "<li>1. В списке <i>Extension</i> выбрать или ввести расширение без точки.</ul>" +
                                "<li>2. В поле <i>From</i> выбрать или ввести каталог поиска файлов.  </ul>" +
                                "<li>3. В поле <i>To</i> выбрать или ввести каталог куда копировать файлы.  </ul>" +
                                "<li>4. Нажать кнопку <i>Copy</i>. </ul>" +
                                "<li>5. При необходимости отмены процесса нажать <i>Cancel</i>. </ul>",
                        "How to use", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void getFileChooser(JTextField field) {
        JFileChooser fileChooser = new JFileChooser("d:/");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// Указываем выбор только директории, а не файла!
        int ret = fileChooser.showDialog(null, "Open directory");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            field.setText(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
        }
    }

    private void copyStart() {
        copy.setEnabled(false);
        cancel.setEnabled(true); //Кнопку отмены делаем активной

        Path startPath = Paths.get(fromDirField.getText());

        MyFileFindVisitor myFileFindVisitor = new MyFileFindVisitor("glob:*." + combo.getSelectedItem()); //Проверка с выпадающим списокм
        try {
            Files.walkFileTree(startPath, myFileFindVisitor);
            System.out.println("File search completed!");
            if (myFileFindVisitor.getArrayFilesCopySize() == 0){
                JOptionPane.showMessageDialog(progressBar, "Files with this extension does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
                setInitialState();
                thread.stop();
            }

        } catch (NoSuchFileException e) { //если не правильно ввели папку поиска
            System.out.println("NoSuchFileException! " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(progressBar, "<html>Directory <i>FROM</i> does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
            setInitialState();
            thread.stop();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        processLabel.setText("Files (total/processed): " + myFileFindVisitor.getArrayFilesCopySize() + "/0");
        progressBar.setMaximum(myFileFindVisitor.getArrayFilesCopySize());


//        System.out.println(thread.isInterrupted());

        for (Path p : myFileFindVisitor.getArray())//копируем каждый файл
        {
            System.out.println(p.getFileName()); //выводим копируемый файл в консоль для отладки
            copyFileLabel.setText("Copy: " + p.toAbsolutePath().toString());

            try {
                //Копируем, к имени копируемого файла добавляем названия последнего каталога!
                new CopyFiles(p.toAbsolutePath().toString(), toDirField.getText() + "/" + p.getName(p.getNameCount() - 2) + "__" + p.getFileName());
            } catch (NoSuchFileException e) { //если не правильно ввели папку назначения
                System.out.println("NoSuchFileException! " + e);
                e.printStackTrace();
                JOptionPane.showMessageDialog(copy, "<html>Directory <i>TO</i> does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
                setInitialState();
                thread.stop();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can't copy " + p.toAbsolutePath().toString());
                JOptionPane.showMessageDialog(copy, "Can't copy " + p.toAbsolutePath().toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
                processed--; //уменьшаем счетчик скопиованных файлов, так как не можем скопировать этот файл
            }

            processed++;
            processLabel.setText("Files (total/processed): " + myFileFindVisitor.getArrayFilesCopySize() + "/" + processed);
            progressBar.setValue(processed);

        }
        JOptionPane.showMessageDialog(progressBar, processed + " files copied successfully!", "READY", JOptionPane.INFORMATION_MESSAGE);
        setInitialState();
    }

    public void setInitialState() {
        processed = 0;
        progressBar.setValue(processed);
        copyFileLabel.setText("Copy");
        processLabel.setText("Files (total/processed): 0/0");
        copy.setEnabled(true);
        cancel.setEnabled(false);
        //Очитска полей для нового ввода
        fromDirField.setText(null);
        toDirField.setText(null);
    }

}

