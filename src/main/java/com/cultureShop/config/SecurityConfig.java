package com.cultureShop.config;

import com.cultureShop.constant.Role;
import com.cultureShop.entity.Member;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error", "/like").permitAll()
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**", "/place/**", "/search/**").permitAll()
                        .requestMatchers("/order/**", "/review/**", "/my-page/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).formLogin(formLogin -> formLogin
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                ).logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl("/")
                );

        // 에러가 발생하면 이렇게 핸들링 해라, 권한이 없는 사용자 리소스 요청하면 "Unauthorized" 에러 발생 시킴
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() { // 패스워드 암호화
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

    @Autowired
    public void configureGlobal(MemberRepository memberRepository) throws Exception {

        Member findAdmin = memberRepository.findByName("admin");

        if(findAdmin == null) {
            Member admin = new Member();
            admin.setEmail("admin@test.com");
            admin.setName("admin");
            admin.setTel("010-1234-5678");
            admin.setPassword(passwordEncoder().encode("00000000"));
            admin.setRole(Role.ADMIN);

            memberRepository.save(admin);
        }
    }
}
