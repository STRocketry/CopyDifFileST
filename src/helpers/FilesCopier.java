package helpers;

import com.sun.nio.file.ExtendedCopyOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class FilesCopier
{
    public static void copyFiles(Path formPath, Path toPath) throws IOException
    {
        Files.copy(formPath, toPath,StandardCopyOption.REPLACE_EXISTING,
                ExtendedCopyOption.INTERRUPTIBLE);//при существовании файла - перезаписывается
        //для отладки выводим в консоль
         System.out.println(String.format("File has been copied: %s, to %s", formPath, toPath));
    }

    public static String createNewName(Path path){
        return path.getName(path.getNameCount() - 2) + "__" + path.getFileName();
    }
}
