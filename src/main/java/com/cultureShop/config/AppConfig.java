package com.cultureShop.config;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    String apiKey = "8373218323335137";
    String secretKey = "JTG8aDfIPX0Bqm33k5hVlcj5Vxxf3ynKIHsbN3dM0fT91mp2TbeIRza00oL6iS1L0DWhiZewxUsSCUFD";

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, secretKey);
    }
}
