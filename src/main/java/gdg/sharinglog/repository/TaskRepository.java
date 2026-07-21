package gdg.sharinglog.repository;

import gdg.sharinglog.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // save(), findById(), findAll() 등을 사용 가능
}