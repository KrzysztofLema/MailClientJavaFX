package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import pl.krzysztofLema.model.EmailMessageBean;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

public class SaveAttachmentsService extends Service<Void> {

    //here we save the attachments:
    private String LOCATION_OF_DOWNLOADS = System.getProperty("user.home") + "/down/";
    private EmailMessageBean messageToDownload;
    private ProgressBar progress;
    private Label label;

    public SaveAttachmentsService(ProgressBar progress, Label label) {

        this.progress = progress;
        this.label = label;
        this.setOnRunning(e -> showVisuals(true));
        this.setOnSucceeded(e -> showVisuals(false));
    }
    //always call before starting!!!
    public void setMessageToDownload(EmailMessageBean messageToDownload) {
        this.messageToDownload = messageToDownload;
    }

    private void showVisuals(boolean show) {
        progress.setVisible(show);
        label.setVisible(show);
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    for (MimeBodyPart mbp : messageToDownload.getAttachmentsList()) {
                        updateProgress(messageToDownload.getAttachmentsList().indexOf(mbp), messageToDownload.getAttachmentsList().size());
                        mbp.saveFile(LOCATION_OF_DOWNLOADS + mbp.getFileName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };
    }
}

