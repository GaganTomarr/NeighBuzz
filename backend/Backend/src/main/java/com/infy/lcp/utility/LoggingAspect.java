package com.infy.lcp.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.infy.lcp.exception.LCP_Exception;

@Aspect
@Component
public class LoggingAspect {

	private static final Log LOGGER = LogFactory.getLog(LoggingAspect.class);

    @AfterThrowing(pointcut = "exception(* com.infy.service.*Impl.*(..))", throwing = "exception")
    public void logServiceException(LCP_Exception exception)
    {
    	LOGGER.error(exception.getMessage(), exception);
    }
    
}
