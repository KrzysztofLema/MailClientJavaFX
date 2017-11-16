package pl.krzysztofLema.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pl.krzysztofLema.controller.*;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public class ViewFactory {

    public static ViewFactory defaultFactory = new ViewFactory();
    private static boolean mainViewInitialized = false;

    private ModelAccess modelAccess = new ModelAccess();
    private final String DEFAULT_CSS = "style.css";
    private final String EMAIL_DETAILS_FXML = "EmailDetails.fxml";
    private final String MAIN_DETAILS_FXML = "MainLayout.fxml";
    private final String COMPOSE_MESSAGE_LAYOUT="ComposeMessageLayout.fxml";
    private MainController mainController;
    private EmailDetailsController emailDetailsController;
    private ComposeMessageController composeMessageController;


    public Scene getMainScene() throws OperationNotSupportedException {
        if (!mainViewInitialized) {
            mainController = new MainController(modelAccess);
            mainViewInitialized = true;
            return initialiceScene(MAIN_DETAILS_FXML, mainController);
        }else {
            throw new OperationNotSupportedException("Main Scene allready initialized!!");
        }
    }

    public Scene getEmailDetailsScene() {
        emailDetailsController = new EmailDetailsController(modelAccess);
        return initialiceScene(EMAIL_DETAILS_FXML, emailDetailsController);
    }

    public Scene getComposeMessageScene(){
        composeMessageController = new ComposeMessageController(modelAccess);
        return initialiceScene(COMPOSE_MESSAGE_LAYOUT,composeMessageController);
    }

    private Scene initialiceScene(String fxmlPath, AbstractController controller) {
        FXMLLoader loader;
        Parent parent;
        Scene scene;

        try {
            loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
            loader.setController(controller);
            parent = loader.load();
        } catch (IOException e) {
            return null;
        }
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getClassLoader().getResource(DEFAULT_CSS).toExternalForm());
        return scene;
    }

    public Node resolveIcon(String treeItemValue) {
        String lowerCaseTreeItemValue = treeItemValue.toLowerCase();
        ImageView returnIcon;
        try {
            if (lowerCaseTreeItemValue.contains("inbox")) {
                returnIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/inbox.png")));
            } else if (lowerCaseTreeItemValue.contains("sent")) {
                returnIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/sent2.png")));
            } else if (lowerCaseTreeItemValue.contains("spam")) {
                returnIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/spam.png")));
            } else if (lowerCaseTreeItemValue.contains("@")) {
                returnIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/email.png")));
            } else
                returnIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("images/folder.png")));
        } catch (NullPointerException e) {
            System.out.println("Invalid image location");
            e.printStackTrace();
            returnIcon = new ImageView();
        }
        return returnIcon;
    }
}
