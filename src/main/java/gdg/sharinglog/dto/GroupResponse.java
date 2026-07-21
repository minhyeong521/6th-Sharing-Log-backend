package gdg.sharinglog.dto;

import gdg.sharinglog.domain.Group;
import gdg.sharinglog.domain.GroupRole;

// 그룹 생성/참여 API의 공통 응답 형태.
// role은 Group 자체가 아니라 "이 요청을 보낸 사람이 이 그룹에서 어떤 역할인지"를 담는다
// (그래서 Group 엔티티가 아니라 파라미터로 따로 받아서 조립함).
public record GroupResponse(Long id, String name, String inviteCode, GroupRole role) {
    public static GroupResponse from(Group group, GroupRole role) {
        return new GroupResponse(group.getId(), group.getName(), group.getInviteCode(), role);
    }
}
