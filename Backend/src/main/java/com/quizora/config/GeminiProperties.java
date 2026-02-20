// // package com.quizora.config;

// // import org.springframework.boot.context.properties.ConfigurationProperties;
// // import org.springframework.stereotype.Component;

// // @Component
// // @ConfigurationProperties(prefix = "gemini")
// // public class GeminiProperties {
    
// //     private String apiKey;
// //     private String apiUrl;
    
// //     public String getApiKey() {
// //         return apiKey;
// //     }
    
// //     public void setApiKey(String apiKey) {
// //         this.apiKey = apiKey;
// //     }
    
// //     public String getApiUrl() {
// //         return apiUrl;
// //     }
    
// //     public void setApiUrl(String apiUrl) {
// //         this.apiUrl = apiUrl;
// //     }
// // }


// package com.quizora.config;

// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @ConfigurationProperties(prefix = "gemini")
// public class GeminiProperties {

//     private String apiKey;
//     private String apiUrl;

//     public String getApiKey() {
//         return apiKey;
//     }

//     public void setApiKey(String apiKey) {
//         this.apiKey = apiKey;
//     }

//     public String getApiUrl() {
//         return apiUrl;
//     }

//     public void setApiUrl(String apiUrl) {
//         this.apiUrl = apiUrl;
//     }
// }


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
