/*******************************************************************************
 * Copyright 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.sakaiproject.kernel.messaging.email;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kernel.api.email.Attachment;
import org.sakaiproject.kernel.api.email.ContentType;
import org.sakaiproject.kernel.api.email.EmailAddress;
import org.sakaiproject.kernel.api.email.EmailMessage;
import org.sakaiproject.kernel.api.email.EmailAddress.RcptType;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

/**
 *
 */
public class EmailMessageListener implements MessageListener {
  private static final Log log = LogFactory.getLog(EmailMessageListener.class);

  /**
   * Testing variable to enable/disable the calling of Transport.send
   */
  private boolean allowTransport = true;
  private final javax.mail.Session session;

  @Inject
  public EmailMessageListener(javax.mail.Session session) {
    this.session = session;
  }

  protected void setAllowTransport(boolean allowTransport) {
    this.allowTransport = allowTransport;
  }

  /**
   * {@inheritDoc}
   *
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  public void onMessage(Message jmsMsg) {
    if (jmsMsg instanceof ObjectMessage) {
      ObjectMessage objMsg = (ObjectMessage) jmsMsg;
      try {
        // get the email message and break out the parts
        EmailMessage email = (EmailMessage) objMsg.getObject();
        handleMessage(email);
      } catch (JMSException e) {
        // log for now. We need to return a message to the sender that something
        // died while processing this message
        log.error(e.getMessage(), e);
      } catch (AddressException e) {
        log.error(e.getMessage(), e);
      } catch (UnsupportedEncodingException e) {
        log.error(e.getMessage(), e);
      } catch (SendFailedException e) {
        // this can also be caught by MessagingException
        // caught here because this exception reveals if the message could not
        // be sent to some or any of the recipients.
        log.error(e.getMessage(), e);
      } catch (MessagingException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public void handleMessage(EmailMessage email) throws AddressException,
      UnsupportedEncodingException, SendFailedException, MessagingException {
    EmailAddress fromAddress = email.getFrom();
    if (fromAddress == null) {
      throw new MessagingException("Unable to send without a 'from' address.");
    }

    String content = email.getBody();
    String charset = email.getCharacterSet();
    String format = email.getFormat();
    List<Attachment> attachments = email.getAttachments();

    // build the content type
    String contentType = email.getContentType();
    if (contentType != null) {
      if (charset != null && charset.trim().length() != 0) {
        contentType += "; charset=" + charset;
      }
    }

    // message format is only used when content type is text/plain as
    // specified in the rfc
    if (ContentType.TEXT_PLAIN.equals(charset) && format != null
        && format.trim().length() != 0) {
      contentType += "; format=" + format;
    }

    // transform to a MimeMessage
    ArrayList<EmailAddress> invalids = new ArrayList<EmailAddress>();

    // convert and validate the 'from' address
    InternetAddress from = new InternetAddress(fromAddress.getAddress(), true);
    from.setPersonal(fromAddress.getPersonal());

    // convert and validate reply to addresses
    InternetAddress[] replyTo = emails2Internets(email.getReplyTo(), invalids);

    // convert and validate the 'to' addresses
    InternetAddress[] to = emails2Internets(email.getRecipients(RcptType.TO),
        invalids);

    // convert and validate 'cc' addresses
    InternetAddress[] cc = emails2Internets(email.getRecipients(RcptType.CC),
        invalids);

    // convert and validate 'bcc' addresses
    InternetAddress[] bcc = emails2Internets(email.getRecipients(RcptType.BCC),
        invalids);

    // convert and validate actual email addresses
    InternetAddress[] actual = emails2Internets(email
        .getRecipients(RcptType.ACTUAL), invalids);

    int totalRcpts = to.length + cc.length + bcc.length;
    if (totalRcpts == 0 && actual.length == 0) {
      throw new MessagingException("No recipients to send to.");
    }

    MimeMessage mimeMsg = new MimeMessage(session);
    mimeMsg.setFrom(from);
    mimeMsg.setReplyTo(replyTo);
    mimeMsg.setRecipients(RecipientType.TO, to);
    mimeMsg.setRecipients(RecipientType.CC, cc);
    mimeMsg.setRecipients(RecipientType.BCC, bcc);

    if (attachments != null && attachments.size() > 0) {
      setContent(mimeMsg, content, charset, contentType);
    } else {
      // create a multipart container
      Multipart multipart = new MimeMultipart();

      // create a body part for the message text
      MimeBodyPart msgBodyPart = new MimeBodyPart();
      setContent(msgBodyPart, content, charset, contentType);

      // add the message part to the container
      multipart.addBodyPart(msgBodyPart);

      // add attachments
      if (attachments != null) {
        for (Attachment attachment : attachments) {
          MimeBodyPart attachPart = createAttachmentPart(attachment);
          multipart.addBodyPart(attachPart);
        }
      }

      // set the multipart container as the content of the message
      mimeMsg.setContent(multipart);
    }

    // add in any additional headers
    Map<String, String> headers = email.getHeaders();
    if (headers != null && !headers.isEmpty()) {
      for (Entry<String, String> header : headers.entrySet()) {
        mimeMsg.setHeader(header.getKey(), header.getValue());
      }
    }

    if (allowTransport) {
      // send
      if (actual.length == 0) {
        Transport.send(mimeMsg);
      } else {
        Transport.send(mimeMsg, actual);
      }
    }
  }

  private void setContent(MimePart mimePart, String content, String charset,
      String contentType) throws MessagingException {
    if (charset == null && contentType == null) {
      mimePart.setText(content);
    } else if (contentType == null) {
      mimePart.setText(content, charset);
    } else {
      mimePart.setContent(content, contentType);
    }
  }

  /**
   * Attaches a file as a body part to the multipart message
   *
   * @param multipart
   * @param attachment
   * @throws MessagingException
   */
  private MimeBodyPart createAttachmentPart(Attachment attachment)
      throws MessagingException {
    FileDataSource source = new FileDataSource(attachment.getFile());
    MimeBodyPart attachPart = new MimeBodyPart();
    attachPart.setDataHandler(new DataHandler(source));
    attachPart.setFileName(attachment.getFile().getPath());
    return attachPart;
  }

  /**
   * Converts a {@link java.util.List} of {@link EmailAddress} to
   * {@link javax.mail.internet.InternetAddress}.
   *
   * @param emails
   * @return Array will be the same size as the list with converted addresses.
   *         If list is null, the array returned will be 0 length (non-null).
   * @throws AddressException
   * @throws UnsupportedEncodingException
   */
  protected InternetAddress[] emails2Internets(List<EmailAddress> emails,
      List<EmailAddress> invalids) {
    // set the default return value
    InternetAddress[] addrs = new InternetAddress[0];

    if (emails != null && !emails.isEmpty()) {
      ArrayList<InternetAddress> laddrs = new ArrayList<InternetAddress>();
      for (int i = 0; i < emails.size(); i++) {
        EmailAddress email = emails.get(i);
        try {
          InternetAddress ia = new InternetAddress(email.getAddress(), true);
          ia.setPersonal(email.getPersonal());
          laddrs.add(ia);
        } catch (AddressException e) {
          invalids.add(email);
        } catch (UnsupportedEncodingException e) {
          invalids.add(email);
        }
      }
      if (!laddrs.isEmpty()) {
        addrs = laddrs.toArray(addrs);
      }
    }

    return addrs;
  }
}
