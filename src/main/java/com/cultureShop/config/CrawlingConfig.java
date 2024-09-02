package com.cultureShop.config;

import com.cultureShop.API.MusArtExplorer;
import com.cultureShop.crawling.exhibition.ExhiCrawlingService;
import com.cultureShop.crawling.festival.FestCrawlingService;
import com.cultureShop.dto.ApiDto.MusArtApiDto;
import com.cultureShop.entity.MusArt;
import com.cultureShop.repository.ItemImgRepository;
import com.cultureShop.repository.ItemRepository;
import com.cultureShop.repository.MusArtRepository;
import com.cultureShop.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@Transactional
public class CrawlingConfig {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MusArtRepository musArtRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    FestCrawlingService festCrawlingService;

    @Autowired
    ExhiCrawlingService exhiCrawlingService;

    @Autowired
    MusArtExplorer apiExplorer;

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

            for(int i=0; i<5; i++) {
                ItemImg itemImg = new ItemImg();
                if(i == 0) {
                    itemImg.setImgUrl(festData.getImgUrl());
                    itemImg.setRepImgYn("Y");
                }
                itemImg.setItem(item);
                itemImgRepository.save(itemImg);
            }
        }
    }

    @Autowired
    public void saveCrawExhiItem() {
        List<ExhiDataDto> exhiDatas = exhiCrawlingService.getExhiDatas();

        for(ExhiDataDto exhiData : exhiDatas) {
            Item item = new Item();
            item.setItemName(exhiData.getItemName());
            item.setPlace(exhiData.getPlace());
            item.setStartDay(exhiData.getStartDay());
            item.setEndDay(exhiData.getEndDay());
            item.setCategory("exhibition");
            item.setPrice(0);
            item.setStockNumber(300);
            item.setItemStartStatus(ItemStartStatus.START);
            itemRepository.save(item);

            for(int i=0; i<5; i++) {
                ItemImg itemImg = new ItemImg();
                if(i == 0) {
                    itemImg.setImgUrl(exhiData.getImgUrl());
                    itemImg.setRepImgYn("Y");
                }
                itemImg.setItem(item);
                itemImgRepository.save(itemImg);
            }
        }
    }

    @Autowired
    public void saveMusArtPlace() {
        List<MusArtApiDto> maDatas = apiExplorer.getMusArtApiDatas();

        for(MusArtApiDto maData : maDatas) {
            MusArt musArt = new MusArt();
            musArt.setName(maData.getName());
            musArt.setType(maData.getType());
            musArt.setAddress(maData.getAddress());
            musArt.setTel(maData.getTel());
            musArt.setOpenTime(maData.getOpenTime());
            musArt.setCloseTime(maData.getCloseTime());
            musArt.setGeoX(maData.getGeoX());
            musArt.setGeoY(maData.getGeoY());
            musArtRepository.save(musArt);
        }
    }

     */


}
