package gdg.sharinglog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "rotations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 그룹(쉐어하우스)의 로테이션인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // 누가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 무슨 업무를?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // 언제?
    @Column(name = "rotation_date", nullable = false)
    private LocalDate rotationDate;

    @Builder
    public Rotation(Group group, User user, Task task, LocalDate rotationDate) {
        this.group = group;
        this.user = user;
        this.task = task;
        this.rotationDate = rotationDate;
    }
}