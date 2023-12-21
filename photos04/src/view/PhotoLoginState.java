package view;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

/**
 * This class represents the Login State. In this state, the display should be text to tell
 * user to login, text field so user can log in, and button to login. It is a subclass of 
 * PhotoState, implementing the State design pattern for Photos.
 * 
 * <p>Modified design of CalculatorController develeoped by Sesh Venogupal
 * 
 * @author Jeffrey Tang
 */
public class PhotoLoginState extends PhotoState{
    
    /**
	 * Singleton instance 
	 */
	private static PhotoLoginState instance = null;
	
    /**
     * Username List
     */
    private static ArrayList<String> userList = parseUsers();

	/**
	 * Prevents instantiation, to implement singleton pattern.
	 */
	private PhotoLoginState() {
		
	}


    /**
     * Called when state is entered.
     * Prompt the user to login
     */
    void enter() {
        try{
            Stage primaryStage = photosController.currentStage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/login.fxml"));
            GridPane root = loader.load();
            Scene scene = new Scene(root);

            photosController.loginController = loader.getController();
            photosController.loginController.setPhotosController(photosController);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Photos");
            primaryStage.setResizable(false);//we dont want the user to be able to resize intiial login
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }

    /**
     * Processes an event that is fired when in login state.
     * 
     * <p>Client can enter one of three strings
     * <p>"admin" - in which case, the user will have admin screen (only meant for admin)
     * <p>"%username" - name of a valid user, in which case, it will log them into that user's account
     * <p>Everything else - invalid username so it will show an error and ask to try again. 
     * 
     * @return New state
     */
    public PhotoState processEvent() {
        //only interactable thing is button 
        //what user has typed in
        String userInput = photosController.loginController.loginText.getText();
        //if text field is "admin", admin login
        if((userInput).equals("admin")){
            photosController.photoAdminState.enter();
            return photosController.photoAdminState;
        } 
        //if actual user
        else if(userList.contains(userInput)){
            photosController.photoUserState.currentUser = userInput;
            try {
                photosController.photoUserState = PhotoUserState.readState(userInput);
                PhotoUserState.setInstance(photosController.photoUserState);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            };
            photosController.photoUserState.enter();
            return photosController.photoUserState;
        }
        else {
            //if it is anything else, send error, change nothing
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid username");
            alert.setContentText("This user does not exist. Please enter an existing username.");            
            alert.showAndWait();
        }
        
        //make compiler happy
        return instance;
    }

    /**
	 * Returns singleton instance.
	 * 
	 * @return Singleton instance
	 */
	public static PhotoLoginState getInstance() {
		if (instance == null) {
			instance = new PhotoLoginState();
		}
		return instance;
	}

   /**
     * Retrieves the usernames of all previous users.
     *
     * @return ArrayList of Strings representing the list of stored usernames.
     */
    public static ArrayList<String> parseUsers(){
        //gets searialized folder 
        File dir = new File(PhotoUserState.storeDir);
        //gets all the file in the directory
        File[] dirFiles = dir.listFiles();
        //start of parsing usernames
        ArrayList<String> result = new ArrayList<String>();
        if(dirFiles != null) {
            for(File f : dirFiles) {
                String[] fileName = f.getName().split("-photosSession.ser");
                result.add(fileName[0]);
            }
        }
        return result;
    }

    /**
     * Updates userList in a different class
     */
    public static void updateUserList(){
        userList = parseUsers();
    }
}
