package com.cultureShop.repository;

import com.cultureShop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 유저의 전체 주문 조회
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    // 유저의 주문 건수 조회
    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);

    // 결제 요청 시 해당 주문 조회
    @Query("select o from Order o " +
            "left join fetch o.payment p " +
            "left join fetch o.member m " +
            "where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPaymentAndMember(String orderUid);

    // 결제 처리 시 해당 주문 조회
    @Query("select o from Order o left join fetch o.payment p where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPayment(String orderUid);

    Order findByOrderUid(String orderUid);

    @Query("select o from Order o where o.member.email = :email order by regTime desc limit 1")
    Order findByMemberLate(String email);
}
