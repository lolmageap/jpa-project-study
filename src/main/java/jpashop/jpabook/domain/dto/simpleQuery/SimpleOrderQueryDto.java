package jpashop.jpabook.domain.dto.simpleQuery;

import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SimpleOrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderQueryDto(Order order){
        orderId = order.getId();
        name = order.getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
    }
}