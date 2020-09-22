package com.autoperfekt.autoperfekt_bus.Email;

import java.security.Security;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator {
    //private String mailhost = "smtp.gmail.com";
    private String mailhost = "smtp.poczta.onet.pl";
    private String user;
    private String password;
    private Session session;

    private List<String> storageFilesPathsList;
    private List<String> invoiceStorageFilesPathsList;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //props.put("mail.smtp.from", "oskail@wp.pl");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);


        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        /*Do wykorzystania jezeli chcemy miec mozliwosc wysylania maili do wielu osob oddzielonych przecinkiem
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));*/

        Transport.send(message);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients, List<String> storageFilesPathsList, List<String> invoiceStorageFilesPathsList) throws Exception {

        this.storageFilesPathsList=storageFilesPathsList;
        this.invoiceStorageFilesPathsList=invoiceStorageFilesPathsList;

        MimeMessage message = new MimeMessage(session);                                     //ustawienia maila
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));


        Multipart multipart = new MimeMultipart(); //adapter do zlaczenia czesci maila


        BodyPart messageBodyPartBody = new MimeBodyPart();                                                      //pierwsza część maila - dane z formularza
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        messageBodyPartBody.setDataHandler(handler);
        multipart.addBodyPart(messageBodyPartBody);


        for(int i=0; i< storageFilesPathsList.size();i++)       //dodawanie wczytanych plikow
        {
            BodyPart messageBodyPartAttachment = new MimeBodyPart();                                                //druga czesc - zalaczniki - zdjecie
            DataSource source = new FileDataSource(storageFilesPathsList.get(i));
            messageBodyPartAttachment.setDataHandler(new DataHandler(source));

            //if(storageFilesPathsList.get(i).contains(subject.substring(subject.lastIndexOf(":")+1)))    //jeżeli nazwa pliku zawiea już numer rejestracyjny
            //{
                messageBodyPartAttachment.setFileName( (storageFilesPathsList.get(i)).substring((storageFilesPathsList.get(i)).lastIndexOf("/")+1));
            //}
            //else
            //{
            //    messageBodyPartAttachment.setFileName( (subject.substring(subject.lastIndexOf(":")+1)) + "_" + (storageFilesPathsList.get(i)).substring((storageFilesPathsList.get(i)).lastIndexOf("/")+1));         //zmiana nazwy zalacznika zeby nie przekazywac sciezki i dodanie nr rejesteacyjnego
            //}

            multipart.addBodyPart(messageBodyPartAttachment);
        }


        for(int i=0; i< invoiceStorageFilesPathsList.size();i++)       //dodawanie wczytanych plikow
        {
            BodyPart messageBodyPartAttachment = new MimeBodyPart();                                                //druga czesc - zalaczniki - zdjecie
            DataSource source = new FileDataSource(invoiceStorageFilesPathsList.get(i));
            messageBodyPartAttachment.setDataHandler(new DataHandler(source));

            messageBodyPartAttachment.setFileName( (invoiceStorageFilesPathsList.get(i)).substring((invoiceStorageFilesPathsList.get(i)).lastIndexOf("/")+1));

            //if(storageFilesPathsList.get(i).contains(subject.substring(subject.lastIndexOf(":")+1)))    //jeżeli nazwa pliku zawiea już numer rejestracyjny
            //{
            //    messageBodyPartAttachment.setFileName( (storageFilesPathsList.get(i)).substring((storageFilesPathsList.get(i)).lastIndexOf("/")+1));
            //}
            //else
            //{
            //    messageBodyPartAttachment.setFileName( (subject.substring(subject.lastIndexOf(":")+1)) + "_" + (storageFilesPathsList.get(i)).substring((storageFilesPathsList.get(i)).lastIndexOf("/")+1));         //zmiana nazwy zalacznika zeby nie przekazywac sciezki i dodanie nr rejesteacyjnego
            //}

            multipart.addBodyPart(messageBodyPartAttachment);
        }


        /*Do wykorzystania jezeli chcemy miec mozliwosc wysylania maili do wielu osob oddzielonych przecinkiem
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));*/


        // Send the complete message parts
        message.setContent(multipart);

        Transport.send(message);
    }
}