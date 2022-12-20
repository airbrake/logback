package io.airbrake.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ch.qos.logback.classic.Logger;

@ControllerAdvice
public class GlobalExceptionContoller {
    
    @Autowired
    Logger logger;

    @ExceptionHandler(Exception.class)
    public void ExceptionHandling(Exception e)
    {
        logger.error(e.getMessage());
    }  
}
