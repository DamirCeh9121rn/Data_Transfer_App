import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MyDirImpl extends MyDirectory  {

    private Config config;

    private File file;

    public MyDirImpl(){
        config = new MyConfig();
        setListFiles(new ArrayList<>());
    }


    @Override
    public void addFile(MyFile myFile) {
        getListFiles().add(myFile);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        getFile().setName(name);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
        getFile().setId(path);
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        super.setDateCreated(dateCreated);
        getFile().setCreatedTime(new DateTime(dateCreated));
    }

    @Override
    public void setDateModify(Date dateModify) {
        super.setDateModify(dateModify);
        getFile().setModifiedTime(new DateTime(dateModify));
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
