package com.cultureShop.repository;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.dto.MainItemDto;
import com.cultureShop.dto.QMainItemDto;
import com.cultureShop.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 오픈인지, 오픈 전인지 찾는 메소드
    private BooleanExpression searchStartStatusEq(ItemStartStatus searchStartStatus) {
        return searchStartStatus == null ?
                null : QItem.item.itemStartStatus.eq(searchStartStatus);
    }

    // 날짜 얼마나 지났는지 찾는 메소드
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDate dateTime = LocalDate.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        }
        else if(StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        }
        else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }
        else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime); // dateTime 이후에 등록된 상품
    }


    // 카테고리별 검색으로 찾기
    private BooleanExpression searchByLike(String searchCategory, String searchQuery) {

        if(StringUtils.equals("exhibition", searchCategory)) {
            return QItem.item.category.eq("exhibition").and(QItem.item.itemName.like("%"+searchQuery+"%"));
        }
        else if(StringUtils.equals("museum", searchCategory)) {
            return QItem.item.category.eq("museum").and(QItem.item.itemName.like("%"+searchQuery+"%"));
        }
        else if(StringUtils.equals("festival", searchCategory)) {
            return QItem.item.category.eq("festival").and(QItem.item.itemName.like("%"+searchQuery+"%"));
        }
        else {
            return QItem.item.itemName.like("%"+searchQuery+"%");
        }
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchStartStatusEq(itemSearchDto.getSearchStartStatus()),
                        searchByLike(itemSearchDto.getSearchCategory(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNameLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemName.like("%"+searchQuery+"%");
    }

    private BooleanExpression categoryLike(String category) {
        return StringUtils.isEmpty(category) ? null : QItem.item.category.like(category);
    }

    @Override
    public List<MainItemDto> getSearchItemList(ItemSearchDto itemSearchDto) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemName, item.place, item.address,
                item.price, item.startDay, item.endDay, itemImg.imgUrl))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).fetch();
        return results;
    }

    // 상품목록 카테고리
    private BooleanExpression itemCategory(String category) {
        return QItem.item.category.eq(category);
    }

    // 상품목록 지역 필터
    private BooleanExpression itemLocation(String address) {
        if(address == null || address.isEmpty()) {
            return QItem.item.itemStartStatus.eq(ItemStartStatus.START);
        }
        return QItem.item.address.like("%"+address+"%");
    }

    // 상품목록 정렬
    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if(sort == null || sort.isEmpty()) {
            return QItem.item.regTime.desc();
        }
        switch (sort) {
            case "endDay":
                return QItem.item.endDay.asc();
            case "review":
                return QItem.item.reviews.size().desc();
            default:
                return QItem.item.regTime.desc();
        }
    }

    @Override
    public Page<MainItemDto> getCateItemList(String category, String address, String sort, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemName, item.place, item.address,
                        item.price, item.startDay, item.endDay, itemImg.imgUrl))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemCategory(category), itemLocation(address))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        long total = results.size();
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long getCateItemCount(String category, String address, String sort) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results_2 = queryFactory.select(new QMainItemDto(item.id, item.itemName, item.place, item.address,
                        item.price, item.startDay, item.endDay, itemImg.imgUrl))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemCategory(category), itemLocation(address))
                .orderBy(getOrderSpecifier(sort)).fetch();

        return results_2.size();
    }
}
