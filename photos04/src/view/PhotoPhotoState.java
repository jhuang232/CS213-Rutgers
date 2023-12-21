package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import photos.*;

/**
 * This class represents the Photo State. In this state, there is a image in the center,
 * a dropdown to apply an existing tag and add a new tag on the left, delete applied tags,
 * use slideshow options to manually traverse the currently opened album, and show the 
 * date of the photo and the caption is made. It is a subclass of PhotoState, 
 * implementing the State design pattern for Photos.
 * 
 * <p>Modified design of CalculatorController develeoped by Sesh Venogupal
 * 
 * @author Jason Huang
 */
public class PhotoPhotoState extends PhotoState{
    
    /** 
	 * List of Tags for current photo
	 */
    private ArrayList<Tag> tagList;
	
	/**
	 * List Photos for next and prev buttons
	 */
	private ArrayList<Photo> photoList;
	
    /**
     * Stores the current displayed photo
     */
	protected Photo currPhoto;

    /**
	 * Singleton instance 
	 */
	private static PhotoPhotoState instance = null;
	
	/**
	 * Prevents instantiation, to implement singleton pattern.
	 */
	private PhotoPhotoState() {
		
	}

    /**
     * Enter the PhotoState of the Application
     */
    void enter() {
		try{
            photosController.currentState = getInstance();
            Stage primaryStage = new Stage();
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/photo.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            photosController.photoController = loader.getController();
            photosController.photoController.setPhotosController(photosController);
			
            this.photoList = photosController.photoAlbumState.currentAlbum.getPhotos();
            this.tagList = currPhoto.getTags();
            photosController.photoController.tagSingularity.setItems(FXCollections.observableArrayList(true, false));
            photosController.photoController.tagSingularity.setValue(false);
            photosController.photoController.tagType.textProperty().addListener((obs, oldValue, newValue) -> tagSingularityFieldModifier());
            
            updateFields();

            primaryStage.setTitle(currPhoto.getFile().toString());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest( e -> {
                photosController.currentState = PhotoAlbumState.getInstance();
                ((PhotoAlbumState)photosController.currentState).updatePhotos();
            });
            primaryStage.showAndWait();

		} catch(IOException e){
			e.printStackTrace();
		}
    }

    /**
     * Processes an event that is fired when in albums
     * @return New state
     */
    PhotoState processEvent() {
        Button b = (Button)lastEvent.getSource();
        switch(b.getId()){
            case "addTag":
                processAddTag();
                break;
            case "next":
                processNext();
                break;
            case "deleteTag":
                processDeleteTag();
                break;
            case "previous":
                processPrev();
                break;
            case "editCaption":
                processEditCaption();
                break;
            case "applyTag":
                processApplyTag();
                break;
        }
        return instance;
    }

    /**
     * Updates photoController's tagSingularity feild based on tagType textfield
     */
    private void tagSingularityFieldModifier(){
        String currTagTypeText = (photosController.photoController.tagType.getText() != null)? photosController.photoController.tagType.getText().trim() : null;
        Boolean targetSingularity = photosController.photoUserState.tagListAll.stream().filter(tag -> tag.getTagType().equals(currTagTypeText)).map(Tag::getSingularity).findFirst().orElse(null);
        if(targetSingularity == null){
            photosController.photoController.tagSingularity.setDisable(false);
        }
        else{
            photosController.photoController.tagSingularity.setValue(targetSingularity);
            photosController.photoController.tagSingularity.setDisable(true);
        }
    }

    /**
     * Algorithm to apply a tag to a open photo
     */
    private void processApplyTag(){
        Tag tagApply = photosController.photoController.tag.getSelectionModel().getSelectedItem();
        if(tagApply==null){
            //user attempts to add an empty tag.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selected Tag");
            alert.setContentText("You are attempting to apply tags when you have created no tags. Create tags to apply them to photos.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        } else if(tagApply.getSingularity() && currPhoto.getTags().stream().anyMatch(t -> t.getTagType().equals(tagApply.getTagType()))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate single tag of same tag type");
            alert.setContentText("This is a single tag and this photo already has a tag with the same tag type, cannot apply the tag.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else if(currPhoto.getTags().stream().anyMatch(t -> t.equals(tagApply)) || tagApply == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attempt to apply existing tag");
            alert.setContentText("Please choose a uniqe tag type or tag value that this photo dosen't currently have\n\nTag cannot be null.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        }else{
            currPhoto.addTag(tagApply);
        }
        updateFields();
    }

    /**
     * Algorithm to add/edit a caption for the opened photo
     */
    private void processEditCaption(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input New Caption for Photo");
        dialog.setHeaderText("Enter the new caption");
        dialog.setContentText("Caption:");
        String newCaption = dialog.showAndWait().get();
        if(!newCaption.equals("")){
            currPhoto.setCaption(newCaption);
        }
        updateFields();
    }

    /**
     * Add tag process adds tag to currPhoto if applicable
     */
    private void processAddTag(){
        String tagTypeField = "";
        String tagValueField = "";
        try{
            tagTypeField = photosController.photoController.tagType.getText().trim();
            tagValueField = photosController.photoController.tagValue.getText().trim();
        }catch(NullPointerException e){

        }

        Tag createdTag = new Tag(tagTypeField, tagValueField, photosController.photoController.tagSingularity.getValue());
        if(tagTypeField == null || tagTypeField.equals("") || tagValueField == null || tagValueField.equals("")){
            //empty tag
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Improper Fields");
            alert.setContentText("Please enter values for both tag type and tag value.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else if(currPhoto.getTags().stream().anyMatch(t -> t.getTagType().equals(createdTag.getTagType()) && t.getSingularity())){
            //attempting to make a new tagValue for a single tag
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate single tag of same tag type");
            alert.setContentText("This is a single tag and this photo already has a tag with the same tag type, cannot create and add the tag.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else if(tagList.stream().anyMatch(t -> t.equals(createdTag))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tag Already Exists");
            alert.setContentText("Please enter a uniqe tag type or tag value that this photo dosen't currently have.");
            alert.initOwner((Stage)photosController.searchController.cancel.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        else{ 
            currPhoto.addTag(createdTag);
            photosController.photoController.tagType.setText(null);
            photosController.photoController.tagValue.setText(null);
        }

        if(!photosController.photoUserState.tagListAll.stream().anyMatch(t -> t.equals(createdTag))){
            photosController.photoUserState.tagListAll.add(createdTag);
        }
        updateFields();
    }

    /**
     * Next process displays next photo in line and updates all fields
     */
    private void processNext(){
        currPhoto = photoList.get(currPhotoIndex() + 1);
        updateFields();
    }

    /**
     * Next process displays next photo in line and updates all fields
     */
    private void processPrev(){
        currPhoto = photoList.get(currPhotoIndex() - 1);
        updateFields();
    }

    /**
     * Delete tag process deletes tag from currPhoto and updates all fields
     */
    private void processDeleteTag(){
        currPhoto.removeTag(photosController.photoController.tags.getSelectionModel().getSelectedItem());
        photosController.photoController.deleteTag.setDisable(true);
        updateFields();
    }

    /**
     * Updates all fields to PhotoPhotoState's fxml using the currPhoto value
     */
    private void updateFields(){
        photosController.photoController.tag.getItems().clear();
        photosController.photoController.tag.getItems().setAll(photosController.photoUserState.tagListAll);
        photosController.photoController.caption.setText(currPhoto.getCaption());
        photosController.photoController.date.setText(currPhoto.getDate().toString());
        tagList = currPhoto.getTags();
        ObservableList<Tag> photoTagData = tagList.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
        photosController.photoController.tags.setItems(photoTagData);
        
        Image image = new Image(currPhoto.getFile().toURI().toString());
        ImageView imageView = new ImageView(image);
        // Adjust the size of the image view if needed
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);
        photosController.photoController.displayedImage.setImage(image);       
        
        photosController.photoController.previous.setDisable(false);
        photosController.photoController.next.setDisable(false);
        if(currPhotoIndex() == 0) photosController.photoController.previous.setDisable(true);
        if(currPhotoIndex() == (photoList.size() - 1)) photosController.photoController.next.setDisable(true);

    }

    /**
     * Getter for photo index in the album
     * @return int - the index of the current photo in the album
     */
    public int currPhotoIndex(){
        return photoList.indexOf(currPhoto);
    }

    /**
	 * Returns singleton instance.
	 * @return Singleton instance
	 */
	public static PhotoPhotoState getInstance() {
		if (instance == null) {
			instance = new PhotoPhotoState();
		}
		return instance;
	}
}
