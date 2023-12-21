package view;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import photos.Tag;


/**
 * This class is the JavaFX controller class for the tag management pop up
 * 
 * @author Jason Huang
 */
public class TagController {

    /**
     * Reference to photosController object
     */
    protected PhotosController photosController;

    /**
     * FXML Textfield for Tag type and Tag value as input
     */
    @FXML
    protected TextField tagValue, tagType;

    /**
     * FXML ChoiceBox to choose if a tag should be singular. Singular tags can only have its tagType applied to a photo once. 
     */
    @FXML
    protected ChoiceBox<Boolean> tagSingularity;

    /**
     * FXML ListView displays all the tags of a user
     */
    @FXML
    protected ListView<Tag> allTagListView;

    /**
     * FXML Buttons for adding a tag and delting a tag
     */
    @FXML
    protected Button addTag, deleteTag;
    
    /**
     * Sets the class photosController to the parameter
     * @param pc - PhotosController object
     */
    public void setPhotosController(PhotosController pc){
        this.photosController = pc;
    }
    
    /**
     * Method to start the search window
     */
    void initialize(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tag.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(photosController.currentStage.getScene().getWindow());
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
    
            photosController.tagController = loader.getController();
            photosController.tagController.setPhotosController(photosController);
            photosController.tagController.tagSingularity.setItems(FXCollections.observableArrayList(true, false));
            photosController.tagController.tagSingularity.setValue(false);
            photosController.tagController.tagType.textProperty().addListener((obs, oldValue, newValue) -> tagSingularityFieldModifier());
    
            updateAllTagListView();
            
            dialog.setScene(scene);
            dialog.setResizable(false);
            // Show and wait so the user must interact if open
            dialog.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or log it
        }
        
    }
    
    /**
     * Updates the allTagListView to show all Tags of user
     */
    private void updateAllTagListView() {
        ObservableList<Tag> tagsObservableList = FXCollections.observableArrayList(photosController.photoUserState.tagListAll);
        photosController.tagController.allTagListView.setItems(tagsObservableList);
    }

    /**
     * Updates photoController's tagSingularity feild based on tagType textfield
     */
    private void tagSingularityFieldModifier(){
        String currTagTypeText = (photosController.tagController.tagType.getText() != null)? photosController.tagController.tagType.getText().trim() : null;
        Boolean targetSingularity = photosController.photoUserState.tagListAll.stream().filter(tag -> tag.getTagType().equals(currTagTypeText)).map(Tag::getSingularity).findFirst().orElse(null);
        if(targetSingularity == null){
            photosController.tagController.tagSingularity.setDisable(false);
        }
        else{
            photosController.tagController.tagSingularity.setValue(targetSingularity);
            photosController.tagController.tagSingularity.setDisable(true);
        }
    }

    /**
     * Handles when user clicks delete tag
     */
    @FXML
    private void processDeleteTag(){
        photosController.photoUserState.tagListAll.remove(photosController.tagController.allTagListView.getSelectionModel().getSelectedItem());
        photosController.photoUserState.albumsList.forEach(a-> a.getPhotos().forEach(p -> p.getTags().remove(photosController.tagController.allTagListView.getSelectionModel().getSelectedItem())));
        updateAllTagListView();
        deleteTag.setDisable(true);
    }
    
    /**
     * Handles when userListView ListView element is selected
     * @param event - event on admin screen
     */
    @FXML
    private void enterListView(MouseEvent event){
        deleteTag.setDisable(allTagListView.getSelectionModel().getSelectedIndex() ==-1);
    }

    /**
     * Handles logic for adding a tag when user clicks Add Tag
     */
    @FXML
    private void processAddTag(){
        Tag createdTag = new Tag(photosController.tagController.tagType.getText().trim(), photosController.tagController.tagValue.getText().trim(), photosController.tagController.tagSingularity.getValue());
        if(photosController.tagController.tagType.getText() == null || photosController.tagController.tagType.getText().trim().equals("") || photosController.tagController.tagValue.getText() == null || photosController.tagController.tagValue.getText().trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Improper Fields");
            alert.setContentText("Please enter values for both tag type and tag value.");
            alert.initOwner((Stage)photosController.tagController.addTag.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else if(photosController.photoUserState.tagListAll.stream().anyMatch(t -> t.equals(createdTag))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tag Already Exists");
            alert.setContentText("Please enter a uniqe tag type or tag value that this user doesn't currently have.");
            alert.initOwner((Stage)photosController.tagController.addTag.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else{
            photosController.photoUserState.tagListAll.add(createdTag);
            photosController.tagController.tagType.clear();
            photosController.tagController.tagValue.clear();
            updateAllTagListView();
        }
    }
}
