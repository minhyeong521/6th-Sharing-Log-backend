package gdg.sharinglog.dto;

// POST /api/groups 요청 바디. { "name": "우리집" } 형태의 JSON을 받는다.
public record GroupCreateRequest(String name) {
}
