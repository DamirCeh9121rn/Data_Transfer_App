import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
/**
 *
 */
public abstract class Config {

    /**
     * @serial fileName naziv fajla
     * @serial numOfFile broj fajlova koje direktorijum podrzava
     */
    private String fileName;
    private Integer numOfFile = 100;

}
