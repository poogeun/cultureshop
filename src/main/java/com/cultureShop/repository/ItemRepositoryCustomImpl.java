package com.cultureShop.repository;

import com.cultureShop.constant.ItemStartStatus;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.QItem;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

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
        LocalDateTime dateTime = LocalDateTime.now();

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
            return QItem.item.category.like("%"+searchQuery+"%");
        }
        else if(StringUtils.equals("museum", searchCategory)) {
            return QItem.item.category.like("%"+searchQuery+"%");
        }
        else if(StringUtils.equals("festival", searchCategory)) {
            return QItem.item.category.like("%"+searchQuery+"%");
        }
        return null;
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
}
