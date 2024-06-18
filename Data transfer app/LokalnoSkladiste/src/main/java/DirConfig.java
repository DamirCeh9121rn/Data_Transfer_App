import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirConfig extends Config{

    public DirConfig(){
        // po default-u name, i broj fajlova
        super.setFileName("default_poddir");
        super.setNumOfFile(10);
    }
    @Override
    public String toString() {
        return "RootConfig{" + '\n' +
                "fileName=" + super.getFileName() + '\n' +
                "numOfFile=" + super.getNumOfFile() + '\n' +
                '}';
    }
}
