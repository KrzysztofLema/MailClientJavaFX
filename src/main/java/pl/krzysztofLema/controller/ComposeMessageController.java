package pl.krzysztofLema.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import pl.krzysztofLema.controller.services.EmailSenderService;
import pl.krzysztofLema.model.EmailConstants;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ComposeMessageController extends AbstractController implements Initializable {


    private List<File> attachments = new ArrayList<>();


    @FXML
    private Label attachemntLabel;

    @FXML
    private ChoiceBox<String> senderChoice;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private Label errorLabel;
    @FXML
    private HTMLEditor composeArea;

    @FXML
    void attachBtnAction() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            attachments.add(selectedFile);
            attachemntLabel.setText(attachemntLabel.getText() + selectedFile.getName() + "; ");
        }
    }

    @FXML
    void sendBtnAction() {
        errorLabel.setText(" ");
        EmailSenderService emailSenderService =
                new EmailSenderService(getModelAccess().getEmailAccountByName(senderChoice.getValue())
                        , subjectField.getText(), recipientField.getText(), composeArea.getHtmlText(), attachments
                );
        emailSenderService.restart();
        emailSenderService.setOnSucceeded(e -> {
            if (emailSenderService.getValue() != EmailConstants.MESSAGE_SENT_OK) {
                errorLabel.setText("Message sent succesfully");
            } else {
                errorLabel.setText("message sending error!!");
            }
        });
    }


    public ComposeMessageController(ModelAccess modelAccess) {
        super(modelAccess);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            senderChoice.setItems(getModelAccess().getEmailAccoutsNames());
            senderChoice.setValue(getModelAccess().getEmailAccoutsNames().get(0));
    }
}
