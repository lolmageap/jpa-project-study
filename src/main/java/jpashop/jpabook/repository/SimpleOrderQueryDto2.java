package jpashop.jpabook.repository;

import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;


public record SimpleOrderQueryDto2(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
//    private Long orderId;
//    private String name;
//    private LocalDateTime orderDate;
//    private OrderStatus orderStatus;
//    private Address address;
//
//    public SimpleOrderQueryDto2(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
//        this.orderId = orderId;
//        this.name = name;
//        this.orderDate = orderDate;
//        this.orderStatus = orderStatus;
//        this.address = address;
//    }

}