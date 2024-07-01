package pe.edu.utp.filebrowser.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.Enums.FileTypes;
import pe.edu.utp.filebrowser.FileBrowser;
import pe.edu.utp.filebrowser.Utilites.JavaFXGlobalExceptionHandler;

import java.io.File;
import java.util.Objects;

/**
 * Controller for handling user input in a dialog for entering a name.
 */
public class EntryNameController {

    @FXML
    private TextField textFieldName;
    @FXML
    private Button buttonAccept;
    @FXML
    private ImageView imageView;

    private String name;
    private String nameInput;

    /**
     * Initializes the controller.
     * Sets up an action for the text field to trigger buttonAccept on Enter.
     */
    @FXML
    private void initialize() {
        textFieldName.setOnAction(_ -> buttonAccept.fire());
    }

    /**
     * Action handler for the accept button.
     * Validates the entered name and handles user input accordingly.
     */
    @FXML
    private void buttonAction(){
        nameInput = textFieldName.getText();
        if(nameInputIsValid() && !nameInput.isBlank()){
            JavaFXGlobalExceptionHandler.alertWarning(
                    "Name entry",
                    "Invalid name",
                    "The name you entered is not valid, please try again."
            );
            return;
        }
        this.name = nameInput;
        getStage().close();
    }

    /**
     * Checks if the current name input is valid.
     *
     * @return true if the name input is valid, false otherwise
     */
    public boolean nameInputIsValid() {
        return (!nameInput.isEmpty()) && !nameInput.matches("^[a-zA-Z0-9-_. ]+$");
    }

    /**
     * Retrieves the entered name.
     *
     * @return the entered name
     */
    public String getName(){
        return name.trim();
    }

    /**
     * Sets the initial name in the text field.
     *
     * @param name the initial name to set
     */
    public void setName(String name){
        this.name = name;
        textFieldName.setText(name);
    }

    /**
     * Retrieves the stage (window) associated with the current scene.
     *
     * @return the stage object
     */
    private Stage getStage(){
        return (Stage) buttonAccept.getScene().getWindow();
    }

    /**
     * Sets the title of the dialog window.
     *
     * @param title the title to set
     */
    public void setTitle(String title){
        getStage().setTitle(title);
    }

    /**
     * Sets the image view based on the specified file type.
     *
     * @param ft the file type to determine the image for
     */
    public void setFileType(FileTypes ft){
        setImageView(ft);
    }

    /**
     * Sets the image view based on the specified file type.
     *
     * @param ft the file type to determine the image for
     */
    private void setImageView(FileTypes ft) {
        String path = switch (ft) {
            case FOLDER -> "imgs/folder.png";
            case PLAINTEXT -> "imgs/file.png";
            case VIRTUALDISK -> "imgs/virtualdisk.png";
            case DIRECTACCESS_FOLDER -> "imgs/folder_da.png";
            case DIRECTACCESS_PLAINTEXT -> "imgs/file_da.png";
            case DIRECTACCESS_VIRTUALDISK -> "imgs/virtualdisk_da.png";
            default -> "imgs/unkown.png";
        };
        File file = new File(Objects
                .requireNonNull(FileBrowser.class.getResource(path))
                .getPath().substring(1));
        imageView.setImage(new Image(file.toURI().toString()));
    }

}
