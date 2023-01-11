package jpashop.jpabook.domain.entity;

import jpashop.jpabook.domain.entity.enums.OrderStatus;

public record OrderSearch(OrderStatus orderStatus, Member memberName) {}
