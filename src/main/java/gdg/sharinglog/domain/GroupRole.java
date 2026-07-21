package gdg.sharinglog.domain;

// 그룹(쉐어하우스) 안에서 이 사람이 어떤 위치인지
public enum GroupRole {
    OWNER,   // 그룹을 만든 사람
    MEMBER   // 초대코드로 나중에 들어온 사람
}
