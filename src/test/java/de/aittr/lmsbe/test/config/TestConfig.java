package de.aittr.lmsbe.test.config;

import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
@EnableAutoConfiguration
@Profile("test")
public class TestConfig {
    public static final String MOCK_STUDENT = "john.doe@example.com";

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (username.equals(MOCK_STUDENT)) {
                    return new AuthenticatedUser(
                            User.builder()
                                    .id(1L)
                                    .email(MOCK_STUDENT)
                                    .role(User.Role.STUDENT)
                                    .build()
                    );
                } else throw new UsernameNotFoundException("User not found");
            }
        };
    }
}
