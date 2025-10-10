package com.bachuco.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachuco.port.EmailSender;
import com.bachuco.port.EmpleadoRepositoryPort;
import com.bachuco.port.OtpRepository;
import com.bachuco.service.IOtpGenerator;
import com.bachuco.service.impl.OtpGeneratorImpl;
import com.bachuco.service.usecase.GenerarYEnviarOtpUseCase;

@Configuration
public class OtpConfig {
	
    @Bean//IOtpGenerator y OtpGeneratorImpl vienen de application
    public IOtpGenerator otpGenerator() {
        return new OtpGeneratorImpl();
    }
    
	@Bean
	public GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase(OtpRepository otpRepository,EmailSender emailSender,IOtpGenerator otpGenerator,EmpleadoRepositoryPort empleadoRepositoryPort) {
	   return new GenerarYEnviarOtpUseCase(otpRepository, emailSender,otpGenerator,empleadoRepositoryPort);
	}
	   
	/*@Bean//opt y OtpRepository viene de la capa de domain
    public OtpRepository otpRepository(OtpJpaRepository jpaRepository) {
        return new JpaOtpRepositoryImpl(jpaRepository);
    }*/


    /*@Bean//GenerarYEnviarOtpUseCase y OtpGeneratorImpl viene de la capa de application, OtpRepository y EmailSender viene de la capa de domain
    public GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase(
            OtpRepository otpRepository,
            EmailSender emailSender,
            OtpGeneratorImpl otpGenerator) {
        return new GenerarYEnviarOtpUseCase(otpRepository, emailSender, otpGenerator);
    }*/

}
