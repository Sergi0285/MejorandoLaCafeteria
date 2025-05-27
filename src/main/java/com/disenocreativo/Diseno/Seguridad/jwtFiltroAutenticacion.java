package com.disenocreativo.Diseno.Seguridad;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class jwtFiltroAutenticacion extends OncePerRequestFilter{
    private final jwtServicio jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
       
        final String token = getTokenFromRequest(request);
        final String username;

        if (token==null)
        {
            filterChain.doFilter(request, response);
            return;
        }

        username=jwtService.getUsernameFromToken(token);

        if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            UserDetails userDetails=userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails))
                {
                    System.out.println("Token válido para usuario: " + userDetails.getUsername());
                    System.out.println("Autoridades del usuario: " + userDetails.getAuthorities()); // Muy importante

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()); // Aquí se llama a administrador.getAuthorities()

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication object seteado en SecurityContext para: " + userDetails.getUsername());
                } else {
                    System.out.println("Token inválido para usuario: " + username);
                }

        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer "))
        {
            return authHeader.substring(7);
        }
        return null;
    }
}