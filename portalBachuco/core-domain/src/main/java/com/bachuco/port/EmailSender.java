package com.bachuco.port;

import com.bachuco.model.EmailMensaje;

public interface EmailSender {

	void enviar(EmailMensaje email);
}
