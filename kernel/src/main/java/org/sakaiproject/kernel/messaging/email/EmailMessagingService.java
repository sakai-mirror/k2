package org.sakaiproject.kernel.messaging.email;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.Email;
import org.sakaiproject.kernel.api.email.CommonsEmailHandler;
import org.sakaiproject.kernel.api.messaging.MessagingException;
import org.sakaiproject.kernel.messaging.JmsMessagingService;
import org.sakaiproject.kernel.messaging.email.commons.HtmlEmail;
import org.sakaiproject.kernel.messaging.email.commons.SimpleEmail;

import com.google.inject.Inject;
import com.google.inject.name.Named;


public class EmailMessagingService extends JmsMessagingService implements
		CommonsEmailHandler {

	private static final Log LOG = LogFactory.getLog(EmailMessagingService.class);
	public static final String EMAIL_JSMTYPE = "kernel.jms.email";
	public static final String EMAIL_QUEUE_NAME = "kernel.email";
	private Long clientId = new Long(1L); ///always use the synchronized getters and setters
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	
	synchronized
	public long getClientId() {
		return clientId;
	}

	synchronized
	public void setClientId(long id) {
		this.clientId = id;
	}

	synchronized
	private String getNextId() {
		setClientId(getClientId()+1);
		return "" + getClientId();
	}
	
	/*
	 * TODO may want to take parameters for num of connections and sessions per connection
	 * 
	 */
	@Inject
	public EmailMessagingService(@Named(JmsMessagingService.PROP_ACTIVEMQ_BROKER_URL) String brokerUrl) {
		super(brokerUrl);
		try {
			Connection conn = connectionFactory.createTopicConnection();/// prob want to use username,pw here
			conn.setClientID("kernel.email1");
			connections.add(conn);
		} catch (JMSException e) {
			connectionFactory = null;
			try {
				throw e;
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
		
		startConnections();
		createSessions();
		
	}



	private void startConnections() {
		for(Connection conn: connections) {
			try {
				conn.start();
			} catch (JMSException e) {
				try {
					LOG.error("Fail to start connection: " + conn.getClientID());
				} catch (JMSException e1) {
					// TMI
				}
				e.printStackTrace();
			}
		}
		
	}

	private void createSessions() {
		int sessionsPerConnection = 1;
		Session sess = null;
		for (Connection conn: connections) {
			for (int i=0;i<sessionsPerConnection;++i) {
				sess = null;
				try {
					sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
					sessions.put(conn.getClientID(), sess);
				} catch (JMSException e) {
					try {
						LOG.error("Fail to create connection[" + i + "]: " + conn.getClientID());
					} catch (JMSException e1) {
						// TMI
					}
					e.printStackTrace();
				}
			}
		}

	}
	

	public String send(Email email) throws MessagingException, JMSException {
		try {
			email.buildMimeMessage();
		} catch (Exception e) {
			// this is a lossy cast. This would be a commons EmailException
			// this up cast is to keep commons-email out of our direct bindings
			throw new MessagingException(e);
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			email.getMimeMessage().writeTo(os);
		} catch (javax.mail.MessagingException e) {
			throw new MessagingException(e);

		} catch (IOException e) {
			throw new MessagingException(e);
		}

		String content = os.toString();
		Connection conn = connectionFactory.createTopicConnection();
		conn.setClientID(getNextId());
		Session clientSession = 
				conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination emailTopic = clientSession.createTopic(EMAIL_QUEUE_NAME);
		MessageProducer client = clientSession.createProducer(emailTopic);
		ObjectMessage mesg = clientSession.createObjectMessage(content);
		mesg.setJMSType(EMAIL_JSMTYPE);
		client.send(mesg);
		// TODO finish this
		return null;

	}
}
