package com.nuclearvet.config.security;

import com.nuclearvet.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración de Spring Security.
 * Define las reglas de seguridad, autenticación y autorización.
 * 
 * Patrón: Builder Pattern (HttpSecurity)
 * Patrón: Strategy Pattern (AuthenticationProvider)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        
                        // Swagger/OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // Actuator (opcional, para monitoreo)
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // Módulo Usuarios - Solo ADMIN
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        
                        // Módulo Pacientes - VETERINARIO y ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/**")
                            .hasAnyRole("VETERINARIO", "ASISTENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/pacientes/**")
                            .hasAnyRole("VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**")
                            .hasAnyRole("VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**")
                            .hasRole("ADMIN")
                        
                        // Módulo Citas - Todos los autenticados pueden ver
                        .requestMatchers(HttpMethod.GET, "/api/citas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/citas/**")
                            .hasAnyRole("CLIENTE", "ASISTENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/citas/**")
                            .hasAnyRole("ASISTENTE", "VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/citas/**")
                            .hasAnyRole("VETERINARIO", "ADMIN")
                        
                        // Módulo Inventario - ADMIN y VETERINARIO
                        .requestMatchers("/api/inventario/**")
                            .hasAnyRole("VETERINARIO", "ADMIN")
                        
                        // Módulo Notificaciones - Todos autenticados
                        .requestMatchers("/api/notificaciones/**").authenticated()
                        
                        // Módulo Administrativo - Solo ADMIN
                        .requestMatchers("/api/administrativo/**").hasRole("ADMIN")
                        .requestMatchers("/api/reportes/**").hasAnyRole("VETERINARIO", "ADMIN")
                        
                        // Cualquier otro request requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
