package com.cultureShop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication+"===========auth");
        String userId = "";
        if(authentication != null) {
            userId = authentication.getName();
            System.out.println("userId" +userId);
            if(authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
                String social = auth.getAuthorizedClientRegistrationId();
                if(social.equals("naver")) {
                    String[] userIdStr = userId.split("email=");
                    String email = userIdStr[1];
                    String[] emailStr = email.split(",");
                    userId = emailStr[0];
                }
                else if(social.equals("kakao")) {
                    Map<String, Object> userAttributes = auth.getPrincipal().getAttributes();
                    Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
                    userId = (String) kakaoAccount.get("email");
                }
                else if(social.equals("google")) {
                    Map<String, Object> userAttributes = auth.getPrincipal().getAttributes();
                    userId = (String) userAttributes.get("email");
                }
            }
        }
        return Optional.of(userId);
    }
}
