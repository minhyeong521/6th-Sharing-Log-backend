package gdg.sharinglog.exception;

// 로그인한 이메일로 DB에서 User를 못 찾았을 때 (예: 구글 로그인만 하고 User 저장은 안 된 경우).
// GlobalExceptionHandler에서 404로 변환된다.
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("사용자를 찾을 수 없습니다: " + email);
    }
}
