package com.cultureShop.entity;

import com.cultureShop.constant.OrderStatus;
import com.cultureShop.constant.PaymentStatus;
import com.cultureShop.dto.OrderFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
                    orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private String orderUid; // 주문 번호
    private LocalDate orderDate;
    private int orderPrice;
    private String orderTel;
    private String orderAddress;
    private String orderDtlAddress;
    private String delReq;
    private String reqWrite;
    private String getTicket;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, Payment payment, List<OrderItem> orderItemList, OrderFormDto orderFormDto) {
        Order order = new Order();
        order.setMember(member);
        order.setPayment(payment);
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderUid(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        order.setOrderPrice(orderFormDto.getOrderPrice());
        order.setOrderTel(orderFormDto.getOrderTel());
        order.setOrderAddress(orderFormDto.getAddress());
        order.setOrderDtlAddress(orderFormDto.getDtlAddress());
        order.setDelReq(orderFormDto.getDelReq());
        order.setReqWrite(orderFormDto.getReqWrite());
        order.setGetTicket(orderFormDto.getGetTicket());
        order.setOrderStatus(OrderStatus.ORDER);

        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
