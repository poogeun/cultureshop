package com.cultureShop.crawling.festival;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Builder
@Getter
@ToString
public class FestDataDto {

    private String imgUrl;
    private String itemName;
    private String startEnd;
    private String address;

    private LocalDate startDay;
    private LocalDate endDay;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public FestDataDto(String imgUrl, String itemName, String startEnd, String address, LocalDate startDay, LocalDate endDay) {
        this.imgUrl = imgUrl;
        this.itemName = itemName;
        this.startEnd = startEnd;
        this.address = address;

        LocalDate[] startEndDay = parseDates(startEnd);
        this.startDay = startEndDay[0];
        this.endDay = startEndDay[1];
    }

    public FestDataDto() {}

    private LocalDate[] parseDates(String startEnd) {
        LocalDate[] startEndDay = new LocalDate[2];
        if(startEnd != null && !startEnd.isEmpty()) {
            try{
                String[] dates = startEnd.split("~");
                if(dates.length == 2) {
                    startEndDay[0] = LocalDate.parse(dates[0].trim(), DATE_TIME_FORMATTER);
                    startEndDay[1] = LocalDate.parse(dates[1].trim(), DATE_TIME_FORMATTER);
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        return startEndDay;
    }
}
