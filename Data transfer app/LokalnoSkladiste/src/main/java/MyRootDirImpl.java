import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;

@Getter
@Setter
public class MyRootDirImpl extends MyDirectory{

    private static MyRootDirImpl instance =  null;
    private RootConfig rootConfig;
    private MyFile file;
    // iskoriscena memorija
    private Long usedMem = Long.valueOf(0);

    private MyRootDirImpl(){
        rootConfig = new RootConfig();
        setListFiles(new ArrayList<>());
        this.setSizeOfFile(Long.MAX_VALUE);
    }

    public static MyRootDirImpl getInstance() {
        if(instance == null){
            synchronized (MyRootDirImpl.class){
                if(instance == null){
                    instance = new MyRootDirImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void addFile(MyFile myFile) {
        this.getListFiles().add(myFile);
    }
}
