package com.example.todolist.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// essa notação serve para definir classes globais para exceções
// toda exceção que tiver vai passar por aqui, mas só vai executar se for do tipo 
@ControllerAdvice
public class ExceptionHandlerController {

	// qual vai ser o tipo de exceção que vai entrar nessa função
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		// vai pegar o erro lançado no model
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage());
	}
}
