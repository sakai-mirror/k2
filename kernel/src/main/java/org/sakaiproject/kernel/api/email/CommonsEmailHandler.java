package org.sakaiproject.kernel.api.email;

import javax.jms.JMSException;
import org.sakaiproject.kernel.api.messaging.MessagingException;

public interface CommonsEmailHandler {

	public String send(org.apache.commons.mail.Email email) throws MessagingException,
			JMSException;

}
