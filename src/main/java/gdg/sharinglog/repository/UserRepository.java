package gdg.sharinglog.repository;

import gdg.sharinglog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인한 사람(OAuth2User)의 이메일로 실제 User 엔티티를 찾을 때 사용.
    // 주의: 구글 로그인 시 User를 자동으로 저장해주는 로직은 아직 없어서,
    // DB에 미리 저장된 User가 없으면 이 메서드는 빈 값을 반환한다 (UserNotFoundException으로 이어짐).
    Optional<User> findByEmail(String email);
}
