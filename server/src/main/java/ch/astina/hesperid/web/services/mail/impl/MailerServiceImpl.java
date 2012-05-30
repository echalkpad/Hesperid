////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.web.services.mail.impl;

import ch.astina.hesperid.exceptions.IncompleteMailException;
import ch.astina.hesperid.mails.SmtpCredentials;
import ch.astina.hesperid.model.mail.HtmlMailMessage;
import ch.astina.hesperid.model.mail.MailAttachment;
import ch.astina.hesperid.model.mail.MailMessage;
import ch.astina.hesperid.model.mail.TextMailMessage;
import ch.astina.hesperid.web.services.mail.MailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class MailerServiceImpl implements MailerService
{
    private Authenticator authenticator;
    private String recipientDomainRedirect; // if set, all mails are redirected to this domain
    private Properties mailProperties;
    private static Logger logger = LoggerFactory.getLogger(MailerServiceImpl.class);

    public MailerServiceImpl(SmtpCredentials credentials, Map<String, String> configuration)
    {
        authenticator = credentials.getAuthenticator();

        recipientDomainRedirect = configuration.get("recipient.domain.redirect");

        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", credentials.getHost());
        mailProperties.put("mail.smtp.auth", credentials.isAuthenticationRequired());
        mailProperties.put("mail.smtp.starttls.enable", credentials.isStartTls());
        mailProperties.put("smtpssl.enable", credentials.isSmtpSsl());
    }

    @Override
    public void sendTextMail(TextMailMessage mailMessage) throws MessagingException
    {
        Session session = Session.getInstance(mailProperties, authenticator);
        try {
            Message message = prepareMessage(session, mailMessage);
            completeTextMessage(message, mailMessage);
            Transport.send(message);
        } catch (Exception ex) {
            logger.error("The text message could not be sent", ex);
            throw new MessagingException(ex.getCause() + " > " + ex.getMessage(), ex);
        }
    }

    @Override
    public void sendHtmlMail(HtmlMailMessage mailMessage) throws MessagingException
    {
        Session session = Session.getInstance(mailProperties, authenticator);
        try {
            Message message = prepareMessage(session, mailMessage);
            completeHtmlMessage(message, mailMessage);
            Transport.send(message);
        } catch (Exception ex) {
            logger.error("The text message could not be sent", ex);
            throw new MessagingException(ex.getCause() + " > " + ex.getMessage(), ex);
        }
    }

    /**
     * Completes a prepared Message (sets body and content type) with the data from the given
     * TextMailMessage
     *
     * @throws IOException
     */
    private void completeTextMessage(Message message, TextMailMessage mailMessage)
            throws MessagingException, IOException
    {
        if (!mailMessage.hasAttachments()) {
            message.setContent(mailMessage.getBody(), mailMessage.getContentType());
            return;
        }

        // if we have attachments, we have to create a multipart message

        MimeMultipart mp = new MimeMultipart();
        BodyPart textPart = new MimeBodyPart();
        textPart.setContent(mailMessage.getBody(), mailMessage.getContentType());
        mp.addBodyPart(textPart);

        addAttachmentParts(mailMessage, mp);

        message.setContent(mp);
    }

    /**
     * Completes a prepared Message (creates mime body parts) with the data from the given
     * HtmlMailMessage
     *
     * @throws MessagingException
     * @throws IOException
     */
    private void completeHtmlMessage(Message message, HtmlMailMessage mailMessage)
            throws MessagingException, IOException
    {
        MimeMultipart altPart = new MimeMultipart("alternative");

        // text part
        if (mailMessage.getAltBody() != null) {
            BodyPart textPart = new MimeBodyPart();
            textPart.setContent(mailMessage.getAltBody(), mailMessage.getAltContentType());
            altPart.addBodyPart(textPart);
        }

        // html part
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(mailMessage.getHtmlBody(), mailMessage.getHtmlContentType());
        altPart.addBodyPart(htmlPart);

        MimeMultipart mp = new MimeMultipart("mixed");
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(altPart);
        mp.addBodyPart(wrap);

        addAttachmentParts(mailMessage, mp);

        message.setContent(mp);
    }

    private void addAttachmentParts(MailMessage mailMessage, MimeMultipart mp)
            throws MessagingException, IOException
    {
        BodyPart attachmentPart;
        String filename;
        for (MailAttachment attachment : mailMessage.getAttachments()) {
            filename = attachment.getFilename();
            attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(attachment.getDataSource()));
            attachmentPart.setFileName(filename);
            attachmentPart.addHeader("Content-Type",
                    String.format("%s; name=\"%s\"", attachment.getMimeType(), filename));
            mp.addBodyPart(attachmentPart);
        }
    }

    /**
     * Prepares a Message object based on the given MailMessage object
     */
    private Message prepareMessage(Session session, MailMessage mailMessage)
            throws IncompleteMailException, MessagingException
    {
        logger.info("Creating email \"{}\" for {}", mailMessage.getSubject(),
                mailMessage.getToRecipientsArray());

        Address sender = mailMessage.getSender();
        String subject = mailMessage.getSubject();

        if (sender == null) {
            throw new IncompleteMailException("No sender given");
        }

        Message message = new MimeMessage(session);

        // sender
        message.setFrom(sender);

        // recipients
        message.setRecipients(Message.RecipientType.TO,
                checkRecipients(mailMessage.getToRecipientsArray()));
        message.setRecipients(Message.RecipientType.CC,
                checkRecipients(mailMessage.getCcRecipientsArray()));
        message.setRecipients(Message.RecipientType.BCC,
                checkRecipients(mailMessage.getBccRecipientsArray()));

        message.setSubject(subject);
        message.setSentDate(new Date());

        return message;
    }

    private InternetAddress[] checkRecipients(InternetAddress[] recipients)
    {
        if (recipientDomainRedirect == null) {
            // no redirect domain configured
            return recipients;
        }

        for (int i = 0; i < recipients.length; i++) {
            InternetAddress recipient = recipients[i];
            String oldAddress = "" + recipient.getAddress();
            recipient.setAddress(recipient.getAddress().replaceFirst("@.+$",
                    "@" + recipientDomainRedirect));
            logger.info("Redirecting {} to {}", oldAddress, recipient.getAddress());
        }
        return recipients;
    }
}
