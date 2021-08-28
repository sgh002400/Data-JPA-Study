package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;


    protected Member() {
        //JPA 표준 스펙에 따라서 엔티티를 만들 때 파라미터가 없는 기본 생성자가 있어야 된다. Private가 아닌 Protected로 해야됨
        //Proxy 할 때 private로 해놓으면 막히기 때문임.
    }

    public Member(String username) {
        this.username = username;
    }
}
