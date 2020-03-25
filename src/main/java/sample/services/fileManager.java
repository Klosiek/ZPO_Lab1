package sample.services;

import sample.helpers.Helper;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class fileManager {



    public void copySelectedFileToDirection(String source, String destination) throws IOException {
        Path file1 = Paths.get(source);
        Path file2 = Paths.get(destination + Paths.get(source).getFileName().toString());


        Files.walk(file1)
                .forEach(src -> Helper.copy(src, file2.resolve(file1.relativize(src))));


    }

    public void deleteSelectedFile(String pathToDelete) throws IOException {
        if(Files.isDirectory(Paths.get(pathToDelete))){


            Files.walkFileTree(Paths.get(pathToDelete), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try{
                        Files.delete(file);
                    }catch (AccessDeniedException e){
                        System.out.println("Program nie ma uprawnień aby usunąć plik" + "\n"+ e.toString());
                    }

                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
                {
                    // try to delete the file anyway, even if its attributes
                    // could not be read, since delete-only access is
                    // theoretically possible
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    try{
                        Files.delete(dir);
                    }catch (DirectoryNotEmptyException e) {
                        System.out.println("Folder nie jest pusty, nie mozna oproznic jego zawartości" + "\n"+  e.toString());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else {
            Files.deleteIfExists(Paths.get(pathToDelete));

            try {
                Files.deleteIfExists(Paths.get(pathToDelete));
            } catch (NoSuchFileException e) {
                System.out.println("No such file/directory exists");
            } catch (DirectoryNotEmptyException e) {
                System.out.println("Directory is not empty.");
            } catch (IOException e) {
                System.out.println("Invalid permissions.");
            }
        }
        System.out.println("Deletion successful.");
    }



    public void moveSelectedFileToDirection(String source, String destination) throws IOException {


        copySelectedFileToDirection(source,destination);

        deleteSelectedFile(source);

    }
}