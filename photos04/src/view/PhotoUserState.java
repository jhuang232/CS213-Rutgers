package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import photos.*;

/**
 * This class represents the User State. In this state, there is a list of all
 * created albums, add a new album by providing a name, rename existing albums,
 * deleting existing albums, manage tags for all photos, and open created albums
 * It is a subclass of PhotoState, implementing the State design pattern for Photos.
 * 
 * Modified design of CalculatorController develeoped by Sesh Venogupal
 * 
 * @author Jason Huang
 */

public class PhotoUserState extends PhotoState implements Serializable{
    
    /**
     * This class represents configuration constants for serializing and storing photo session data.
     *
     * <p>The {@code serialVersionUID} is a version identifier for the Serializable class.
     * The {@code storeDir} specifies the directory where serialized data will be stored.
     * The {@code storeFile} specifies the name of the serialized file storing the photo session data.
     *
     * <p>Note: It is advisable to change the {@code serialVersionUID} value when there are changes
     * to the class that might affect its serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The directory where serialized data will be stored.
     */
    public static final String storeDir = "./data/ser";

    /**
     * The name of the serialized file storing the photo session data.
     */
    public static final String storeFile = "photosSession.ser";
    
    /** 
     * List of Tags that user creates
     */
    protected ArrayList<Tag> tagListAll = new ArrayList<Tag>();
    
    /** 
     * List of Albums for that user
     */
    protected ArrayList<Album> albumsList = new ArrayList<Album>();

    /** 
     * Create an observable list to store the data for the ListView 
     */
    protected transient ObservableList<Album> albumListData;

    /**
	 * Singleton instance 
	 */
	private static PhotoUserState instance = null;
	
    /**
     * The current logged in user
     */
    protected String currentUser;
    
    /**
	 * Prevents instantiation, to implement singleton pattern.
	 */
	private PhotoUserState() {
		
	}

    /**
     * On enter it display all the albums of username, if new user display empty list
     */
    void enter() {
        try {
            Stage primaryStage = photosController.currentStage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/user.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            photosController.userController = loader.getController();
            photosController.userController.setPhotosController(photosController);

            //setup welcome user and menu
            photosController.userController.userMenu.setText(currentUser);
            photosController.userController.welcome.setText("Welcome, \n" + currentUser);
            photosController.userController.userMenu.setText("User(" + currentUser + ")");
                        
            primaryStage.setScene(scene);
            primaryStage.show();

            // used to set up stock user run this before last push and then delete
            // either delete all other serialized users or just soley add ./data/ser/stock...
            // we can enter into stock and add captions and tags and stuff if we want
            // if(currentUser.equals("stock") && albumsList.size() == 0){
            //     Album stock = new Album("stock");
            //     for(int i = 1; i <=7; i++){
            //         stock.addPhoto(new Photo(new File("./data/Stock" + i + ".png")));
            //         System.out.println(stock.getPhotos().get(i-1).getFile().toString());
            //     }
            //     albumsList.add(stock);
            // }

            //populates allbumList and also ListView of albums
            updateUserAlbums();
            if(!albumsList.isEmpty()) {
               photosController.userController.listOfAlbums.getSelectionModel().select(0);
            }
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes an event that is fired when in albums an event is any button press and the logout menu
     * @return New state
     */
    PhotoState processEvent() {
        String event = determineEventSource();
        switch(event){
            case "button":
            //we hit a button so handle it
                Button b = (Button)lastEvent.getSource();
                //what user typed in
                String albumInput = photosController.userController.albumText.getText().trim();
                updateUserAlbums();
                switch(b.getId()){
                    case "rename":
                        renameProcess(photosController.userController.listOfAlbums.getSelectionModel().getSelectedItem());
                        break;
                    case "create":
                        if(validateAlbum(albumInput)){
                            addAlbumByName(albumInput);
                            updateUserAlbums();
                            photosController.userController.albumText.clear();
                        }
                        break;
                    case "search":
                        photosController.searchController.initialize();
                        break;
                    case "open":
                        photosController.photoAlbumState.currentAlbum = photosController.userController.listOfAlbums.getSelectionModel().getSelectedItem();
                        photosController.photoAlbumState.enter();
                        return photosController.photoAlbumState;
                    case "delete":
                        deleteAlbum(photosController.userController.listOfAlbums.getSelectionModel().getSelectedItem());
                        updateUserAlbums();
                        photosController.userController.delete.setDisable(true);
                        photosController.userController.open.setDisable(true);
                        photosController.userController.rename.setDisable(true);
                        break;
                    case "manageTags":
                        photosController.tagController.initialize();
                        break;
                }
                break;
            case "menuItem":
            //we hit a menuItem, only one is logout so logout logic here
                photosController.photoLoginState.enter();
                try {
                    writeState(this, currentUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return photosController.photoLoginState;
        }

        //make compiler happy
        return instance;
    }


    /**
     * Used to write the serialized Photos state, this ineffect stores the current state of the app as a whole for later use
     * 
     * @param state PhotoUserState - reference to PhotoUserState object, reference to data tied to user
     * @param username String - username of user 
     * @throws IOException Error when when the file does not exist in directory
     */
    public static void writeState(PhotoUserState state, String username)throws IOException {
        //need to add username before storeFile
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + username +"-"+ storeFile));
        oos.writeObject(state);
        oos.close();
    }
    
    /**
     * Used to read a previous state of Photos, and should return it;
     * Also need to add a username param before storeFile
     * 
     * @param username String - name of the user to load their profile
     * @return PhotoUserState - state of user, reference to data tied to user 
     * @throws IOException - Error when the file does not exist in directory
     * @throws ClassNotFoundException - Error when class, referring to PhotoUserState object, cannot be found 
     */
    public static PhotoUserState readState(String username) throws IOException, ClassNotFoundException{ 
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + username +"-"+ storeFile)); 
        PhotoUserState state = (PhotoUserState)ois.readObject();
        ois.close();
        return state;
    }
    
    /**
     * Adds an album to the albumList given a name
     * @param albumName name of the album to be added
     */
    public void addAlbumByName(String albumName){
        albumsList.add(new Album(albumName));
    }

    /**
     * Adds an album to the albumList given an Album
     * @param album {@link Album} - reference to album object
     */
    public void addAlbumFromSearchResult(Album album){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input New Album Name");
        dialog.setHeaderText("Enter the new album name");
        dialog.setContentText("Name:");
        String newName = dialog.showAndWait().get();
        if(validateAlbum(newName)){
            album.setName(newName);
            albumsList.add(album);
            updateUserAlbums();
            photosController.albumController.makeAlbum.setVisible(false);
            photosController.albumController.albumTitle.setText("Current Album: \n" + newName);
        }
    }

    /**
     * Renames a given album to the new given name
     * @param albumTarget Album - reference to album object
     * @param newName String - new chosen name for selected album
     */
    public void renameAlbum(Album albumTarget, String newName){
        albumsList.get(albumsList.indexOf(albumTarget)).setName(newName);
    }

    /** 
     * Full process for renaming an album
     * @param albumTarget Album - reference to album object
     */
    public void renameProcess(Album albumTarget){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input New Album Name");
        dialog.setHeaderText("Enter the new album name");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && validateAlbum(result.get())){
            renameAlbum(albumTarget, result.get());
            updateUserAlbums();
        }
    }

    /**
     * Deletes a given album from the albumList
     * @param albumTarget Album - reference to album object to delete
     * @return the deleted album
     */
    public Album deleteAlbum(Album albumTarget){
        albumsList.remove(albumTarget);
        return albumTarget;
    }

    /**
     * Gets all the albums of the user
     * @return Arraylist of user's albums
     */
    public ArrayList<Album> getAlbums(){
        return albumsList;
    }

    /**
     * Gets all the tags of the user
     * @return ArrayList containg all tags of current user
     */
    public ArrayList<Tag> getTags(){
        return tagListAll;
    }

    /**
     * Filters list of photos by a range of dates
     * @param state - current PhotoUserState reference
     * @param startDate - Start date entered
     * @param endDate - End date entered
     * @return Album with the photos in the date range
     */
    public static Album isInDateRange(PhotoUserState state, LocalDate startDate, LocalDate endDate){
        Album result = new Album("");
        for(Album a: state.albumsList){
            for (Photo currPhoto: a.getPhotos()){
                if(!(result.getPhotos().contains(currPhoto))
                    && (currPhoto.getDate().compareTo(startDate) >= 0 && currPhoto.getDate().compareTo(endDate) <= 0)){
                    result.addPhoto(currPhoto);
                }
            }
        }
        return result;
    }


    /**
     * Filters the list of photos by tags using a specified operator.
     *
     * @param state    - reference to current PhotoUserState
     * @param tag1     - First tag to compare.
     * @param operator - The operator to use ("AND" or "OR").
     * @param tag2     - Second tag to compare.
     * @return Album - album filled with photos that contains one or both tags
     */
    public static Album tagFilter(PhotoUserState state, Tag tag1, String operator, Tag tag2) {
        Album result = new Album("");
        ArrayList<Album> list = state.albumsList;
        Predicate<Photo> pred;

        //switch statement to compost a proper predicate for specific tag searches
        switch (operator) {
            case "AND":
                pred = (Photo photo) -> (photo.hasTag(tag1) && photo.hasTag(tag2));
                break;
            case "OR":
                pred = (Photo photo) -> (photo.hasTag(tag1) || photo.hasTag(tag2));
                break;
            default:
                pred = (Photo photo) -> (photo.hasTag(tag1));
                break;
        }

        //actual appendage of photos to new list
        for(Album a: list){
            for (Photo currPhoto : a.getPhotos()) {
                if (!(result.getPhotos().contains(currPhoto)) && pred.test(currPhoto)) {
                    result.addPhoto(currPhoto);
                }
            }
        }
        return result;
    }

    /**
     * This method determines the source of the last event 
     * @return String - the type of button that was pressed
     */
    private String determineEventSource(){
        if(lastEvent.getSource() instanceof Button){
            return "button";
        } else {
            return "menuItem";
        }
    }

    /**
     * This method checks if an entered album name is empty or exists
     * <p>Returns true if it is valid, false if not
     * @param albumInput - entered name for an album to create
     * @return boolean to see if the input was valid or not
     */
    private boolean validateAlbum(String albumInput){
        //need to check albumInput is valid 
        if(albumInput.equals("")){
            //if it is anything else, send error, change nothing
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Album Title");
            alert.setContentText("Album titles cannot be empty. Please try again.");
            alert.showAndWait();
            return false;
        } 
        //if the user already has that album return an error
        else if(albumsList.stream().anyMatch(a -> a.getName().equals(albumInput))){
            //if it is anything else, send error, change nothing
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Album Title");
            alert.setContentText("This album already exists. Please enter a non-existing album title.");            
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
	 * Returns singleton instance.
	 * 
	 * @return Singleton instance
	 */
	public static PhotoUserState getInstance() {
        if (instance == null) {
			instance = new PhotoUserState();
		}
		return instance;
	}

    /**
     * Clears the singleton instance
     */
    public static void clearInstance(){
        instance = null;
    }

    /**
     * Sets the singleton instance from a deserialized file
     * @param deserializedState - deserialized state of the current PhotoUserState object 
     */
    public static void setInstance(PhotoUserState deserializedState){
        instance = deserializedState;
    }

    /** 
     * Updates the ListView list with the new edits
     */
    public void updateUserAlbums(){
        albumListData = FXCollections.observableArrayList(albumsList);
        photosController.userController.listOfAlbums.setItems(albumListData);
    }

}
