package tiagobarbosa.springboot.repository;

import tiagobarbosa.springboot.domain.Anime;

import java.util.List;

public interface AnimeRepository {
    List<Anime> listAll();
}
