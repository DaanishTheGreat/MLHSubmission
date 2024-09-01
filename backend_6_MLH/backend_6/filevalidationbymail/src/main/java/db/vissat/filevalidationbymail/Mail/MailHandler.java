package db.vissat.filevalidationbymail.Mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.mail.search.SubjectTerm;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.web.multipart.MultipartFile;

import db.vissat.filevalidationbymail.Data.DataHandlerJ;
import db.vissat.filevalidationbymail.Data.Log;
import db.vissat.filevalidationbymail.Data.MongoDBHandler;
import db.vissat.filevalidationbymail.Mail.ReadMailData.ReadMailDataHandler;
import db.vissat.filevalidationbymail.Mail.helper.MultipartfileParser;

public class MailHandler {

    public MailHandler(String Host, String Email, String password, String SubjectFilter, String ImapProtocol, String SmtpHost, String SmtpPort)
    {
        this.Host = Host; this.Email = Email; this.password = password; this.SubjectFilter = SubjectFilter; this.ImapProtocol = ImapProtocol;
        this.SmtpHost = SmtpHost; this.SmtpPort = Integer.parseInt(SmtpPort);
    }

    
    private String Host;
    private String Email;
    private String password;

    private String SubjectFilter;

    private String ImapProtocol;
    private String SmtpHost;
    private int SmtpPort;

    public void GetEmailData(DataHandlerJ DataHandlerJObject, Log LogObject, MongoDBHandler MongoDBHandlerObject, String OrganizationName) {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", ImapProtocol);

        Session session = Session.getInstance(properties);
        try (Store store = session.getStore(ImapProtocol)) {
            store.connect(Host, Email, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            FlagTerm unreadFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages;

            if (SubjectFilter == null || SubjectFilter.isEmpty()) {
                messages = emailFolder.search(unreadFlagTerm);
            } else {
                SubjectTerm subjectTerm = new SubjectTerm(SubjectFilter);
                messages = emailFolder.search(subjectTerm, emailFolder.search(unreadFlagTerm));
            }
            for (Message message : messages) {

                String[] MessageIDList = message.getHeader("Message-ID");
                if(MessageIDList != null && MessageIDList.length > 0)
                {
                    boolean MessageIDExists = ReadMailDataHandler.CheckMessageIDExistsToProcess(MessageIDList[0]);
                    if(MessageIDExists == true)
                    {
                        System.out.println("Email Already Processed for: " + MessageIDList[0]);
                        return;
                    }
                }

                String emailContent = getTextFromMessage(message);
                String FileName = "";

                boolean SendSuccessReplyEmail = false;

                Object content = message.getContent();
                if (content instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) content;
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)
                                && (bodyPart.getFileName().endsWith(".xlsx") || bodyPart.getFileName().endsWith(".csv"))) {
                            String fileName = bodyPart.getFileName();
                            InputStream inputStream = bodyPart.getInputStream();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                            }
                            byte[] fileBytes = byteArrayOutputStream.toByteArray();

                            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileBytes);
                            FileName = bodyPart.getFileName();
                            SendSuccessReplyEmail = ValidateAndSave(emailContent, multipartFile, DataHandlerJObject, LogObject, MongoDBHandlerObject, OrganizationName);
                            UpdateLastUploadedUploadFrequency(message, fileName, DataHandlerJObject);
                        }
                    }
                }
                if(SendSuccessReplyEmail == true)
                {
                    SendReplyEmailSuccess(message, FileName);
                }
                else 
                {
                    System.out.println(MultipartFileParserObject.GetInvalidFormat() + " Invalid Format");
                    SendReplyEmailUnsuccessful(message, FileName, MultipartFileParserObject.GetInvalidFormat());
                }
                message.setFlag(Flags.Flag.SEEN, true);
            }
            emailFolder.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void UpdateLastUploadedUploadFrequency(Message MessageObject, String FileNameSubtype, DataHandlerJ DataHandlerJObject) throws MessagingException
    {
        Address[] FromAddresses = MessageObject.getFrom();
        if(FromAddresses != null && FromAddresses.length > 0)
        {
            InternetAddress FromAddress = (InternetAddress) FromAddresses[0];
            String SenderEmail = FromAddress.getAddress();

            DataHandlerJObject.UpdateLastUploadedUploadFrequency(SenderEmail, FileNameSubtype);
        }
        else
        {
            System.out.println("Sender Email is empty, unable to update LastUploadedUploadFrequency");
        }
    }

    public void NotifyContactsForExpiredFileUpload(DataHandlerJ DataHandlerJObject)
    {
        List<String> UploadFrequencyData = DataHandlerJObject.GetUploadFrequencyData(true);

        for(String UploadFrequencyElement : UploadFrequencyData)
        {
            //System.out.println("Upload Frequency Data: " + UploadFrequencyElement + " " + CurrentUnixTimeStamp);
            String SendToEmailData = UploadFrequencyElement.split(":%%")[1].split(":%")[2];
            List<String> SendToEmailList = Arrays.asList(SendToEmailData.split(":"));
            
            for(String SendToEmail : SendToEmailList)
            {
                String Frequency = UploadFrequencyElement.split(":%%")[1].split(":%")[0];
                String LastUploadedDate = UploadFrequencyElement.split(":%%")[1].split(":%")[1];

                String FileSubtype = UploadFrequencyElement.split(":%%")[0].split(":%")[1];

                try {
                    SendCustomTextEmail(SendToEmail, "Email Expected","Expecting Upload For: " + FileSubtype + " Please upload the required file for: " + FileSubtype + " to this email address, This file is expected at a frequency of: " + Frequency + " File Last Recieved/UploadFrequency created on " + LastUploadedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void SendCustomTextEmail(String SendToEmail, String Subject, String Content) throws MessagingException
    {
        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.host", SmtpHost);
        smtpProperties.put("mail.smtp.port", Integer.toString(SmtpPort));
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");

        Session smtpSession = Session.getInstance(smtpProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Email, password);
            }
        });

        MimeMessage EmailMessage = new MimeMessage(smtpSession);
        EmailMessage.setFrom(new InternetAddress(Email));
        EmailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(SendToEmail));
        EmailMessage.setSubject(Subject);
        EmailMessage.setText(Content);

        Transport transport = smtpSession.getTransport("smtp");
        try {
            transport.connect("smtp.gmail.com", Email, password);
            transport.sendMessage(EmailMessage, EmailMessage.getAllRecipients());
        } finally {
            System.out.println("Custom Email Message Sent to: " + SendToEmail);
            transport.close();
        }
    }


/* delete


    private String Host = "outlook.office365.com";
    private String Email = "daanishbawan@outlook.com";
    private String password = "NMVQ5NV5F4RDV25P";

    private String SubjectFilter = "FileValidation";

    public void GetEmailData() {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        Session session = Session.getInstance(properties);
        try (Store store = session.getStore("imaps")) {
            store.connect(Host, Email, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            FlagTerm unreadFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SubjectTerm subjectTerm = new SubjectTerm(SubjectFilter);
            Message[] messages = emailFolder.search(subjectTerm, emailFolder.search(unreadFlagTerm));

            for (Message message : messages) {
                String emailContent = getTextFromMessage(message);

                Object content = message.getContent();
                if (content instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) content;
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)
                                && bodyPart.getFileName().endsWith(".xlsx")) {
                            String fileName = bodyPart.getFileName();
                            InputStream inputStream = bodyPart.getInputStream();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                            }
                            byte[] fileBytes = byteArrayOutputStream.toByteArray();

                            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileBytes);
                            ValidateAndSave(emailContent, multipartFile);
                        }
                    }
                }
                message.setFlag(Flags.Flag.SEEN, true);
            }
            emailFolder.close(false);
            store.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }




 */

    private void SendReplyEmailSuccess(Message OriginalMessage, String FileName) throws MessagingException, IOException
    {
        String replyTextMain = "Your File ["+FileName+"] have been received and processed.";
        String replyText = "could not be processed Invalid Formats:";
        String replyText2 = "have been received and processed.";

        if(OriginalMessage.isMimeType("text/plain"))
        {
            System.out.println("Content: " + OriginalMessage.getContent().toString());
            if(OriginalMessage.getContent().toString().contains(replyText) || OriginalMessage.getContent().toString().contains(replyText2))
            {
                return;
            }
        }

        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.host", SmtpHost);
        smtpProperties.put("mail.smtp.port", Integer.toString(SmtpPort));
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");

        Session smtpSession = Session.getInstance(smtpProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Email, password);
            }
        });

        MimeMessage replyMessage = (MimeMessage) OriginalMessage.reply(false);
        replyMessage.setFrom(new InternetAddress(Email));
        replyMessage.setText(replyTextMain);

        Transport transport = smtpSession.getTransport("smtp");
        try {
            transport.connect("smtp.gmail.com", Email, password);
            transport.sendMessage(replyMessage, replyMessage.getAllRecipients());
        } finally {
            System.out.println("Reply Sent");
            transport.close();
        }
    }

    private void SendReplyEmailUnsuccessful(Message OriginalMessage, String FileName, String ErrorString) throws MessagingException, IOException
    {
        String replyTextMain = "Your File ["+FileName+"] could not be processed Invalid Formats: \n";
        String replyText = "could not be processed Invalid Formats:";
        String replyText2 = "have been received and processed.";

        if(OriginalMessage.isMimeType("text/plain"))
        {
            if(OriginalMessage.getContent().toString().contains(replyText) || OriginalMessage.getContent().toString().contains(replyText2))
            {
                return;
            }
        }

        if(ErrorString.startsWith(":%"))
        {
            ErrorString = ErrorString.substring(2);
        }

        String[] InvalidFormatObjects = ErrorString.split(":%");

        for(String InvalidFormats : InvalidFormatObjects)
        {
            String ErrorType = InvalidFormats.split(":")[0];
            int Row = Integer.parseInt(InvalidFormats.split(":")[1]) + 1;
            int Column = Integer.parseInt(InvalidFormats.split(":")[2]) + 1;
            replyTextMain += "Error Type: " + ErrorType + " Location(Row, Col): (" + Row + "," + Column + ") \n";
        }


        if(OriginalMessage.isMimeType("text/plain"))
        {
            if(OriginalMessage.getContent().toString().contains(replyTextMain))
            {
                return;
            }
        }

        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.host", SmtpHost);
        smtpProperties.put("mail.smtp.port", Integer.toString(SmtpPort));
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");

        Session smtpSession = Session.getInstance(smtpProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Email, password);
            }
        });

        MimeMessage replyMessage = (MimeMessage) OriginalMessage.reply(false);
        replyMessage.setFrom(new InternetAddress(Email));
        replyMessage.setText(replyTextMain);

        Transport transport = smtpSession.getTransport("smtp");
        try {
            transport.connect("smtp.gmail.com", Email, password);
            transport.sendMessage(replyMessage, replyMessage.getAllRecipients());
        } finally {
            System.out.println("Reply Sent");
            transport.close();
        }
    }


    private String getTextFromMessage(Message message) throws MessagingException, IOException
    {
        String MessageData = "";
        if(message.isMimeType("text/plain"))
        {
            MessageData = message.getContent().toString();
        }
        else if(message.isMimeType("multipart/*"))
        {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            MessageData = getTextFromMimeMultipart(mimeMultipart);
        }
        return MessageData; 
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException
    {
        StringBuilder MessageData = new StringBuilder();
        int count = mimeMultipart.getCount();
        for(int i = 0; i < count; i++)
        {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if(bodyPart.isMimeType("text/plain"))
            {
                MessageData.append(bodyPart.getContent());
            }
            else if(bodyPart.isMimeType("text/html"))
            {
                String html  = (String) bodyPart.getContentType();
                MessageData.append(org.jsoup.Jsoup.parse(html).text());
            }
            else if(bodyPart.getContent() instanceof MimeMultipart)
            {
                MessageData.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return MessageData.toString();
    }

    MultipartfileParser MultipartFileParserObject;
    private boolean ValidateAndSave(String EmailContent, MultipartFile FileObject, DataHandlerJ DataHandlerJObject, Log LogObject, MongoDBHandler MongoDBHandlerObject, String OrganizationName)
    {
        MultipartfileParser MultipartFileParserObject = new MultipartfileParser(FileObject, DataHandlerJObject, LogObject);
        boolean IsValidFormat = MultipartFileParserObject.VerifyFormat();

        this.MultipartFileParserObject = MultipartFileParserObject;

        if(IsValidFormat == true)
        {
            String StoredFilePath = MultipartFileParserObject.StoreFile();

            if(!StoredFilePath.equals(""))
            {
                String FileTypeAndSubtype = MultipartFileParserObject.GetFileTypeAndSubtype();
                String FileTypeName = FileTypeAndSubtype.split(":%")[0];
                String FileSubtypeName = FileTypeAndSubtype.split(":%")[1];
                MongoDBHandlerObject.UpdateFileUploadDetails(StoredFilePath, FileSubtypeName, FileTypeName, OrganizationName);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        //System.out.println("Result_Email: " + Result);
    }

}
