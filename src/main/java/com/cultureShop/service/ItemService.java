package com.cultureShop.service;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.crawling.festival.FestCrawlingService;
import com.cultureShop.crawling.festival.FestDataDto;
import com.cultureShop.dto.*;
import com.cultureShop.entity.*;
import com.cultureShop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final FestCrawlingService festCrawlingService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // 상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        // 이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0){
                itemImg.setRepImgYn("Y");
            }
            else{
                itemImg.setRepImgYn("N");
            }
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for(int i=0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public List<MainItemDto> getSearchItemList(ItemSearchDto itemSearchDto) {
        return itemRepository.getSearchItemList(itemSearchDto);
    }

    // 배너에 들어갈 상품
    public List<MainItemDto> getBannerItem() {

        List<Item> bannerItems = itemRepository.findAll().reversed();
        List<MainItemDto> bannerItemDetails = new ArrayList<>();

        for(int i=0; i<12; i++){
            Item bannerItem = bannerItems.get(i);
            Long itemId = bannerItem.getId();
            MainItemDto bannerItemDetail = itemRepository.findMainItemDto(itemId);
            bannerItemDetails.add(bannerItemDetail);
        }

        return bannerItemDetails;
    }

    //  지역별 상품
    public List<MainItemDto> getAddressItem(String address) {

        List<Item> addressItems = itemRepository.findByAddress(address);
        List<MainItemDto> itemDetails = new ArrayList<>();

        for(int i=0; i<5; i++) {
            Item addressItem = addressItems.get(i);
            Long itemId = addressItem.getId();
            MainItemDto mainItemDto = itemRepository.findMainItemDto(itemId);
            itemDetails.add(mainItemDto);
        }

        return itemDetails;
    }

    // 카테고리별 상품
    public List<MainItemDto> getCategoryItem(String category) {

        List<Item> categoryItems = itemRepository.findByCategoryOrderByRegTimeDesc(category);
        List<MainItemDto> mainItemDetails = new ArrayList<>();

        for(int i=0; i<5; i++){
            Item mainCateItem = categoryItems.get(i);
            Long itemId = mainCateItem.getId();
            MainItemDto mainItemDto = itemRepository.findMainItemDto(itemId);
            mainItemDetails.add(mainItemDto);
        }

        return mainItemDetails;
    }

    // 카테고리 메뉴 상품 가져오기
    public Page<MainItemDto> getAllCategoryItem(String category, Pageable pageable) {
        List<Item> categoryItems = itemRepository.findByCategoryOrderByRegTimeDesc(category, pageable);
        List<Item> cateItems = itemRepository.findByCategoryOrderByRegTimeDesc(category);
        int totalCount = cateItems.size();

        System.out.println(totalCount+"============totalCount====");
        List<MainItemDto> mainItemDetails = new ArrayList<>();
        for(Item item : categoryItems) {
            MainItemDto mainItemDto = itemRepository.findMainItemDto(item.getId());
            mainItemDetails.add(mainItemDto);
        }

        return new PageImpl<MainItemDto>(mainItemDetails, pageable, totalCount);
    }

    // 카테고리 필터 (종료 임박순)
    public Page<MainItemDto> getEndDayCategoryItem(String category, Pageable pageable) {
        List<Item> categoryItems = itemRepository.findByCategoryOrderByEndDayAsc(category, pageable);
        List<Item> cateItems = itemRepository.findByCategoryOrderByRegTimeDesc(category);
        int totalCount = cateItems.size();

        List<MainItemDto> mainItemDetails = new ArrayList<>();
        for(Item item : categoryItems) {
            MainItemDto mainItemDto = itemRepository.findMainItemDto(item.getId());
            mainItemDetails.add(mainItemDto);
        }

        return new PageImpl<MainItemDto>(mainItemDetails, pageable, totalCount);
    }

    // 카테고리 필터 (리뷰 많은순)
    public Page<MainItemDto> getReviewCategoryItem(String category, Pageable pageable) {
        List<Item> categoryItems = itemRepository.findItemsOrderByReviewCount(category, pageable);
        List<Item> cateItems = itemRepository.findByCategoryOrderByRegTimeDesc(category);
        int totalCount = cateItems.size();

        List<MainItemDto> mainItemDetails = new ArrayList<>();
        for(Item item : categoryItems) {
            MainItemDto mainItemDto = itemRepository.findMainItemDto(item.getId());
            mainItemDetails.add(mainItemDto);
        }

        return new PageImpl<MainItemDto>(mainItemDetails, pageable, totalCount);
    }

    // place 축제 리스트 (주소입력시)
    @Transactional(readOnly = true)
    public List<MainItemDto> getPlaceFest(String address, String category) {

        List<Item> addrItems = itemRepository.findByAddressAndCategory(address, category);
        List<MainItemDto> itemDetails = new ArrayList<>();

        for(int i=0; i<5; i++) {
            Item addrItem = addrItems.get(i);
            MainItemDto mainItemDto = itemRepository.findMainItemDto(addrItem.getId());
            itemDetails.add(mainItemDto);
        }

        return itemDetails;
    }


}
