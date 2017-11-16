package pl.krzysztofLema.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pl.krzysztofLema.model.table.AbstractTableItem;
import pl.krzysztofLema.model.table.FormatableInteger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.util.*;

public class EmailMessageBean extends AbstractTableItem {



    private SimpleStringProperty sender;
    private SimpleStringProperty subject;
    private SimpleObjectProperty<FormatableInteger> size;
    private String content;
    private Message messageReference;
    private SimpleObjectProperty<Date> date;

    //Attachments handling

    private List<MimeBodyPart> attachmentsList = new ArrayList<MimeBodyPart>();
    private StringBuffer attachmentsNames = new StringBuffer();

    public List<MimeBodyPart> getAttachmentsList() {
        return attachmentsList;
    }

    public String getAttachmentsNames() {
        return attachmentsNames.toString();
    }

    public void addAttachment(MimeBodyPart mbp) {
        attachmentsList.add(mbp);
        try {
            attachmentsNames.append(mbp.getFileName() + ";");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean hasAttachments() {
        return attachmentsList.size() > 0;
    }


    public String getContent() {
        return content;
    }


    public Date getDate() {
        return date.get();
    }

    public SimpleObjectProperty<Date> dateProperty() {
        return date;
    }

    public EmailMessageBean(String Subject, String Sender, int size, boolean isRead, Date date, Message messageReference) {
        super(isRead);
        this.subject = new SimpleStringProperty(Subject);
        this.sender = new SimpleStringProperty(Sender);
        this.size = new SimpleObjectProperty<FormatableInteger>(new FormatableInteger(size));
        this.messageReference = messageReference;
        this.date = new SimpleObjectProperty<Date>(date);
    }

    public String getSender() {
        return sender.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public FormatableInteger getSize() {
        return size.get();
    }

    @Override
    public String toString() {
        return "EmailMessageBean{" +
                "sender=" + sender +
                ", subject=" + subject +
                ", size=" + size +
                ", content='" + content + '\'' +
                '}';
    }

    //clear methods
    public Message getMessageReference() {
        return messageReference;
    }

    public void clearAttachments() {
        attachmentsList.clear();
        attachmentsNames.setLength(0);
    }
}
