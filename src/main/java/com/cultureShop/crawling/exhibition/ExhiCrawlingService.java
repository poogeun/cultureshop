package com.cultureShop.crawling.exhibition;

import com.cultureShop.crawling.CrawlingService;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
public class ExhiCrawlingService implements CrawlingService {

    public List<ExhiDataDto> exhiDatas;
    private final WebDriver driver;

    public ExhiCrawlingService() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        driver = new ChromeDriver(options);
        exhiDatas = new ArrayList<>();

        exhiCrawling();
    }

    @Override
    public List<ExhiDataDto> exhiCrawling() {

        try {
            driver.navigate().to("https://tickets.interpark.com/contents/genre/exhibition");
            Thread.sleep(2000);
            WebElement exhiButton = driver.findElement(By.cssSelector(".GenreFilterTab_genreFilterTab__O9JDM button[aria-label='전시회']"));
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", exhiButton);
            exhiButton.click();
            Thread.sleep(2000);

            boolean moreItems = true;
            Set<WebElement> previousElements = new HashSet<>();
            while(moreItems) {
                //스크롤을 화면 하단으로 이동
                ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(1000);

                // 현재 페이지에서 상품 요소 찾기
                List<WebElement> elements = driver.findElements(By.cssSelector(".ProductList_contents__eUxgq a"));

                //현재 페이지 요소를 추가
                Set<WebElement> currentElements = new HashSet<>(elements);
                //새로 로드된 요소와 이전요소 비교
                currentElements.removeAll(previousElements);
                // 새로 로드된 요소 있으면 true
                moreItems = !currentElements.isEmpty();

                // 중복된 상품이 있을 경우 대비
                Set<WebElement> uniqueElements = new HashSet<>(elements);
                for(WebElement element : uniqueElements) {
                    if(!isElementPresentInList(element)) {
                        String imgUrl = element.findElement(By.cssSelector(".TicketItem_imageWrap__iVEOw img")).getAttribute("src");
                        String itemName = element.findElement(By.cssSelector(".TicketItem_contentsWrap__xCe3A .TicketItem_goodsName__Ju76j")).getText();
                        String startEnd = element.findElement(By.cssSelector(".TicketItem_contentsWrap__xCe3A .TicketItem_playDate__5ePr2")).getText();
                        String place = element.findElement(By.cssSelector(".TicketItem_contentsWrap__xCe3A .TicketItem_placeName__ls_9C")).getText();

                        exhiDatas.add(
                                ExhiDataDto.builder()
                                        .imgUrl(imgUrl)
                                        .itemName(itemName)
                                        .startEnd(startEnd)
                                        .place(place)
                                        .build()
                        );
                        System.out.println(itemName);
                    }
                }

                previousElements.addAll(elements);
                System.out.println(moreItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return exhiDatas;
    }

    private boolean isElementPresentInList(WebElement element) {
        for(ExhiDataDto exhiData : exhiDatas) {
            if(exhiData.getImgUrl().equals(element.findElement(By.cssSelector(".TicketItem_imageWrap__iVEOw img")).getAttribute("src"))) {
                return true;
            }
        }
        return false;
    }

}
