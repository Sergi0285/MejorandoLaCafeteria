package com.disenocreativo.Diseno.Seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class configuracionSeguridad {
    private final jwtFiltroAutenticacion jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
  private final String[] whitelist={"/auth/**","/clienteControlador/**","/Css/**","/assets/**","/Html/**","/Js/**","/Imagenes/**","/vendor/**",
  "index.html","src/main/resources/static/Recursos/Imagenes/**","/Recursos/Imagenes/**","/Recursos/**","/Recursos/Css/**","/Recursos/Js/**","/Recursos/vendor/**",
  "/productoControlador/**","/productos/**","/cafeterias/**","/cafeteriaControlador/**","/cafeterias","/Js/agregarProducto.js","/img/**","/interaccionControlador/**",
  "/interaccion/**", "/Js/productosJS.js", "/Js/productosCaf2JS.js", "/Js/productosCaf3JS.js"};
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers(whitelist).permitAll()
                .anyRequest().authenticated()
                )
            .sessionManagement(sessionManager->
                sessionManager 
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();     
    }
}