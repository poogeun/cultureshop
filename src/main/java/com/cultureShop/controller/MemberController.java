package com.cultureShop.controller;

import com.cultureShop.config.CustomOAuth2UserService;
import com.cultureShop.dto.MemberFormDto;
import com.cultureShop.entity.Member;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
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
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping(value = "/new")
    public String memberForm(Model model, HttpServletRequest request){

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, @RequestParam("checkYn") String checkYn,
                             BindingResult bindingResult, Model model, HttpServletRequest request){

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        if(checkYn.equals("N")){
            model.addAttribute("errorMessage", "개인정보수집 및 이용동의는 필수입니다.");
            return "member/memberForm";
        }

        if(!memberFormDto.getPassword().equals(memberFormDto.getPasswordCk())) {
            model.addAttribute("errorMessage", "비밀번호 확인을 다시 입력해주세요.");
            return "member/memberForm";
        }


        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "member/memberLogin";
    }

    @GetMapping(value = "/email-login")
    public String emailLogin() {
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/update")
    public String memberUpdate(Model model, Principal principal) {
        String email = customOAuth2UserService.getSocialEmail(principal);
        if(email == null) {
            email = principal.getName();
        }
        MemberFormDto memberFormDto = memberService.getMemDto(email);

        model.addAttribute("memberFormDto", memberFormDto);
        return "member/memberUpdateForm";
    }

    @PostMapping(value = "/update")
    public String memberUpdate(@Valid MemberFormDto memberFormDto, Principal principal,
                               BindingResult bindingResult, Model model) {
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
