package tiagobarbosa.springboot.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class AnimePostRequestBody {
    @NotEmpty(message = "Anime name cannot be empty or null")
    private String name;
}
