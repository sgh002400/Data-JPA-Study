package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * findHelloBy 이런식으로 작성하면 member 테이블의 모든 컬럼을 조회해옴!
     * By 다음의 내용이 중요함.
     */

    //아래의 @Query 부분을 지워도 잘 작동한다. 왜? -> 관례적으로 메서드 이름인 findByUsername을 Member에서 @NamedQuery를 먼저 찾기 때문이다!
    //없다면 이름으로 위의 메서드처럼 이름으로 만든 쿼리를 찾는다.
    @Query(name = "Member.findByUsername") //기본적으로 Member에 @NamedQuery를 정의해줘야 한다.
    //MemberJpaRepository와 다르게 구현체를 구현하지 않아도 @NamedQuery를 쉽게 호출할 수 있음을 보여주는 예시이다.
    List<Member> findByUsername(@Param("username") String username);
    //@Param 같은건 jpql을 직접 작성 했을 때 (Member.java에 보면 :username 이런거 있을 때) 붙여준다.

    /**
     * @NamedQuery의 장점!
     * JQPL을 사용하면 이는 그냥 문자로 인식하기 때문에 컴파일 시점에 오류를 잡아주지 않는다. 해당 쿼리를 날리게 될 때 에러가 발생하는 최악의 상황이지만
     * NamedQuery를 사용하면 application 로딩 시점에 파싱을 해서 오류가 있다면 에러를 알려준다!!
     */

    /**
     * @Query는 이름이 없는 NamedQuery라고 생각하면 된다.
     * @Query 역시 application 로딩 시점에 파싱을 해서 sql을 만들어 놓는다. 그렇기 때문에 오류를 바로 잡아준다!
     */
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m") //username만 뽑는 방법
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
}
