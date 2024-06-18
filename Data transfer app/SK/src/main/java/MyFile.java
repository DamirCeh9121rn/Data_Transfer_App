import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public abstract class MyFile {

    /**
     * @serial name ime fajla
     * @serial path putanja zadatog direktorijuma
     * @serial parentFilePath putanja nad direktorijuma
     * @serial sizeOfFile velicina fajla
     * @serial dateCreated datum kreiranja
     * @serial dateModify datum modifikacije
     *
     */
    private String name;
    private String path;
    private String parentFilePath;
    private Long sizeOfFile;
    private Date dateCreated;
    private Date dateModify;
    private Long size;


}
