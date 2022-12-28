package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

    Logger logger = Logger.getLogger(FileUploadExceptionAdvice.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorUpload", "File exceeds maximum size!");
        logger.info("File exceeds maximum size!");
        return "redirect:/books/shelf";
    }
}
