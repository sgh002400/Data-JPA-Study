package study.datajpa.repository;

public interface NestedClosedProjections {

    //유저의 이름과 팀의 이름 두 가지를 가져오기
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

    /**
     * 단 쿼리를 보면
     * 첫 번째 getUsername은 최적화를 해서 username만 가져오지만
     * 중첩 구조인 두 번째 getTeam은 team 전체를 가져오는 문제가 있다.
     *
     * 프로젝션 대상이 root 엔티티면 유용하다.
     * 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
     * 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
     * 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자
     *
     * 프로젝션 대상이 ROOT가 아니면
     * LEFT OUTER JOIN 처리
     * 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
     */
}