package com.cultureShop.service;

import com.cultureShop.constant.PaymentStatus;
import com.cultureShop.dto.paymentDto.PaymentCallbackRequest;
import com.cultureShop.dto.paymentDto.RequestPayDto;
import com.cultureShop.entity.Order;
import com.cultureShop.repository.OrderRepository;
import com.cultureShop.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    // 결제 요청 데이터 조회
    public RequestPayDto findRequestDto(String orderUid) {

        Order order = orderRepository.findOrderAndPaymentAndMember(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        return RequestPayDto.builder()
                .buyerName(order.getMember().getName())
                .buyerEmail(order.getMember().getEmail())
                .buyerAddress(order.getMember().getAddress())
                .paymentPrice(order.getOrderPrice())
                .itemName(order.getOrderItems().getFirst().getItem().getItemName())
                .orderUid(order.getOrderUid())
                .build();
    }

    // 결제
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {
        try {
            // 결제 조회 (아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            // 주문내역 조회
            Order order = orderRepository.findOrderAndPayment(request.getOrderUid())
                    .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

            if(!iamportResponse.getResponse().getStatus().equals("paid")) {
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            int price = order.getPayment().getPrice();
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

            if(iamportPrice != price) {
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(),
                        true, new BigDecimal(iamportPrice)));
                throw new RuntimeException("결제금액 위변조 의심");
            }

            order.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
