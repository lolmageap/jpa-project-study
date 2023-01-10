package jpashop.jpabook.service;

import jpashop.jpabook.domain.entity.Member;
import jpashop.jpabook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원가입
    @Transactional(readOnly = false)
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        //Exception
    }

    //회원 단건 조회
    public Member findMember(Long id) {
        return memberRepository.findOne(id);
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = false)
    public void update(Long id, String name) {
        Member findMember = memberRepository.findOne(id);
        findMember.setName(name);
    }
}
