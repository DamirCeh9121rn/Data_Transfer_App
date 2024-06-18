import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MainTest {


    public static void main(String[] args) throws IOException, GeneralSecurityException {
        UpravljanjeSkladistem upravljanjeGDrive = new UpravljanjeGDrive();

        upravljanjeGDrive.makeRootDirectory(null, "stagod");
        upravljanjeGDrive.setSizeRootDirectory(Long.parseLong("1000000"));
        upravljanjeGDrive.numOfFiles(10, null);
        upravljanjeGDrive.extError(".jpg.mp3");
       // upravljanjeGDrive.makeRootDirectory(null, "stagod");
       // upravljanjeGDrive.extError(List.of(".jpg"));
        //upravljanjeGDrive.putFile("C:\\Users\\Damir\\Desktop\\slika1.jpg", MyRootDirImpl.getInstance().getPath());
        //upravljanjeGDrive.saveConfigFile("sad");

        //upravljanjeGDrive.makeMoreDirectory("create folder(1-4)", MyRootDirImpl.getInstance().getPath());

        //upravljanjeGDrive.move(((MyFile) MyRootDirImpl.getInstance().getListFiles().get(0)).getPath(),((MyFile) MyRootDirImpl.getInstance().getListFiles().get(2)).getPath());


        //upravljanjeGDrive.putFile("C:\\Users\\Damir\\Desktop\\nissan-skyline-r34-4k-j6.jpg", ((MyFile) MyRootDirImpl.getInstance().getListFiles().get(2)).getPath());
        //upravljanjeGDrive.changeName("promenjenoIme",((MyDirImpl) MyRootDirImpl.getInstance().getListFiles().get(2)).getListFiles().get(0).getPath());
        //upravljanjeGDrive.changeName("noviFajl.txt", MyRootDirImpl.getInstance().getListFiles().get(0).getPath());
        //upravljanjeGDrive.downloadFile(MyRootDirImpl.getInstance().getListFiles().get(0).getPath(), "C:\\Users\\Damir\\Desktop\\New folder (2)");
        //upravljanjeGDrive.delete(MyRootDirImpl.getInstance().getListFiles().get(2).getPath());

        //upravljanjeGDrive.delete(((File) MyRootDirImpl.getInstance().getFile()).getId());
        //upravljanjeGDrive.changeName("novoIme", ((File) MyRootDirImpl.getInstance().getFile()).getId());
        //System.out.println(MyRootDirImpl.getInstance().getName() + " " +MyRootDirImpl.getInstance().getDateCreated() + " " + MyRootDirImpl.getInstance().getPath());

    }
}
