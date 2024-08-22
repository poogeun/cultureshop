package com.cultureShop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class LikeOrderFormDto {

    private Long likeItemId;

    private Long itemId;

    private int count;

    private LocalDate viewDay;

    private int orderPrice;

    private String address;

    private String delReq;
    private String reqWrite;
    private String getTicket;

    private List<LikeOrderFormDto> likeOrderFormDtoList;
}
