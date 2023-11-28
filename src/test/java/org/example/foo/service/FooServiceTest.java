package org.example.foo.service;

import org.example.foo.FooException;
import org.example.foo.NonRecoverableException;
import org.example.foo.RecoverableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.ExhaustedRetryException;

@SpringBootTest()
public class FooServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(FooServiceTest.class);

	@Autowired
	RetriableFooService retriableFooService;

	@Autowired
	FooService fooService;

	private static final int ATTEMPTS_COUNT = 5;

	@Test
	void testRetryWithoutCB() {
		int exhaustedRetryExceptionCount = 0;
		int fooExceptionCount = 0;
		for (int i = 0; i < ATTEMPTS_COUNT; i++) {
			try {
				retriableFooService.call(i);
			} catch (ExhaustedRetryException e) {
				exhaustedRetryExceptionCount++;
				// e.printStackTrace();
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			} catch (FooException e) {
				fooExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			}
		}
		Assertions.assertEquals(ATTEMPTS_COUNT, fooExceptionCount);
		Assertions.assertEquals(0, exhaustedRetryExceptionCount);
	}

	@Test
	void testRetryWithCB() {
		int exhaustedRetryExceptionCount = 0;
		int runtimeExceptionCount = 0;
		for (int i = 0; i < ATTEMPTS_COUNT; i++) {
			try {
				retriableFooService.callWithCB(i);
			} catch (ExhaustedRetryException e) {
				exhaustedRetryExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			} catch (FooException e) {
				runtimeExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			}
		}
		Assertions.assertEquals(ATTEMPTS_COUNT, runtimeExceptionCount);
		Assertions.assertEquals(0, exhaustedRetryExceptionCount);
	}

	@Test
	void testDirectCB() {
		int exhaustedRetryExceptionCount = 0;
		int runtimeExceptionCount = 0;
		for (int i = 0; i < ATTEMPTS_COUNT; i++) {
			try {
				fooService.callWithCB(i);
			} catch (ExhaustedRetryException e) {
				exhaustedRetryExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			} catch (FooException e) {
				runtimeExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			}
		}
		Assertions.assertEquals(5, runtimeExceptionCount);
		Assertions.assertEquals(0, exhaustedRetryExceptionCount);
	}

	@Test
	void testCallWithRT() {
		int exhaustedRetryExceptionCount = 0;
		int runtimeExceptionCount = 0;
		for (int i = 0; i < ATTEMPTS_COUNT; i++) {
			try {
				retriableFooService.callWithRT(i);
			} catch (ExhaustedRetryException e) {
				exhaustedRetryExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			} catch (FooException e) {
				runtimeExceptionCount++;
				logger.debug("{} {}", e.getClass().getName(), e.getMessage());
			}
		}
		Assertions.assertEquals(5, runtimeExceptionCount);
		Assertions.assertEquals(0, exhaustedRetryExceptionCount);
	}

	@Test
	void testNonRecoverableExceptionIsReturnedAsIs() throws RecoverableException, NonRecoverableException {
		boolean nonRecoverable = false;
		Assertions.assertThrows(NonRecoverableException.class, () -> fooService.throwsException(nonRecoverable));
	}

	@Test
	void testRecoverableExceptionIsReturnedAsIs() throws RecoverableException, NonRecoverableException {
		boolean recoverable = true;
		Assertions.assertFalse(fooService.throwsException(recoverable));
	}

}
