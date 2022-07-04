package tiagobarbosa.springboot.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tiagobarbosa.springboot.domain.Anime;

@DataJpaTest
@DisplayName("Tests for anime repository")
class AnimeRepositoryTest {
    @Autowired
    private AnimeRepository animeRepository;

    private Anime createAnime() {
        return Anime.builder().name("Hajime no Ippo").build();
    }

    @Test
    @DisplayName("Save creates anime when successful")
    void save_PersisteAnime_WhenSuccessful() {
        Anime animeToBeSaved = createAnime();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);
        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }
}