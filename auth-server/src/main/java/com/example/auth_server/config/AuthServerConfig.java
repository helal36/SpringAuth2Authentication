package com.example.auth_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
public class AuthServerConfig {

    // 1. Authorization Server Security Chain
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply OAuth2 Authorization Server's default security
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .formLogin(Customizer.withDefaults()) // Enable form-based login for unauthenticated users
                .logout(logout -> logout
                        .logoutUrl("/logout") // Define the logout URL
                        .logoutSuccessUrl("http://localhost:3000") // Redirect after logout (to React app)
                        .invalidateHttpSession(true) // Invalidate the session
                        .clearAuthentication(true) // Clear the SecurityContextHolder
                        .deleteCookies("JSESSIONID") // Delete session cookie
                );

        return http.build();
    }

    // 2. Application-wide Security Filter Chain
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Require authentication for all requests
                )
                .formLogin(Customizer.withDefaults()) // Enable form-based login
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("http://localhost:3000") // Redirect to client after logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    // 3. Register OAuth2 Clients
    @Bean
    public InMemoryRegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("fundtracker-client")
                .clientSecret("{noop}secret") // NoOp encoder for plain text password (testing only)
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE) // Authorization Code flow
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.REFRESH_TOKEN) // Refresh Token flow
                .redirectUri("http://localhost:3000/callback") // Redirect URI for your React client
                .scope("read:balance") // Define the scopes allowed
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    // 4. Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 5. UserDetailsService for In-Memory Users
    @Bean
    public UserDetailsService userDetailsService() {
        // Add in-memory users for testing
        var user1 = User.withUsername("john")
                .password("{noop}1234") // Plaintext password for testing
                .roles("USER") // Assign "USER" role
                .build();

        var user2 = User.withUsername("jane")
                .password("{noop}5678")
                .roles("ADMIN")
                .build();

        var user3 = User.withUsername("doe")
                .password("{noop}abcd")
                .roles("USER", "MANAGER")
                .build();

        // Manage the accounts in memory
        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    // 6. Configure Authorization Server Settings
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000") // The issuer URL for the Authorization Server
                .build();
    }
}