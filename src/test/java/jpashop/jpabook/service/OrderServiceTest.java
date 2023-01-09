package jpashop.jpabook.service;

import jakarta.persistence.EntityManager;
import jpashop.jpabook.domain.entity.Delivery;
import jpashop.jpabook.domain.entity.Member;
import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.OrderItem;
import jpashop.jpabook.domain.entity.embedded.Address;
import jpashop.jpabook.domain.entity.enums.OrderStatus;
import jpashop.jpabook.domain.entity.exception.NotEnoughtStockException;
import jpashop.jpabook.domain.entity.item.Book;
import jpashop.jpabook.domain.entity.item.Item;
import jpashop.jpabook.repository.ItemRepository;
import jpashop.jpabook.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Item book = createBook("data jpa", 10000, 10);

        int orderCount = 3;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, findOrder.getStatus());
        assertEquals(1, findOrder.getOrderItems().size());
        assertEquals(10000 * orderCount, findOrder.getTotalPrice());
        assertEquals(7,book.getStockQuantity());

    }

    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("data jpa", 10000, 10);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        em.flush();
        orderService.cancelOrder(orderId);

        //then
        Order findOrder = orderRepository.findOne(orderId);
        assertEquals(10,book.getStockQuantity());
        assertEquals(OrderStatus.CANCEL,findOrder.getStatus());

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("data jpa", 10000, 10);
        int orderCount = 11;
        //then
        assertThrows(NotEnoughtStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderCount));
    }

    private Item createBook(String name, int price, int quantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("memberA");
        member.setAddress(new Address("서울", "강남", "단석빌딩"));
        em.persist(member);
        return member;
    }

}