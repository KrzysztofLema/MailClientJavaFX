package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.krzysztofLema.model.folder.EmailFolderBean;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

public class FetchMessagesOnFolderService extends Service {

    private EmailFolderBean<String> emailFolder;
    private Folder folder;

    public FetchMessagesOnFolderService(EmailFolderBean<String> emailFolder, Folder folder) {
        this.emailFolder = emailFolder;
        this.folder = folder;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                if (folder.getType() != Folder.HOLDS_FOLDERS) {
                    folder.open(Folder.READ_WRITE);
                }
                int foldersSize = folder.getMessageCount();
                for (int i = foldersSize; i > 0; i--) {
                    Message currentMessage = folder.getMessage(i);
                    emailFolder.addEmails(-1,currentMessage);
                }
                return null;
            }
        };
    }


}
