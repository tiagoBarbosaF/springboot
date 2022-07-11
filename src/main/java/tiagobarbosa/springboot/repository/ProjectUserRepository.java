package tiagobarbosa.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tiagobarbosa.springboot.domain.ProjectUser;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    ProjectUser findByUsername(String username);
}
