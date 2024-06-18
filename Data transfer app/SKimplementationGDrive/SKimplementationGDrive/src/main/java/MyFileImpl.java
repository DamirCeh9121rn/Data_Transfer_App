import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

public class MyFileImpl extends MyFile  {

    private File file;
    private String extension;

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

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
