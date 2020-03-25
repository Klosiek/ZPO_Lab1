package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MainAnchorPaneController extends AnchorPane{

    @FXML
    private ChildAnchorPaneController leftAnchorPaneController;

    @FXML
    private ChildAnchorPaneController rightAnchorPaneController;

    public ChildAnchorPaneController getOtherChildController(ChildAnchorPaneController child){
        if(child.hashCode() == rightAnchorPaneController.hashCode())
            return leftAnchorPaneController;
        else
            return rightAnchorPaneController;
    }

    @FXML
    private void initialize(){
        leftAnchorPaneController.incjectMain(this);
        rightAnchorPaneController.incjectMain(this);
    }
}
