package org.example.foo.service;

import java.util.MissingResourceException;

import org.example.foo.FooException;
import org.example.foo.NonRecoverableException;
import org.example.foo.RecoverableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class FooService {
	private static final Logger logger = LoggerFactory.getLogger(FooService.class);

	static final long OPEN_TIMEOUT = 50000L;
	static final long RESET_TIMEOUT = 30000L;
	static final int MAX_ATTEMPTS = 3;

	public void call(int i) {
		logger.debug("FooService.call({})", i);
		throw new FooException("call(" + i + ") failed");
	}

	@CircuitBreaker(maxAttempts = MAX_ATTEMPTS, resetTimeout = RESET_TIMEOUT, openTimeout = OPEN_TIMEOUT, retryFor = FooException.class)
	public void callWithCB(int i) {
		logger.debug("FooService.callWithCB({})", i);
		throw new FooException("callWithCB(" + i + ") failed");
	}

	@Recover
	public void recover(FooException e, int i) {
		logger.debug("recover  FooService.call({}) because of {})", i, e.getMessage());
		throw e;
	}

	@Retryable(retryFor = RecoverableException.class, notRecoverable = NonRecoverableException.class, maxAttempts = 2, backoff = @Backoff(delay = 1000))
	public boolean throwsException(boolean recoverable) throws RecoverableException, NonRecoverableException {
		if (recoverable) {
			throw new RecoverableException();
		} else {
			throw new NonRecoverableException();
		}
	}

	@Recover
	public boolean recoverNonRecoverableException(NonRecoverableException e, boolean recoverable) throws NonRecoverableException {
		throw e;
	}

	@Recover
	public boolean recoverRecoverableException(RecoverableException e, boolean recoverable) throws RecoverableException {
		throw e;
	}

}
