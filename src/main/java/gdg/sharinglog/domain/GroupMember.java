package gdg.sharinglog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// "어떤 그룹"에 "어떤 유저"가 "어떤 역할"로 속해 있는지를 나타내는 중간 테이블.
// User와 Group을 직접 다대다로 연결하지 않고 이 엔티티를 두는 이유는
// role, joinedAt 같은 "가입 자체에 대한 정보"를 같이 저장하기 위해서.
@Entity
@Getter
@Table(name = "group_members", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
// uniqueConstraints: 같은 유저가 같은 그룹에 두 번 가입하는 걸 DB 레벨에서 막아준다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private GroupRole role;    // OWNER인지 MEMBER인지

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(Group group, User user, GroupRole role) {
        this.group = group;
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }
}
