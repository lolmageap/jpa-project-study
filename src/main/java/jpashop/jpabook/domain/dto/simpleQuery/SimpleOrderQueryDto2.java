package jpashop.jpabook.domain.dto.simpleQuery;

import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;


import java.time.LocalDateTime;


public record SimpleOrderQueryDto2(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {}