package jpashop.jpabook.repository.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpashop.jpabook.domain.entity.Member;
import jpashop.jpabook.domain.entity.Order;
import jpashop.jpabook.domain.entity.OrderSearch;
import jpashop.jpabook.domain.dto.simpleQuery.SimpleOrderQueryDto2;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.orderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText((CharSequence) orderSearch.memberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.orderStatus() != null) {
            query = query.setParameter("status", orderSearch.orderStatus());
        }
        if (StringUtils.hasText((CharSequence) orderSearch.memberName())) {
            query = query.setParameter("name", orderSearch.memberName());
        }
        return query.getResultList();
    }
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.orderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.orderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText((CharSequence) orderSearch.memberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.memberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    @Transactional
    public List<Order> findAllWithMemberDelivery() {
        return  em.createQuery("select o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d", Order.class).getResultList();
    }
    @Transactional
    public List<SimpleOrderQueryDto2> findOrderDto() {
        return em.createQuery(
                "select new jpashop.jpabook.repository.order.simpleQuery.SimpleOrderQueryDto2(o.id, m.name, o.orderDate, o.status, d.address) from Order o " +
                "join o.member m " +
                "join o.delivery d",
                SimpleOrderQueryDto2.class)
                .getResultList();
    }

    public List<Order> findAllWithItem(OrderSearch orderSearch) {
        return em.createQuery("select distinct o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d " +
                "join fetch o.orderItems oi " +
                "join fetch oi.item i"
                , Order.class).getResultList();
    }


    public List<Order> findAllWithItemPaging(OrderSearch orderSearch, int offset, int limit) {
        return em.createQuery("select distinct o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }

}
