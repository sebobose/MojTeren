package org.zavrsni.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/sport/all").permitAll()
                                .requestMatchers("/sport-center/all").permitAll()
                                .requestMatchers("/sport-center/search").permitAll()
                                .requestMatchers("/sport-center/user/all").hasAnyAuthority("ADMIN", "FIELD_OWNER")
                                .requestMatchers("/reservations/sport-center/**").permitAll()
                                .requestMatchers("/reservations/by-week/**").permitAll()
                                .requestMatchers("/reservations/check-user/**").hasAnyAuthority("ADMIN", "FIELD_OWNER")
                                .requestMatchers("/reservations/canceled").hasAuthority("FIELD_OWNER")
                                .requestMatchers("/user/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/user/field-owner/**").hasAuthority("FIELD_OWNER")
                                .requestMatchers("/sport/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/sport-center/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/field/add").hasAnyAuthority("ADMIN", "FIELD_OWNER")
                                .requestMatchers("/field/update/**").hasAnyAuthority("ADMIN", "FIELD_OWNER")
                                .requestMatchers("/field/deactivate/**").hasAnyAuthority("ADMIN", "FIELD_OWNER")
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
