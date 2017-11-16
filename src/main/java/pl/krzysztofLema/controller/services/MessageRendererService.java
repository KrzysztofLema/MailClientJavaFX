package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import pl.krzysztofLema.model.EmailMessageBean;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

public class MessageRendererService extends Service<Void> {

    private EmailMessageBean messageToRender;
    private WebEngine messageRendererEngine;

    private StringBuffer sb = new StringBuffer();

    public MessageRendererService(WebEngine messageRendererEngine) {
        this.messageRendererEngine = messageRendererEngine;
    }

    public void setMessageToRender(EmailMessageBean messageToRender) {
        this.messageToRender = messageToRender;
        this.setOnSucceeded(e -> {
            loadMessage();
        });
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                renderMessage();
                return null;
            }
        };
    }

    private void renderMessage() {
        //clears the sb:
        sb.setLength(0);
        messageToRender.clearAttachments();
        Message message = messageToRender.getMessageReference();
        try {
            String messageType = message.getContentType();
            if (messageType.contains("TEXT/HTML") ||
                    messageType.contains("TEXT/PLAIN") ||
                    messageType.contains("text")) {
                sb.append(message.getContent().toString());
            } else if (messageType.contains("multipart")) {
                Multipart mp = (Multipart) message.getContent();
                for (int i = mp.getCount() - 1; i >= 0; i--) {
                    BodyPart bp = mp.getBodyPart(i);
                    String contentType = bp.getContentType();
                    if (contentType.contains("TEXT/HTML") ||
                            contentType.contains("TEXT/PLAIN") ||
                            contentType.contains("MIXED") ||
                            contentType.contains("TEXT")) {
                        if (sb.length()== 0) {
                            sb.append(bp.getContent().toString()); }}
                            else if (contentType.toLowerCase().contains("application") ||
                                contentType.toLowerCase().contains("image") ||
                                contentType.toLowerCase().contains("audio") ||
                                contentType.toLowerCase().contains("pdf")) {
                            MimeBodyPart mbp = (MimeBodyPart) bp;
                            messageToRender.addAttachment(mbp);
                        } else if (bp.getContentType().contains("multipart")) {
                            Multipart mp2 = (Multipart) bp.getContent();
                            for (int j = mp2.getCount() - 1; j >= 0; j--) {
                                BodyPart bp2 = mp2.getBodyPart(i);
                                if ((bp2.getContentType().contains("TEXT/HTML") || bp2.getContentType().contains("TEXT/PLAIN"))) {
                                    sb.append(bp2.getContent().toString());
                                }
                            }
                        }
                    }
                }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Only call once the message is loaded!!
     * handle the info about attachments
     */
    private void loadMessage() {
        messageRendererEngine.loadContent(sb.toString());
    }

}
