package co.brangcol.shared.web;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TrazaFiltro extends OncePerRequestFilter {

	public static final String CABECERA = "X-Trace-Id";

	private final Tracer tracer;

	public TrazaFiltro(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Span span = tracer.currentSpan();
		if (span != null) {
			response.setHeader(CABECERA, span.context().traceId());
		}
		chain.doFilter(request, response);
	}

}
