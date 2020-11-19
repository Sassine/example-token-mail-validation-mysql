package dev.sassine.token.service;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import dev.sassine.token.model.MailProperties;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Service
public class SendingMailService {

	private static final String SUBJECT_MAIL = "Please verify your email";

	private static final String KEY_MAP_VERIFICATION = "VERIFICATION_URL";

	private static final String TEMPLATE_MAIL_VERIFICATION = "email-verification.ftl";

	@Autowired
	private MailProperties mailProperties;

	@Autowired
	private Configuration templates;

	public void sendVerificationMail(String toEmail, String verificationCode) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException, MessagingException {

		String bodyMail = ofNullable(
				processTemplateIntoString(templates.getTemplate(TEMPLATE_MAIL_VERIFICATION), singletonMap(
						KEY_MAP_VERIFICATION, format("%s%s", mailProperties.getVerificationapi(), verificationCode))))
								.orElse("");

		sendMail(toEmail, SUBJECT_MAIL, bodyMail);
	}

	private void sendMail(String toEmail, String subject, String body) throws MessagingException {
		JavaMailSender mail = getMailSender();
		MimeMessage message = mail.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(toEmail);
		helper.setSubject(subject);
		message.setContent(body, "text/html; charset=utf-8");
		mail.send(message);
	}

	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailProperties.getSmtp().getHost());
		mailSender.setPort(Integer.valueOf(mailProperties.getSmtp().getPort()));

		mailSender.setUsername(mailProperties.getSmtp().getUsername());
		mailSender.setPassword(mailProperties.getSmtp().getPassword());
		
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");
		props.put("mail.from", mailProperties.getFrom());
		return mailSender;
	}
}
