package com.bachoco.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.bachoco.exception.SendEmailException;
import com.bachoco.model.EmailMensaje;
import com.bachoco.port.EmailSender;

@Component
public class SmtpEmailSenderAdapter implements EmailSender {

	private static final Logger logger = LoggerFactory.getLogger(SmtpEmailSenderAdapter.class);
	@Autowired
	private JavaMailSender mailSender;
	//@Value("${app.mail.application}")
	private String mailApplication;

	@Override
	public int enviar(EmailMensaje email) throws SendEmailException{
		int respuesta=-1;
		try {
			 SimpleMailMessage message = new SimpleMailMessage();
			 message.setTo(email.getDestinatario());
		        message.setTo(email.getDestinatario());
		        message.setSubject(email.getAsunto());
		        message.setText(email.getCuerpo());
		        mailSender.send(message);
		        respuesta=1;
		}catch (Exception e) {
			logger.warn("Hubo un error en el envio de corrreo: "+e.getMessage());
			throw new SendEmailException("Hubo un error en el envio de correo del doble factor de autenticacion");
		}
		return respuesta;
	}

}
