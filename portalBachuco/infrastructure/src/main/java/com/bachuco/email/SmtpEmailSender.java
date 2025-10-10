package com.bachuco.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.bachuco.model.EmailMensaje;
import com.bachuco.port.EmailSender;

public class SmtpEmailSender implements EmailSender {

	private final JavaMailSender mailSender;

	public SmtpEmailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void enviar(EmailMensaje email) {
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(email.getDestinatario());
		mensaje.setSubject(email.getAsunto());
		mensaje.setText(email.getCuerpo());
		mailSender.send(mensaje);
	}

}
