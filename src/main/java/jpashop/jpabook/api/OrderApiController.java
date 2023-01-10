package jpashop.jpabook.api;

import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.OrderSearch;
import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;
import jpashop.jpabook.repository.OrderRepository;
import jpashop.jpabook.repository.SimpleOrderQueryDto2;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        var all = orderRepository.findAllByString(new OrderSearch(null,null));
        all.forEach(order -> {
            order.getMember().getName(); //lazy 강제 초기화
            order.getDelivery().getAddress(); //lazy 강제 초기화
        });
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderQueryDto> ordersV2(){
        var orders = orderRepository.findAllByString(new OrderSearch(null,null));

        List<SimpleOrderQueryDto> collect = orders.stream()
                .map(o -> new SimpleOrderQueryDto(o))
                .collect(toList());

        return collect;
    }


    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderQueryDto> ordersV3(){
        var resultList = orderRepository.findAllwithMemberDelivery();
        var collect = resultList.stream()
                .map(SimpleOrderQueryDto::new)
                .collect(toList());
        return collect;
    }  
    
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto2> ordersV4(){
        return orderRepository.findOrderDto();
    }

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
}
