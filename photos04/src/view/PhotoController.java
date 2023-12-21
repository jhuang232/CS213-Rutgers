package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import photos.Tag;

/**
 * This is the JavaFX controller class for the photo page
 * 
 * @author Jeffrey Tang
 */
public class PhotoController {
    
    /**
     * Reference to photosController object
     */
    protected PhotosController photosController;

    /**
     * FXML TextField for TagType and TagValue 
     */
    @FXML
    protected TextField tagType, tagValue;

    /**
     * FXML ListView that displays all tags of a photo
     */
    @FXML
    protected ListView<Tag> tags;

    /**
     * FXML ChoiceBox to choose from all tags of the user to apply to a photo
     */
    @FXML
    protected ChoiceBox<Tag> tag;

    /**
     * FXML ChoiceBox to choose if a tag should be singular. Singular tags can only have that tagType applied once.
     */
    @FXML
    protected ChoiceBox<Boolean> tagSingularity;

    /**
     * FXML Text to display for the date photo was uploaded to device and caption (if possible) of the photo
     */
    @FXML
    protected Text date,caption;

    /**
     * FXML ImageView that shows currently opened photo
     */
    @FXML 
    protected ImageView displayedImage;

    /**
     * FXML Buttons.
     * <p>Previous - part of slideshow design choice. Can go to preceding photo in Album if possible.
     * <p>Next - part of slideshow design choice, Can got to succeding photo in Album if possible.
     * <p>Add Tag - adds entered tag to current photo.
     * <p>Delete Tag - deletes a tag from current photo.
     * <p>Apply Tag - applies an already existing tag from all of the user's tags.
     */
    @FXML
    protected Button previous, next, addTag, deleteTag, editCaption, applyTag;

    /**
     * Handles a button press on the screen
     * @param event - event on photo screen
     */
    @FXML
    private void buttonPressed(ActionEvent event){
        //pass event over to controller
        photosController.processEvent(event);
    }

    /**
     * Handles when user enters the tags ListView.
     * We enable the delete tag button if a tag in the ListView is selected
     * @param event
     */
    @FXML
    private void enterListView(MouseEvent event){
        deleteTag.setDisable(tags.getSelectionModel().getSelectedIndex()==-1);
    }

    /**
     * Sets the class photosController to the parameter
     * @param pc - PhotosController object
     */
    public void setPhotosController(PhotosController pc){
        this.photosController = pc;
    }
}
