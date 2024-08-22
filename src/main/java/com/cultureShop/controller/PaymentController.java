package com.cultureShop.controller;

import com.cultureShop.dto.paymentDto.PaymentCallbackRequest;
import com.cultureShop.service.OrderService;
import com.cultureShop.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @ResponseBody
    @PostMapping(value = "/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request){

        System.out.println(request.toString());
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }

    @DeleteMapping("/order/{orderUid}")
    public @ResponseBody ResponseEntity cancelPayment(@PathVariable String orderUid) {
        try{
            System.out.println(orderUid);
            orderService.deleteOrder(orderUid);
            return new ResponseEntity<String>(orderUid, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("주문서 삭제 실패", HttpStatus.BAD_REQUEST);
        }
    }
}
