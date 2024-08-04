package de.aittr.lmsbe.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.dto.StandardResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

    UserDetailsService userDetailsServiceImpl;
    PasswordEncoder passwordEncoder;
    ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .headers().frameOptions().disable().and()
                .authorizeRequests()
                .antMatchers("/swagger-ui.html/**").permitAll()
                .and()
                .formLogin().loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> fillResponse(response, HttpStatus.OK.value(), "Successful authorization"))
                .failureHandler((request, response, exception) ->
                        fillResponse(response, HttpStatus.UNAUTHORIZED.value(), "Invalid login or password"))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .defaultAuthenticationEntryPointFor((request, response, authException) ->
                                fillResponse(response, HttpStatus.FORBIDDEN.value(), "User not authenticated"),
                        new AntPathRequestMatcher("/api/**"))
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    fillResponse(response, HttpStatus.FORBIDDEN.value(), "Access denied for user with email <" +
                            authentication.getName() + "> and role " + authentication.getAuthorities());
                })
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) ->
                        fillResponse(response, HttpStatus.OK.value(), "Successful logout"));
        return httpSecurity.build();
    }

    @Autowired
    public void bindUserDetailsServiceAndPasswordEncoder(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder);
    }

    private void fillResponse(HttpServletResponse response, int statusCode, String message) {
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        StandardResponseDto standardResponseDto = StandardResponseDto.builder()
                .message(message)
                .status(statusCode)
                .build();

        try {
            response.getWriter().write(objectMapper.writeValueAsString(standardResponseDto));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
