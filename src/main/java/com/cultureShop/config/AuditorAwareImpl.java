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
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if(authentication != null) {
            userId = authentication.getName();
            if(!Pattern.matches(emailPattern, userId)) {
                String[] userIdStr = userId.split("email=");
                String email = userIdStr[1];
                String[] emailStr = email.split(",");
                userId = emailStr[0];
            }
        }
        return Optional.of(userId);
    }
}
