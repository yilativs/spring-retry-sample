package org.example.foo.service;

import org.example.foo.FooException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class RetriableFooService {
	private static final int ATTEMPTS_COUNT = 2;

	private static final Logger logger = LoggerFactory.getLogger(RetriableFooService.class);

	@Autowired
	FooService fooService;

	@Autowired
	RetryTemplate retryTemplate;

	public void callWithRT(final int i) {
		logger.debug("RetriableFooService.callWithRT({})", i);
		retryTemplate.execute(new RetryCallback<Void, FooException>() {

			@Override
			public Void doWithRetry(RetryContext context) throws FooException {
				fooService.call(i);
				return null;
			}
		}, new RecoveryCallback<Void>() {

			@Override
			public Void recover(RetryContext context) throws Exception {
				logger.debug(context.getLastThrowable().toString());
				throw (Exception)context.getLastThrowable();
			}
		});
		fooService.call(i);
	}

	@Retryable(maxAttempts = ATTEMPTS_COUNT, retryFor = FooException.class)
	public void call(int i) {
		logger.debug("RetriableFooService.call({})", i);
		fooService.call(i);
	}

	@Retryable(maxAttempts = ATTEMPTS_COUNT, retryFor = FooException.class, backoff = @Backoff(delay = 3000, multiplier = 2))
	public void callWithCB(int i) {
		logger.debug("RetriableFooService.callWithCB({})", i);
		fooService.callWithCB(i);
	}

	@Recover
	public void recover(FooException e, int i) {
		logger.debug("recover  RetriableFooService.call({}) because of {}", i, e.getMessage());
		throw e;
	}
}
