package jpashop.jpabook.repository.order;

import jakarta.persistence.EntityManager;
import jpashop.jpabook.domain.dto.orderDto.OrderFlatDto;
import jpashop.jpabook.domain.dto.orderDto.OrderItemQueryDto;
import jpashop.jpabook.domain.dto.orderDto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDto(){
        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrder();

        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpashop.jpabook.domain.dto.orderDto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrder() {
        return em.createQuery("select new jpashop.jpabook.domain.dto.orderDto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o join o.member m " +
                        "join o.delivery d", OrderQueryDto.class)
                .setFirstResult(0)
                .setMaxResults(100)
                .getResultList();
    }

    public List<OrderQueryDto> findByDto_optimization() {
        List<OrderQueryDto> result = findOrder();

        List<OrderItemQueryDto> orderItems = findOrderItemMap(result);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<OrderItemQueryDto> findOrderItemMap(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream().map(o -> o.getOrderId())
                .collect(toList());


        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpashop.jpabook.domain.dto.orderDto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in : orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        return orderItems;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new " +
                "jpashop.jpabook.domain.dto.orderDto.OrderFlatDto(o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d " +
                "join o.orderItems oi " +
                "join oi.item i ",
                OrderFlatDto.class).getResultList();
    }
}
