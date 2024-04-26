package com.upao.notas.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Array;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de CORS
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("*"));
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
            configuration.setAllowedMethods(Arrays.asList("GET, POST, PUT, DELETE, OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("Authorization, Content-Type, Cache-Control"));
            configuration.setAllowCredentials(true); // Permitir uso de credenciales en CORS
            return configuration;
        }));

        // Configuración para evitar CSRF
        SecurityFilterChain filterChain = http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET).permitAll(); // Configuración para rutas públicas
                    auth.requestMatchers(HttpMethod.POST).permitAll();
                    auth.requestMatchers(HttpMethod.PUT).permitAll();
                    auth.requestMatchers(HttpMethod.DELETE).permitAll();
                    auth.requestMatchers(HttpMethod.OPTIONS).permitAll();
                    auth.requestMatchers("/usuario/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html", "/v3/api-docs/*", "/swagger-ui/*").permitAll();
                    auth.anyRequest().authenticated(); // Requiere autenticación para otras rutas
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin estado para las sesiones
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

        return filterChain; // Asegúrate de incluir el `return`
    }
}
