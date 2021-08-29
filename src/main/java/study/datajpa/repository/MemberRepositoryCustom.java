package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();

    /** 인터페이스의 메서드를 직접 구현하고 싶다면? - QueryDsl 사용할 때 많이 사용함
     *
     * 1. 새로운 인터페이스를 만든다.
     * 2. 그 인터페이스의 구현체를 만든다.
     * 3. 메서드를 만든다.
     * 4. 기존의 인터페이스에서 extend 부분에 구현한 인터페이스를 추가해준다.
     */
}
