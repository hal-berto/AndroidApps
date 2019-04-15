package it.androidapp.secretsanta.mail;

public class MailText {

    static final String SMTP_USERNAME = "pagani.alberto@gmail.com";
    static final String SMTP_PASSWORD = "menodue";
    static final String HOST = "smtp.gmail.com";
    static final int PORT = 587;

    public static String MAIL_SUBJECT = "Secret santa del: {EVENT_DATE}";
    public static String MAIL_BODY = "<h1>Ciao {PARTICIPANT_FIRST_NAME}</h1>" +
            "<p>Il destinatario del tuo regalo è: {RECIPIENT_FIRST_NAME} {RECIPIENT_LAST_NAME}<p>";
}
