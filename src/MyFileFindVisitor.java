/**
 * Created by Admin on 17.05.15.
 */
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

class MyFileFindVisitor extends SimpleFileVisitor<Path> {
    private PathMatcher matcher;
    private ArrayList<Path> arrayFilesCopy;

    public MyFileFindVisitor(String pattern) {
        arrayFilesCopy = new ArrayList<Path>();
        try {
            matcher = FileSystems.getDefault().getPathMatcher(pattern);
        } catch (IllegalArgumentException iae) {
            System.err.println("Invalid pattern; did you forget to prefix \"glob:\" or \"regex:\"?");
            System.exit(1);
        }
    }
    private void find(Path path) {
        Path name = path.getFileName();
        if (name != null && matcher.matches(name)) {
            arrayFilesCopy.add(path.toAbsolutePath());
        }
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
        find(path);
        return FileVisitResult.CONTINUE;
    }


    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes fileAttributes) {
        find(path);
        return FileVisitResult.CONTINUE;
    }

    public ArrayList<Path> getArray() {
        return arrayFilesCopy;
    }
}

