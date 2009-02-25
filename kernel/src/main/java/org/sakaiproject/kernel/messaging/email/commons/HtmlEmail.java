package org.sakaiproject.kernel.messaging.email.commons;

import com.google.inject.Inject;

import org.sakaiproject.kernel.api.messaging.MessagingException;
import org.sakaiproject.kernel.messaging.email.EmailMessagingService;

import java.io.Serializable;

import javax.jms.JMSException;

public class HtmlEmail extends org.apache.commons.mail.HtmlEmail implements
    Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2655080957685664296L;
  private transient EmailMessagingService messagingService;

  /**
   * 
   */
  @Inject
  public HtmlEmail(EmailMessagingService messagingService) {
    this.messagingService = messagingService;
  }

  /**
   * Does the work of actually sending the email.
   * 
   * @exception MessagingException
   *              if there was an error.
   * 
   * @return - the message id
   */
  @Override
  public String send() throws MessagingException {
    String messageId;
    try {
      messageId = messagingService.send(this);
    } catch (JMSException e) {
      throw new MessagingException(e);
    }

    return messageId;
  }

}