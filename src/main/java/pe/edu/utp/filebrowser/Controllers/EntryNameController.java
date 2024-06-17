package pe.edu.utp.filebrowser.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.utp.filebrowser.Utilites.GlobalExceptionHandler;

public class EntryNameController {

    @FXML
    private TextField textFieldName;
    @FXML
    private Button buttonAccept;

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
            GlobalExceptionHandler.alertWarning(
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
        return name;
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


}
