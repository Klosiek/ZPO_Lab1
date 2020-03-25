package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import sample.services.ChildControllerService;
import sample.services.fileManager;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;


public class ChildAnchorPaneController {

    private ChildControllerService childControllerService = new ChildControllerService();

    private fileManager filesManagmentService = new fileManager();

    private MainAnchorPaneController main;

    private ChildAnchorPaneController otherChild;


    @FXML
    private TextField diskSizeTextArea;

    @FXML
    private TextField freeSpaceTextArea;

    @FXML
    private TextField usedSpaceTextArea;

    @FXML
    private ListView<String> ExplorerList;

    @FXML
    private TextField PathTextField;

    @FXML
    private ComboBox<String> DriveComboBox;

    @FXML
    private Button deleteSelectedFileButton;

    @FXML
    private Button copySelectedFileButton;

    @FXML
    private Button goingUpButton;

    @FXML
    private Button moveSelectedFileButton;

    private ChildControllerService getChildControllerService() {
        return childControllerService;
    }

    public void incjectMain(MainAnchorPaneController main){
        this.main = main;
    }

    private void initializnigPathTextField(){
        this.childControllerService.setCurrPath(this.DriveComboBox.getSelectionModel().getSelectedItem());
        this.PathTextField.setText(this.childControllerService.getCurrPath());
    }

    private void isDoubleClicked() throws IOException {
        String newPath = childControllerService.getCurrPath() + this.ExplorerList.getSelectionModel().getSelectedItem()+"\\";
        if(!this.ExplorerList.getSelectionModel().isEmpty() && Files.isDirectory(Paths.get(newPath)) && Files.exists(Paths.get(newPath))){


            childControllerService.setCurrPath(newPath);
            this.PathTextField.setText(newPath);
            this.ExplorerList.getItems().remove(0,this.ExplorerList.getItems().size());
            childControllerService.initializingExplorerList(this.ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }else if(!this.ExplorerList.getSelectionModel().isEmpty() && !Files.isDirectory(Paths.get(newPath)) && Files.exists(Paths.get(newPath)))
        {
            Desktop.getDesktop().open(Paths.get(newPath).toFile());
        }
    }

    @FXML
    public void choosingDriveFromComboBox(ActionEvent event) throws IOException {
        ComboBox comboBox = (ComboBox)event.getSource();
        if(!comboBox.getSelectionModel().isEmpty()){
            this.childControllerService.setCurrPath(comboBox.getSelectionModel().getSelectedItem().toString());
            this.PathTextField.setText(childControllerService.getCurrPath());
            ExplorerList.getItems().remove(0,ExplorerList.getItems().size());
            childControllerService.initializingExplorerList(ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }
    }

    @FXML
    public void choosingDirectoryFromList() throws IOException {
        if(childControllerService.isElementOfListDoubleClicked())
            isDoubleClicked();
    }


    @FXML
    void goingUp() throws IOException {
        Path file = Paths.get(childControllerService.getCurrPath());
        if(file.getParent() != null){
            if(file.getParent() == null)
                childControllerService.setCurrPath(file.getParent().toString());
            else
                childControllerService.setCurrPath(file.getParent()+"\\");
            this.PathTextField.setText(childControllerService.getCurrPath());
            this.ExplorerList.getItems().remove(0,this.ExplorerList.getItems().size());
            childControllerService.initializingExplorerList(this.ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }
    }

    @FXML
    void ifEnterKeyIsTyped(KeyEvent event) throws IOException {
        char znak = event.getCharacter().charAt(0);
        Path file = Paths.get(this.PathTextField.getText());
        if(znak == 13)
            if(!this.PathTextField.getText().isEmpty() && Files.exists(file) && Files.isDirectory(file)){
                childControllerService.setCurrPath(PathTextField.getText());
                this.ExplorerList.getItems().remove(0,this.ExplorerList.getItems().size());
                childControllerService.initializingExplorerList(this.ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
            }
    }

    @FXML
    public void reloadDiskList(){
        if(!DriveComboBox.getItems().isEmpty()){
            this.DriveComboBox.getSelectionModel().clearSelection();
            this.DriveComboBox.getItems().clear();
            childControllerService.initializeDrivers(this.DriveComboBox);
        }
    }

    @FXML
    void copySelectedFile() throws IOException {
        //Przypisanie do składowej otherChild referencji do bliźniaczej kontrolki
        if(otherChild == null)
            otherChild = main.getOtherChildController(this);

        //Utworzenie zmiennych przechowujących ścieżki
        String pathToCopy;
        String destinationPath = otherChild.getChildControllerService().getCurrPath();

        //Jeżeli został wybrany z listy jakiś element
        if(!this.ExplorerList.getSelectionModel().isEmpty()){
            pathToCopy = childControllerService.getCurrPath() + "\\" + this.ExplorerList.getSelectionModel().getSelectedItem();

            filesManagmentService.copySelectedFileToDirection(pathToCopy,destinationPath);
            //Odświeżenie listy w bliźniaczej kontrolce
            otherChild.ExplorerList.getItems().clear();
            otherChild.childControllerService.initializingExplorerList(otherChild.ExplorerList,otherChild.childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }
    }

    @FXML
    public void deleteSelectedFile() throws IOException {
        //Przypisanie do składowej otherChild referencji do bliźniaczej kontrolki
        if(otherChild == null)
            otherChild = main.getOtherChildController(this);
        String pathToDelete;
        //Jeżeli został wybrany z listy jakiś element
        if(!this.ExplorerList.getSelectionModel().isEmpty()){
            pathToDelete = childControllerService.getCurrPath()+ "\\" + this.ExplorerList.getSelectionModel().getSelectedItem();
            filesManagmentService.deleteSelectedFile(pathToDelete);
            //Jeżeli obie kontrolki wskazują na tą samą ścieżkę to lista bliźniaczej kontrolki jest odświeżana
            if(otherChild.childControllerService.getCurrPath().equals(this.childControllerService.getCurrPath())){
                otherChild.ExplorerList.getItems().clear();
                otherChild.childControllerService.initializingExplorerList(otherChild.ExplorerList,otherChild.childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
            }
            this.ExplorerList.getItems().clear();
            childControllerService.initializingExplorerList(this.ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }
    }

    @FXML
    public void moveSelectedFile() throws IOException {
        //Przypisanie do składowej otherChild referencji do bliźniaczej kontrolki
        if(otherChild == null)
            otherChild = main.getOtherChildController(this);
        String pathToMove;
        String destinationPath = otherChild.getChildControllerService().getCurrPath();

        //Jeżeli został wybrany z listy jakiś element
        if(!this.ExplorerList.getSelectionModel().isEmpty()){
            pathToMove = childControllerService.getCurrPath()+ "\\" + this.ExplorerList.getSelectionModel().getSelectedItem();
            filesManagmentService.moveSelectedFileToDirection(pathToMove,destinationPath);
            //Odświeżenie list w obu kontrolkach
            this.ExplorerList.getItems().clear();
            this.childControllerService.initializingExplorerList(this.ExplorerList,this.childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
            otherChild.ExplorerList.getItems().clear();
            otherChild.childControllerService.initializingExplorerList(otherChild.ExplorerList,otherChild.childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );
        }

    }

    @FXML
    public void initialize() throws IOException {
        childControllerService.initializeDrivers(this.DriveComboBox);
        this.DriveComboBox.getSelectionModel().select(0);
        initializnigPathTextField();
        childControllerService.initializingExplorerList(this.ExplorerList, childControllerService.getCurrPath(),diskSizeTextArea,freeSpaceTextArea,usedSpaceTextArea );


    }



}