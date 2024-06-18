import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, ParseException {
        Class.forName("UpravljanjeGDrive");
        //Class.forName("LocalRepository");
        UpravljanjeSkladistem upravljanjeSkladistem = UpravljanjeSklExporter.getUpravljanjeSkladistem();

        boolean izadji = true;
        boolean forRoot = true;
        Scanner in = new Scanner(System.in);
        String name;
        String lokacija;
        String velicina;
        String ekstenszije;
        String path;
        String brojFajlova;
        String type;
        String str;
        String filter;
        List<MyFile> fajlovi = null;

        System.out.println("Unesi lokaciju skladista:");
        lokacija = in.nextLine();
        System.out.println("Unesi ime:");
        name = in.nextLine();
        if(lokacija.equals("")){
            upravljanjeSkladistem.makeRootDirectory("", name);
        }else{
            upravljanjeSkladistem.makeRootDirectory(lokacija, name);
        }
        System.out.println("Da li zelis da konfigurises skladiste? (y/n)");
       while(forRoot) {
           String ans = in.nextLine();
           switch (ans){
               case "y":
                   System.out.println("Unesi velicinu u bajtovima");
                   velicina = in.nextLine();
                   upravljanjeSkladistem.setSizeRootDirectory(Long.parseLong(velicina));
                   System.out.println("Unesi broj fajlova za root");
                   brojFajlova = in.nextLine();
                   upravljanjeSkladistem.numOfFiles(Integer.valueOf(brojFajlova), "");
                   System.out.println("Unesi ekstenzije koje ne podrzava skladiste:");
                   ekstenszije = in.nextLine();
                   upravljanjeSkladistem.extError(ekstenszije);
                   System.out.println("Skladiste je konfigurisano");
                   forRoot = false;
                   break;
               case "n":
                   upravljanjeSkladistem.setSizeRootDirectory(Long.MAX_VALUE);
                   upravljanjeSkladistem.numOfFiles(30, "");
                   upravljanjeSkladistem.extError("");
                   forRoot = false;
                   break;
               default:
                   break;
           }
          /* if (ans.equals("y")) {
               System.out.println("Unesi velicinu u bajtovima");
               upravljanjeSkladistem.setSizeRootDirectory(Long.parseLong(in.nextLine()));
               System.out.println("Unesi broj fajlova za root");
               upravljanjeSkladistem.numOfFiles(in.nextInt(), null);
               System.out.println("Unesi ekstenzije koje ne podrzava skladiste:");
               ekstenszije = in.nextLine();
               upravljanjeSkladistem.extError(ekstenszije);

               forRoot = false;
           } else if (ans.equals("n")) {
               upravljanjeSkladistem.setSizeRootDirectory(Long.MAX_VALUE);
               upravljanjeSkladistem.numOfFiles(30, null);
               upravljanjeSkladistem.extError("");
               forRoot = false;
           }*/
       }

        while(izadji){
            System.out.println("1. Kreiraj direktorijum\n"
                                +"2. Smesti fajl u skladiste\n"
                                +"3. Obrisi fajl\n"
                                +"4. Premesti fajl\n"
                                +"5. Preuzmi fajl\n"
                                +"6. Preimenuj fajl\n"
                                +"7. Vrati sve fajlove za zadati direktorijum\n"
                                +"8. Vrati sve fajlove iz direktorijuma\n"
                                +"9. Vrati sve fajlove iz direktorijuma i svim poddirektorijumima\n"
                                +"10. Vrati sve fajlove sa odredjenom ekstenzijom\n"
                                +"11. Vrati sve fajlove koji sadrze string\n"
                                +"12. Vrati sve fajlove koji su kreirani u nekom periodu, u nekom direktorijumu\n"
                                +"13. Filtriraj listu\n"
                                +"14. Da li direktorijum sadrzi falove sa imenima\n"
                                +"15. Pronadji folder\n"
                                +"16. Sortiraj listu\n"
                                +"17. Exit\n" );
            switch (Integer.parseInt(in.nextLine())){
                case 1:
                    System.out.println("1. Jedan direktorijum\n" +"2. Vise direktorijuma");
                    String ans = in.nextLine();
                    if(ans.equals("1")) {
                        System.out.println("Unesi ime direktorijuma");
                        name = in.nextLine();
                        System.out.println("Unesi lokaciju direktorijuma");
                        lokacija = in.nextLine();
                        upravljanjeSkladistem.makeDirectory(lokacija, name);
                    }else if(ans.equals("2")){
                        System.out.println("Unesi imena direktorijuma (name,name..) ili name(1-5)");
                        name = in.nextLine();
                        System.out.println("Unesi lokaciju direktorijuma");
                        lokacija = in.nextLine();
                        upravljanjeSkladistem.makeMoreDirectory(lokacija, name);
                    }
                    break;
                case 2:
                    System.out.println("1. Jedan fajl\n" +"2. Vise fajlova");
                    String ans1 = in.nextLine();
                    if(ans1.equals("1")) {
                        System.out.println("Unesi putanju do fajla");
                        path = in.nextLine();
                        System.out.println("Unesi lokaciju direktorijuma gde zelis da smestis fajl ");
                        lokacija = in.nextLine();
                        upravljanjeSkladistem.putFile(path, lokacija);
                    }else if(ans1.equals("2")){
                        System.out.println("Unesi putanje do fajlova (path,path...)");
                        path = in.nextLine();
                        System.out.println("Unesi lokaciju direktorijuma gde zelis da smestis fajl ");
                        lokacija = in.nextLine();
                        upravljanjeSkladistem.putMoreFiles(path, lokacija);
                    }
                    break;
                case 3:
                    System.out.println("Unesi putanju do fajla");
                    path = in.nextLine();
                    upravljanjeSkladistem.delete(path);
                    break;
                case 4:
                    System.out.println("Unesi putanju do fajla");
                    path = in.nextLine();
                    System.out.println("Unesite novu destinaciju(putanju)");
                    lokacija = in.nextLine();
                    upravljanjeSkladistem.move(path, lokacija);
                    break;
                case 5:
                    System.out.println("Unesi putanju do fajla koji zelite da skinete");
                    path = in.nextLine();
                    System.out.println("Unesi putanju gde zelite da smestite fajl u sistemu");
                    lokacija = in.nextLine();

                    upravljanjeSkladistem.downloadFile(path, lokacija);
                    break;
                case 6:
                    System.out.println("Unesi putanju fajla kojeg zelis da premienujes");
                    path = in.nextLine();
                    System.out.println("Unesi novo ime");
                    name = in.nextLine();
                    upravljanjeSkladistem.changeName(name, path);
                    break;
                case 7:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    fajlovi = upravljanjeSkladistem.getFiles(path);
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 8:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    fajlovi = upravljanjeSkladistem.getAllFiles(path);
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 9:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    fajlovi = upravljanjeSkladistem.getAllDirectoryFiles(path);
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 10:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    System.out.println("Unesite ekstenziju");
                    ekstenszije = in.nextLine();

                    fajlovi = upravljanjeSkladistem.getFileByExtension(ekstenszije, path);
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 11:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    System.out.println("Unesite substring");
                    str = in.nextLine();

                    fajlovi = upravljanjeSkladistem.getFileBySubstring(str, path);
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 12:
                    System.out.println("Unesi putanju do direkrotijuma");
                    path = in.nextLine();
                    System.out.println("Unesi pocetni datum (dd/MM/yyyy)");
                    String startDate = in.nextLine();
                    System.out.println("Unesi krajnji datum (dd/MM/yyyy)");
                    String endDate = in.nextLine();
                    SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
                    fajlovi = upravljanjeSkladistem.periodWhenCreated(path, new SimpleDateFormat("dd/MM/yyyy").parse(startDate),
                                                                            new SimpleDateFormat("dd/MM/yyyy").parse(endDate));
                    upravljanjeSkladistem.printFiles(fajlovi);
                    break;
                case 13:
                    System.out.println("Unestite podatke koje zelite da fitrirate (name,path...)");
                    filter = in.nextLine();
                    if(fajlovi != null)
                        upravljanjeSkladistem.filterData(fajlovi, filter);
                    else
                        System.out.println("Da biste filtrirali listu morate uraditi korake 7, 8, 9, 10, 11 ili 12");
                    break;
                case 14:
                    System.out.println("Unesi imena:");
                    name = in.nextLine();
                    System.out.println("Unesi putanju direktorijuma:");
                    path = in.nextLine();

                    upravljanjeSkladistem.directoryContains(name, path);
                    break;
                case 15:
                    String ime ="";
                    System.out.println("Pronadji fajlove sa imenom");
                    ime = in.nextLine();
                    upravljanjeSkladistem.findFolder(ime);
                    break;
                case 16:
                    System.out.println("Unestite po cemu zelite da sortirate (name/dateCreated)");
                    type = in.nextLine();
                    System.out.println("Da li zelite listu sortirati uzlazno ili silazno (asc/desc)");
                    String tip = in.nextLine();

                    if(fajlovi != null) {
                        fajlovi = upravljanjeSkladistem.sortType(fajlovi, type, tip);
                        upravljanjeSkladistem.printFiles(fajlovi);
                    }else
                        System.out.println("Da biste filtrirali listu morate uraditi korake 7, 8, 9, 10, 11 ili 12");

                    break;
                case 17:
                    izadji = false;
                    break;
                default:
                    break;
            }

        }

    }
}
