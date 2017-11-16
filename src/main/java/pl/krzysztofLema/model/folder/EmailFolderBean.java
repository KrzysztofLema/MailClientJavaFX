package pl.krzysztofLema.model.folder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import pl.krzysztofLema.model.EmailMessageBean;
import pl.krzysztofLema.view.ViewFactory;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.security.auth.Subject;

public class EmailFolderBean<T> extends TreeItem<String> {
    private boolean topElement = false;
    private int unreadMessageCount;
    private String name;
    private String completeName;
    private ObservableList<EmailMessageBean> date = FXCollections.observableArrayList();

    public EmailFolderBean(String value) {
        super(value, ViewFactory.defaultFactory.resolveIcon(value));
        this.name = value;
        this.completeName = value;
        date = null;
        topElement = true;
        this.setExpanded(true);
    }

    public EmailFolderBean(String value, String completName) {
        super(value, ViewFactory.defaultFactory.resolveIcon(value));
        this.name = value;
        this.completeName = completName;
    }

    private void updateValue() {
        if (unreadMessageCount > 0) {
            this.setValue((String) (name + "(" + unreadMessageCount + ")"));
        } else {
            this.setValue(name);
        }
    }

    public void incrementUnreadMessagesCount(int newMessages) {
        unreadMessageCount = unreadMessageCount + newMessages;
        updateValue();
    }

    public void decrementUnreadMessagesCount() {
        unreadMessageCount--;
        updateValue();
    }

    public void addEmails(int position, Message message) throws MessagingException {
        boolean isRead = message.getFlags().contains(Flags.Flag.SEEN);
        EmailMessageBean emailMessageBean = new EmailMessageBean(message.getSubject(),
                message.getFrom()[0].toString(),
                message.getSize(),
                isRead,
                message.getSentDate(),
                message);
        //  date.add(emailMessageBean);
        if (position < 0) {
            date.add(emailMessageBean);
        } else date.add(position, emailMessageBean);
        if (!isRead) {
            incrementUnreadMessagesCount(1);
        }
    }

    public boolean isTopElement() {
        return topElement;
    }

    public ObservableList<EmailMessageBean> getDate() {
        return date;
    }
}

