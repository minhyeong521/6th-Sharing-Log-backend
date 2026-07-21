package gdg.sharinglog.exception;

// 존재하지 않는 초대코드로 그룹 참여를 시도했을 때. GlobalExceptionHandler에서 400으로 변환된다.
public class InvalidInviteCodeException extends RuntimeException {
    public InvalidInviteCodeException(String inviteCode) {
        super("유효하지 않은 초대코드입니다: " + inviteCode);
    }
}
