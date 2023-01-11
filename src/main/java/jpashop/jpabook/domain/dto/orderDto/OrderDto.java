package jpashop.jpabook.domain.dto.orderDto;

import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto{
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItem;

    public OrderDto(Order order){
        orderId = order.getId();
        name = order.getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        orderItem = order.getOrderItems()
                .stream().map(OrderItemDto::new)
                .collect(toList());
    }

}
