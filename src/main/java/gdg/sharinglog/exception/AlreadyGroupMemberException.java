package gdg.sharinglog.exception;

// 이미 가입된 그룹에 초대코드로 또 참여하려고 할 때. GlobalExceptionHandler에서 409(Conflict)로 변환된다.
public class AlreadyGroupMemberException extends RuntimeException {
    public AlreadyGroupMemberException() {
        super("이미 해당 그룹의 멤버입니다.");
    }
}
