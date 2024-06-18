import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        LocalRepository LR = new LocalRepository();
        LR.makeRootDirectory("c:\\", "Lokalni-repo");

//        Saljemo prazan jer krecemo od c:\\Lokalni-repo
        LR.makeDirectory("\\", "PodDir_Hey");

        LR.makeDirectory("PodDir_Hey", "Hello");
        LR.makeDirectory("PodDir_Hey", "Ihatethis");
        LR.makeDirectory("PodDir_Hey", "World");
        LR.makeDirectory("PodDir_Hey", "Hey");
        LR.makeDirectory("PodDir_Hey", "WOW");

        LR.makeDirectory("PodDir_Hey\\Hello", "Hajde");

        LR.makeDirectory("PodDir_Hey\\Hello\\Hajde", "HAHA");

//
        LR.makeMoreDirectory("PodDir_Hey\\Hello", "createFolder(1-4)");
        LR.makeMoreDirectory("PodDir_Hey", "Minja1,Dunja");

        //System.out.println(LR.getFileBySubstring("ch", "PodDir_Hey"));
        //System.out.println(LR.getFileBySubstring("onf"));

        //LR.findFolder("Dajboze");
        //System.out.println("getAllFiles-> " + LR.getAllFiles("\\"));
        //LR.printFiles(LR.getAllFiles("\\"));
//        System.out.println("lista svih fajlova -> " + LR.getAllDirectoryFiles("PodDir_Hey"));
        // posto je "", sacuvace u lokalni repo, da smo hteli u Hello bilo bi "PodDir_Hey\\Hello"
        //LR.putFile("C:\\blaa.txt", "");
        //System.out.println(LR.getFileByExtension("txt"));
        //LR.putMoreFiles("C:\\Users\\User\\Desktop\\DUMMY", "\\");
        //LR.directoryContains(List.of("WOW","Hey"), "PodDir_Hey");
        //System.out.println(LR.getAllDirectoryFiles("PodDir_Hey"));
        //LR.downloadFile("PodDir_Hey", "C:\\Users\\User\\Desktop\\DUMMY");

        //System.out.println(LR.periodWhenCreated("PodDir_Hey", LocalDate.of(2022, 11, 10), LocalDate.of(2022, 11, 24)));
        //LR.move("PodDir_Hey\\Hey", "Hello\\WOW");
        //LR.move("PodDir_Hey\\WOW\\Hello", "");
        // menjamo numOfFiles u konfigu, za dati dir, proveriti config.txt fajl nakon ovoga
        //LR.numOfFiles(1312, "\\");
        //LR.changeName("PodDir1", "c:\\\\Lokalni-repo\\Dir1");
        //LR.changeName("config.txt", "PodDir_Hey\\WOW\\configNeverica.txt");
       // LR.changeName("PodDir", "PodDir_Hey");

        //LR.changeName("miiiimmm", "vvvvv\\Hello");
        //LR.changeName("LOKALNOOO", "c:\\Lokalni-repo");
        // //LR.makeDirectory("PodDir1", "IsusePogledajMe");
        //brisemo fajl
       // Hello
        //LR.delete("PodDir_Hey\\njami.txt");

        //brisemo poddir
        //LR.delete("PodDir");

       //LR.findFolder("Hey");
    }
}
