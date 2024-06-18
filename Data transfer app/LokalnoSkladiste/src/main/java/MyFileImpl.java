import lombok.Getter;

import java.io.File;
import java.util.Date;

@Getter
public class MyFileImpl extends MyFile{

    private File file;
    private String extension;

    public MyFileImpl(File file){
        this.file = file;
        //this.extension = file.getAbsolutePath().split(".")[0];
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        super.setDateCreated(dateCreated);
    }

    @Override
    public void setDateModify(Date dateModify) {
        super.setDateModify(dateModify);
    }
}
