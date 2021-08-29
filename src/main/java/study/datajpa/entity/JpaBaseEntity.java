package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass //진짜 상속 관계는 아니고 속성들(데이터?)만 공유하는 애임.
@Getter
public class JpaBaseEntity {

    @Column(updatable = false) //createdDate의 값을 실수로 바꿔도 DB에 값이 변경되지 않는다.
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @PrePersist //persist(저장)하기 전에 이벤트를 발생시키는거
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now; //IDE가 멤버 변수에 대해서 색칠해줘서 강조할 때 외에는 빼도 됨.
        updatedDate = now; //굳이 왜 now를 왜 넣냐라고 할 수 있는데 나중에 사용할 때 편함을 느낌. null이 있으면 불편함.
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}