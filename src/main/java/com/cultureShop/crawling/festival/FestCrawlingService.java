package com.cultureShop.crawling.festival;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Getter
@Setter
public class FestCrawlingService{
/*
    public List<FestDataDto> festDatas;
    private final WebDriver driver;


    public FestCrawlingService() {

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        driver = new ChromeDriver(options);
        festDatas = new ArrayList<>();

        festCrawling();
    }



    public List<FestDataDto> festCrawling() {
        driver.navigate().to("https://korean.visitkorea.or.kr/kfes/list/wntyFstvlList.do");
        WebElement item = driver.findElement(By.cssSelector("#fstvlList a"));

        try {
            Long stTime = new Date().getTime();
            while(new Date().getTime() < stTime + 20000) {
                Thread.sleep(500);
                ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight)", item);
            }

            List<WebElement> elements = driver.findElements(By.cssSelector("#fstvlList a"));

            for(WebElement element : elements) {
                driver.manage().timeouts().implicitlyWait(100000, TimeUnit.MILLISECONDS);

                String imgUrl = element.findElement(By.cssSelector(".other_festival_img img")).getAttribute("src");
                String itemName = element.findElement(By.cssSelector(".other_festival_content strong")).getText();
                String startEnd = element.findElement(By.cssSelector(".other_festival_content .date")).getText();
                String address = element.findElement(By.cssSelector(".other_festival_content .loc")).getText();

                festDatas.add(
                        FestDataDto.builder()
                                .imgUrl(imgUrl)
                                .itemName(itemName)
                                .startEnd(startEnd)
                                .address(address)
                                .build()
                );
            }
            System.out.println(festDatas.toString());
            System.out.println(festDatas.size());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }

        return festDatas;
    }

 */


}
