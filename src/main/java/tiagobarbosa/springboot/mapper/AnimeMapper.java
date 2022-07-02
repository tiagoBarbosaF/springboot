package tiagobarbosa.springboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tiagobarbosa.springboot.domain.Anime;
import tiagobarbosa.springboot.requests.AnimePostRequestBody;
import tiagobarbosa.springboot.requests.AnimePutRequestBody;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    public static AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);

    public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
