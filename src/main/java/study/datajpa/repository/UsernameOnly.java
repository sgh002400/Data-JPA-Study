package study.datajpa.repository;

public interface UsernameOnly {

    //전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면?
    //조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
    //프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
    String getUsername();
    //int getAge();

    /** 두 개 이상이어도 쿼리가 날아간다.
     *    select
     *         member0_.username as col_0_0_,
     *         member0_.age as col_1_0_
     *     from
     *         member member0_
     *     where
     *         member0_.username=?
     */
}
