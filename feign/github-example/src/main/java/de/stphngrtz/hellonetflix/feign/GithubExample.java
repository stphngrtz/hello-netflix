package de.stphngrtz.hellonetflix.feign;

import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;

import java.util.List;

public class GitHubExample {

    public static void main(String[] args) {
        GitHubRaw githubraw = Feign.builder()
                .logger(new SystemOutLogger())
                .logLevel(Logger.Level.BASIC)
                .target(GitHubRaw.class, "https://api.github.com");

        System.out.println(githubraw.contributors("netflix", "feign"));

        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .logger(new SystemOutLogger())
                .logLevel(Logger.Level.BASIC)
                .target(GitHub.class, "https://api.github.com");

        github.contributors("netflix", "feign").forEach(c -> System.out.println(c.login + " (" + c.contributions + ")"));
    }

    private interface GitHubRaw {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        String contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    private interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    private static class Contributor {
        public String login;
        public int contributions;
    }

    private static class SystemOutLogger extends Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            System.out.println(String.format(format, args));
        }
    }
}
