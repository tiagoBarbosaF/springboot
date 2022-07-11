package tiagobarbosa.springboot.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import tiagobarbosa.springboot.domain.Anime;
import tiagobarbosa.springboot.domain.ProjectUser;
import tiagobarbosa.springboot.repository.AnimeRepository;
import tiagobarbosa.springboot.repository.ProjectUserRepository;
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
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    private static final ProjectUser USER = ProjectUser.builder()
            .name("Peter Mendonça")
            .password("{bcrypt}$2a$10$JaW2X35yHMj3vvOsISpgoei/zLTtzeqUqAzKjSOn8E4AuyPdhPQMO")
            .username("peter")
            .authorities("ROLE_USER")
            .build();
    private static final ProjectUser ADMIN = ProjectUser.builder()
            .name("Tiago Barbosa")
            .password("{bcrypt}$2a$10$JaW2X35yHMj3vvOsISpgoei/zLTtzeqUqAzKjSOn8E4AuyPdhPQMO")
            .username("tiago")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder =
                    new RestTemplateBuilder()
                            .rootUri("http://localhost:" + port)
                            .basicAuthentication("peter", "tiagospring");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder =
                    new RestTemplateBuilder()
                            .rootUri("http://localhost:" + port)
                            .basicAuthentication("tiago", "tiagospring");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("Returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        projectUserRepository.save(USER);

        String expectedName = animeSaved.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
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
        projectUserRepository.save(USER);
        String expectedName = animeSaved.getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
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
        projectUserRepository.save(USER);
        Long expectedId = animeSaved.getId();
        String url = String.format("/animes/findById/?id=%d", expectedId);
        Anime anime = testRestTemplateRoleUser.getForObject(url, Anime.class, expectedId);
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName return list of animes when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        projectUserRepository.save(USER);
        String expectedName = animeSaved.getName();
        String url = String.format("/animes/findByName?name=%s", expectedName);
        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
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
        projectUserRepository.save(USER);
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/findByName?name=sao", HttpMethod.GET, null,
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
        projectUserRepository.save(USER);

        ResponseEntity<Anime> animeResponseEntity =
                testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace updates animes when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        projectUserRepository.save(USER);
        animeSaved.setName("new name");
        ResponseEntity<Void> animeResponseEntity =
                testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(animeSaved), Void.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete removes animes when successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        projectUserRepository.save(ADMIN);
        ResponseEntity<Void> animeResponseEntity =
                testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class,
                        animeSaved.getId());
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Anime animeSaved = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        projectUserRepository.save(USER);
        ResponseEntity<Void> animeResponseEntity =
                testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class,
                        animeSaved.getId());
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
