package com.example.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

	@Autowired
	private IUserRepository userRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// verificar a rota
		var servletPath = request.getServletPath();
		
		if(servletPath.startsWith("/tasks/")) {
			
			// pegar autenticação (usuário e senha)
			var authorization = request.getHeader("Authorization");
			
			//// tirar "basic" e o espaço, codificar
			var authEncoded = authorization.substring("Basic".length()).trim();
			
			//// criando um array de bytes, decodificar
			byte[] authDecode = Base64.getDecoder().decode(authEncoded);
			
			//// agora vamos converter esse array de bytes em uma string
			//// depois da conversão ficou: 'luizaboaventura:123456'
			var authString = new String(authDecode);
			
			//// agora vamos dividir essa string anterior
			String[] credentials = authString.split(":");
			String username = credentials[0];
			String password = credentials[1];
			
			// validar usuário 
			var user = userRepository.findByUsername(username);
			
			if (user == null) {
				response.sendError(401);
			} else {
				// validar senha
				/// vamos comparar
				var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
				if (passwordVerify.verified) {
					request.setAttribute("idUser", user.getId());
					filterChain.doFilter(request, response);
				} else {
					response.sendError(401);
				}
			}
			
		} else {
			filterChain.doFilter(request, response);
		}
		
	}
}
