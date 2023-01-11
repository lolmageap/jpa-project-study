package jpashop.jpabook.api;

import jpashop.jpabook.domain.dto.orderDto.OrderFlatDto;
import jpashop.jpabook.domain.dto.orderDto.OrderItemQueryDto;
import jpashop.jpabook.domain.dto.orderDto.OrderQueryDto;
import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.OrderItem;
import jpashop.jpabook.domain.entity.OrderSearch;
import jpashop.jpabook.repository.order.OrderRepository;
import jpashop.jpabook.domain.dto.orderDto.OrderDto;
import jpashop.jpabook.repository.order.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;


/**
 * V1. 엔티티 직접 노출
 * - 엔티티가 변하면 API 스펙이 변한다.
 * - 트랜잭션 안에서 지연 로딩 필요
 * - 양방향 연관관계 문제
 *
 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
 * - 트랜잭션 안에서 지연 로딩 필요
 * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
 * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
 *
 * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
 * - 페이징 가능
 * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
 * - 페이징 가능
 * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
 * - 페이징 불가능...
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        var all = orderRepository.findAllByString(new OrderSearch(null,null));
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        var all = orderRepository.findAllByString(new OrderSearch(null, null));
        var res = all.stream()
                .map(OrderDto::new)
                .collect(toList());
        return res;
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        var all = orderRepository.findAllWithItem(new OrderSearch(null, null));
        List<OrderDto> res = all.stream().map(OrderDto::new)
                .collect(toList());
        return res;
    }


    @GetMapping("/api/v3/orders/page")
    public List<OrderDto> ordersV3Paging(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                         @RequestParam(value = "limit", defaultValue = "100") int limit){
        var all = orderRepository.findAllWithItemPaging(new OrderSearch(null, null),offset,limit);
        List<OrderDto> res = all.stream().map(OrderDto::new)
                .collect(toList());
        return res;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        List<OrderQueryDto> res = orderQueryRepository.findOrderQueryDto();
        return res;
    }

    // 1:1 and N:1 매핑이 되어있는 엔티티끼리 join 해서 값을 가져오고 1:N 데이터끼리 또 join 해서 가져오고 엔티티 안에 가져온 데이터를 넣는다
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        List<OrderQueryDto> res = orderQueryRepository.findByDto_optimization();
        return res;
    }

    // 한방쿼리로 가져오고 중복 데이터를 제거하는 작업이 필요
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(){
        List<OrderFlatDto> res = orderQueryRepository.findAllByDto_flat();
////    중복 데이터 제거 작업
//        return res.stream().collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
//                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
//                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
//                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
//                        e.getKey().getAddress(), e.getValue()))
//                .collect(toList());
        return res;
    }

}
