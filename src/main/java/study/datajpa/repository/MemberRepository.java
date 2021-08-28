package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * findHelloBy 이런식으로 작성하면 member 테이블의 모든 컬럼을 조회해옴!
     * By 다음의 내용이 중요함.
     */
}
