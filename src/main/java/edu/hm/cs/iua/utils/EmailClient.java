package edu.hm.cs.iua.utils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailClient {

    private String userName;

    private String password;

    private String server;

    private String port;

    public EmailClient(String userName, String password, String server, String port) {
        this.userName = userName;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    public void sendMail(String receiver, String subject, String text)
            throws MessagingException {

        final Properties properties = new Properties();
        properties.put("mail.smtp.host", server);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("Mail.smtp.socketFactory.fallback", "false");

        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        final MimeMessage message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        message.setFrom(new InternetAddress(userName));
        message.setSubject(subject);
        message.setContent(text, "text/html; charset=utf-8");

        Transport.send(message);
    }

}
