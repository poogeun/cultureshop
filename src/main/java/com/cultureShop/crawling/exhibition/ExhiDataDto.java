package com.cultureShop.crawling.exhibition;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Builder
@Getter
@ToString
public class ExhiDataDto {

    private String imgUrl;
    private String itemName;
    private String startEnd;
    private String place;

    private LocalDate startDay;
    private LocalDate endDay;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.M.d");

    public ExhiDataDto(String imgUrl, String itemName, String startEnd, String place, LocalDate startDay, LocalDate endDay) {
        this.imgUrl = imgUrl;
        this.itemName = itemName;
        this.startEnd = startEnd;
        this.place = place;

        LocalDate[] startEndDay = parseDates(startEnd);
        this.startDay = startEndDay[0];
        this.endDay = startEndDay[1];
    }

    public ExhiDataDto() {}

    private LocalDate[] parseDates(String startEnd) {
        LocalDate[] startEndDay = new LocalDate[2];
        if(startEnd != null && !startEnd.isEmpty()) {
            try{
                String[] dates = startEnd.split("~");
                LocalDate startDate = LocalDate.parse(dates[0].trim(), DATE_TIME_FORMATTER);
                startEndDay[0] = startDate;
                String endDate = dates[1].trim();
                if(endDate.length() <= 5){
                    endDate = startDate.getYear() + "." + endDate;
                }
                startEndDay[1] = LocalDate.parse(endDate, DATE_TIME_FORMATTER);

            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        return startEndDay;
    }
}
