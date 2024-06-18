import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Damir i Minja
 */
public interface UpravljanjeSkladistem<T> {

    /**
     * Metoda kreira korenski direktorijum u skladistu i dodeljuje mu ime
     *
     *
     * @param path putanja na kojoj ce biti smesten korenski direktorijum
     * @param name naziv direktorijuma
     */
    void makeRootDirectory(String path, String name);

    /**
     * Metoda dodeljuje velicinu korenskom direktorijumu u bajtovima
     *
     *
     * @param size velicina korenskog direktorijuma
     */
    void setSizeRootDirectory(Long size);

    /**
     * Dodeljuje se lista ekstenzija koje root ne podrzava
     *
     *
     * @param extensions lista ekstenzija koje se ne mogu skladistiti
     */

    void extError(String extensions);

    /**
     * Dodeljuje odredjenom direktorijumu koliko fajlova moze da sadrzi
     *
     * @param filePath  putanja do fajla kom setujemo velicinu
     * @param numOfFiles zadaje se kolicina fajlova koji se mogu smestiti u direktorijum
     */

    void numOfFiles(Integer numOfFiles, String filePath);


    /**
     * Metoda cekira da li fajl koji se inportuje u skladiste zadovoljava
     * ekstenzije koje fajl podrzava i da li u skladistu ima prostora za dati fajl
     *
     * @param extenzija extenzija fajla koji je exportuje u skladiste
     * @param sizeOfFile velicina fajla koji se exportuje
     * @return vraca da li fajl zadovoljava ova dva uslova
     *          ako zadovoljava, fajl ce biti exportovan u skladiste
     */

    boolean rootDirectoryChecker(String extenzija, Long sizeOfFile);

    /**
     * Metoda kreira direktorijum na odredjeno mesto u skladistu
     * i dodeljuje mu ime
     *
     * @param path putanja na kojoj kreiramo direktorijum
     * @param name ime direktorijuma
     */
    void makeDirectory(String path, String name); // treba da moze da pravi vise!!

    /**
     * Metoda kreira vise direktorijma po stistemu (name,name...) ili name(1-3)
     * i smesta ih na zadatu putanju u skladistu
     *
     * @param path putanja na kojoj kreiramo direktorijume
     * @param name string koji se prasira za imena direktorijuma
     */

    void makeMoreDirectory(String path, String name);

    /**
     * Metoda smesta odredjeni fajl, do kog zadajemo putanju, na odredjeno mesto u skladistu
     *
     * @param filePath putanja do fajla kojeg smestamo na odredjenu putanju u skladistu
     * @param destination zeljena destinacija fajla
     */

    void putFile(String filePath, String destination);

    /**
     * Mestoda smesta vise fajlova u skladiste po sistemu (filePath,filePath..) na odredjeno mesto u skladisu
     *
     * @param filesPaths putanja do fajlova koje smestamo na odredjenu putanju u skladistu
     * @param destination zeljena destinacija fajlova
     */
    void putMoreFiles(String filesPaths, String destination);

    /**
     * Metoda brise fajl ili direktorijum iz skladista
     *
     * @param filePath putanja falja ili direktorijuma koji brisemo
     */
    void delete(String filePath);

    /**
     * Metoda premesta fajl ili direktorijum na drugo mesto u skladistu
     *
     * @param file fajl koji premeštamo
     * @param to direktorijum u koji premestamo fajl
     */

    void move(String file, String to);

    /**
     * Metoda preuzima fajl iz skladista i smesta ga na odabrano mesto u sistemu
     *
     * @param path putanja fajla koji zelimo da preuzmemo
     * @param pathDestination putanja do lokalnog foldera
     */

    void downloadFile(String path, String pathDestination);

    /**
     * Metoda menja ime fajla ili direktoriuma u skladistu
     *
     * @param name novi naziv fajla
     * @param file fajl kome menjamo ime
     */

    void changeName(String name, String file);

    /**
     * Metoda vraca listu fajlova za zadatu putanju,
     * ispisuje samo fajlove koji se nalaze unutar direktorijuma
     *
     * @param path putanja zadatog direktorijuma
     * @return vraca fajlove iz zadatog direktorijuma
     */
    List<MyFile> getFiles(String path);

    /**
     * Metoda vraca listu svih fajlova i direktorijuma, koji se nalaze
     * u unutar direktorijuma i svim poddirektorijumima
     *
     * @param path direktorijum iz kojeg se vracaju svi fajlovi
     * @return vraca sve fajlove iz svih direktorijuma u nekom direktorijumu
     */
    List<MyFile> getAllFiles(String path);

    /**
     * Metoda vraca falove i direktorijume koji se nalaze u zadatom direktorijumu
     * i direktorijumima koji se nalaze unutar tog direktorijuma
     *
     * @param path putanja zadatog direktorijuma
     * @return vraca sve fajlove u zadatom direktorijumu i svim poddirektorijumima
     */
    List<MyFile> getAllDirectoryFiles(String path);

    /**
     * Metoda vraca listu fajlova koji su odredjene ekstenzije unutar nekog direktorijuma
     *
     * @param fileID putanja do odredjenog direktorijuma
     * @param extension odredjena ekstenzija
     * @return vraca fajlove sa određenom ekstenzijom
     */

    List<MyFile> getFileByExtension(String extension, String fileID);

    /**
     * Metoda vraca listu fajlova koji u imenu sadrze odredjeni podstring unutar nekog direktorijuma
     *
     * @param fileID putanja do direktorijma
     * @param string podstring po kojem izvrsavamo filtriranje
     * @return vraci fajlove koji u svom imenu sadrže, počinju, ili se završavaju nekim
     * zadatim podstringom
     */

    List<MyFile> getFileBySubstring(String string, String fileID);

    /**
     * Metoda ispisuje u konzoli prosledjenu listu fajlova
     *
     * @param files lista fajlova koja se ispisuje
     */
    void printFiles(List<MyFile> files);


    /**
     * Metoda proverava da li odredjeni direktorijum sadrzi fajlove sa zadatim
     * imenom ili imenima
     *
     * @param name ime ili vise imena  fajlova koje proveravamo da li se nalaze u direktorijumu
     * @param direktorijumID direktorijum nad kojim izvrsavamo proveru
     */

    void directoryContains(String name, String direktorijumID);

    /**
     * Metoda vraca folder, jedan ili vise, u kom se nalazi fajl sa odredjenim imenom
     *
     * @param file fajl kojem trazimo folder
     */
    void findFolder(String file);

    /**
     * Metoda sortira odredjenu listu, ulazno ili silazno,
     * po odredjenom parametru(name,path,dateCreated...)
     *
     * @param files lista fajlova nad kojim se izvrsava sortiranje
     * @param type kriterijum sortiranja
     * @param tip da li ce se lista sortirati ulazno ili silazno
     * @return vraca sortiranu listu po zadatom kriterijumu
     */
    List<MyFile> sortType(List<MyFile> files, String type, String tip);

    /**
     * Metoda vraca listu fajlova u odredjenom direktorijumu,
     * koji su kreirani izmedju dva zadata datuma
     *
     * @param periodBegin pocetak perioda
     * @param periodEnd kraj perioda
     * @param directory direktorijum nad kojim izvrsavamo filtriranje
     * @return vraca fajlove koji su kreirani/modifikovani u nekom periodu, u nekom
     * direktorijumu
     */

    List<MyFile> periodWhenCreated(String directory, Date periodBegin, Date periodEnd);

    /**
     * Metoda ispisuje odredjenu listu po parametru koju mu zadamo (name,path...)
     *
     *
     * @param files fajlovi koji se filtriraju
     * @param type tip po kojem se filtriraju podaci
     */
    void filterData(List<MyFile> files, String type);



}
