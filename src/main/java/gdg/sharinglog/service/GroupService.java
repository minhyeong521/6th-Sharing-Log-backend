package gdg.sharinglog.service;

import gdg.sharinglog.domain.Group;
import gdg.sharinglog.domain.GroupMember;
import gdg.sharinglog.domain.GroupRole;
import gdg.sharinglog.domain.User;
import gdg.sharinglog.dto.GroupResponse;
import gdg.sharinglog.exception.AlreadyGroupMemberException;
import gdg.sharinglog.exception.InvalidInviteCodeException;
import gdg.sharinglog.exception.UserNotFoundException;
import gdg.sharinglog.repository.GroupMemberRepository;
import gdg.sharinglog.repository.GroupRepository;
import gdg.sharinglog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;

// 그룹 생성 / 초대코드 참여의 실제 로직을 담당. 컨트롤러는 이 클래스를 호출만 한다.
@Service
@Transactional(readOnly = true) // 클래스 기본값은 조회 전용. DB에 쓰는 메서드는 아래처럼 @Transactional을 따로 붙여 덮어씀
public class GroupService {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int INVITE_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                         GroupMemberRepository groupMemberRepository,
                         UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    // 그룹 생성: 이름 검증 -> Group 저장 -> 만든 사람을 OWNER로 GroupMember에 저장
    @Transactional
    public GroupResponse createGroup(String email, String name) {
        if (!StringUtils.hasText(name) || name.length() > 50) {
            throw new IllegalArgumentException("그룹 이름은 1자 이상 50자 이하로 입력해주세요.");
        }
        User user = findUserByEmail(email);

        Group group = Group.builder()
                .name(name)
                .inviteCode(generateUniqueInviteCode())
                .build();
        groupRepository.save(group);

        GroupMember owner = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.OWNER)
                .build();
        groupMemberRepository.save(owner);

        return GroupResponse.from(group, GroupRole.OWNER);
    }

    // 초대코드 참여: 코드로 그룹 조회 -> 이미 멤버면 거절 -> MEMBER로 GroupMember에 저장
    @Transactional
    public GroupResponse joinGroup(String email, String inviteCode) {
        User user = findUserByEmail(email);
        Group group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InvalidInviteCodeException(inviteCode));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new AlreadyGroupMemberException();
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .build();
        groupMemberRepository.save(member);

        return GroupResponse.from(group, GroupRole.MEMBER);
    }

    // 컨트롤러는 OAuth2User(이메일)만 알고 있으므로, 실제 DB의 User로 바꿔주는 공통 로직
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    // 8자리 랜덤 코드를 만들고, 혹시 이미 다른 그룹이 쓰고 있으면(아주 드문 충돌) 다시 뽑는다
    private String generateUniqueInviteCode() {
        String code;
        do {
            code = generateInviteCode();
        } while (groupRepository.existsByInviteCode(code));
        return code;
    }

    private String generateInviteCode() {
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            sb.append(INVITE_CODE_CHARS.charAt(RANDOM.nextInt(INVITE_CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
