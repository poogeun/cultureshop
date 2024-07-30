package com.cultureShop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFormDto {

    private String name;
    private String email;
    private String password;
    private String passwordCk;
    private String address;
    private String tel;
    private String checkYn; // 개인정보이용 동의
}
