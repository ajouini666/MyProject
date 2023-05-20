package com.example.packageassignment.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.packageassignment.Security.jwt.AuthEntryPointJwt;
import com.example.packageassignment.Security.jwt.AuthTokenFilter;
import com.example.packageassignment.Security.services.EmployeeDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    EmployeeDetailsServiceImpl employeeDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http.cors().and().csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/api/feedback").permitAll()
                .requestMatchers("/api/SignupPermission/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/sendMailTemp").permitAll()
                .requestMatchers("/api/test/deleteemployee/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/test/updateemployee/**").permitAll()
                .requestMatchers("/api/news/**").permitAll()
                .requestMatchers("/api/tickets/**").permitAll()
                .requestMatchers("/api/pointage/**").permitAll()
                .requestMatchers("/employee/**").permitAll()
                .requestMatchers("/team-chat/chat").permitAll()
                .requestMatchers("/employee/team-chat/**").permitAll()
                .requestMatchers("/admin/**").permitAll()
                .requestMatchers("/team-chat/websocket/**").permitAll()
                .requestMatchers("/team-chat/**").permitAll()
                .requestMatchers("/**").permitAll()

                .anyRequest()
                .authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration
                .getAuthenticationManager();
    }
}
