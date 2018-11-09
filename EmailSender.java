import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

public class EmailSender {
    private String fromName;
    private String fromGmailEmail;
    private String gmailPassword;

    public EmailSender(String fromGmailEmail, String gmailPassword, String fromName) {
        this.fromGmailEmail = fromGmailEmail;
        this.gmailPassword = gmailPassword;
        this.fromName = fromName;
    }

    public void sendEmail(String toEmail, String subject, String message) {
        Email email = EmailBuilder.startingBlank()
                .from(fromName, fromGmailEmail)
                .to(toEmail)
                .withSubject(subject)
                .withPlainText(message)
                .buildEmail();

        String host = "smtp.gmail.com";
        int port = 587;

        Mailer m = MailerBuilder.withSMTPServer(
                host, port, fromGmailEmail, gmailPassword)
                .withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
        m.sendMail(email);
    }
}
