package pl.krzysztofLema.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import pl.krzysztofLema.model.EmailMessageBean;
import pl.krzysztofLema.view.ViewFactory;

import javax.naming.OperationNotSupportedException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmailDetailsController extends AbstractController implements Initializable {



    @FXML
    WebView webView;

    @FXML
    Label subjectLabel,senderLabel;
    @FXML
    void ilegalOperation() throws OperationNotSupportedException {
        ViewFactory view = new ViewFactory();
        Scene mainScene = view.getMainScene();
        Stage stage = new Stage();
        stage.setScene(mainScene);
        stage.show();
    }

    public EmailDetailsController(ModelAccess modelAccess) {
        super(modelAccess);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        EmailMessageBean selectedMessage = getModelAccess().getSelectedMessage();

        subjectLabel.setText("Subject: " + selectedMessage.getSubject());
        senderLabel.setText("Sender: " + selectedMessage.getSender());
//        webView.getEngine().loadContent(selectedMessage.getContent());
    }
}
