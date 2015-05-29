import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Admin on 16.04.15.
 */
public class CopyFiles
{
    CopyFiles(String pathSrc, String pathDest)
    {
        Path pathSource = Paths.get(pathSrc);
        Path pathDestination = Paths.get(pathDest);

        System.out.println(pathSource); //для отладки выводим в консоль

        try {
            Files.copy(pathSource, pathDestination, StandardCopyOption.REPLACE_EXISTING);//при существовании файла - перезаписывается
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't copy " + pathSource);
        }

        System.out.println(pathDest);
    }

}
