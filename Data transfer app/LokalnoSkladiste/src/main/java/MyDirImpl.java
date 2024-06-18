import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class MyDirImpl extends MyDirectory{

    private Config config;
    private MyFile file;
    private int currentFileCount = 0;

    public MyDirImpl(){
        config = new DirConfig();
        this.setListFiles(new ArrayList<>());
    }

    @Override
    public void addFile(MyFile myFile) {
        this.getListFiles().add(myFile);
    }

}
