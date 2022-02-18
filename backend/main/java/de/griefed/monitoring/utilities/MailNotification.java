/* MIT License
 *
 * Copyright (c) 2021 SUK-IT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.griefed.monitoring.utilities;

import de.griefed.monitoring.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * This class provides the sendMailNotification method to send an email with the passed body to the configured
 * email-recipients.
 * @author Griefed
 */
@Service
public class MailNotification {

    private static final Logger LOG = LogManager.getLogger(MailNotification.class);

    private final ApplicationProperties PROPERTIES;
    private final Message MESSAGE;

    private final boolean mailEnabled;

    /**
     * Constructor responsible for our DI and setting up the email-notification system.
     * @author Griefed
     * @param injectedApplicationProperties Instance of {@link ApplicationProperties}.
     */
    @Autowired
    public MailNotification(ApplicationProperties injectedApplicationProperties) {
        this.PROPERTIES = injectedApplicationProperties;
        Session SESSION = Session.getInstance(PROPERTIES, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        PROPERTIES.getProperty("mail.user", "example@example.com"),
                        PROPERTIES.getProperty("mail.password", "123456"));
            }
        });

        this.MESSAGE = new MimeMessage(SESSION);

        boolean mailing = false;

        try {

            if (
                    PROPERTIES.getProperty("mail.smtp.host").equalsIgnoreCase("smtp.example.com") ||
                    PROPERTIES.getProperty("mail.recipients").equalsIgnoreCase("example@example.com") ||
                    PROPERTIES.getProperty("mail.from").equalsIgnoreCase("monitoring@example.com") ||
                    PROPERTIES.getProperty("mail.user").equalsIgnoreCase("example@example.com") ||
                    PROPERTIES.getProperty("mail.password").equalsIgnoreCase("123456")
            ) {

                LOG.info("Default email-notifications values detected.");

            } else {

                InternetAddress internetAddress = new InternetAddress(PROPERTIES.getProperty("mail.from","monitoring@example.com"));
                this.MESSAGE.setFrom(internetAddress);
                this.MESSAGE.setRecipients(Message.RecipientType.TO, InternetAddress.parse(PROPERTIES.getProperty("mail.recipients","example@example.com")));

                mailing = true;

            }

        } catch (MessagingException ex) {

            LOG.error("Mailing not setup properly.", ex);

        } finally {
            this.mailEnabled = mailing;

            if (this.mailEnabled) {
                LOG.info("Email-notifications enabled.");
            } else {
                LOG.info("Email-notifications disabled.");
            }

        }
    }

    /**
     * Email the configured recipients with the passed subject and body.
     * @author Griefed
     * @param subject String. Subject of the mail to send.
     * @param content String. The content which should make up the mails body.
     * @throws MessagingException Exception thrown if an error occurs sending the email.
     */
    public void sendMailNotification(String subject, String content) throws MessagingException {
        if (this.mailEnabled) {

            Message message = this.MESSAGE;

            message.setSubject(subject);
            message.setContent(content, "text/html");
            message.setSentDate(new Date());

            Transport.send(message);

        }
    }
}
