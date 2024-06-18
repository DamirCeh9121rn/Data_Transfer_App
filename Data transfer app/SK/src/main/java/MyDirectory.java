import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class MyDirectory extends MyFile{

    /**
     * @serial listFiles lista fajlova koje direktorijum sadrzi
     */
    private List<MyFile> listFiles;

    /**
     *
     * @param myFile fajl koji se dodaje u direktorijum
     */
    public abstract void addFile(MyFile myFile);



}
