package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.krzysztofLema.model.EmailAccountBean;
import pl.krzysztofLema.model.EmailConstants;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmailSenderService extends Service<Integer> {


    private int result;
    private EmailAccountBean emailAccountBean;
    private String subject;
    private String reciepients;
    private String content;
    private List<File> attachments = new ArrayList<>();

    public EmailSenderService(EmailAccountBean emailAccountBean, String subject, String reciepient, String content, List<File> attachments) {
        this.emailAccountBean = emailAccountBean;
        this.subject = subject;
        this.reciepients = reciepient;
        this.content = content;
        this.attachments = attachments;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {


                try {
                    Session session = emailAccountBean.getSession();
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(emailAccountBean.getEmailAdress());
                    message.addRecipients(Message.RecipientType.TO, reciepients);
                    message.setSubject(subject);

                    //Setting the content
                    Multipart multipart = new MimeMultipart();
                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(content, "text/html");
                    multipart.addBodyPart(messageBodyPart);
                    //adding attachments
                    if (attachments.size() > 0) {
                        for (File file : attachments) {
                            MimeBodyPart messageBodyPartAttach = new MimeBodyPart();
                            DataSource source = new FileDataSource(file.getAbsolutePath());
                            messageBodyPartAttach.setDataHandler(new DataHandler(source));
                            messageBodyPartAttach.setFileName(file.getName());
                            multipart.addBodyPart(messageBodyPart);
                        }
                    }
                    message.setContent(multipart);
                    //sending the message:
                    Transport transport = session.getTransport();
                    transport.connect(emailAccountBean.getProperties().getProperty("outgoingHost"), emailAccountBean.getEmailAdress(), emailAccountBean.getPassword());
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    result = EmailConstants.MESSAGE_SENT_OK;

                } catch (Exception e) {
                    result = EmailConstants.MESSAGE_SENT_ERROR;
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
