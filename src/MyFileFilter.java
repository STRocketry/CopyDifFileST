import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Admin on 22.04.15.
 */
public class MyFileFilter implements FilenameFilter
{
    private final String extension;

    public MyFileFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File directory, String fileName)
    {
        if (fileName.endsWith("."+ extension))
        {
            return true;
        }
        return false;
    }
}

