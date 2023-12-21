package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import photos.Tag;

/**
 * This class is the JavaFX controller class for the search pop up
 * 
 * @author Jeffrey Tang
 */
public class SearchController {

    /**
     * Reference to photosController object
     */
    protected PhotosController photosController;

    /**
     * FXML DatePicker for start date and end date of search criterion
     */
    @FXML
    protected DatePicker startDate, endDate;

    /**
     * FXML ChoiceBox for tag1 and tag2(optional) search criterion
     */
    @FXML
    protected ChoiceBox<Tag> tag1, tag2;

    /**
     * FXML ChoiceBox for junctive if in tag mode. Optional for a search via tags
     */
    @FXML
    protected ChoiceBox<Junctive> junction;

    /**
     * FXML Text to show "Tag 1", "Tag 2", "Junction", and "Date"
     */
    @FXML
    protected Text tag1Text,tag2Text,junctionText, dateText;

    /**
     * FXML Buttons for Cancel and Search(confirm)
     */
    @FXML
    protected Button cancel, search;
    
    /**
     * Enumerated type SearchMode to determine if the serach is using dates or tag(s) as its search criterion
     */
    private static SearchMode sm;

    /**
     * Sets the class photosController to the parameter
     * @param pc - PhotosController object
     */
    public void setPhotosController(PhotosController pc){
        this.photosController = pc;
    }

    /**
     * Enumerated type for types of junctions a user can have for two tags.
     * This enum specifies different conjunctions or disjunctions for tag combinations.
     * <p>Usage:
     * <ul>
     *      <li>{@link #SELECT}: Default setting, no specific functionality.</li>
     *      <li>{@link #AND}: Conjunctive option, requires both tags to be present.</li>
     *      <li>{@link #OR}: Disjunctive option, either one of the tags or both can be present.</li>
     * </ul>
     */
    public enum Junctive {
        /**
         * Default setting, no specific functionality.
         */
        SELECT,
        /**
         * Conjunctive option, requires both tags to be present.
         */
        AND,
        /**
         * Disjunctive option, either one of the tags or both can be present.
         */
        OR;
    }


    /**
     * Enumerated type for types of searches a user can perform.
     * This enum specifies different search modes for user queries.
     * <p>Usage:
     * <ul>
     *      <li>{@link #DATE}: Search by the date of photos.</li>
     *      <li>{@link #TAGS}: Search by the tags of photos.</li>
     * </ul>
     */
    public enum SearchMode {
        /**
         * Search by the date of photos.
         */
        DATE,
        /**
         * Search by the tags of photos.
         */
        TAGS;
    }
    
    /**
     * Method to start the search window
     */
    void initialize(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/search.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(photosController.currentStage.getScene().getWindow());
            AnchorPane root = loader.load();
    
            photosController.searchController = loader.getController();
            photosController.searchController.setPhotosController(photosController);
    
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            
            photosController.searchController.setAllFieldsInvisible();
            //need to srt up choices in Choice boxes depending on what user selects
            
            // Show and wait so the user must interact if open
            dialog.show();
            
            //actual search options
            Optional<String> resultOption = showSearchOptionsDialog();
            resultOption.ifPresent(this::handleSearchOptionDisplay);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or log it
        }
    }

    /**
     * FXML method for when User click the cancel button
     * <p>Will close the window
     * @param event - ActionEvent on search window
     */
    @FXML
    private void cancel(ActionEvent event){
        //close screen
        Stage stage = (Stage)cancel.getScene().getWindow();
        stage.close();
    }

    /**
     * FXML method to enable second tag tag2 dependent on junction in ChoiceBox
     * <p>SELECT - disable 
     * <p>AND/OR - enable
     * @param event - ActionEvent on search window
     */
    @FXML
    private void changeJunctive(ActionEvent event){
        Junctive selectedOption = junction.getValue();
        if(selectedOption == Junctive.SELECT){
            tag2.getSelectionModel().clearSelection();
            tag2.setDisable(true);
        }else{
            tag2.setDisable(false);
        }
    }

    /**
     * Sets all fields on search window to invisible
     */
    public void setAllFieldsInvisible() {
        tag1Text.setVisible(false);
        tag2Text.setVisible(false);
        junctionText.setVisible(false);
        tag1.setVisible(false);
        tag2.setVisible(false);
        junction.setVisible(false);
        startDate.setVisible(false);
        endDate.setVisible(false);
        dateText.setVisible(false);
    }


    /**
     * Sets only date-specific items to visible or invisible
     * @param visible true to make date fields visible, false to make them invisible
     */
    public void setDateFieldsVisibility(boolean visible) {
        startDate.setVisible(visible);
        endDate.setVisible(visible);
        dateText.setVisible(visible);
    }

    /**
     * Sets only tag-specific items to visible or invisible
     * @param visible true to make tag fields visible, false to make them invisible
     */
    public void setTagFieldsVisibility(boolean visible) {
        tag1Text.setVisible(visible);
        tag2Text.setVisible(visible);
        junctionText.setVisible(visible);
        tag1.setVisible(visible);
        tag2.setVisible(visible);
        tag2.setDisable(visible);
        junction.setVisible(visible);
    }

    /**
     * Populates the tag choice boxes with the provided list of tags
     */
    public void populateTagSearchChoiceBoxes() {
        if(photosController.photoUserState.tagListAll != null){
            ObservableList<Tag> tagsObservableList = FXCollections.observableArrayList(photosController.photoUserState.tagListAll);
            ObservableList<Junctive> junctionValues = FXCollections.observableArrayList(Junctive.values());
            
            // Populate all choiceBoxes with corresponding values
            tag1.setItems(tagsObservableList);
            tag2.setItems(tagsObservableList);
            junction.setItems(junctionValues);

            // Sets the default value to select
            junction.setValue(Junctive.SELECT);
        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Valid Tags for Search");
            alert.setContentText("You have not tags to search by please add tags to photos, and then search");
            alert.showAndWait();
            Stage searchStage = (Stage) photosController.searchController.cancel.getScene().getWindow();
            searchStage.close();
        }
    }

    /**
     * FXML method to hanlde/initialize Search logic
     * @param event - ActionEvent on search window
     */
    @FXML
    private void initSearch(ActionEvent event){
        if(validSearch()){
            switch(sm){
                case TAGS:
                    photosController.photoAlbumState.currentAlbum = PhotoUserState.tagFilter(photosController.photoUserState, tag1.getValue(), junction.getValue().toString(), tag2.getValue());
                    photosController.photoAlbumState.enter();
                    photosController.currentState = photosController.photoAlbumState;
                    break;
                case DATE:
                    photosController.photoAlbumState.currentAlbum = PhotoUserState.isInDateRange(photosController.photoUserState, startDate.getValue(), endDate.getValue());
                    photosController.photoAlbumState.enter();
                    photosController.currentState = photosController.photoAlbumState;
                    break;
            }
            //close screen
            Stage stage = (Stage)search.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Prompts user method for search - search criteria 
     * @return Optional<String> with their response
     */
    private Optional<String> showSearchOptionsDialog() {
        List<String> choices = Arrays.asList("Search by Date Range", "Search by Tag");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Search Options");
        dialog.setHeaderText("Choose Search Option");
        dialog.setContentText("Select the search option:");
        // Get the dialog pane to access its buttons
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.lookupButton(ButtonType.CANCEL).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Stage stage = (Stage)photosController.searchController.cancel.getScene().getWindow();
            stage.close();        
        });
        return dialog.showAndWait();
    }

    /**
     * Sets searchmode to the corresponding response 
     * @param searchOption String - the selected search crtieria
     */
    private void handleSearchOptionDisplay(String searchOption) {
        if (searchOption.equals("Search by Date Range")) {
            photosController.searchController.setTagFieldsVisibility(false);
            photosController.searchController.setDateFieldsVisibility(true);
            sm = SearchMode.DATE;
        } else if (searchOption.equals("Search by Tag")) {
            photosController.searchController.setTagFieldsVisibility(true);
            photosController.searchController.setDateFieldsVisibility(false);
            photosController.searchController.populateTagSearchChoiceBoxes(); 
            sm = SearchMode.TAGS;
        }
    }

    /**
     * Validates the status of the search, checking for proper types and numbers of search criteria
     * @return boolean - whether the serach criteria was valid(true) or not(false)
     */
    private boolean validSearch(){
        switch(sm){
            case DATE:
                LocalDate date1 = startDate.getValue();
                LocalDate date2 = endDate.getValue();
                
                try{
                    //check that the first is before the second
                    if(date1.isAfter(date2)){
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Date");
                        alert.setContentText("Start Date is greater than End Date");
                        alert.showAndWait();
                        return false;
                    }    
                    //check if the dates are valid dates
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    String fmtDate1 = date1.format(fmt);
                    String fmtDate2 = date2.format(fmt);
                    return fmtDate1.equals(String.format("%02d/%02d/%d", date1.getMonthValue(), date1.getDayOfMonth(), date1.getYear()))
                        && fmtDate2.equals(String.format("%02d/%02d/%d", date2.getMonthValue(), date2.getDayOfMonth(), date2.getYear()));
                } catch (DateTimeParseException | NullPointerException e){
                    //if not an error will error so we catch and state an error
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Date");
                    alert.setContentText("At least one of your provided dates are invalid. Dates are in the format MM/dd/yyyy. Please try again.");
                    alert.showAndWait();
                    return false;
                }
            case TAGS:
                if((tag1.getValue()==null) || (junction.getValue()!=Junctive.SELECT && tag2.getValue()==null)){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Tags");
                    alert.setContentText("One of your selected tags are empty. Please set your search target and try again.");
                    alert.showAndWait();
                    return false;
                }
        }
        return true;
    }
}
