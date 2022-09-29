package bssm.db.bssmgit.domain.user.service;

import bssm.db.bssmgit.domain.user.domain.User;
import bssm.db.bssmgit.domain.user.repository.UserRepository;
import bssm.db.bssmgit.global.exception.CustomException;
import bssm.db.bssmgit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
@Service
public class GithubService {

    private final UserRepository userRepository;

    GitHub github;

    @Value("${spring.oauth.git.url.token}")
    String token;

    private void connectToGithub(String token) throws IOException {
        github = new GitHubBuilder().withOAuthToken(token).build();
        github.checkApiUrlValidity();
    }

    @Scheduled(cron = "0 3 * * * ?") // 매일 새벽 3시
    public void updateUser() {
        try {
            connectToGithub(token);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.GIT_CONNECTION_REFUSED);
        }

        ArrayList<User> users = new ArrayList<>();

        userRepository.findAll().stream()
                .filter(u -> u.getGithubId() != null)
                .forEach(u -> {
                    try {
                        String commits = null;
                        boolean b = false;

                        URL url = new URL("https://github.com/" + u.getGithubId());
                        URLConnection uc = url.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

                        String inputLine;
                        while ((inputLine = br.readLine()) != null) {
                            if (b) {
                                commits = inputLine;
                                break;
                            }
                            if (inputLine.contains("<h2 class=\"f4 text-normal mb-2\">")) {
                                b = true;
                            }
                        }

                        int commit = 0;
                        if (commits != null) {
                            commits = commits.replaceAll("\\s+", "");
                            commits = commits.replaceAll(",", "");
                            commit = Integer.parseInt(commits);
                        }
                        String bio = github.getUser(u.getGithubId()).getBio();
                        String img = github.getUser(u.getGithubId()).getAvatarUrl();

                        u.updateGitInfo(commit, bio, img);
                        users.add(u);

                        br.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });

        userRepository.saveAll(users);
    }
}
