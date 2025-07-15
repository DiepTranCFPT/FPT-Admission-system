package com.sba.configs;

import com.sba.authentications.services.TokenService;
import com.sba.accounts.services.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.sba.exceptions.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:application.properties")
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final String[] PUBLIC_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api-docs/**",
            "/admin/login",
            "/admin/register",
            "/api/authen/login/**",
            "/api/authen/register/**",
            "/api/test/public-api",
            "/api/authen/profile",
            "/api/authen/firebase-login",
            "/login/oauth2/code/google",
<<<<<<< Updated upstream
            "/api/posts/latest",
            "/api/posts/{id}",
            "/api/posts/{category}/{title}",
            "/ws/**",
=======
            "/api/accounts/**",  // Temporarily make accounts endpoints public for testing

>>>>>>> Stashed changes
    };
    private final String[] PUBLIC_ENDPOINTS_METHOD = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/test/admin-api/**",
            "/api/authen/register/**",
    };

    final AuthenticationHandler authenticationHandler;
    final CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter;
    final AccountService accountService;
    final TokenService tokenService;

    @Autowired
    public SecurityConfig(AuthenticationHandler authenticationHandler,
                          @Lazy CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter, AccountService accountService, TokenService tokenService) {
        this.authenticationHandler = authenticationHandler;
        this.customJwtGrantedAuthoritiesConverter = customJwtGrantedAuthoritiesConverter;
        this.accountService = accountService;
        this.tokenService = tokenService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS_METHOD).hasAnyRole( "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationHandler).accessDeniedHandler((request, response, accessDeniedException) -> {
                            authenticationHandler.handleAccessDeniedException(request, response);
                        }))
                .userDetailsService(accountService)
                .csrf(AbstractHttpConfigurer::disable);
        httpSecurity.addFilterBefore(new JwtAuthenticationFilter(tokenService, jwtDecoder(), accountService),
                UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return converter;
    }


    @Bean
    public CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter() {
        return new CustomJwtGrantedAuthoritiesConverter();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
