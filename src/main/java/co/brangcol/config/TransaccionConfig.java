package co.brangcol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import co.brangcol.shared.sesion.SesionTransactionManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class TransaccionConfig {

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new SesionTransactionManager(entityManagerFactory);
	}

}
