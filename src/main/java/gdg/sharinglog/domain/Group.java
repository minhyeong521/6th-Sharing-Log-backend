package gdg.sharinglog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// 쉐어하우스 하나 = Group 하나. 실제 "누가 이 그룹에 속해있는지"는
// GroupMember 테이블에서 따로 관리한다 (Group 자체는 이름/초대코드만 가짐).
@Entity
@Getter
@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;    // 쉐어하우스(그룹) 이름

    // 다른 사람이 이 그룹에 들어올 때 입력하는 코드. GroupService에서 8자리로 랜덤 생성해서 넣어준다.
    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Group(String name, String inviteCode) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.createdAt = LocalDateTime.now();
    }
}
