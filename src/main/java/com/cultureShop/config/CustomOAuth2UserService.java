package com.cultureShop.config;

import com.cultureShop.dto.SessionUser;
import com.cultureShop.entity.Member;
import com.cultureShop.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(member));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());

    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findSocialMem(attributes.getEmail())
                .map(entity -> entity.updateSocialMem(attributes.getName()))
                .orElse(attributes.toEntity());
        return memberRepository.save(member);
    }

    public String getSocialEmail(Principal principal) {
        String email = "";

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorizedClientRegistrationId().equals("google")){
            Map<String, Object> userAttributes = auth.getPrincipal().getAttributes();
            email = (String) userAttributes.get("email");
        }
        else if(auth.getAuthorizedClientRegistrationId().equals("kakao")) {
            Map<String, Object> userAttributes = auth.getPrincipal().getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }
        else if(auth.getAuthorizedClientRegistrationId().equals("naver")) {
            Map<String, Object> userAttributes = auth.getPrincipal().getAttributes();
            Map<String, Object> response = (Map<String, Object>) userAttributes.get("response");
            email = (String) response.get("email");
        }

        return email;
    }
}
