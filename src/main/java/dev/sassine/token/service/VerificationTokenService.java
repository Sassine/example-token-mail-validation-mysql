package dev.sassine.token.service;

import static dev.sassine.token.model.VerificationToken.STATUS_VERIFIED;
import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.sassine.token.model.User;
import dev.sassine.token.model.VerificationToken;
import dev.sassine.token.repository.UserRepository;
import dev.sassine.token.repository.VerificationTokenRepository;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Service
public class VerificationTokenService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@Autowired
	private SendingMailService sendingMailService;

	public void createVerification(String email) throws TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException, MessagingException {
		List<User> users = userRepository.findByEmail(email);
		User user;
		if (users.isEmpty()) {
			user = new User();
			user.setEmail(email);
			userRepository.save(user);
		} else {
			user = users.get(0);
		}

		List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserEmail(email);
		VerificationToken verificationToken;
		if (verificationTokens.isEmpty()) {
			verificationToken = new VerificationToken();
			verificationToken.setUser(user);
			verificationTokenRepository.save(verificationToken);
		} else {
			verificationToken = verificationTokens.get(0);
		}

		sendingMailService.sendVerificationMail(email, verificationToken.getToken());
	}

	public ResponseEntity<String> verifyEmail(String token) {
		List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
		if (verificationTokens.isEmpty()) {
			return ResponseEntity.badRequest().body("Invalid token.");
		}

		VerificationToken verificationToken = verificationTokens.get(0);
		if (verificationToken.getExpiredDateTime().isBefore(now())) {
			return ResponseEntity.unprocessableEntity().body("Expired token.");
		}

		verificationToken.setConfirmedDateTime(now());
		verificationToken.setStatus(STATUS_VERIFIED);
		verificationToken.getUser().setIsActive(true);
		verificationTokenRepository.save(verificationToken);

		return ResponseEntity.ok("You have successfully verified your email address.");
	}
}
