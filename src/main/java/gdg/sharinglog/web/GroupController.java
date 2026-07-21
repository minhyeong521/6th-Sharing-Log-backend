package gdg.sharinglog.web;

import gdg.sharinglog.dto.GroupCreateRequest;
import gdg.sharinglog.dto.GroupJoinRequest;
import gdg.sharinglog.dto.GroupResponse;
import gdg.sharinglog.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 그룹 생성/참여 JSON API. 기존 LoginController/HomeController처럼 HTML을 직접 반환하지 않고,
// 모바일 클라이언트가 그대로 파싱할 수 있게 @RestController로 JSON만 응답한다.
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // POST /api/groups  { "name": "우리집" } -> 그룹 생성, 요청자는 자동으로 OWNER가 됨
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@AuthenticationPrincipal OAuth2User principal,
                                      @RequestBody GroupCreateRequest request) {
        return groupService.createGroup(email(principal), request.name());
    }

    // POST /api/groups/join  { "inviteCode": "AB12CD34" } -> 해당 그룹에 MEMBER로 참여
    @PostMapping("/join")
    public GroupResponse joinGroup(@AuthenticationPrincipal OAuth2User principal,
                                    @RequestBody GroupJoinRequest request) {
        return groupService.joinGroup(email(principal), request.inviteCode());
    }

    // @AuthenticationPrincipal로 들어오는 건 구글 로그인 정보(OAuth2User)라서 DB의 User와는 별개.
    // 여기서는 이메일만 뽑아서 GroupService에 넘기고, 실제 User 조회는 서비스 쪽에서 처리한다.
    private String email(OAuth2User principal) {
        return principal.getAttribute("email");
    }
}
