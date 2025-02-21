package com.securefilestorage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Spring Security Configuration class.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity  // Enables @PreAuthorize and other method-level security
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain.
     *
     * @param http HttpSecurity instance.
     * @return SecurityFilterChain.
     * @throws Exception in case of errors.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs (stateless authentication)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // Configure URL authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/info",
                                "/api/users/register",
                                "/api/auth/**",
                                "/favicon.ico").permitAll()  // Allow login/register APIs
                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()  // Swagger UI if you're using it
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // Add JWT Authentication Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Keep track of active user sessions.
     *
     * @return a SessionRegistry bean.
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Spring Security listener that helps track session lifecycle events.
     *
     * @return a HttpSessionEventPublisher bean.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * Stores passwords in a hashed format.
     *
     * @return a PasswordEncoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the AuthenticationManager bean.
     *
     * @param authConfig AuthenticationConfiguration.
     * @return AuthenticationManager.
     * @throws Exception in case of errors.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}

