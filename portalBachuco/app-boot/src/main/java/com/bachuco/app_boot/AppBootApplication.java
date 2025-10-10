package com.bachuco.app_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication(scanBasePackages = {
		"com.bachuco",
		"com.bachuco.infrastructure"
})
@EnableJpaRepositories({
	"com.bachuco.infrastructure.persistence.repository",
	"com.bachuco.persistence.repository"
})
@EntityScan({
	"com.bachuco.infrastructure.persistence.entity",
	"com.bachuco.persistence.entity"
})
public class AppBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppBootApplication.class, args);
	}
	
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilterRegistration() {
        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
