package com.cultureShop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자이상, 16자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordCk;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    private String dtlAddress;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

    private String checkYn; // 개인정보이용 동의

    public MemberFormDto() {
    }

    public MemberFormDto(String name, String email, String password, String address, String dtlAddress, String tel) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.dtlAddress = dtlAddress;
        this.tel = tel;
    }
}
