package c.e.c.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.carrentalapp.BuildConfig;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask {
    private Session session;
    private String email;
    private String subject;
    private String message;

    public SendMail(String email, String subject, String message){
        this.email = email;
        this.subject = subject;
        this.message = message;
        Log.d("SEND_MESSAGE", "send mail created");
    }

    @Override
    protected Object doInBackground(Object... args) {
        Log.d("SEND_MESSAGE", "background job called");
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        Log.d("SEND_MESSAGE", "prop set");
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(BuildConfig.EMAIL_USER, BuildConfig.EMAIL_PASS);
            }
        });
        Log.d("SEND_MESSAGE", "send message session established");
        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(BuildConfig.EMAIL_USER));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setContent(message, "text/html");
            Log.d("SEND_MESSAGE", "message created");
            Transport.send(mm);
            Log.d("SEND_MESSAGE", "send message success");
        }
        catch (MessagingException e) {
            e.printStackTrace();
            Log.d("SEND_MESSAGE", "send message failed: " + e.getMessage());
        }
        // Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
        return null;
    }
}
