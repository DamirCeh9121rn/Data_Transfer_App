import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RootConfig extends Config{
    // velicina direktorijuma u bajtovima
    private Long size;
    // lista ekstenzija koje nisu prihvatljive
    private List<String> extensions;
    private int currentFileCount = 0;

    public RootConfig(){
        //po default-u size=MAX_VAL, prazna lista ekstenzija
        size = Long.MAX_VALUE;
        extensions = new ArrayList<>();
        // po default-u name, i broj fajlova
        super.setFileName("default_name");
        super.setNumOfFile(10);
    }

    @Override
    public String toString() {
        return "RootConfig{" + '\n' +
                "fileName=" + super.getFileName() + '\n' +
                "numOfFile=" + super.getNumOfFile() + '\n' +
                "size=" + size + '\n' +
                "extensions=" + extensions +
                '}';
    }
}
