package com.cultureShop.controller;

import com.cultureShop.dto.ItemFormDto;
import com.cultureShop.dto.ItemSearchDto;
import com.cultureShop.entity.Item;
import com.cultureShop.entity.Member;
import com.cultureShop.entity.UserLikeItem;
import com.cultureShop.repository.MemberRepository;
import com.cultureShop.service.ItemService;
import com.cultureShop.service.UserLikeItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserLikeItemService userLikeItemService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model, HttpServletRequest request){

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.getFirst().isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/item/new";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@PathVariable("itemId") Long itemId, Model model){
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.getFirst().isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/items";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);


        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(@PathVariable("itemId")Long itemId, Model model, Principal principal){

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        List<UserLikeItem> likeItems = userLikeItemService.getLikeItems(itemId);
        int likeCount = likeItems.size();

        if(principal != null) {
            System.out.println("========================================================");
            System.out.println(userLikeItemService.findLikeItem(principal.getName(), itemId));
            if (userLikeItemService.findLikeItem(principal.getName(), itemId)) {
                model.addAttribute("isLikeItem", "afterLike");
            }
            else {
                model.addAttribute("isLikeItem", "beforeLike");
            }
        }

        model.addAttribute("item", itemFormDto);
        model.addAttribute("likeItems", likeItems);
        model.addAttribute("likeCount", likeCount);
        return "item/itemDtl";
    }

    @PostMapping(value = "/like") // 찜 추가
    public String addLike(@RequestParam("itemId") Long itemId, Principal principal, Model model) {

        System.out.println("-----------------------------");

        if(principal != null) {
            userLikeItemService.addLike(principal.getName(), itemId);
            return "redirect:/item/"+itemId;
        }
        else {
            model.addAttribute("errorMessage", "찜 기능은 로그인 후 이용 가능합니다.");
        }
        return "redirect:/item/"+itemId;
    }
}
