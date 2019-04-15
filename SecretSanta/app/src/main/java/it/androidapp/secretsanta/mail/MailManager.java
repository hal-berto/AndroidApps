package it.androidapp.secretsanta.mail;

import android.content.Context;
import android.os.StrictMode;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import it.androidapp.secretsanta.database.AppDatabase;
import it.androidapp.secretsanta.database.DatabaseHandler;
import it.androidapp.secretsanta.database.entity.*;
import it.androidapp.secretsanta.util.DateConverterUtil;

public class MailManager {

    private Integer eventId;
    private Context context;

    public MailManager(Context cw, Integer eventId){
        this.eventId = eventId;
        this.context = cw;
    }

    public void sendInvitationEmail() throws Exception {
        AppDatabase database = DatabaseHandler.getDatabase(context);
        Event event = database.eventDao().getById(eventId);
        List<EventResult> eventResultList = database.eventResultDao().getAllByEvent(eventId);
        for(EventResult currEventResult : eventResultList){
            Participant participantFrom = database.participantDao().getById(currEventResult.getIdParticipantFrom());
            Participant participantTo = database.participantDao().getById(currEventResult.getIdParticipantTo());
            try {
                processEventResult(event, participantFrom, participantTo);
            }catch(Exception e){
                throw e;
            }
        }

    }

    public void processEventResult(Event event, final Participant participantFrom, final Participant participantTo) throws Exception {
        Map<String, String> mailDataMap = fillTemplateMap(event, participantFrom, participantTo);
        final String mailSubject = StrSubstitutor.replace(MailText.MAIL_SUBJECT, mailDataMap,"{","}");
        final String mailBody = StrSubstitutor.replace(MailText.MAIL_BODY, mailDataMap,"{","}");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            sendMail(participantFrom, participantTo, mailSubject, mailBody);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw e1;
        }

        /*
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    sendMail(participantFrom, participantTo, mailSubject, mailBody);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new RuntimeException(e1);
                }
            }
        }).start();
        */
    }

    private Map<String, String> fillTemplateMap(Event event, Participant participantFrom, Participant participantTo){
        Map<String,String> map = new HashMap<String, String>();
        map.put("EVENT_NAME", event.getName());
        map.put("EVENT_DATE", DateConverterUtil.dateToString(event.getDate()));
        map.put("EVENT_LOCATION", event.getLocation());
        map.put("EVENT_MIN_AMOUNT", String.valueOf(event.getMinimumAmount()));
        map.put("EVENT_MAX_AMOUNT", String.valueOf(event.getMaximumAmount()));

        map.put("PARTICIPANT_FIRST_NAME", participantFrom.getFirstName());
        map.put("PARTICIPANT_LAST_NAME", participantFrom.getLastName());

        map.put("RECIPIENT_FIRST_NAME", participantTo.getFirstName());
        map.put("RECIPIENT_LAST_NAME", participantTo.getLastName());

        return map;
    }

    private void sendMail (Participant participantFrom, Participant participantTo, String mailSubject, String mailBody)
            throws Exception {
        // Create a Properties object to contain connection configuration information.
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", MailText.PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(participantTo.getEmail(), participantTo.getFirstName() + " " + participantTo.getLastName()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(participantFrom.getEmail()));
        msg.setSubject(mailSubject);

        msg.setContent(mailBody, "text/html");

        // Create a transport.
        Transport transport = session.getTransport();

        // Send the message.
        try
        {
            System.out.println("Sending...");
            transport.connect(MailText.HOST, MailText.SMTP_USERNAME, MailText.SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            throw ex;
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }
}
