package jpashop.jpabook.domain.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpashop.jpabook.domain.entity.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext //entity manager 주입
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
        /*
        return member를 사용하지 않는 이유 === 커맨드와 쿼리를 분리하기 위해
        사이드 이펙트를 일으키는 커맨드성 리턴값이기 때문
 */
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }


}
