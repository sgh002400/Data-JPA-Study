package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    //전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면?
    //조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
    //프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공


    /** 인터페이스 기반 Open Proejctions
     * 단! 이렇게 SpEL문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다!
     * 따라서 JPQL SELECT 절 최적화가 안된다.
     *
     * 단점!
     * member의 데이터를 모두 다 가져온다. 거기에서 username과 age를 더해주는거임.
     */
    @Value("#{target.username + ' ' + target.age}") //username이랑 age를 가져와서 더해서 getUsername에 넣어줌 (SpEL문법)
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
