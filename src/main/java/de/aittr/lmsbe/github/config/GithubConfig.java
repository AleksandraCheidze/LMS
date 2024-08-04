package de.aittr.lmsbe.github.config;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GithubConfig {

    @Value("${github.key}")
    private String githubKey;
    @Value("${github.user}")
    private String githubUser;

    /**
     * Establishes a connection to GitHub using the provided OAuth token and user.
     *
     * @return An initialized GitHub object representing the connection to GitHub.
     * @throws IOException if an I/O error occurs while connecting to GitHub.
     */
    @Bean
    public GitHub gitHubConnection() throws IOException {
        return new GitHubBuilder()
                .withOAuthToken(githubKey, githubUser)
                .build();
    }
}
