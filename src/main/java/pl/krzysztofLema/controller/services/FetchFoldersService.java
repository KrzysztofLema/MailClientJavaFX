package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.krzysztofLema.controller.ModelAccess;
import pl.krzysztofLema.model.EmailAccountBean;
import pl.krzysztofLema.model.folder.EmailFolderBean;
import sun.dc.pr.PRError;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

public class FetchFoldersService extends Service<Void> {

    private EmailFolderBean<String> foldersRoot;
    private EmailAccountBean emailAccountBean;
    private ModelAccess modelAccess;
    private static int NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE = 0;

    public FetchFoldersService(EmailFolderBean<String> foldersRoot, EmailAccountBean emailAccountBean, ModelAccess modelAccess) {
        this.foldersRoot = foldersRoot;
        this.emailAccountBean = emailAccountBean;
        this.modelAccess = modelAccess;
        this.setOnSucceeded(event -> {
            NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE--;
        });
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE++;
                if (emailAccountBean != null) {
                    Folder[] folders = emailAccountBean.getStore().getDefaultFolder().list();
                    for (Folder folder : folders) {
                        modelAccess.addFolder(folder);
                        EmailFolderBean<String> item = new EmailFolderBean<String>(folder.getName(), folder.getFullName());
                        foldersRoot.getChildren().add(item);
                        item.setExpanded(true);
                        addMessageListenerToFolder(folder, item);
                        FetchMessagesOnFolderService fetchMessagesOnFolderService = new FetchMessagesOnFolderService(item, folder);
                        fetchMessagesOnFolderService.start();
                        System.out.println("added: " + folder.getName());
                        Folder[] subFolders = folder.list();
                        for (Folder subfolder : subFolders) {
                            modelAccess.addFolder(subfolder);
                            EmailFolderBean<String> subItem = new EmailFolderBean<String>(subfolder.getName(), subfolder.getFullName());
                            item.getChildren().add(subItem);
                            addMessageListenerToFolder(subfolder,subItem);
                            FetchMessagesOnFolderService fetchMessagesOnSubFolderService = new FetchMessagesOnFolderService(subItem, subfolder);
                            fetchMessagesOnSubFolderService.start();
                            System.out.println("added: " + subfolder.getName());
                        }
                    }
                }
                return null;
            }
        };
    }

    private void addMessageListenerToFolder(Folder folder, EmailFolderBean<String> item) {
        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                for (int i = 0; i < e.getMessages().length; i++) {
                    try {
                        Message currentMessage = folder.getMessage(folder.getMessageCount() - i);
                        item.addEmails(0, currentMessage);
                    } catch (MessagingException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    public static boolean noServiceIsActive(){
        return NUMBER_OF_FETCHFOLDERSSERVICES_ACTIVE == 0;
    }
}
