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

public class EntryNameController {

    @FXML
    private TextField textFieldName;
    @FXML
    private Button buttonAccept;
    @FXML
    private ImageView imageView;

    private String name;
    private String nameInput;

    @FXML
    private void initialize() {
        textFieldName.setOnAction(_ -> buttonAccept.fire());
    }

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

    public boolean nameInputIsValid() {
        return (!nameInput.isEmpty()) && !nameInput.matches("^[a-zA-Z0-9-_. ]+$");
    }

    public String getName(){
        return name.trim();
    }

    public void setName(String name){
        this.name = name;
        textFieldName.setText(name);
    }


    private Stage getStage(){
        return (Stage) buttonAccept.getScene().getWindow();
    }

    public void setTitle(String title){
        getStage().setTitle(title);
    }

    public void setFileType(FileTypes ft){
        setImageView(ft);
    }

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
