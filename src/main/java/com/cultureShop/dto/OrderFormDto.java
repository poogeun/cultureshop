package com.cultureShop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderFormDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개입니다.")
    private int count;

    private LocalDate viewDay;
    private int orderPrice;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    private String dtlAddress;
    private String delReq;
    private String reqWrite;
    private String getTicket;

}
