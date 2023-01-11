package jpashop.jpabook.api;

import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.OrderSearch;
import jpashop.jpabook.repository.order.OrderRepository;
import jpashop.jpabook.domain.dto.simpleQuery.SimpleOrderQueryDto;
import jpashop.jpabook.domain.dto.simpleQuery.SimpleOrderQueryDto2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {


    //의존관계 주입
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

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */


    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderQueryDto> ordersV2(){
        var orders = orderRepository.findAllByString(new OrderSearch(null,null));

        List<SimpleOrderQueryDto> collect = orders.stream()
                .map(o -> new SimpleOrderQueryDto(o))
                .collect(toList());

        return collect;
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - fetch join으로 쿼리 1번 호출
     * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderQueryDto> ordersV3(){
        var resultList = orderRepository.findAllWithMemberDelivery();
        var collect = resultList.stream()
                .map(SimpleOrderQueryDto::new)
                .collect(toList());
        return collect;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto2> ordersV4(){
        return orderRepository.findOrderDto();
    }

}
