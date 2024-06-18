import com.google.api.client.util.DateTime;
import com.google.api.client.util.Value;
import com.google.api.services.drive.model.File;

import java.io.Serializable;
import java.util.*;

public class MyRootDirImpl extends MyDirImpl {

    private static MyRootDirImpl instance =  null;
    private RootConfig rootConfig;
    private Long usedMem = Long.valueOf(0);
    private List<String> dirNameAndSize;
    private Map<String,String> mapNameAndSize;
    private String configID = null;


    private MyRootDirImpl(){
        rootConfig = new RootConfig();
        setListFiles(new ArrayList<>());
        dirNameAndSize = new ArrayList<>();
        mapNameAndSize = new HashMap<>();
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

    public String printRoot(){
        return "Name: " + this.getName() + "\n"
                + "Size: " + this.getRootConfig().getSize() + "bytes\n"
                + "Exstension: " + this.getRootConfig().getExtensions() + "\n"
                + "Num of file - " + this.getRootConfig().getNumOfFile() +"\n";
    }
    public void setUsedMem(Integer integer) {
        this.usedMem = Long.valueOf(integer);
    }

    public Long getUsedMem() {
        return usedMem;
    }

    public RootConfig getRootConfig() {
        return rootConfig;
    }

    public void setRootConfig(RootConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    public List<String> getDirNameAndSize() {
        return dirNameAndSize;
    }

    public void setDirNameAndSize(List<String> dirNameAndSize) {
        this.dirNameAndSize = dirNameAndSize;
    }

    public String getConfigID() {
        return configID;
    }

    public void setConfigID(String configID) {
        this.configID = configID;
    }

    public Map<String, String> getMapNameAndSize() {
        return mapNameAndSize;
    }
}
