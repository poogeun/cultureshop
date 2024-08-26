package com.cultureShop.config;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.crawling.festival.FestCrawlingService;
import com.cultureShop.crawling.festival.FestDataDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.ItemImg;
import com.cultureShop.repository.ItemImgRepository;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CrawlingConfig {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    FestCrawlingService festCrawlingService;

    /*
    @Autowired
    public void saveCrawFestItem() {
        List<FestDataDto> festDatas = festCrawlingService.getFestDatas();

        for(FestDataDto festData : festDatas) {
            Item item = new Item();
            item.setItemName(festData.getItemName());
            item.setAddress(festData.getAddress());
            item.setStartDay(festData.getStartDay());
            item.setEndDay(festData.getEndDay());
            item.setCategory("festival");
            item.setPrice(0);
            item.setStockNumber(300);
            item.setItemStartStatus(ItemStartStatus.START);
            itemRepository.save(item);

            ItemImg itemImg = new ItemImg();
            itemImg.setImgUrl(festData.getImgUrl());
            itemImg.setItem(item);
            itemImg.setRepImgYn("Y");
            itemImgRepository.save(itemImg);
        }
    }

     */
}
