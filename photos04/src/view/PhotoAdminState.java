package view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * This class represents the Admin State. In this state, the display should show all users on device,
 * the ability to add a new user, and delete existing user. It is a subclass of PhotoState, 
 * implementing the State design pattern for Photos.
 * 
 * <p>Modified design of CalculatorController develeoped by Sesh Venogupal
 * 
 * @author Jeffrey Tang
 */
public class PhotoAdminState extends PhotoState{
    /**
	 * Singleton instance 
	 */
	private static PhotoAdminState instance = null;

    /**
	 * An ArrayList of strings that stores the added users
	 */
	private static ArrayList<String> userList = PhotoLoginState.parseUsers();
	
	/**
	 * An observable list that helps display the list of users
	 */
	private ObservableList<String> observableList;

	/**
	 * Prevents instantiation, to implement singleton pattern.
	 */
	private PhotoAdminState() {
		
	}

    /**
     * Called when state is entered.
     * <p>Display all existing users.
     * <p>Capable to add and delete users.
     */
    void enter() {
        try{
            Stage primaryStage = photosController.currentStage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/admin.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            photosController.adminController = loader.getController();
            photosController.adminController.setPhotosController(photosController);

            primaryStage.setScene(scene);
            primaryStage.show();

            //populates userList and also listView of usernames
            observableList = FXCollections.observableArrayList(userList);
            photosController.adminController.userListView.setItems(observableList);
            photosController.adminController.userListView.refresh();
            if(!userList.isEmpty()) {
                photosController.adminController.userListView.getSelectionModel().select(0);
            }
            //user shouldnt be able to delete unless selecting smth in listview
            photosController.adminController.deleteUser.setDisable(true);
        }  catch (IOException e){
            e.printStackTrace();
        }
        
    }

    /**
     * Processes an event that is fired when in the Admin State.
     * Admins can add users, delete users, and logout
     * 
     * @return New state
     */
    public PhotoState processEvent() {
        
        String event = determineEventSource();
        switch(event){
            case "button":
            //we hit a button so handle it
                Button b = (Button)lastEvent.getSource();
                //what user has typed in
                String adminInput = photosController.adminController.newUser.getText().trim().toLowerCase();
                //System.out.print("\n\n\n" + adminInput + " : " + b.getText() + "\n\n\n");
                switch(b.getId()){
                    case "addUser":
                        processAdd(adminInput);
                        photosController.adminController.newUser.setText("");//want to remove added name
                        break;
                    case "deleteUser":
                        processDelete();
                        break;
                }
                break;
            case "menuItem":
            //we hit a menuItem, only one is logout so logout logic here
                photosController.photoLoginState.enter();
                return photosController.photoLoginState;
        }
        
        //make compiler happy
        return instance;
    }

    /**
     * This method will handle to logic to add a user to the application.
     * It may output an alert error or add the user to the userList
     * 
     * @param adminInput
     */
    private void processAdd(String adminInput){
        //we do not accept empty strings
        if(adminInput.equals("")){
            //if it is anything else, send error, change nothing
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid username");
            alert.setContentText("This is an invalid username. Usernames canot be empty. Please try again.");
            alert.showAndWait();
        //if username is not used and admin presses add user, then add the user
        } else if(!userList.contains(adminInput)){
            addUser(adminInput);
            updateList();
        } 
        //if username is selected from ListView and admin presses delete user, then remove the user
        else {
            //if it is anything else, send error, change nothing
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid username");
            alert.setContentText("This user already exists. Please enter a non-existing username.");            
            // else if(b.getText().equals("Delete User")){
            //     alert.setContentText("This user does not exist. Please enter an existing username.");            
            // }
            alert.showAndWait();
        }

    }

    /**
     * This method handles the logic for deleting a user
     */
    private void processDelete(){
        if(userList.contains(photosController.adminController.userListView.getSelectionModel().getSelectedItem())){
            deleteUser(photosController.adminController.userListView.getSelectionModel().getSelectedItem());
            updateList();
        }
    }

    /**
     * Deletes user by deleting thier serialized file
     * @param username
     */
    private static String deleteUser(String username){
        //gets searialized folder 
        File dir = new File(PhotoUserState.storeDir);
        //gets all the file in the directory
        File[] dirFiles = dir.listFiles();
        if(dirFiles != null) {
            for(File f : dirFiles) {
                if(f.getName().split("-photosSession.ser")[0].equals(username)){
                    f.delete();
                }
            }
        }
        photosController.adminController.deleteUser.setDisable(true);
        return username;
    }

    /**
     * Creates a new serialized file using username and PhotoAlbumState.getInstance()
     * @param username
     */
    private static void addUser(String username){
        try {
            PhotoUserState.clearInstance();
            photosController.photoUserState = PhotoUserState.getInstance();
            photosController.photoUserState.currentUser = username;
            PhotoUserState.writeState(photosController.photoUserState, username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Updates the ListView list to current user list
     */
    public void updateList(){
        userList = PhotoLoginState.parseUsers();
        observableList = FXCollections.observableArrayList(userList);
        photosController.adminController.userListView.setItems(observableList);
        PhotoLoginState.updateUserList();
    }

    /**
     * Method that determines type of FXML element was last clicked.
     * @return String to find the type of FXML element that was interacted with on Admin screen. Either button or menuItem
     */
    private String determineEventSource(){
        if(lastEvent.getSource() instanceof Button){
            return "button";
        } else {
            return "menuItem";
        }
    }

    /**
     * Returns singleton instance.
     * 
     * @return Singleton instance
     */
    public static PhotoAdminState getInstance() {
        if (instance == null) {
            instance = new PhotoAdminState();
        }
        return instance;
    }
}
 
