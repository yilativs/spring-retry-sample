package org.example.foo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry
@Configuration
public class RetryConfiguration {

	@Bean
	RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(2);
		CircuitBreakerRetryPolicy circuitBreakerRetryPolicy = new CircuitBreakerRetryPolicy(retryPolicy);
		circuitBreakerRetryPolicy.setOpenTimeout(2000L);
		circuitBreakerRetryPolicy.setResetTimeout(15000L);
		retryTemplate.setRetryPolicy(circuitBreakerRetryPolicy);
		return retryTemplate;
	}

}
