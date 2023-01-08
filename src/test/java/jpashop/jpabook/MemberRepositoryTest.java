package jpashop.jpabook;

import jpashop.jpabook.domain.entity.Member;
import jpashop.jpabook.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void testMember() throws Exception{
        //given
        Member member = new Member();
        member.setName("memberA");

        System.out.println("member = " + member.getName());

        //when
        Long saveId = memberRepository.save(member);
        System.out.println("saveId = " + saveId);

        Member findMember = memberRepository.find(saveId);
        System.out.println("findMember = " + findMember.getName());

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember).isEqualTo(member);
    }
}
