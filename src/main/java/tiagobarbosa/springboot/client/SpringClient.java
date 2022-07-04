package tiagobarbosa.springboot.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import tiagobarbosa.springboot.domain.Anime;

import java.util.Arrays;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/findById?id={id}", Anime.class, 23);
        log.info(entity);

        Anime anime = new RestTemplate().getForObject("http://localhost:8080/animes/findById?id={id}", Anime.class, 4);
        log.info(anime);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animes));

        log.info("--------------------------------------------------");
        ResponseEntity<Object> animeList = new RestTemplate().exchange("http://localhost:8080/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        log.info(animeList.getBody());

        log.info("--------------------------------------------------");

//        Anime kancolle = Anime.builder().name("KanColle: Itsuka Ano Umi de").build();
//        Anime kancolleSaved = new RestTemplate().postForObject("http://localhost:8080/animes/", kancolle, Anime.class);
//        log.info("Saved anime -> {}", kancolleSaved);

        Anime vazzrock = Anime.builder().name("VAZZROCK THE ANIMATION").build();
        ResponseEntity<Anime> vazzrockSaved = new RestTemplate().exchange("http://localhost:8080/animes/", HttpMethod.POST, new HttpEntity<>(vazzrock, creatJsonHandler()), Anime.class);
        log.info("Saved anime -> {}", vazzrockSaved);

        log.info("--------------------------------------------------");

        Anime animeToBeUpdated = vazzrockSaved.getBody();
        assert animeToBeUpdated != null;
        animeToBeUpdated.setName("Ijiranaide, Nagatoro-san 2nd Attack");

        ResponseEntity<Void> ijiranaideUpdated = new RestTemplate().exchange("http://localhost:8080/animes/", HttpMethod.PUT, new HttpEntity<>(animeToBeUpdated, creatJsonHandler()), Void.class);
        log.info(ijiranaideUpdated);

        log.info("--------------------------------------------------");
        ResponseEntity<Void> ijiranaideDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}", HttpMethod.DELETE, null, Void.class, animeToBeUpdated.getId());
        log.info(ijiranaideDeleted);
    }

    private static HttpHeaders creatJsonHandler() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
