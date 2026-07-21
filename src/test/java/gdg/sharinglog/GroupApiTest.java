package gdg.sharinglog;

import tools.jackson.databind.ObjectMapper;
import gdg.sharinglog.domain.User;
import gdg.sharinglog.dto.GroupCreateRequest;
import gdg.sharinglog.dto.GroupJoinRequest;
import gdg.sharinglog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 그룹 생성/참여 API의 동작을 실제 서버(MockMvc)를 띄워서 검증하는 통합 테스트.
// 구글 로그인 시 User가 자동 저장되지 않는 문제는 아직 해결 전이라, @BeforeEach에서
// UserRepository로 테스트용 User를 직접 심어두고 oauth2Login()으로 그 이메일을 흉내낸다.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트가 끝나면 DB 변경사항을 롤백해서, 테스트끼리 데이터가 서로 섞이지 않게 한다
class GroupApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static final String OWNER_EMAIL = "owner@example.com";
    private static final String MEMBER_EMAIL = "member@example.com";

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder().email(OWNER_EMAIL).nickname("owner").build());
        userRepository.save(User.builder().email(MEMBER_EMAIL).nickname("member").build());
    }

    @Test
    void createGroupMakesRequesterOwner() throws Exception {
        mockMvc.perform(post("/api/groups")
                        .with(oauth2Login().attributes(a -> a.put("email", OWNER_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupCreateRequest("우리집"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("우리집"))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.inviteCode").isNotEmpty());
    }

    @Test
    void joinGroupWithValidInviteCodeMakesRequesterMember() throws Exception {
        String inviteCode = createGroupAndGetInviteCode();

        // owner가 만든 그룹에 다른 사람(member)이 초대코드로 들어오는 시나리오
        mockMvc.perform(post("/api/groups/join")
                        .with(oauth2Login().attributes(a -> a.put("email", MEMBER_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupJoinRequest(inviteCode))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void joinGroupWithInvalidInviteCodeReturns400() throws Exception {
        mockMvc.perform(post("/api/groups/join")
                        .with(oauth2Login().attributes(a -> a.put("email", MEMBER_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupJoinRequest("NOTREAL1"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void joiningTwiceReturns409() throws Exception {
        String inviteCode = createGroupAndGetInviteCode();

        // 첫 번째 참여는 성공
        mockMvc.perform(post("/api/groups/join")
                .with(oauth2Login().attributes(a -> a.put("email", MEMBER_EMAIL)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GroupJoinRequest(inviteCode))));

        // 같은 사람이 같은 그룹에 또 참여하면 409(Conflict)
        mockMvc.perform(post("/api/groups/join")
                        .with(oauth2Login().attributes(a -> a.put("email", MEMBER_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupJoinRequest(inviteCode))))
                .andExpect(status().isConflict());
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        // oauth2Login()을 안 붙였으니 로그인 안 한 상태 -> WebOAuthSecurityConfig 설정대로 401이 나와야 정상
        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupCreateRequest("우리집"))))
                .andExpect(status().isUnauthorized());
    }

    // 그룹을 하나 만들고, 응답 JSON에서 inviteCode만 뽑아오는 헬퍼 (join 테스트들에서 재사용)
    private String createGroupAndGetInviteCode() throws Exception {
        String response = mockMvc.perform(post("/api/groups")
                        .with(oauth2Login().attributes(a -> a.put("email", OWNER_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupCreateRequest("우리집"))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("inviteCode").asText();
    }
}
