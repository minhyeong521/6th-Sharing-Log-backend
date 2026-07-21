package gdg.sharinglog.repository;

import gdg.sharinglog.domain.Rotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RotationRepository extends JpaRepository<Rotation, Long> {
    // 특정 날짜의 로테이션 목록을 조회하는 메서드
    List<Rotation> findAllByRotationDate(LocalDate date);
}
