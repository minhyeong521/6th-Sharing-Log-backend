package gdg.sharinglog.repository;

import gdg.sharinglog.domain.Group;
import gdg.sharinglog.domain.GroupMember;
import gdg.sharinglog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    // 이미 이 그룹의 멤버인지 확인 (중복 가입 방지용, GroupService.joinGroup에서 사용)
    boolean existsByGroupAndUser(Group group, User user);
}
