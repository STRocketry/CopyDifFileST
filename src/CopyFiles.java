import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Admin on 16.04.15.
 */
public class CopyFiles
{
    CopyFiles(String pathSrc, String pathDest) throws IOException
    {
        Path pathSource = Paths.get(pathSrc);
        Path pathDestination = Paths.get(pathDest);

        System.out.println(pathSource); //для отладки выводим в консоль
        Files.copy(pathSource, pathDestination, StandardCopyOption.REPLACE_EXISTING);//при существовании файла - перезаписывается

       /* try {
            Files.copy(pathSource, pathDestination, StandardCopyOption.REPLACE_EXISTING);//при существовании файла - перезаписывается
        }
        catch (NoSuchFileException e) { //если не правильно ввели папку назначения
            System.out.println("NoSuchFileException! " + e);
            JOptionPane.showMessageDialog(null, "Directory TO does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't copy " + pathSource);
        }*/

        System.out.println(pathDest);
    }

}
