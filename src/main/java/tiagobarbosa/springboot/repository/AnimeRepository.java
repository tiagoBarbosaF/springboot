package tiagobarbosa.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tiagobarbosa.springboot.domain.Anime;

import java.util.List;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByName(String name);
}
