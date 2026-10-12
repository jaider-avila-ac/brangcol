package co.brangcol.shared.sesion;

import org.slf4j.MDC;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DelegatingTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SesionTransactionManager extends JpaTransactionManager {

	public static final String MDC_EMPRESA = "empresa";

	public SesionTransactionManager(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		// usar_sesion escribe el contexto en la base, por eso nunca se abre en solo lectura
		super.doBegin(transaction, new EscrituraDefinition(definition));
		String token = SesionActual.token();
		if (token == null) {
			return;
		}
		EntityManager entityManager = entityManagerActual();
		try {
			Object empresa = entityManager.createNativeQuery("select plataforma.usar_sesion(?1)")
				.setParameter(1, token)
				.getSingleResult();
			MDC.put(MDC_EMPRESA, String.valueOf(empresa));
		}
		catch (RuntimeException ex) {
			deshacer(transaction, entityManager);
			throw ex;
		}
	}

	private EntityManager entityManagerActual() {
		EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager
			.getResource(obtainEntityManagerFactory());
		return holder.getEntityManager();
	}

	private void deshacer(Object transaction, EntityManager entityManager) {
		try {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
		}
		finally {
			doCleanupAfterCompletion(transaction);
		}
	}

	private static final class EscrituraDefinition extends DelegatingTransactionDefinition {

		EscrituraDefinition(TransactionDefinition definition) {
			super(definition);
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	}

}
