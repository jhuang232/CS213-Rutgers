package view;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import photos.Album;
import photos.Photo;

/**
 * Java FXML class for choosing an album destination for a photo when moving/copying
 * 
 * @author Jeffrey Tang
 */
public class AlbumDestinationController {

    /**
     * Reference to photosController object
     */
    protected PhotosController photosController;
    
    /**
     * FXML ChoiceBox to choose which Album to move selected photo to
     */
    @FXML
    protected ChoiceBox<String> albumList;

    /**
     * FXML buttons. 
     * <p>Confirm button - commiting to which album to move/copy selected photo to. 
     * Cancel button - cancel the photo moving/copying process
     */
    @FXML
    protected Button confirm,cancel;

    /**
     * Enumerated type TransferMode to indiciate whether photo transfer is to COPY or to MOVE selected photo to desired album
     */
    private static TransferMode tm;

    /**
     * Method that starts Album Destination window popup 
     * @param newtm - TransferMode: indicates if selected photo should be copied or moved to new Album
     */
    void initialize(TransferMode newtm){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/albumDestination.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);//make it dialog box
            dialog.initOwner(photosController.currentStage.getScene().getWindow());
            AnchorPane root = loader.load();

            photosController.albumDestinationController = loader.getController();
            photosController.albumDestinationController.setPhotosController(photosController);

            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.setResizable(false);

            //need to set up choices in Choice Box
            ObservableList<String> userAlbumsNames = photosController.photoUserState.albumListData.stream()
                                                        .map(Album::getName)
                                                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            photosController.albumDestinationController.albumList.setItems(userAlbumsNames.filtered(a ->!a.equals(photosController.photoAlbumState.currentAlbum.getName())));
            photosController.albumDestinationController.albumList.getSelectionModel().selectFirst();
            tm = newtm;

            //show and wait so user must interact if open
            dialog.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Handles the logic for when the confirm button is clicked. Checks if the album exist, 
     * if so, check if the photo already, gives a popup, else add the photo to the seleced album.
     * Moving will delete the photo from current album. Copying will do nothing to photo in current album.
     * @param event - Action event on the screen
     */
    @FXML
    private void confirm(ActionEvent event){
        Album newDest = null; 
        try{
            newDest = photosController.photoUserState.albumsList.stream()
                        .filter(a -> a.getName().equals(albumList.getSelectionModel().getSelectedItem()))
                        .findFirst().get(); 
        } catch (NoSuchElementException e){
            //case where there is only 1 album, user attempts to copy/move
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Albums");
            alert.setContentText("There are no other albums to move or copy this photo. Please create an album to use copy/move");
            alert.initOwner(confirm.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        Photo photoToAdd = (Photo)photosController.photoAlbumState.clickedImageView.getUserData();
        //if photo exist moveAlbum show a popup 
        if(newDest.getPhotos().contains(photoToAdd)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Photo Already Exists");
            alert.setContentText("The photo already exists in " + newDest + ".");
            alert.initOwner(confirm.getScene().getWindow());
            alert.showAndWait();
            return;
        }
        switch(tm){
            case MOVE:
                photosController.photoAlbumState.currentAlbum.removePhoto(photoToAdd);
                photosController.albumController.photosDisplay.getChildren().remove(photosController.photoAlbumState.clickedImageView);
                break;
            case COPY:
                break;
        }

        //if it gets here then it is not in the newDestination album
        newDest.addPhoto(photoToAdd);

        //close screen
        Stage stage = (Stage)confirm.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles logic when the cancel button is clicked. Closes album destination popup.
     * @param event - Action event on the screen
     */
    @FXML
    private void cancel(ActionEvent event){
        //close screen
        Stage stage = (Stage)cancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the class photosController to the parameter
     * @param pc - PhotosController object
     */
    public void setPhotosController(PhotosController pc){
        this.photosController = pc;
    }

    /**
     * Enumerated type for the transfer mode of the album destination window.
     * This enum is determined by which button was pressed for photo transfer.
     * There are two available options: {@link #COPY} and {@link #MOVE}.
     *
     * <p>Usage:
     * <ul>
     *     <li>{@link #COPY}: Copy mode - Indicates that a photo should be copied to the destination.</li>
     *     <li>{@link #MOVE}: Move mode - Indicates that a photo should be moved to the destination.</li>
     * </ul>
     */
    public enum TransferMode {
        /**
         * Copy mode: Indicates that a photo should be copied to the destination.
         */
        COPY,
        /**
         * Move mode: Indicates that a photo should be moved to the destination.
         */
        MOVE;
}


}
