package sample.services;


import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sample.helpers.Helper;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class ChildControllerService {

    private String currPath;

    private long currentTime;
    private long lastTime;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public ChildControllerService(){
        lastTime = 0;
    }

    public String getCurrPath() {
        return currPath;
    }

    public void setCurrPath(String currPath) {
        this.currPath = currPath;
    }

    public void initializeDrivers(ComboBox<String> comboBox ){

        Iterable<Path> driv = FileSystems.getDefault().getRootDirectories();

        for(Path d:driv) comboBox.getItems().add(d.toString());
    }

    public void initializingExplorerList(ListView listView, String parentPath, TextField diskSize,TextField freeSpace,TextField usedSpace) throws IOException {
        Path startDirectory = Paths.get(parentPath);


        List<Path> directories = Files.walk(startDirectory,1).filter(Files::isDirectory)
                .collect(Collectors.toList());
        List<Path> files = Files.walk(startDirectory,1).filter(path -> !Files.isDirectory(path))
                .collect(Collectors.toList());

        for (Path path: directories) {
            if(!path.toFile().isHidden()  )
                listView.getItems().add(path.toFile().getName() );
        }

        for (Path path: files) {
            if(!path.toFile().isHidden())
                listView.getItems().add( path.toFile().getName());
        }

        diskSize.setText(Helper.readableFileSize(Files.getFileStore(startDirectory).getTotalSpace()));
        freeSpace.setText(Helper.readableFileSize(Files.getFileStore(startDirectory).getUsableSpace()));
        usedSpace.setText(Helper.readableFileSize( Files.getFileStore(startDirectory).getTotalSpace() - Files.getFileStore(startDirectory).getUsableSpace()));

    }


    public boolean isElementOfListDoubleClicked(){
        long diff;
        boolean isdblClicked = false;
        currentTime= System.currentTimeMillis();
        if(lastTime!=0 && currentTime!=0){
            diff=currentTime-lastTime;
            isdblClicked = diff <= 215;
        }
        lastTime=currentTime;
        return isdblClicked;
    }}