package view;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.*;
import view.AlbumDestinationController.TransferMode;

/**
 * This class represents the Album State. In this state, thumbnails of photos in the album
 * are shown. Hovering over a photo will show its caption if it has one. The way to access
 * the photo is by doubling clicking. User is able to add, delete, move, copy photos, and 
 * close the album. It is a subclass of PhotoState, implementing the State design pattern for Photos.
 * 
 * <p>Modified design of CalculatorController develeoped by Sesh Venogupal
 * 
 * @author Jason Huang
 */

public class PhotoAlbumState extends PhotoState{
    
    /**
     * Reference to the currently opened album
     */
    protected Album currentAlbum;

    /**
	 * Singleton instance of Album state
	 */
	private static PhotoAlbumState instance = null;

    /**
     * Clicked ImageView item from photoDisplay - where the photos are shown
     */
    protected BorderPane clickedImageView;
	
	/**
	 * Prevents instantiation, to implement singleton pattern.
	 */
	private PhotoAlbumState() {
		
	}

    /**
     * Enters this state on openAlbum and should display all photos
     * and populates flowPane in fxml with id:photosDisplay with the images
     */
    void enter() {
        //set up fxml
        try{
            Stage primaryStage = photosController.currentStage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/album.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            photosController.albumController = loader.getController();
            photosController.albumController.setPhotosController(photosController);

            //show album cover
            photosController.albumController.albumTitle.setText("Current Album: \n" + currentAlbum.getName());
            photosController.albumController.makeAlbum.setVisible(false);

            if(!photosController.photoUserState.albumsList.stream().anyMatch(a -> a.getName().equals(currentAlbum.getName())) || currentAlbum.getName().equals("")){
                photosController.albumController.makeAlbum.setVisible(true);
            }

            primaryStage.setScene(scene);
            primaryStage.show();
            updatePhotos();
        }  catch (IOException e){
            e.printStackTrace();
        }        
    }

    /**
     * Processes an event that is fired when in album. Typically only clicking photo objects in this case ImageView
     * @return New state
     */
    PhotoState processEvent() {
        Button b = (Button)lastEvent.getSource();
        switch(b.getId()){
            case "addPhoto":
                addPhotos();
                break;
            case "closeAlbum":
                photosController.photoUserState.enter();
                return photosController.photoUserState;
            case "deletePhoto":
                deletePhotoProcess();
                break;
            case "movePhoto": case "copyPhoto":
                photosController.albumDestinationController.initialize((b.getId().equals("movePhoto")? TransferMode.MOVE:TransferMode.COPY));
                break;
            case "makeAlbum":
                photosController.photoUserState.addAlbumFromSearchResult(currentAlbum);
                break;
        }
        updatePhotos();
        return instance;
    }

    /**
     * This method handles the logic to delete a photo from the current album
     */
    private void deletePhotoProcess(){
        if(clickedImageView != null){
            Photo associatedPhoto = (Photo) clickedImageView.getUserData();
            currentAlbum.removePhoto(associatedPhoto);
            photosController.albumController.photosDisplay.getChildren().remove(clickedImageView);
            clickedImageView = null;
            updatePhotos();
            photosController.albumController.disablePhotoButtons();
        }
    }

    /**
     * This method opens a file chooser for the user to choose a photo to add this currently opened album
     */
    private void addPhotos(){
        //Set up to access files
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Photo");

        //Filter
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Images(*.BMP, *.GIF, *.JPEG, *.JPG, *.PNG)", "*.BMP", "*.GIF", "*.JPEG", "*.JPG", "*.PNG");
        fileChooser.getExtensionFilters().add(imageFilter);

        //show pop up to choose
        File selectedFile = fileChooser.showOpenDialog(photosController.currentStage);
        if(selectedFile!=null){
            //add it 
            Photo photoToAdd = new Photo(selectedFile);
            if(currentAlbum.getPhotos().stream().anyMatch(p -> p.getFile().equals(selectedFile))){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Photo Already Exists");
                alert.setContentText("The photo already exists in " + currentAlbum + ".");
                alert.initOwner(photosController.currentStage);
                alert.showAndWait();
                return;
            }
            currentAlbum.addPhoto(photoToAdd);
            //create a photo object and add to current album
        }

    }

    /**
     * This method updates the screen to display all photos
     */
    public void updatePhotos(){
        // Set up the layout for the TilePane
        photosController.albumController.photosDisplay.setHgap(10); // Set horizontal gap
        photosController.albumController.photosDisplay.setVgap(10); // Set vertical gap
        //display of photos
        photosController.albumController.photosDisplay.getChildren().clear();
        currentAlbum.getPhotos().forEach(p -> {
            Image image = new Image(p.getFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            BorderPane borderPane = new BorderPane(imageView);
            Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null));
            borderPane.setBorder(border);
            borderPane.setUserData(p);            
            // Adjust the size of the image view if needed
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            photosController.albumController.photosDisplay.getChildren().add(borderPane);

            //need to add event handlers to imageView
            Tooltip tooltip = new Tooltip("Caption: \n" + p.getCaption());
            Tooltip.install(borderPane, tooltip);

            borderPane.setOnMouseEntered(e -> {
                // Show the tooltip when the mouse enters the image view
                tooltip.show(imageView, e.getScreenX(), e.getScreenY() + 10);
            });
        
            borderPane.setOnMouseExited(e -> {
                // Hide the tooltip when the mouse exits the image view
                tooltip.hide();
            });

            borderPane.setOnMouseClicked(e->{
                if (e.getClickCount() == 2) {
                    // Double-click detected
                    photosController.photoPhotoState.currPhoto = (Photo)((BorderPane) e.getSource()).getUserData();
                    photosController.photoPhotoState.enter();
                } else {
                    // Single-click logic
                    if (clickedImageView != null) clickedImageView.setBorder(border);
                    
                    // Highlight the clicked BorderPane
                    BorderPane clickedPane = (BorderPane) e.getSource();
                    ((BorderPane) e.getSource()).setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null)));
                    
                    // Enable/disable buttons or perform other actions
                    photosController.albumController.deletePhoto.setDisable(false);
                    photosController.albumController.copyPhoto.setDisable(false);
                    photosController.albumController.movePhoto.setDisable(false);
                    
                    // Update clickedImageView reference
                    clickedImageView = clickedPane;
                }            
            });
        });
    }
    
    /**
	 * Returns singleton instance.
	 * 
	 * @return Singleton instance
	 */
	public static PhotoAlbumState getInstance() {
		if (instance == null) {
			instance = new PhotoAlbumState();
		}
		return instance;
	}
}
