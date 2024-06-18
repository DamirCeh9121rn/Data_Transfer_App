import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RootConfig extends Config {
    private Long size;
    private List<String> extensions;

    public RootConfig(){
        extensions = new ArrayList<>();
    }
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }


}
