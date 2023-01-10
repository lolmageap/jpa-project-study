package jpashop.jpabook.service;

import jpashop.jpabook.domain.entity.*;
import jpashop.jpabook.domain.entity.item.Item;
import jpashop.jpabook.repository.ItemRepository;
import jpashop.jpabook.repository.MemberRepository;
import jpashop.jpabook.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional(readOnly = false)
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member findMember = memberRepository.findOne(memberId);
        Item findItem = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(findMember.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(findItem, findItem.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(findMember, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    //취소
    @Transactional(readOnly = false)
    public void cancelOrder(Long orderId){
        Order findOrder = orderRepository.findOne(orderId);
        findOrder.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
         return orderRepository.findAllByCriteria(orderSearch);
    }


}
