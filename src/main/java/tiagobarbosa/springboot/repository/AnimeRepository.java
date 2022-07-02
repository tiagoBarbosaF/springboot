package tiagobarbosa.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tiagobarbosa.springboot.domain.Anime;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
}
