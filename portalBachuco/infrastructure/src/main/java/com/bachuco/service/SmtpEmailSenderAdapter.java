package com.bachuco.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.bachuco.model.EmailMensaje;
import com.bachuco.port.EmailSender;

@Component
public class SmtpEmailSenderAdapter implements EmailSender {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void enviar(EmailMensaje email) {
		try {
			 SimpleMailMessage message = new SimpleMailMessage();
		        message.setTo(email.getDestinatario());
		        message.setSubject(email.getAsunto());
		        message.setText(email.getCuerpo());
		        mailSender.send(message);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
