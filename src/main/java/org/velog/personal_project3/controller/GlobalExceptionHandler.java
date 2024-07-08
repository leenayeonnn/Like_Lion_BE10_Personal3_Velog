package org.velog.personal_project3.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public String handlerException(Exception e, Model model) {
		log.error("Server Error: ", e);
		model.addAttribute("e", e.getMessage());
		return "pages/error";
	}
}
