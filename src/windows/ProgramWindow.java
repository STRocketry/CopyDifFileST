package windows;

import helpers.FilesCopier;
import helpers.FilesFinder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class ProgramWindow extends JFrame {
    private JTextField fromDirField;
    private JTextField toDirField;
    private JComboBox combo;
    private JLabel copyFileLabel;
    private JLabel processLabel;
    private JButton copy;
    private JButton cancel;
    private JProgressBar progressBar;
    private int countFiles;
    private long countSize;
    private Thread thread;

    public ProgramWindow() {
        super("Copy different files ST");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenuItem about = new JMenuItem("About");
        menuBar.add(about);

        // Настраиваем первую горизонтальную панель (для ввода расширения файла)
        Box box1 = Box.createHorizontalBox();
        final JLabel extensionLabel = new JLabel("Extension:");
        box1.add(extensionLabel);
        box1.add(Box.createHorizontalStrut(6));
        String[] extElements = new String[]{"mp3", "pdf", "avi", "jpg", "txt"};
        combo = new JComboBox<String>(extElements);
        combo.setMaximumSize(new Dimension(20, 20));
        combo.setEditable(true); // Позволяет ввести произвольны элемент
        box1.add(combo);
        box1.add(Box.createHorizontalStrut(190));
        box1.add(Box.createHorizontalGlue());

        // Настраиваем вторую горизонтальную панель (для ввода пути каталога нахождения файлов)
        Box box2 = Box.createHorizontalBox();
        final JLabel fromLabel = new JLabel("From:");
        fromDirField = new JTextField(15);
        fromDirField.setPreferredSize(new Dimension(310, 20));
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
        final JLabel toLabel = new JLabel("To:");
        toDirField = new JTextField(15);
        toDirField.setPreferredSize(fromDirField.getMinimumSize());
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
        copyFileLabel = new JLabel();
        Font font = new Font("Verdana", Font.ITALIC, 9); //фонты для label
        copyFileLabel.setFont(font);
        copyFileLabel.setPreferredSize(new Dimension(380, 12));
        box4.add(copyFileLabel);
        box4.add(Box.createHorizontalGlue());

        Box box5 = Box.createHorizontalBox();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(380, 20));
        box5.add(progressBar);
        box5.add(Box.createHorizontalGlue());

        Box box6 = Box.createHorizontalBox();
        processLabel = new JLabel();
        box6.add(processLabel);
        Font font2 = new Font("Verdana", Font.PLAIN, 9);
        processLabel.setFont(font2);
        processLabel.setPreferredSize(new Dimension(240, 10));
        box6.add(Box.createHorizontalStrut(2));
        copy = new JButton("Copy");
        box6.add(copy);
        box6.add(Box.createHorizontalStrut(5));
        cancel = new JButton("Cancel");
        box6.add(cancel);
        box6.add(Box.createHorizontalGlue());

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
//        setSize(350, 300); //Ручная установка размера окна
        pack(); //Автоматически устанавливает предпочтительный размер
        setResizable(true); //запретить окну изменять свои размеры
        setInitialState();

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

        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                fieldSetEnabled(false);
                if (combo.getSelectedItem().toString().length() == 0) {
                    showEmptyFieldError(extensionLabel);

                } else if (fromDirField.getText().length() == 0) {
                    showEmptyFieldError(fromLabel);

                } else if (toDirField.getText().length() == 0) {
                    showEmptyFieldError(toLabel);

                } else {
                    thread = new Thread() {
                        public void run() {
                            copyStart();
                        }
                    };
                    thread.start();
                }
            }
        });

        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                thread.interrupt();
                JOptionPane.showMessageDialog(progressBar,
                        countFiles + " files copied successfully!",
                        "INTERRUPTED", JOptionPane.WARNING_MESSAGE);
                setInitialState();
            }
        });

        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(progressBar,
                        "<html> Project on GitHub " +
                                "<a href=\"URL\">https://github.com/STRocketry/CopyDifFileST</a>",
                        "ABOUT", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void getFileChooser(JTextField field) {
        JFileChooser fileChooser = new JFileChooser(field.getText());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = fileChooser.showDialog(null, "Open directory");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            field.setText(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
        }
    }

    private void copyStart() {
        setButtonState(false);

        FilesFinder filesFinder = new FilesFinder(Paths.get(fromDirField.getText()),
                "glob:*." + combo.getSelectedItem());

        if(searchFiles(filesFinder)){
            startCopyProcess(filesFinder);
        }

        setInitialState();
    }

    private boolean searchFiles(FilesFinder filesFinder) {
        try {
            copyFileLabel.setText("Files search...");
            filesFinder.startSearch();

            progressBar.setMaximum((int) filesFinder.getAllFilesLength());
            setProcessCount(filesFinder.getMapFilesCopySize());

            if (filesFinder.getMapFilesCopySize() == 0) {
                JOptionPane.showMessageDialog(progressBar,
                        "Files with this extension does not exist!",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (NoSuchFileException e) { //если не правильно ввели папку поиска
            System.out.println("NoSuchFileException! " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(progressBar,
                    "<html>Directory <i>FROM</i> does not exist!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void startCopyProcess(FilesFinder filesFinder){
        for (Map.Entry<Path, Long> p : filesFinder.getMapFilesCopy().entrySet())//копируем каждый файл
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            copyFileLabel.setText("Copy: " + p.getKey().toString());

            try {
                //Копируем, к имени копируемого файла добавляем название последнего каталога!
                FilesCopier.copyFiles(p.getKey(),
                        Paths.get(toDirField.getText(), FilesCopier.createNewName(p.getKey())));
            } catch (NoSuchFileException e) { //если не правильно ввели папку назначения
                System.out.println("NoSuchFileException!" + e);
                e.printStackTrace();
                JOptionPane.showMessageDialog(copy,
                        "<html>Directory <i>TO</i> does not exist!",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                setInitialState();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Can't copy " + p.getKey().toString());
                JOptionPane.showMessageDialog(copy,
                        "Can't copy " + p.getKey().toString(),
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                countFiles--; //уменьшаем счетчик скопиованных файлов, так как не можем скопировать этот файл
            }

            countFiles++;
            setProcessCount(filesFinder.getMapFilesCopySize());
            countSize = countSize + p.getValue();
            progressBar.setValue((int) countSize);
        }

        JOptionPane.showMessageDialog(progressBar,
                countFiles + " files copied successfully!",
                "READY", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setInitialState() {
        countFiles = 0;
        countSize = 0;
        progressBar.setValue(0);
        copyFileLabel.setText("Ready");
        setProcessCount(0);
        fieldSetEnabled(true);
        setButtonState(true);
    }

    private void setProcessCount (Integer total){
        processLabel.setText("Files (total/countFiles): " + total + "/" + countFiles);
    }

    private void setButtonState(boolean bool){
        copy.setEnabled(bool);
        cancel.setEnabled(!bool);
    }

    private void fieldSetEnabled(boolean bool){
        combo.setEnabled(bool);
        fromDirField.setEnabled(bool);
        toDirField.setEnabled(bool);
    }

    private void showEmptyFieldError(JLabel label) {
        JOptionPane.showMessageDialog(progressBar,
                "<html>Field <i>"+label.getText()+"</i> is empty!",
                "ERROR", JOptionPane.ERROR_MESSAGE);
    }

}

