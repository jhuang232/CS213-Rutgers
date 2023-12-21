package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * This class is the JavaFX controller class for the admin page
 * 
 * @author Jeffrey Tang
 *
 */
public class AdminController{
    
    /**
     * Reference to photosController object
     */
    protected PhotosController photosController;

    /**
     * FXML ListView that displays all the users on device
     */
    @FXML
    protected ListView<String> userListView; 

    /**
     * FXML TextField where user can enter a username
     */
    @FXML
    protected TextField newUser;

    /**
     * FXML Buttons - button to add user, button to delete user
     */
    @FXML
    protected Button addUser, deleteUser;

    /**
     * FXML MenuItem - logout MenuItem for admin
     */
    @FXML
    protected MenuItem logoutMenuItem; 

    /**
     * Handles a button press on the screen
     * @param event - event on admin screen
     */
    @FXML
    private void buttonPressed(ActionEvent event){
        //pass event over to controller
        photosController.processEvent(event);
    }

    /**
     * Handles when admin hits logout
     * @param event - event on admin screen
     */
    @FXML
    private void logout(ActionEvent event){
        photosController.processEvent(event);
    }

    /**
     * Handles when userListView ListView element is selected
     * @param event - event on admin screen
     */
    @FXML
    private void enterListView(MouseEvent event){
        deleteUser.setDisable(userListView.getSelectionModel().getSelectedIndex() ==-1);
    }

    /**
     * Sets the class photosController to the parameter
     * @param pc - PhotosController object
     */
    public void setPhotosController(PhotosController pc){
        this.photosController = pc;
    }
}
