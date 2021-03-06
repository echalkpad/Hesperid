import java.util.Map;

import ch.astina.hesperid.groovy.ParameterGatherer;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class SimpleSmtpServerObserver implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		final String user = parameters.get("user");
		final String password = parameters.get("password");
		final String smtpHost = parameters.get("smtpHost");
		final String senderAddress = parameters.get("senderAddress");
		final String recipientAddress = parameters.get("recipientAddress");
		final String subject = "Monitoring Test Mail";
		final String text = "Monitoring Test Mail";
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.auth", "true");
		
		Authenticator auth = new MyAuthenticator(user, password);
		
		Session session = Session.getInstance(properties, auth);
		
		// New Mime message
		Message msg = new MimeMessage(session);
		
		// Set Recipients and Sender
		msg.setFrom(new InternetAddress(senderAddress));
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recipientAddress, false));
		
		// Set Subject and Text
		msg.setSubject(subject);
		msg.setText(text);
		
		msg.setSentDate(new Date());
		
		// Send 
		Transport.send(msg);
		
		return "success";
	}
}

class MyAuthenticator extends Authenticator
{
	String user;
	String pass;
	
	public MyAuthenticator(String user, String pass)
	{
		this.user = user;
		this.pass = pass;
	}
	
	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(user, pass);
	}
}
