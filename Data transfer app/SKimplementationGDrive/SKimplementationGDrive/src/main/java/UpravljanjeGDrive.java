import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.*;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.*;

public class UpravljanjeGDrive implements UpravljanjeSkladistem {

    static {
        UpravljanjeSklExporter.kreirajUpravljanjeSkladistem(new UpravljanjeGDrive());
    }

    private static final String APPLICATION_NAME = "My project";

    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    private static Drive service;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = getDriveService();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = UpravljanjeGDrive.class.getResourceAsStream("/client_secret_736009269377-i09r6ru8913ek5oa71u5djvkn7q4gikb.apps.googleusercontent.com.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public void makeRootDirectory(String parentID, String name) {
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        File file = null;
        try {
            if (parentID.equals("")) {
                file = service.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                file.setCreatedTime(new DateTime(System.currentTimeMillis()));

            } else {
                fileMetadata.setParents(Collections.singletonList(parentID));
                file = service.files().create(fileMetadata)
                        .setFields("id, parents")
                        .execute();

                file.setCreatedTime(new DateTime(System.currentTimeMillis()));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyRootDirImpl.getInstance().setFile(file);
        MyRootDirImpl.getInstance().setName(name);
        MyRootDirImpl.getInstance().setPath(file.getId());
        MyRootDirImpl.getInstance().setParentFilePath(parentID);
        MyRootDirImpl.getInstance().setDateCreated(new Date(new DateTime(System.currentTimeMillis()).getValue()));
        MyRootDirImpl.getInstance().getRootConfig().setSize(Long.MAX_VALUE);

    }

    @Override
    public void setSizeRootDirectory(Long size) {
        MyRootDirImpl.getInstance().getRootConfig().setSize(size);
    }

    @Override
    public void numOfFiles(Integer integer, String fileID) {
        if(fileID.equals("")){
            MyRootDirImpl.getInstance().getRootConfig().setNumOfFile(integer);
            return;
        }

        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());
        if(file instanceof MyDirImpl){
            ((MyDirImpl) file).getConfig().setNumOfFile(integer);
        }
    }

    private void saveConfig() throws IOException {
        java.io.File config = new java.io.File("C:\\Users\\Damir\\Desktop\\SKimplementationGDrive\\SKimplementationGDrive\\src\\main\\resources\\config.txt");
        if(MyRootDirImpl.getInstance().getConfigID() == null){
            config.createNewFile();
                FileWriter fileWriter = new FileWriter(config);
                fileWriter.write(MyRootDirImpl.getInstance().printRoot());
                fileWriter.close();


        }else {
            delete(MyRootDirImpl.getInstance().getConfigID());

            FileWriter fw = new FileWriter(config, false);

            PrintWriter pw = new PrintWriter(fw, false);

            pw.flush();

            fw.write(MyRootDirImpl.getInstance().printRoot());
//            for(String s: MyRootDirImpl.getInstance().getDirNameAndSize())
//                fw.write(s+"\n");
            for (Map.Entry<String, String> dir: MyRootDirImpl.getInstance().getMapNameAndSize().entrySet()) {
                fw.write(dir.getValue() + "\n");
            }


            pw.close();

            fw.close();
        }

        File fileMetadata = new File();
        fileMetadata.setName("Config");
        FileContent mediaContet = new FileContent("text/plain", config);
        fileMetadata.setMimeType("text/plain");

        try {
            fileMetadata.setParents(Collections.singletonList(MyRootDirImpl.getInstance().getPath()));
            File file = service.files().create(fileMetadata, mediaContet)
                    .setFields("id, parents")
                    .execute();
            MyFileImpl myFile = new MyFileImpl();
            myFile.setFile(file);
            myFile.setName("Config");
            myFile.setExtension(".txt");
            myFile.setPath(file.getId());
            myFile.setParentFilePath(MyRootDirImpl.getInstance().getPath());
            myFile.setSizeOfFile(config.length());
            myFile.setDateCreated(new Date(new DateTime(System.currentTimeMillis()).getValue()));
            MyRootDirImpl.getInstance().addFile(myFile);
            MyRootDirImpl.getInstance().setConfigID(file.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean rootDirectoryChecker(String extension, Long size) {
        if(MyRootDirImpl.getInstance().getRootConfig().getExtensions().size()> 0
                && MyRootDirImpl.getInstance().getRootConfig().getExtensions().contains(extension)) {
            System.out.println("Skladiste ne podrzava format: " + extension);
            return false;
        }
        Long usedMem = MyRootDirImpl.getInstance().getUsedMem();
        Long sizeOfStorage = MyRootDirImpl.getInstance().getRootConfig().getSize();

        if( sizeOfStorage - usedMem - size <0 ) {
            System.out.println("Skladiste neam dovoljno memorije za taj fajl ");
            return false;
        }
        MyRootDirImpl.getInstance().setUsedMem((int) (usedMem+size));
        return true;
    }

    @Override
    public void makeDirectory(String fileID, String name) {
        MyDirectory  directory = (MyDirectory) pomFindFile(fileID, MyRootDirImpl.getInstance());
        if(directory == null){
            System.out.println("nije pronadjena lokacija");
            return;
        }

        if(directory instanceof  MyRootDirImpl){
            if(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() < MyRootDirImpl.getInstance().getListFiles().size() +1){
                System.out.println("U direkrorijumu " + MyRootDirImpl.getInstance().getName() + "  nema vise mesta za fajlove");
                return;
            }
        }else if(directory instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) directory;
            if(myDir.getConfig().getNumOfFile() < myDir.getListFiles().size() +1 ){
                System.out.println("U direkrorijumu " + myDir.getName() + "  nema vise mesta za fajlove");
                return;
            }
        }

        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(fileID));
        File file= null;
        try {
            file = service.files().create(fileMetadata)
                    .setFields("id, parents")
                    .execute();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to create folder: " + e.getDetails());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyDirImpl newDir = new MyDirImpl();
        newDir.setFile(file);
        newDir.setName(name);
        newDir.setPath(file.getId());
        newDir.setParentFilePath(fileID);
        newDir.setDateCreated(new Date(new DateTime(System.currentTimeMillis()).getValue()));

        System.out.println("Da li zelite da zadate broj fajlova za direktorijum " + newDir.getName()+ "? (y,n)");
        String ans= "";
        Scanner in = new Scanner(System.in);
        ans = in.nextLine();
        int brojFajlova = 30;
        if(ans.equals("y")){
            System.out.println("Unesite broj fajlova: ");
            brojFajlova = Integer.parseInt(in.nextLine());
            newDir.getConfig().setNumOfFile(brojFajlova);
            //MyRootDirImpl.getInstance().getDirNameAndSize().add(newDir.getName() + "-" + newDir.getConfig().getNumOfFile());
            MyRootDirImpl.getInstance().getMapNameAndSize().put(newDir.getPath(), newDir.getName() + "-" + newDir.getConfig().getNumOfFile());
        }else {
            newDir.getConfig().setNumOfFile(brojFajlova);
            //MyRootDirImpl.getInstance().getDirNameAndSize().add(newDir.getName() + "-" + newDir.getConfig().getNumOfFile());
            MyRootDirImpl.getInstance().getMapNameAndSize().put(newDir.getPath(), newDir.getName() + "-" + newDir.getConfig().getNumOfFile());
        }

        directory.addFile(newDir);
        try {
            saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void makeMoreDirectory(String fileID, String str) {

        MyFile dir = pomFindFile(fileID, MyRootDirImpl.getInstance());

        if(!(dir instanceof MyDirectory)) {
            System.out.println("Izabrani fajl nije direktorijum");
            return;
        }
        MyDirImpl myDir= (MyDirImpl) dir;
        if(str.contains(",")) {
           String[] name = str.split(",");

            if(dir instanceof  MyRootDirImpl){
                if(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() < MyRootDirImpl.getInstance().getListFiles().size() + name.length){
                    System.out.println("U direktoriju "+ myDir.getName() + " nema dovoljno mesta za sve fajlove");
                    return;
                }
            }else if(dir instanceof MyDirImpl){
                MyDirImpl myDirImp = (MyDirImpl) dir;
                if(myDirImp.getConfig().getNumOfFile() < myDir.getListFiles().size() + name.length ){
                    System.out.println("U direktoriju "+ myDir.getName() + " nema dovoljno mesta za sve fajlove");
                    return;
                }
            }

            for (String s: name) {
                makeDirectory(fileID, s);
            }
        }else {
            String name = str.substring(0, str.indexOf("("));
            //create folder(1-10)
            if(dir instanceof  MyRootDirImpl){
                if(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() < MyRootDirImpl.getInstance().getListFiles().size() + Integer.parseInt(str.substring(str.indexOf("-")+1, str.indexOf(")")))){
                    System.out.println("U direktoriju "+ myDir.getName() + " nema dovoljno mesta za sve fajlove");
                    return;
                }
            }else if(dir instanceof MyDirImpl){
                MyDirImpl myDirImp = (MyDirImpl) dir;
                if(myDirImp.getConfig().getNumOfFile() < myDir.getListFiles().size() + Integer.parseInt(str.substring(str.indexOf("-")+1, str.indexOf(")"))) ){
                    System.out.println("U direktoriju "+ myDir.getName() + " nema dovoljno mesta za sve fajlove");
                    return;
                }
            }
            for(int i = Integer.parseInt(str.substring(str.indexOf("(")+1, str.indexOf("-")));
                i<= Integer.parseInt(str.substring(str.indexOf("-")+1, str.indexOf(")"))); i++){
                makeDirectory(fileID, name + i);
            }
        }
    }

    @Override
    public void putFile(String filePath, String fileID) {
        File fileMetadata = new File();
        String fileName[] = filePath.split("\\\\");
        String name = fileName[fileName.length -1].substring(0,fileName[fileName.length -1].indexOf('.'));
        String extension = fileName[fileName.length -1].substring(fileName[fileName.length -1].indexOf('.'), fileName[fileName.length -1].length());
        java.io.File fileForGDrive = new java.io.File(filePath);


        if(!rootDirectoryChecker(extension,fileForGDrive.length())){
            return;
        }

        MyDirectory directory = (MyDirectory) pomFindFile(fileID, MyRootDirImpl.getInstance());

        if(directory instanceof  MyRootDirImpl){
            if(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() < MyRootDirImpl.getInstance().getListFiles().size() +1){
                System.out.println("U direkrorijumu " + MyRootDirImpl.getInstance().getName() + "  nema vise mesta za fajlove");
                return;
            }
        }else if(directory instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) directory;
            if(myDir.getConfig().getNumOfFile() < myDir.getListFiles().size() +1 ){
                System.out.println("U direkrorijumu " + myDir.getName() + "  nema vise mesta za fajlove");
                return;
            }
        }

        fileMetadata.setName(name);
        FileContent mediaContet = null;

        if(".jpg.jpeg".contains(extension)){
            fileMetadata.setMimeType("image/jpeg");
            mediaContet = new FileContent("image/jpeg", fileForGDrive);
        }else if(".png".contains(extension)){
            fileMetadata.setMimeType("image/png");
            mediaContet = new FileContent("image/png", fileForGDrive);
        }else if(".docx".contains(extension)){
            fileMetadata.setMimeType("application/vnd.google-apps.document");
            mediaContet = new FileContent("application/vnd.google-apps.document", fileForGDrive);
        }else if(".csv".contains(extension)){
            fileMetadata.setMimeType("text/plain");
            mediaContet = new FileContent("text/plain", fileForGDrive);
        }else if(".json".contains(extension)){
            fileMetadata.setMimeType("application/vnd.google-apps.script+json");
            mediaContet = new FileContent("application/vnd.google-apps.script+json", fileForGDrive);
        }else if(".zip".equals(extension)){
            fileMetadata.setMimeType("application/zip");
            mediaContet = new FileContent("application/zip", fileForGDrive);
        } else if(".rar".equals(extension)){
            fileMetadata.setMimeType("application/rar");
            mediaContet = new FileContent("application/rar", fileForGDrive);
        }else if (".txt".equals(extension)) {
            fileMetadata.setMimeType("text/plain");
            mediaContet = new FileContent("text/plain", fileForGDrive);
        } else if(".pdf".contains(extension)){
            fileMetadata.setMimeType("application/pdf");
            mediaContet = new FileContent("application/pdf", fileForGDrive);
        }else if(".mp3".contains(extension)){
            fileMetadata.setMimeType("audio/mpeg");
            mediaContet = new FileContent("audio/mpeg", fileForGDrive);
        }


        try {
            fileMetadata.setParents(Collections.singletonList(fileID));
            File file = service.files().create(fileMetadata, mediaContet)
                    .setFields("id, parents")
                    .execute();
            MyFileImpl myFile = new MyFileImpl();
            myFile.setFile(file);
            myFile.setName(name);
            myFile.setExtension(extension);
            myFile.setPath(file.getId());
            myFile.setParentFilePath(fileID);
            myFile.setSizeOfFile(fileForGDrive.length());
            myFile.setDateCreated(new Date(new DateTime(System.currentTimeMillis()).getValue()));
            directory.addFile(myFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void putMoreFiles(String str, String fileID) {
        String[] fajlovi = str.split(",");

        for (String file : fajlovi)
            putFile(file, fileID);
    }

    @Override
    public void delete(String fileID) {
        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());
        MyDirImpl parent =(MyDirImpl) pomFindFile(file.getParentFilePath(), MyRootDirImpl.getInstance());

        try {
            for(int i = 0; i < parent.getListFiles().size(); i++){
                if(parent.getListFiles().get(i).getPath().equals(fileID)){
                    parent.getListFiles().remove(i);
                    i--;
                }
            }

            if(MyRootDirImpl.getInstance().getMapNameAndSize().containsKey(fileID)){
                MyRootDirImpl.getInstance().getMapNameAndSize().remove(fileID);
                saveConfig();
            }

            service.files().delete(fileID).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void move(String fileID, String destinationID) {
        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());
        MyDirectory parentFile = (MyDirectory) pomFindFile(file.getParentFilePath(), MyRootDirImpl.getInstance());
        MyDirectory destinationFile = (MyDirectory) pomFindFile(destinationID, MyRootDirImpl.getInstance());

        if(file instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) file;
            try {
                myDir.setFile( service.files().update(fileID, null)
                        .setAddParents(destinationID)
                        .setRemoveParents(file.getParentFilePath())
                        .setFields("id, parents")
                        .execute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            MyFileImpl myFile= (MyFileImpl) file;

            try {
                myFile.setFile( service.files().update(fileID, null)
                        .setAddParents(destinationID)
                        .setRemoveParents(parentFile.getPath())
                        .setFields("id, parents")
                        .execute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        file.setParentFilePath(destinationID);
        file.setDateModify(new Date(new DateTime(System.currentTimeMillis()).getValue()));
        for (int i = 0; i < parentFile.getListFiles().size(); i++) {
            if(parentFile.getListFiles().get(i).getPath().equals(fileID))
                parentFile.getListFiles().remove(i);
        }
        destinationFile.getListFiles().add(file);
    }

    @Override
    public void downloadFile(String fileID, String pathDestination) {
        MyFile myFile = pomFindFile(fileID, MyRootDirImpl.getInstance());

        java.io.File file = null;
        MyFileImpl fileImp = null;
        if(myFile instanceof MyFileImpl) {
            fileImp = (MyFileImpl) myFile;
            file = new java.io.File(pathDestination + java.io.File.separator + myFile.getName() + fileImp.getExtension());
        }else{
            System.out.println("Data putanja nije putanja od fajla ");
            return;
        }
        try {
            file.createNewFile();
            OutputStream outputStream = new ByteArrayOutputStream();

            if(".jpg.png.jpeg.txt.csv.zip.pdf.mp3.rar".contains(fileImp.getExtension())){
                 //service.files().export(fileID, "image/jpg").executeMediaAndDownloadTo(outputStream);
                service.files().get(fileID).executeMediaAndDownloadTo(outputStream);
            }else if(".docx".contains(fileImp.getExtension())){
                service.files().export(fileID,"application/vnd.google-apps.document" ).executeMediaAndDownloadTo(outputStream);
            }else if(".json".equals(fileImp.getExtension())){
                service.files().export(fileID, "application/vnd.google-apps.script+json").executeMediaAndDownloadTo(outputStream);
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(String.valueOf(outputStream));
            fileWriter.close();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void changeName(String name, String fileID) {
        File file = new File();
        file.setName(name);

        try {
            //File updatedFile = service.files().update(fileID, file).execute();
            //File updatedFile = null;
            MyFile myFile = null;
            if(pomFindFile(fileID, MyRootDirImpl.getInstance()) instanceof MyDirImpl){
                myFile = (MyDirImpl) pomFindFile(fileID, MyRootDirImpl.getInstance());
                ((MyDirImpl) myFile).setFile(service.files().update(fileID, file).execute());
                ((MyDirImpl) myFile).setName(name);
                ((MyDirImpl) myFile).setDateModify(new Date(new DateTime(System.currentTimeMillis()).getValue()));

                if(MyRootDirImpl.getInstance().getMapNameAndSize().containsKey(fileID)){
                    MyRootDirImpl.getInstance().getMapNameAndSize().put(fileID, ((MyDirImpl) myFile).getName() + "-" + ((MyDirImpl) myFile).getConfig().getNumOfFile());
                    saveConfig();
                }

            }else{
                myFile = (MyFileImpl) pomFindFile(fileID, MyRootDirImpl.getInstance());
                ((MyFileImpl) myFile).setFile(service.files().update(fileID, file).execute());
                ((MyFileImpl) myFile).setName(name);
                ((MyFileImpl) myFile).setDateModify(new Date(new DateTime(System.currentTimeMillis()).getValue()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<MyFile> getFiles(String fileID) {
        List<MyFile> fajlovi = new ArrayList<>();
        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());
        if(file == null)
            System.out.println("Lokacija nije pronadjena");


        if(file instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) file;

            for(int i =0; i< myDir.getListFiles().size(); i++){
                    fajlovi.add(myDir.getListFiles().get(i));
            }

        }else{
            System.out.println("Lokacija koja je zadata nije direktorijum");
        }
        return fajlovi;
    }

    @Override
    public List<MyFile> getAllFiles(String fileID) {
        List<MyFile> fajlovi = new ArrayList<>();

        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());

        if(file == null)
            System.out.println("Lokacija nije pronadjena");

        if(file instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) file;

            for(int i = 0; i < myDir.getListFiles().size(); i++) {
                if (myDir.getListFiles().get(i) instanceof MyFileImpl) {
                    fajlovi.add(myDir.getListFiles().get(i));
                } else {
                    fajlovi.add(myDir.getListFiles().get(i));
                    fajlovi.addAll(getAllFiles(myDir.getListFiles().get(i).getPath()));
                }
            }
        }else{
            System.out.println("Lokacija koja je zadata nije direktorijum");
        }

        return fajlovi;

    }

    @Override
    public List<MyFile> getAllDirectoryFiles(String fileID) {
        List<MyFile> fajlovi = new ArrayList<>();
        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());
        if(file == null)
            System.out.println("Lokacija nije pronadjena");

        if(file instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) file;

            for(int i =0; i< myDir.getListFiles().size(); i++){
                if(myDir.getListFiles().get(i) instanceof MyFileImpl){
                    fajlovi.add(myDir.getListFiles().get(i));
                } else if (myDir.getListFiles().get(i) instanceof MyDirImpl) {
                    MyDirImpl subDir = (MyDirImpl) myDir.getListFiles().get(i);
                    fajlovi.add(subDir);
                    for(int j = 0; j< subDir.getListFiles().size(); j++){
                        fajlovi.add(subDir.getListFiles().get(j));
                    }
                }
            }

        }else{
            System.out.println("Lokacija koja je zadata nije direktorijum");
        }
        return fajlovi;

    }

    @Override
    public List<MyFile> getFileByExtension(String exstension, String fileID) {
        List<MyFile> fajlovi = getAllFiles(fileID);
        List<MyFile> fajloviSaEkstenzijom = new ArrayList<>();

        for(int i = 0; i < fajlovi.size(); i++){
            if(fajlovi.get(i) instanceof MyFileImpl) {
                if (((MyFileImpl) fajlovi.get(i)).getExtension().equals(exstension)) {
                    fajloviSaEkstenzijom.add(fajlovi.get(i));
                }
            }
        }



        return fajloviSaEkstenzijom;
    }

    @Override
    public List<MyFile> getFileBySubstring(String subString, String fileID) {
        List<MyFile> fajlovi = getAllFiles(fileID);
        List<MyFile> fajloviSaSubStringom= new ArrayList<>();

        for(int i = 0; i < fajlovi.size(); i++){
            if((fajlovi.get(i)).getName().contains(subString)){
                fajloviSaSubStringom.add(fajlovi.get(i));
            }
        }

        return fajloviSaSubStringom;
    }

    @Override
    public void directoryContains(String names, String fileID) {

        List<String> imenaFajlova = new ArrayList<>();
        String[] foundFilesNames = names.split(",");
        for(int i = 0; i < foundFilesNames.length; i++){
            imenaFajlova.add(foundFilesNames[i]);
        }

        MyFile file = pomFindFile(fileID, MyRootDirImpl.getInstance());

        if(file == null)
            System.out.println("Direktorijum nije ponadjen");

        if(!(file instanceof MyDirImpl))
            System.out.println("Na prosledjenoj lokaciji nije direktorijum");

        MyDirImpl myDir = (MyDirImpl) file;

        for(int i = 0; i < myDir.getListFiles().size(); i++){
            if(imenaFajlova.contains(myDir.getListFiles().get(i).getName())){
                imenaFajlova.remove(myDir.getListFiles().get(i).getName());
            }
        }
        if(imenaFajlova.size() > 0){
            System.out.println("Direktorijum ne sadrzi ove fajlove: " + imenaFajlova);
        }else if(imenaFajlova.size() == 0){
            System.out.println("Direktorijum sadrzi fajlove sa tim imenima");
        }

    }

    @Override
    public void printFiles(List list) {

        List<MyFile> fajlovi = list;

        for(MyFile file : fajlovi){
            if(file instanceof MyFileImpl){
                System.out.println(file.getName() + " " + ((MyFileImpl) file).getExtension() + " " + file.getPath() + " " + file.getSizeOfFile() + "bytes " + file.getDateCreated());
            }else {
                System.out.println(file.getName() + " " + file.getPath() + " " + file.getDateCreated());
            }
        }
    }

    @Override
    public void findFolder(String name) {
        List<MyFile> allFiles = getAllFiles(MyRootDirImpl.getInstance().getPath());
        List<String> fajlFolder = new ArrayList<>();
        String imeFajla = null;
        String imeFoldera = null;
        for (MyFile file : allFiles) {
            if(file.getName().equals(name)){
                if(file instanceof MyFileImpl){
                    imeFajla = file.getName();
                    for(MyFile folder : allFiles){
                        if(folder.getPath().equals(file.getParentFilePath())){
                            imeFoldera = folder.getName();
                            fajlFolder.add("Folder: " + imeFoldera + " Fajl: " + imeFajla);
                        }
                    }
                }
            }
        }

        if(fajlFolder.size() == 0)
            System.out.println("Ne postoji fajl sa imenom: " + name);
        else {
            for (String s : fajlFolder) {
                System.out.println(s);
            }
        }
    }

    @Override
    public List<MyFile> sortType(List list, String type, String tip) {
        List<MyFile> fajlovi = list;

        if(type.equals("name")){
            if(tip.equals("asc")) {
                Collections.sort(fajlovi, new Comparator<MyFile>() {

                    public int compare(MyFile o1, MyFile o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }else if(tip.equals("desc")){
                Collections.sort(fajlovi, new Comparator<MyFile>() {

                    public int compare(MyFile o1, MyFile o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                });
            }
        }else if(type.equals("dateCreated")){
            if(tip.equals("asc")) {
                Collections.sort(fajlovi, new Comparator<MyFile>() {

                    public int compare(MyFile o1, MyFile o2) {
                        return o1.getDateCreated().compareTo(o2.getDateCreated());
                    }
                });
            }else if(tip.equals("desc")){
                Collections.sort(fajlovi, new Comparator<MyFile>() {

                    public int compare(MyFile o1, MyFile o2) {
                        return o2.getDateCreated().compareTo(o1.getDateCreated());
                    }
                });
            }

        }

        return fajlovi;
    }

    @Override
    public List<MyFile> periodWhenCreated(String directoryID, Date periodBegin, Date periodEnd) {
        List<MyFile> fajlovi = new ArrayList<>();
        MyFile dir = pomFindFile(directoryID, MyRootDirImpl.getInstance());

        if(dir instanceof MyDirImpl){
            MyDirImpl myDir = (MyDirImpl) dir;
            for(MyFile f : myDir.getListFiles()){
                if(periodBegin.before(f.getDateCreated()) && periodEnd.after(f.getDateCreated())){
                    fajlovi.add(f);
                }else if(f.getDateModify() != null){
                    if(periodBegin.before(f.getDateModify()) && periodEnd.after(f.getDateModify())){
                        fajlovi.add(f);
                    }
                }
            }
        }else {
            System.out.println("Prosledjeni id nije id od direktorijuma");
            return fajlovi;
        }


        return fajlovi;
    }


    private MyFile pomFindFile(String fileID, Object myFile) {
        MyDirImpl file = (MyDirImpl) myFile;

        if(file.getPath().equals(fileID)) {
            return file;
        }else {
           for(int i = 0; i <  file.getListFiles().size(); i++ ) {
               MyFile f = file.getListFiles().get(i);
                if (f instanceof MyDirImpl) {
                    if (f.getPath().equals(fileID)){
                        return f;
                    }
                    if(((MyDirImpl) f).getListFiles().size() == 0)
                        continue;
                    else {
                        MyFile fajl= pomFindFile(fileID, f);
                        if(fajl == null)
                            continue;
                         return fajl;
                    }
                } else if (f instanceof MyFileImpl){
                    if (f.getPath().equals(fileID))
                        return f;
                }
            }
        }
        return null;
    }

    @Override
    public void filterData(List list, String filter) {
        List<MyFile> fajlovi = list;

        for (MyFile f : fajlovi) {
            String ispis ="";

            if(filter.contains("name"))
                ispis+= f.getName() + " ";

            if (filter.contains("extension")){
                if(f instanceof MyFileImpl)
                    ispis += ((MyFileImpl) f).getExtension() + " ";
            }

            if(filter.contains("size")){
                if(f instanceof MyFileImpl)
                    ispis += ((MyFileImpl) f).getExtension() + "bytes ";
            }

            if(filter.contains("path"))
                ispis += f.getPath() + " ";

            if(filter.contains("dateCreated"))
                ispis += f.getDateCreated() + " ";

            if(filter.contains("dateModify"))
                ispis += f.getDateModify() + " ";

            System.out.println(ispis);
        }
    }


    @Override
    public void extError(String extensions) {
        List<String> list = new ArrayList<>();
        String[] extension = extensions.split(",");
        for (int i=0; i< extension.length; i++){
            list.add(extension[i]);
        }

        MyRootDirImpl.getInstance().getRootConfig().setExtensions(list);
        try {
            saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
