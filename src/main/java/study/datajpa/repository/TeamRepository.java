package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;


/**
 * @Repository 어노테이션 생략 가능!
 *
 * @Repository 어노테이션 기능은 사실 두 가지임.
 * 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
 * JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
 */
public interface TeamRepository extends JpaRepository<Team, Long> { //첫 번째 인자는 타입, 두 번째 인자는 PK
}
