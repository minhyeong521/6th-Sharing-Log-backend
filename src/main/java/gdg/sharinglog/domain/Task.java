package gdg.sharinglog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 무분별한 생성을 방지
public class Task {

    @Id
    // Task를 추가할 때마다 자동으로 번호를 매겨줌
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;    // 업무 이름

    // 어느 그룹(쉐어하우스)의 업무인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // 객체를 편리하게 만들기 위한 도구
    @Builder
    public Task(String name, Group group) {
        this.name = name;
        this.group = group;
    }
}