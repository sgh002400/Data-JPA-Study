package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //Protect 파라미터 없는 생성자 만들어준다.
@ToString(of = {"id", "username", "age"}) //team을 포함하면 연관관계 타서 무한루프가 될 수 있기 때문에 연관 관계 필드는 제외하는게 좋다.
/**
 * @ToString(exclude = "password") 붙인 뒤,
 *
 * User user = new User();
 * user.setId(1L);
 * user.setUsername("dale");
 * user.setUsername("1234");
 * user.setScores(new int[]{80, 70, 100});
 * System.out.println(user);
 *
 * 결과 -> User(id=1, username=1234, scores=[80, 70, 100])
 */
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
public class Member extends JpaBaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this(username, 0);
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}