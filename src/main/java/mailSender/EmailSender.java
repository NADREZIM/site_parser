package mailSender;

import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Created by user on 1/11/17.
 */
public class EmailSender {
    private static final Logger logger = Logger.getLogger(EmailSender.class);
    static final String username = "artem.borisov260@gmail.com";
    static final String password = "12345bor";
    String fileName="report.xls";
    String pathToFile="src\\main\\java\\reportFolder\\report.xls";

    public void sendMail() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("artem.borisov260@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("nadrezim@mail.ru,iryna@testmatick.com"));
            message.setSubject("LinkedIn Companies Info");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            DataSource source = new FileDataSource(pathToFile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            logger.info("mail successfully sent");

        } catch (MessagingException e) {
            logger.error("Exception in 'sendMail()' method " + e);
        }

    }
}
