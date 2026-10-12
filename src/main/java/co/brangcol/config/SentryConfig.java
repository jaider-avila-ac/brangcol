package co.brangcol.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.brangcol.shared.sesion.SesionTransactionManager;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;

@Configuration
public class SentryConfig {

	@Bean
	public SentryOptions.BeforeSendCallback etiquetasSentry() {
		return new EtiquetasSentry();
	}

	static final class EtiquetasSentry implements SentryOptions.BeforeSendCallback {

		@Override
		public SentryEvent execute(SentryEvent evento, Hint hint) {
			String traceId = MDC.get("traceId");
			if (traceId != null) {
				evento.setTag("traceId", traceId);
			}
			String empresa = MDC.get(SesionTransactionManager.MDC_EMPRESA);
			if (empresa != null) {
				evento.setTag("empresa", empresa);
			}
			return evento;
		}

	}

}
