package pl.krzysztofLema.controller;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import pl.krzysztofLema.controller.services.CreateAndRegisterEmailAccountService;
import pl.krzysztofLema.controller.services.FolderUpdaterService;
import pl.krzysztofLema.controller.services.MessageRendererService;
import pl.krzysztofLema.controller.services.SaveAttachmentsService;
import pl.krzysztofLema.model.EmailMessageBean;
import pl.krzysztofLema.model.folder.EmailFolderBean;
import pl.krzysztofLema.model.table.BoldableRowFactory;
import pl.krzysztofLema.model.table.FormatableInteger;
import pl.krzysztofLema.view.ViewFactory;


import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController extends AbstractController implements Initializable {


    @FXML
    private TreeView<String> emailFoldersTreeView;
    @FXML
    private Button btnCompose;

    private MenuItem showDetails = new MenuItem("Show details");

    private SaveAttachmentsService saveAttachmentsService;
    private MessageRendererService messageRendererService;
    @FXML
    private Label downloadingAtachmentsLabel;

    @FXML
    private ProgressBar downAtachmentsProgress;

    @FXML
    void downAttachBtnAction(ActionEvent event) {
        EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
        if (message != null && message.hasAttachments()) {
            saveAttachmentsService.setMessageToDownload(message);
            saveAttachmentsService.restart();
        }
    }

    @FXML
    private TableView<EmailMessageBean> emailTableView;

    @FXML
    private TableColumn<EmailMessageBean, String> subjectICol;

    @FXML
    private TableColumn<EmailMessageBean, String> senderCol;

    @FXML
    private TableColumn<EmailMessageBean, FormatableInteger> sizeCol;
    @FXML
    private TableColumn<EmailMessageBean, Date> dateCol;

    @FXML
    private WebView messageRenderer;


    public MainController(ModelAccess modelAccess) {
        super(modelAccess);
    }

    public void initialize(URL location, ResourceBundle resources) {

        downAtachmentsProgress.setVisible(false);
        downloadingAtachmentsLabel.setVisible(false);

        saveAttachmentsService = new SaveAttachmentsService(downAtachmentsProgress, downloadingAtachmentsLabel);
        messageRendererService = new MessageRendererService(messageRenderer.getEngine());
        FolderUpdaterService folderUpdaterService = new FolderUpdaterService(getModelAccess().getFolderList());
        downAtachmentsProgress.progressProperty().bind(saveAttachmentsService.progressProperty());
        folderUpdaterService.start();


        ViewFactory viewFactory = ViewFactory.defaultFactory;

        subjectICol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, String>("subject"));
        senderCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, String>("sender"));
        dateCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, Date>("date"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, FormatableInteger>("size"));

        //BUG: size doese't get it's default comparator overridden, so we do it by hand!!
        sizeCol.setComparator(new FormatableInteger(0));


        EmailFolderBean<String> root = new EmailFolderBean<String>("");
        emailFoldersTreeView.setRoot(root);
        emailFoldersTreeView.setShowRoot(false);
        // here you should paste your login and password to e-mail account
        CreateAndRegisterEmailAccountService createAndRegisterEmailAccountService = new CreateAndRegisterEmailAccountService("LOGIN",
                "PASSWORD", root,
                getModelAccess());
        createAndRegisterEmailAccountService.start();

        emailTableView.setContextMenu(new ContextMenu(showDetails));

        emailFoldersTreeView.setOnMouseClicked(e -> {
            EmailFolderBean<String> item = (EmailFolderBean<String>) emailFoldersTreeView.getSelectionModel().getSelectedItem();
            if (item != null && !item.isTopElement()) {
                emailTableView.setItems(item.getDate());
                getModelAccess().setSelectedFolder(item);
                getModelAccess().setSelectedMessage(null);
            }
        });
        emailTableView.setContextMenu(new ContextMenu(showDetails));
        emailTableView.setRowFactory(e -> new BoldableRowFactory<>());
        showDetails.setOnAction(event -> {
            viewFactory.getEmailDetailsScene();

            Stage stage = new Stage();
            stage.setScene(viewFactory.getEmailDetailsScene());
            stage.show();
        });
        emailTableView.setOnMouseClicked(event -> {
            EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
            if (message != null) {
                getModelAccess().setSelectedMessage(message);
                messageRendererService.setMessageToRender(message);
                messageRendererService.restart();
                // Platform.runLater(messageRendererService);// on Application Thread!!
            }
        });

        btnCompose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene scene = ViewFactory.defaultFactory.getComposeMessageScene();
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        });
    }
}
