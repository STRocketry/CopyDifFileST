package helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

public class FilesFinder extends SimpleFileVisitor<Path> {
    private PathMatcher matcher;
    private HashMap<Path,Long> mapFilesCopy;
    private Path startPath;
    private long allFilesLength = 0;

    public FilesFinder(Path startPath, String pattern) {
        this.startPath = startPath;
        this.mapFilesCopy = new HashMap<Path,Long>();
        matcher = FileSystems.getDefault().getPathMatcher(pattern);
    }

    public void startSearch() throws IOException {
        Files.walkFileTree(this.startPath, this);
        System.out.println("Files search completed!");
    }

    public HashMap<Path,Long> getMapFilesCopy() {
        return mapFilesCopy;
    }

    public int getMapFilesCopySize() {
        return mapFilesCopy.size();
    }

    public long getAllFilesLength() {
        return allFilesLength;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
        checkIsValid(path);
        return Thread.interrupted() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
    }

    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes fileAttributes) {
        checkIsValid(path);
        return FileVisitResult.CONTINUE;
    }

    private void checkIsValid(Path path) {
        if (isValidPath(path)) {
            File file = new File(String.valueOf(path));
            mapFilesCopy.put(path, file.length()/1024);
            allFilesLength = allFilesLength + file.length()/1024;
        }
    }

    private boolean isValidPath(Path path){
        Path name = path.getFileName();
        return name != null && matcher.matches(name);
    }
}

