package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    /** MemberRepository 인터페이스가 동작한 이유
     *
     * memberRepository.getClass()를 출력해보면 class com.sun.proxy.$ProxyXXX 가 나온다.
     * -> 스프링이 인터페이스를 보고 프록시 객체를 만들어준거(구현체).
     */

    @PersistenceContext
    EntityManager em; //이게 영속성 컨텍스트이다.

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    /**
     * 같은 트랜잭션이면 같은 영속성 컨텍스트를 사용한다. 따라서 em, memberRepository, teamRepository 모두 같은 영속성 컨텍스트를 사용한다.
     */

    @Test
    public void testMember() {

        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get(); //type이 Optional로 제공이 된다.

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNameQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMemberList = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        /**
         * 두 번째 파라미터로 받은 Pagable 은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한
         * org.springframework.data.domain.PageRequest 객체를 사용한다.
         * PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력한다.
         * 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 0부터 시작한다.
         */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //Sort는 옵션임

        //when
        /** page 사용 **/
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest); //절대!!! API에 그대로 반환하면 안된다! -> DTO로 변환해서 넘겨야됨
        //반환 값을 Page로 받으면 totalCount 쿼리를 알아서 날려줌
        //Page<Member> page = memberRepository.findTop3ByAge(age); 이런식으로 단순히 3개만 조회하고 싶을 때 이런식으로 사용 가능하다!

        /** map으로 DTO로 변환해서 반환하는 방법! **/
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> pageContent = page.getContent(); //내부의 데이터를 꺼내고 싶으면 .getContent() 사용 -> 여기선 3개가 뽑히겠지

        long totalElements = page.getTotalElements(); //totalCount와 동일

        for (Member member : pageContent) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        /** page **/
        assertThat(pageContent.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?


        /** slice 사용 **/
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest); //반환 타입에 따라 쓸 수 있는 메서드가 달라진다!
        //limit 쿼리에 보면 3이 아닌 4가 들어가 있다!

        List<Member> sliceContent = slice.getContent();

        for (Member member : sliceContent) {
            System.out.println("member = " + member);
        }

        /** slice **/
        assertThat(sliceContent.size()).isEqualTo(3);
        //assertThat(slice.getTotalElements()).isEqualTo(5); //slice는 count 쿼리를 날리지 않는다!
        assertThat(slice.getNumber()).isEqualTo(0);
        //assertThat(slice.getTotalPages()).isEqualTo(2);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //JPQL을 사용하면 JPQL이 실행되기 전에 자동으로 영속성 컨텍스트가 flush되기 때문에 자동으로 먼저 위의 save 쿼리가 날라가게 된다.

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush(); //flush()를 하면 변경되지 않은 부분이 DB에 반영된다.
        //여기서 flush() 했을 때 db에 40이 반영되지 않고 41로 남아 있는 이유
        //-> 엔티티에 직접적인 변경 내용이 있어야 flush() 시점에 변경 감지의 대상이 돼서 변경된 내용을 반영하는데 벌크 연산은 엔티티에 영향을 주지 않아서 그렇다!

        em.clear(); //영속성 컨텍스트 초기화화

       //벌크 연산을 했기 때문에 DB에는 member5가 41로 update 됐지만 영속성 컨텍스트에서는 40살로 남아있다.
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        System.out.println("member5 = " + member5); //결과 : membr5 = Member(id=5, username=member5, age=40)

        //then
        assertThat(resultCount).isEqualTo(3);

        /** 벌크 연산의 문제점!
         *JPA에서는 영속성 컨테이너에서 엔티티들이 관리가 되는데 그걸 가지고 1차 캐쉬에서 변경이 일어나면 자동으로 update 쿼리를 날려주고 하는건데
         *벌크 연산은 영속성 컨텍스트를 무시하고 DB에 바로 쿼리를 날리니까 영속성 컨텍스트는 변경이 된지 모른다!!
         *
         * 해결 방법 -> 벌크 연산 이후에는 영속성 컨텍스트를 초기화하면 됨. -> em.flush(), em.clear()
         * 영속성 컨텍스트를 날려버리고 나면 .findByUsername()을 할 때 완전 깔끔한 상태에서 DB에서 다시 조회해온다.
         */

    }

    @Test
    public void findMemberLazyFetch() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> membersLazy = memberRepository.findAll();

        //then
        // N+1 문제 발생
        for (Member lazy : membersLazy) {
            System.out.println("lazy.getUsername() = " + lazy.getUsername());
            System.out.println("member.team = " + lazy.getTeam().getName());
        }

        //N+1 문제 발생 X
        List<Member> memberFetch = memberRepository.findMemberFetchJoin();
        //List<Member> membersLazy = memberRepository.findEntityGraphByUsername("member1"); 하면 위와 동일하게 fetch join 적용됨

        for (Member fetch : memberFetch) {
            System.out.println("fetch.getUsername() = " + fetch.getUsername());
            System.out.println("fetch.team = " + fetch.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {


        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush(); //영속성 컨텍스트에 있는 결과를 DB에 동기화 하는 것이지 영속성 컨텍스트를 초기화까지는 하지 않기 때문에
        em.clear(); //여기서 초기화 해준거

        //when
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");

        em.flush(); //변경 감지에 의해 update Query가 날아감

        /**
         * 변경 감지를 사용하기 위해서는 내부적으로 원본, 복사본이 있어야 변경이 되었는지 확인이 가능하기 때문에 메모리를 더 잡아 먹는다!
         *
         * Member findMember = memberRepository.findReadOnlyByUsername("member1");
         * 이 순간에 원본, 복사본을 만든다. (.setUsername()을 하지 않아도 만들어놓는다.)
         *
         * 근데 만약 나는 조회용으로만 쓰고 싶다면?? (복사용을 안만들고 싶다면?) -> Hint 사용
         * Hibernate가 제공한다. JPA 표준은 제공 X
         */

         Member findReadOnlyByUsername = memberRepository.findReadOnlyByUsername("member1");
         findReadOnlyByUsername.setUsername("member2"); //변경 감지 체크 자체를 안하기 때문에 update 쿼리도 당연히 안날라감

         em.flush();
    }

    @Test
    public void lock() {


        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findListByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections() throws Exception {

        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class); //쿼리 조건만 findbyusername이고 실제 projection에 대한 조건은 nestedclosedprojection을 따른다.

        for (NestedClosedProjections nestedClosedProjections : result) {

            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);

            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
}
