package gdg.sharinglog.repository;

import gdg.sharinglog.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // 새 초대코드를 뽑을 때 이미 쓰이고 있는 코드인지 확인하는 용도 (GroupService.generateUniqueInviteCode)
    boolean existsByInviteCode(String inviteCode);

    // 초대코드로 그룹 참여할 때, 코드로 실제 그룹을 찾아오는 용도
    Optional<Group> findByInviteCode(String inviteCode);
}
