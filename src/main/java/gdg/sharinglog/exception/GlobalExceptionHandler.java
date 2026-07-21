package gdg.sharinglog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// 컨트롤러에서 던진 예외를 { "message": "..." } 형태의 JSON 에러 응답으로 바꿔주는 곳.
// 이게 없으면 Spring 기본 에러 페이지(HTML/디폴트 JSON)가 나가서 모바일 클라이언트가 다루기 불편하다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InvalidInviteCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInviteCode(InvalidInviteCodeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AlreadyGroupMemberException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyGroupMember(AlreadyGroupMemberException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    // 그룹 이름 검증 실패 등 GroupService에서 직접 던진 IllegalArgumentException도 여기서 400으로 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
