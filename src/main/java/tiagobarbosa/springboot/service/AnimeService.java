package tiagobarbosa.springboot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tiagobarbosa.springboot.domain.Anime;
import tiagobarbosa.springboot.exception.BadRequestException;
import tiagobarbosa.springboot.mapper.AnimeMapper;
import tiagobarbosa.springboot.repository.AnimeRepository;
import tiagobarbosa.springboot.requests.AnimePostRequestBody;
import tiagobarbosa.springboot.requests.AnimePutRequestBody;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;

    public List<Anime> listAll() {
        return animeRepository.findAll();
    }

    public List<Anime> findByName(String name) {
        return animeRepository.findByName(name);
    }

    public Anime findByIdOrThrowBadRequestException(long id) {
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not found"));
    }

    @Transactional
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        return animeRepository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
    }

    public void delete(long id) {
        animeRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime savedAnime = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        anime.setId(savedAnime.getId());
        animeRepository.save(anime);
    }
}
