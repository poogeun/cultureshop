package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.MemberFormDto;
import com.cultureShop.entity.Member;
import com.cultureShop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;

    /* 회원가입 페이지 */
    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, @RequestParam("checkYn") String checkYn,
                             BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
        /* 개인정보동의 체크 확인 */
        if(checkYn.equals("N")){
            model.addAttribute("errorMessage", "개인정보수집 및 이용동의는 필수입니다.");
            return "member/memberForm";
        }
        /* 비밀번호 확인 입력값 검증 */
        if(!memberFormDto.getPassword().equals(memberFormDto.getPasswordCk())) {
            model.addAttribute("errorMessage", "비밀번호 확인을 다시 입력해주세요.");
            return "member/memberForm";
        }

        try{
            /* 회원객체 생성, 저장(+비밀번호 암호화) */
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    /* 로그인 페이지 */
    @GetMapping(value = "/login")
    public String login() {
        return "member/memberLogin";
    }

    /* 이메일 로그인 페이지 */
    @GetMapping(value = "/email-login")
    public String emailLogin() {
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    /* 회원정보수정 페이지 */
    @GetMapping(value = "/update")
    public String memberUpdate(Model model, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal); // 소셜 로그인일 경우
        if(email == null) { // 이메일 로그인일 경우
            email = principal.getName();
        }

        /* 로그인된 회원 정보 */
        MemberFormDto memberFormDto = memberService.getMemDto(email);
        model.addAttribute("memberFormDto", memberFormDto);
        return "member/memberUpdateForm";
    }

    /* 회원정보수정 */
    @PostMapping(value = "/update")
    public String memberUpdate(@Valid MemberFormDto memberFormDto, Principal principal,
                               BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "member/memberUpdateForm";
        }

        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }
        memberService.updateMember(email, memberFormDto, passwordEncoder);

        return "redirect:/my-page/orders";
    }
}
