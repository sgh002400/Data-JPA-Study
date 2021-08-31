package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    @Query("select m from Member m where m.username in :names") //Collection 타입으로 in절 지원
    List<Member> findByNames(@Param("names") Collection<String> names);

    /** 유연한 반환타입 지원
     * find 뒤에 붙는 List, Member, Optional은 의미 있는게 아님! 아무거나 적어도 상관 없다.
     */
    List<Member> findListByUsername(String name); //컬렉션 - 결과 없음: 빈 컬렉션 반환
    Member findMemberByUsername(String name); //단건 - 결과 없음: null 반환, 결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생
    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    /**
     * 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
     * Query.getSingleResult() 메서드를 호출한다. 이 메서드를 호출했을 때 조회 결과가 없으면
     * javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히
     * 불편하다. 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을
     * 반환한다.
     */


    /** 페이징
     *
     * 페이징과 정렬 파라미터
     * org.springframework.data.domain.Sort : 정렬 기능
     * org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)

     * 특별한 반환 타입
     * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징

     * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능
     * 내부적으로 인자로 넣어준 limit + 1조회 -> 다음 페이지가 있는지 없는지 확인할 때 사용된다.
     * 예를 들어 [더보기] 같이 페이지가 필요 없고 element가 더 있는지 없는지만 확인할 때 사용됨됨
     *
     * List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

     * Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
     * List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
     * List<Member> findByUsername(String name, Sort sort);
     */
    Page<Member> findPageByAge(int age, Pageable pageable); //반환 타입을 Page로 하고 파라미터로 Pageable interface를 넣어주면 끝! //count 쿼리 사용함

    Slice<Member> findSliceByAge(int age, Pageable pageable); //count 쿼리 사용안함

    //이렇게도 사용할 수 있다
//    List<Member> findByAge(int age, Pageable pageable); //count 쿼리 사용안함
//    List<Member> findByAge(int age, Sort sort);

    /** Count 쿼리 분리하기!
     *
     * 위에서 만든 메서드를 예로 들자면
     *
     * @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
     * Page<Member> findPageByAge(int age, Pageable pageable);
     *
     * 복잡하게 join을 여러번 하는 경우에는 count 쿼리도 join을 여러번해서 날아가게 되는데 이러면 성능상 매우 좋지 않다.
     * 그래서 그런게 필요 없는 경우에는 countQuery만 분리해서 간단하게 원하는 쿼리를 작성해줄 수 있다.
     */

    /**
     * @Modifying 어노테이션을 사용하지 않으면 다음 예외 발생
     * org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
     */
    @Modifying//벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용해야 한다.
    //영속성 컨텍스트가 초기화되지 않아서 생기는 문제는 em.clear()로 해결할 수도 있고 여기서 @Modifying(clearAutomatically = true)로 해결할 수도 있다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age); //반환 타입이 int여야 한다.

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();


    /**
     * JPQL로 fetch 조인을 쓰지 않고 @EntityGraph로 동일한 기능을 수행하는 방법!
     *
     * LEFT OUTER JOIN 사용한다.
     */
    //findAll() 공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프 -> 쿼리를 원하는대로 짜면서 fetch 기능으로 연관된걸 한번에 뽑고 싶을 때 사용
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /** JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트) **/
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //이렇게하면 내부적으로 복사본(스냅샷)을 안만든다.
    Member findReadOnlyByUsername(String username);

    /** forCounting : 반환 타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true ) **/
    @QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly",
            value = "true")},
            forCounting = true)
    Page<Member> findReadOnlyPagingByUsername(String name, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);


    /** Projection
     *
     * 반환 타입에 프로젝션하고 싶은 데이터를 넣은 인터페이스를 넣으면 끝
     */
    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
}
