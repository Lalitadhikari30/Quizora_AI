package com.quizora.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini.api")
public class GeminiProperties {

    private String key;
    private String url;

    public String getApiKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getApiUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
