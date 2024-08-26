package com.cultureShop.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CrawTest {
    public static void main(String[] args) throws IOException {
        String url = "https://tickets.interpark.com/contents/genre/exhibition";
        Document document = Jsoup.connect(url).get();

        Elements elements = document.select(".ProductList_contents__eUxgq");
        System.out.println(elements.size());
    }
}
