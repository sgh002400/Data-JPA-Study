package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) //Member.class는 반환 타입.
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {

        return em.createNamedQuery("Member.findByUsername", Member.class)
                        .setParameter("username", username)
                        .getResultList();
    }

    /** 순수 JPA를 사용한 페이징 쿼리
     *
     * 검색 조건: 나이가 10살
     * 정렬 조건: 이름으로 내림차순
     * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
     */
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                        .setParameter("age", age)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) { //모든 직원의 연봉 10퍼센트 인상과 같이 한번에 모든 객체를 대상으로 하는 쿼리를 벌크성 수정 쿼리라고 한다.
        int resultCount = em.createQuery(
                        "update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
        return resultCount;
    }
}