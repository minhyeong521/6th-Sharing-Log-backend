package gdg.sharinglog.dto;

// POST /api/groups/join 요청 바디. { "inviteCode": "AB12CD34" } 형태의 JSON을 받는다.
public record GroupJoinRequest(String inviteCode) {
}
