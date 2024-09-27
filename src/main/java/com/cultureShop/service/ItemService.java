package com.cultureShop.service;

import com.cultureShop.dto.*;
import com.cultureShop.entity.*;
import com.cultureShop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final MemberRepository memberRepository;
    private final UserLikeRepository userLikeRepository;

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
        ItemFormDto itemFormDto = ItemFormDto.of(item); // 모델매퍼
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto); // 상품 정보 업데이트

        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for(int i=0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i)); // 이미지 업데이트
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

    // 메인배너에 들어갈 상품
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

    // 메인페이지 지역별 상품
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

    // 로그인 시 메인페이지 지역별 상품 (찜 여부)
    public List<MainItemDto> getUserAddrItem(String address, String email) {
        List<Item> addressItems = itemRepository.findByAddress(address);
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }
        List<MainItemDto> itemDetails = new ArrayList<>();

        for(int i=0; i<5; i++) {
            Item addressItem = addressItems.get(i);
            Long itemId = addressItem.getId();
            MainItemDto mainItemDto = itemRepository.findUserMainItemDto(itemId, userLike.getId());
            itemDetails.add(mainItemDto);
        }

        return itemDetails;
    }

    // 메인페이지 카테고리별 상품
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

    // 로그인 시 메인페이지 카테고리별 상품
    public List<MainItemDto> getUserCategoryItem(String category, String email) {

        List<Item> categoryItems = itemRepository.findByCategoryOrderByRegTimeDesc(category);
        Member member = memberRepository.findByEmail(email);
        UserLike userLike = userLikeRepository.findByMemberId(member.getId());
        if(userLike == null) {
            userLike = UserLike.createLike(member);
            userLikeRepository.save(userLike);
        }
        List<MainItemDto> mainItemDetails = new ArrayList<>();

        for(int i=0; i<5; i++){
            Item mainCateItem = categoryItems.get(i);
            Long itemId = mainCateItem.getId();
            MainItemDto mainItemDto = itemRepository.findUserMainItemDto(itemId, userLike.getId());
            mainItemDetails.add(mainItemDto);
        }

        return mainItemDetails;
    }

    // 카테고리 메뉴 상품 가져오기
    public Page<MainItemDto> getAllCategoryItem(String category, String address, String sort, Pageable pageable) {
        return itemRepository.getCateItemList(category, address, sort, pageable);
    }

    // 카테고리 메뉴 상품 건수
    @Transactional(readOnly = true)
    public long getCateItemCount(String category, String address, String sort) {
        return itemRepository.getCateItemCount(category, address, sort);
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
