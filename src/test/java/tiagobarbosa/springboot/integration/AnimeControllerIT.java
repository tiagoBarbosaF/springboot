package tiagobarbosa.springboot.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import tiagobarbosa.springboot.domain.Anime;
import tiagobarbosa.springboot.repository.AnimeRepository;
import tiagobarbosa.springboot.requests.AnimePostRequestBody;
import tiagobarbosa.springboot.util.AnimeCreator;
import tiagobarbosa.springboot.util.AnimePostRequestBodyCreator;
import tiagobarbosa.springboot.wrapper.PageableResponse;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = animeSaved.getName();

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();
        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll animes when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = animeSaved.getName();

        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById animes when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        Long expectedId = animeSaved.getId();
        String url = String.format("/animes/findById/?id=%d", expectedId);
        Anime anime = testRestTemplate.getForObject(url, Anime.class, expectedId);
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName return list of animes when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = animeSaved.getName();
        String url = String.format("/animes/findByName?name=%s", expectedName);
        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName return an empty list of animes when anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
        List<Anime> animes = testRestTemplate.exchange("/animes/findByName?name=sao", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();
        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns animes when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        ResponseEntity<Anime> animeResponseEntity =
                testRestTemplate.postForEntity("/animes", animePostRequestBody, Anime.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace updates animes when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        animeSaved.setName("new name");
        ResponseEntity<Void> animeResponseEntity =
                testRestTemplate.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(animeSaved), Void.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes animes when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        ResponseEntity<Void> animeResponseEntity =
                testRestTemplate.exchange("/animes/{id}", HttpMethod.DELETE, null, Void.class, animeSaved.getId());
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
