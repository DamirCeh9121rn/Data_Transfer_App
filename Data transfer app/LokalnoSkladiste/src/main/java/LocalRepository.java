import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Getter
@Setter
public class LocalRepository implements UpravljanjeSkladistem<File> {

    static {
        UpravljanjeSklExporter.kreirajUpravljanjeSkladistem(new LocalRepository());
    }

    @Override
    public void makeRootDirectory(String path, String name) {
        try {
            // objekat tipa Direktorijum
            MyRootDirImpl.getInstance().getRootConfig().setFileName(name);
            MyRootDirImpl.getInstance().setName(name);
            MyRootDirImpl.getInstance().setPath(path + '\\' + name);

            // pravimo konrkretan fajl
            File dirFile = new File(MyRootDirImpl.getInstance().getPath());
            dirFile.mkdir();
            MyFileImpl myFile = new MyFileImpl(dirFile);
            MyRootDirImpl.getInstance().setFile(myFile);

            // datum kreiranja
            MyRootDirImpl.getInstance().setDateCreated(new Date());

            //  pravimo config fajl u okviru rootDir-a
            File configFile = new File(MyRootDirImpl.getInstance().getPath() + '\\' + "config.txt");
            if (configFile.createNewFile()) {
                System.out.println("Config file created: " + configFile.getName());
            } else {
                System.out.println("Config already exists.");
            }

            FileWriter myWriter = new FileWriter(configFile);
            myWriter.write(MyRootDirImpl.getInstance().getRootConfig().toString());
            myWriter.close();
            //myFile za config file
            MyFileImpl configMyFile = new MyFileImpl(configFile);
            configMyFile.setName("config");
            configMyFile.setExtension("txt");
            configMyFile.setPath(MyRootDirImpl.getInstance().getPath() + configMyFile.getName() + "\\" + "\\." + configMyFile.getExtension());
            System.out.println(configMyFile.getPath());
            configMyFile.setDateCreated(new Date());
            configMyFile.setDateModify(new Date());
            MyRootDirImpl.getInstance().getListFiles().add(configMyFile);

            System.out.println("Successfully wrote to the config file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSizeRootDirectory(Long aLong) {
        MyRootDirImpl.getInstance().setSizeOfFile(aLong);
    }

    @Override
    public void extError(String extensions) {

        List<String> list = new ArrayList<>();
        String[] extension = extensions.split(",");
        for (int i=0; i< extension.length; i++){
            list.add(extension[i]);
        }

        MyRootDirImpl.getInstance().getRootConfig().setExtensions(list);
    }


    @Override
    public void numOfFiles(Integer numOfFiles, String path) {
        //ako je root
        if (path.equals("\\")) {
            MyRootDirImpl.getInstance().getRootConfig().setNumOfFile(numOfFiles);
            //nakon ovoga treba rewrite-ovati config fajl
            FileWriter myWriter = null;
            try {
                File toChange = new File(MyRootDirImpl.getInstance().getPath() + "\\" + "config.txt");
                myWriter = new FileWriter(toChange);
                myWriter.write(MyRootDirImpl.getInstance().getRootConfig().toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successfully wrote to the config file.");
            return;
        }
        //Ako je poddir, tj. ako uopste postoji taj path
        MyFile file = findDir(path);
        if (file instanceof MyDirImpl) {
            MyDirImpl dirImpl = (MyDirImpl) file;
            dirImpl.getConfig().setNumOfFile(numOfFiles);
            //nakon ovoga treba rewrite-ovati config fajl
            FileWriter myWriter = null;
            try {
                File toChange = new File(dirImpl.getPath() + "\\" + "config.txt");
                myWriter = new FileWriter(toChange);
                myWriter.write(dirImpl.getConfig().toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successfully wrote to the config file.");
        } else {
            System.out.println("Path/config file doesnt exist");
        }
    }

    @Override
    public boolean rootDirectoryChecker(String targetPath, Long sizeOfFile) {
        MyDirImpl dir = (MyDirImpl) findDir(targetPath);

        //ako je dir
        if(targetPath.equalsIgnoreCase("\\")){
            if(MyRootDirImpl.getInstance().getSizeOfFile() - sizeOfFile > 0){
                MyRootDirImpl.getInstance().setSizeOfFile(MyRootDirImpl.getInstance().getSizeOfFile() - sizeOfFile);
                return true;
            }
        }

        if(dir.getCurrentFileCount() + 1 > dir.getConfig().getNumOfFile()){
            return false;
        }
        if(dir.getSizeOfFile() - sizeOfFile < 0){
            return false;
        }
        dir.setSizeOfFile(dir.getSizeOfFile() - sizeOfFile);
        return true;
    }

    @SneakyThrows
    @Override
    public void makeDirectory(String path, String name) {
        // pravi se poddirektorijum sa default config-om
        MyDirImpl podDir = new MyDirImpl();
        podDir.getConfig().setFileName(name);
        podDir.setName(podDir.getConfig().getFileName());
        podDir.setPath(MyRootDirImpl.getInstance().getPath() + '\\' + path + '\\' + name);

        // Pravimo konkretan fajl
        File dirFile = new File(podDir.getPath());
        dirFile.mkdir();
        MyFileImpl myFile = new MyFileImpl(dirFile);
        myFile.setPath(path);
        myFile.setName(name);
        podDir.setFile(myFile);

        //dodajemo u parent dir, tj. u njegovu listu fajlova
        if (path.equals("\\")) {
            MyRootDirImpl.getInstance().getListFiles().add(podDir);
            //MyRootDirImpl.getInstance().setUsedMem(MyRootDirImpl.getInstance().getUsedMem() + myFile.getSizeOfFile());
        } else {
            ((MyDirImpl) findDir(path)).getListFiles().add(podDir);
        }

        // datum kreiranja
        podDir.setDateCreated(new Date());
        podDir.setDateModify(new Date());
        // parent path ('C:/Lokalni-repo')
        podDir.setParentFilePath(path);

        // kada smo napravili podDir onda pravimo config fajl u okviru njega
        File configFile = new File(podDir.getPath() + '\\' + "config.txt");
        if (configFile.createNewFile()) {
            System.out.println("Config file created: " + configFile.getName());
        } else {
            System.out.println("Config already exists.");
        }

        FileWriter myWriter = new FileWriter(configFile);
        myWriter.write(podDir.getConfig().toString());
        myWriter.close();
        //myFile za config file
        MyFileImpl configMyFile = new MyFileImpl(configFile);
        configMyFile.setName("config");
        configMyFile.setExtension("txt");
        configMyFile.setPath(podDir.getPath() + "\\" + configMyFile.getName() + "\\." + configMyFile.getExtension());
        System.out.println(configMyFile.getPath());
        configMyFile.setDateCreated(new Date());
        configMyFile.setDateModify(new Date());
        podDir.getListFiles().add(configMyFile);
        System.out.println("Successfully wrote to the config file.");
    }

    @Override
    public void makeMoreDirectory(String path, String name) {

        if(name.contains(",")){
            //prvi slucaj -> createFolder1, createFolder2
            for(String newDirName : name.split(",")){
                makeDirectory(path, newDirName);
            }
        }else{
            // drugi slucaj -> createFolder(1-4)
            String[] parts = name.split("\\(");
            String newDirName = parts[0];
            String[] range = parts[1].split("-");
            int from = Integer.valueOf(range[0]);
            int to = Integer.valueOf(String.valueOf(range[1].charAt(0)));
            for(int i=from; i<=to; i++){
                makeDirectory(path, newDirName + i);
            }
        }
    }


    @Override
    public void putFile(String filePath, String destinationPath) {
        String[] arrFilePath = filePath.split("\\\\");
        String fileName = arrFilePath[arrFilePath.length - 1];
        String destinationFilePath = MyRootDirImpl.getInstance().getPath() + '\\' + destinationPath + '\\' + fileName;

        //PRE NEGO STO GA PREMESTIMO, MORAMO DA PROVERIMO DA LI TAJ DIREKTORIJUM MOZE DA GA PODRZI
        File currFile = new File(filePath);
//        if(!rootDirectoryChecker(destinationPath, currFile.getTotalSpace())){
//            return;
//        }

        try {
            Files.move
                    (Paths.get(filePath),
                            Paths.get(destinationFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Pravimo MyFile za nas novi Fajl
        File rawFile = new File(destinationFilePath);
        MyFileImpl newFile = new MyFileImpl(rawFile);
        newFile.setName(fileName);
        newFile.setExtension(filePath.split("\\.")[1]);
        newFile.setPath(destinationFilePath);
        //treba fajl da nakacimo na direktorijum // dodamo u listu
        //ako je root
        if (destinationPath.equalsIgnoreCase("\\")) {
            MyRootDirImpl.getInstance().getListFiles().add(newFile);
        } else {
            //ako nije root, hocemo da nadjemo MyFile od tog Direktorijuma
            MyFile dirFile = findDir(destinationPath);
            ((MyDirImpl) dirFile).getListFiles().add(newFile);
            //num of file +1
            ((MyDirImpl) dirFile).setCurrentFileCount(((MyDirImpl) dirFile).getCurrentFileCount() + 1);
        }
        MyRootDirImpl.getInstance().getRootConfig().setCurrentFileCount(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() + 1);
        //MyRootDirImpl.getInstance().setUsedMem(MyRootDirImpl.getInstance().getUsedMem() + (rawFile.getTotalSpace()));
    }

    @Override
    public void putMoreFiles(String dirFromPath, String dirToPath) {

        String[] gottenPaths = dirFromPath.split(",");
        for(String paths : gottenPaths) {
           putFile(paths, dirToPath);

        }
    }

    @SneakyThrows
    @Override
    public void delete(String path) {
        // unos ce da bude za dir npr "PodDir_Hey"
        // ako ocemo da brisemo fajl "PodDir_Hey\\njami.txt"
        //kada obrisemo direktorijum ili fajl, moramo da oslobodimo memoriju
        //i oduzimamo na fileCount - 1
        File toRemove = new File(MyRootDirImpl.getInstance().getPath() + "\\" + path);
        if (toRemove.exists()) {
            //oslobadjamo memoriju
            //MyRootDirImpl.getInstance().setUsedMem(MyRootDirImpl.getInstance().getUsedMem() - toRemove.getTotalSpace());
            //file count sad se brise iz parent dir-a
            if (path.contains("\\\\")) {
                String[] type = path.split("\\\\");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < type.length - 1; i++) {
                    sb.append(type[i]);
                    sb.append("\\\\");

                    MyFile parentDir = findDir(sb.toString());
                    ((MyDirImpl) parentDir).setCurrentFileCount(((MyDirImpl) parentDir).getCurrentFileCount() - 1);
                }
            } else {
                MyRootDirImpl.getInstance().getRootConfig().setCurrentFileCount(MyRootDirImpl.getInstance().getRootConfig().getCurrentFileCount() - 1);
            }
            //dir-ove brisemo rekurzivno, fajlove samo sa .delete()
            //.txt
            if (path.contains("\\\\")) {
                String[] type = path.split("\\\\");
                if (type[type.length - 1].contains(".")) {
                    toRemove.delete();
                } else {
                    //poddir
                    deleteDirectoryRecursion(Paths.get(MyRootDirImpl.getInstance().getPath() + "\\" + path));
                }
            } else {
                if (path.contains(".")) {
                    // za fajlove u root-dir
                    toRemove.delete();
                } else {
                    //poddir ali u root-dir
                    deleteDirectoryRecursion(Paths.get(MyRootDirImpl.getInstance().getPath() + "\\" + path));
                }
            }
        } else {
            System.out.println("Ne postoji ovaj fajl");
        }
    }

    //reci da ako je fajl poziva putFile
    @SneakyThrows
    @Override
    public void move(String file, String pathTo) {
        String filePath = MyRootDirImpl.getInstance().getPath() + '\\' + file; //path file-a koji pomeramo // C://
        String[] arrFilePath = filePath.split("\\\\");
        String fileName = arrFilePath[arrFilePath.length - 1]; // ime tog direktorijuma

        String parentPath = "4" +
                "";
        if (filePath.contains("\\")) {
            String[] type = filePath.split("\\\\");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < type.length - 1; i++) {
                sb.append(type[i]);
                sb.append("\\");
                parentPath = sb.toString();
            }
        }
        String destinationFilePath = MyRootDirImpl.getInstance().getPath() + "\\" + pathTo + "\\" + fileName; //path koji ce fajl imati nakon promene mesta

        File sourceFile = new File(filePath);
        File destFile = new File(destinationFilePath);

        try {
            Files.move
                    (Paths.get(filePath),
                            Paths.get(destinationFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        File rawFile = new File(destinationFilePath);
        MyFileImpl newFile = new MyFileImpl(rawFile);
        newFile.setName(fileName);
        newFile.setPath(destinationFilePath);

        //treba fajl da nakacimo na direktorijum // dodamo u listu
        //ako je root
        if (pathTo.equalsIgnoreCase("\\")) {
            MyRootDirImpl.getInstance().getListFiles().add(newFile);

        } else {
            //ako nije root, hocemo da nadjemo MyFile od tog Direktorijuma
            MyDirImpl dirFile = (MyDirImpl)findDir(pathTo);
            (dirFile.getListFiles()).add(newFile);
            //num of file +1
            dirFile.setCurrentFileCount(dirFile.getCurrentFileCount()+1);

        }
        MyRootDirImpl.getInstance().getRootConfig().setCurrentFileCount(MyRootDirImpl.getInstance().getRootConfig().getNumOfFile() + 1);
        //MyRootDirImpl.getInstance().setUsedMem(MyRootDirImpl.getInstance().getUsedMem() + Long.valueOf(rawFile.getTotalSpace()));

    }
    @SneakyThrows
    @Override
    public void downloadFile(String pathFrom, String destinationPath) {
        Path destination = Paths.get(destinationPath);
        //Da li je root?
        if(pathFrom.equalsIgnoreCase("\\")){
            Files.copy(Paths.get(MyRootDirImpl.getInstance().getPath()), destination);
            return;
        }

        Path from = Paths.get(MyRootDirImpl.getInstance().getPath() + "\\" + pathFrom);

        //ako je fajl
        if(pathFrom.contains(".")){
            Files.copy(from, destination);
            return;
        }

        Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = from.relativize(dir);
                Path target = destination.resolve(relative);
                File folder = target.toFile();
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = from.relativize(file);
                Path target = destination.resolve(relative);
                Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @SneakyThrows
    @Override
    public void changeName(String newName, String path) {

        String filePath = MyRootDirImpl.getInstance().getPath() + '\\' + path;
        String[] arrFilePath = filePath.split("\\\\");
        String fileName = arrFilePath[arrFilePath.length - 1]; // poslednji naziv

        String parentPath = "";
        if (filePath.contains("\\")) {
            String[] type = filePath.split("\\\\");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < type.length - 1; i++) {
                sb.append(type[i]);
                sb.append("\\");
                parentPath = sb.toString();
            }
        }
        String destinationFilePath = parentPath + '\\' + newName; //path koji ce fajl imati nakon promene imena

        File sourceFile = new File(filePath);
        File destFile = new File(destinationFilePath);
        if (sourceFile.renameTo(destFile)) {

            System.out.println("Directory renamed successfully");
        } else {
            System.out.println("Directory failed to rename");
        }

        if(path.contains(".txt")) {
            sourceFile.delete();
            return;
        }
        //myfile obj
        MyFileImpl myNewFile = (new MyFileImpl(destFile));
        myNewFile.setName(newName);
        myNewFile.setPath(destinationFilePath);
        //dir obj

        MyDirImpl dirObj = (MyDirImpl)findDir(path);

            //brisemo stari
            MyRootDirImpl.getInstance().getListFiles().remove(dirObj);
            //menjamo stari da bismo napravili novi
            dirObj.setFile(myNewFile);
            dirObj.setName(newName);
            dirObj.setDateModify(new Date());
            dirObj.getConfig().setFileName(newName);

            MyRootDirImpl.getInstance().getListFiles().add(dirObj);

            //treba sad ovde promeniti unutar konfig fajla
            File configFile = new File(destinationFilePath + '\\' + "config.txt");
            if (configFile.createNewFile()) {
                System.out.println("Config file created: " + configFile.getName());
            } else {
                System.out.println("Config already exists.");
            }

            FileWriter myWriter = new FileWriter(configFile);
            myWriter.write(dirObj.getConfig().toString());
            myWriter.close();

            MyFileImpl configMyFile = new MyFileImpl(configFile);
            configMyFile.setName("config");
            configMyFile.setDateCreated(new Date());
            configMyFile.setDateModify(new Date());
            dirObj.getListFiles().add(configMyFile);

    }

    @Override
    public List<MyFile> getFiles(String dirPath) {
        if(dirPath.equals("\\")){
            return MyRootDirImpl.getInstance().getListFiles();
        }else{
            return ((MyDirImpl)findDir(dirPath)).getListFiles();
        }
    }

    @Override
    public List<MyFile> getAllFiles(String dirPath) {
        List<MyFile> files = new ArrayList<>();
        //ako je root
        if(dirPath.equals("\\")){
            for(MyFile myFile : MyRootDirImpl.getInstance().getListFiles()){
                if(myFile instanceof MyDirImpl){ // PodDir_Hey
                    files.addAll(getAllFiles(myFile.getName()));
                    files.add(myFile);
                }else{
                    files.add(myFile);
                }
            }
        }else{
            //nije root nego neki poddir //PodDir_Hey//Hello
            MyDirImpl dir = (MyDirImpl)findDir(dirPath);
            for(MyFile myFile : dir.getListFiles()){
                if(myFile instanceof MyDirImpl){ //
                    files.addAll(getAllFiles(dirPath + '\\' + myFile.getName()));
                    files.add(myFile);
                }else{
                    files.add(myFile);
                }
            }
        }
        return files;
    }

    @Override
    public List<MyFile> getAllDirectoryFiles(String dirPath) {
            List<MyFile> files = new ArrayList<>();
            //ako je root
            if(dirPath.equals("\\")){
                for(MyFile myFile : MyRootDirImpl.getInstance().getListFiles()){
                    files.add(myFile);
                }
            }else{
                //nije root nego neki poddir
                MyDirImpl dir = (MyDirImpl)findDir(dirPath);

                for(MyFile myFile : dir.getListFiles()){
                    files.add(myFile);
                }
            }
            return files;
    }

    @Override
    public List<MyFile> getFileByExtension(String extension, String ID) {
        ArrayList<MyFile> filteredFiles = new ArrayList<>();
        ArrayList<MyFile> files = new ArrayList<>();
        files.addAll(getAllFiles(ID));

        for(MyFile file : files) {
            if (file instanceof MyFileImpl) {
                MyFileImpl fp = (MyFileImpl) file;
                if (fp.getExtension().equalsIgnoreCase(extension)) {
                    filteredFiles.add(file);
                }
            }
        }
        return filteredFiles;
    }

    @Override
    public List<MyFile> getFileBySubstring(String subString, String ID) {
        ArrayList<MyFile> filteredFiles = new ArrayList<>();
        ArrayList<MyFile> files = new ArrayList<>();
        files.addAll(getAllFiles(ID));

        for(MyFile file : files){
            //MyFileImpl fp = (MyFileImpl) file;
            if(file.getName().contains(subString)){
                filteredFiles.add(file);
            }
        }
        return filteredFiles;
    }

    @Override
    public void printFiles(List<MyFile> list) {
        for(MyFile myFile : list){
            if(myFile instanceof MyFileImpl){
                System.out.println(myFile.getName() + ' ' + myFile.getPath() + ' ' + ((MyFileImpl) myFile).getExtension() + ' ' + myFile.getDateCreated());
            }else{
                System.out.println(myFile.getName() + ' ' + myFile.getPath() + ' ' + myFile.getDateCreated());
            }

        }
    }


    @Override
    public void directoryContains(String names, String dirPath) {

        List<String> nameList = new ArrayList<>();
        String[] namesFound = names.split(",");
        for(int i = 0; i < namesFound.length; i++) {
            nameList.addAll(Collections.singleton(namesFound[i]));
        }
        ArrayList<String> found = new ArrayList<String>();

        if(dirPath.equals("\\")){
            for(String name : nameList){
                for(MyFile file: MyRootDirImpl.getInstance().getListFiles()){
                    if(file.getName().equalsIgnoreCase(name)){
                        found.add(name);
                    }
                }
            }
        }else{
            MyDirImpl dir = (MyDirImpl)findDir(dirPath);

            for(String name : nameList){
                for(MyFile file: dir.getListFiles()){
                    if(file.getName().equalsIgnoreCase(name)){
                        found.add(name);
                        break;
                    }
                }
            }
        }
        if (nameList.size()==found.size()) {
            System.out.println("DIREKTORIJUM SADRZI FAJL!");
        }
    }

    //void
    @Override
    public void findFolder(String targetName){
        MyFile myFile = null;
        for(MyFile file : MyRootDirImpl.getInstance().getListFiles()){
            myFile = findTargetFile(file.getName(), targetName);
            if(myFile != null){
                System.out.println(myFile.getName());
            }
        }
        if(myFile == null){
            System.out.println("Ne postoji fajl sa nazivom: " + targetName);
        }
    }

    //name & DateCreated su parametri za sortiranje
    @Override
    public List<MyFile> sortType(List<MyFile> files, String parametar, String tipSortiranja) {
        if(parametar.equalsIgnoreCase("name")){
            if(tipSortiranja.equalsIgnoreCase("asc")){
                Collections.sort(files, new Comparator<MyFile>() {
                    public int compare(MyFile o1, MyFile o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }else{
                Collections.sort(files, new Comparator<MyFile>() {
                    public int compare(MyFile o1, MyFile o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                });
            }
        }else if(parametar.equalsIgnoreCase("dateCreated")){
            if(tipSortiranja.equalsIgnoreCase("asc")){
                Collections.sort(files, new Comparator<MyFile>() {
                    public int compare(MyFile o1, MyFile o2) {
                        return o1.getDateCreated().compareTo(o2.getDateCreated());
                    }
                });
            }else{
                Collections.sort(files, new Comparator<MyFile>() {
                    public int compare(MyFile o1, MyFile o2) {
                        return o2.getDateCreated().compareTo(o1.getDateCreated());
                    }
                });
            }
        }
        return files;
    }

    @SneakyThrows
    @Override
    public List<MyFile> periodWhenCreated(String dirPath, Date fromDate, Date toDate) {
        ArrayList<MyFile> files = new ArrayList<>();
        MyDirImpl dir = (MyDirImpl)findDir(dirPath);

        Date from = fromDate; //Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = toDate; //Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        for(MyFile file : dir.getListFiles()){
            if((file.getDateCreated().compareTo(from) >= 0) && (file.getDateCreated().compareTo(to) < 0)){
                files.add(file);
            }
            //da vidimo i za modif
            if((file.getDateModify().compareTo(from) >= 0) && (file.getDateModify().compareTo(to) < 0)){
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public void filterData(List<MyFile> list, String filter) {
        List<MyFile> files = list;

        for (MyFile f : files) {
            String write ="";

            if(filter.contains("name"))
                write+= f.getName() + " ";

            if (filter.contains("extension")){
                if(f instanceof MyFileImpl)
                    write += ((MyFileImpl) f).getExtension() + " ";
            }

            if(filter.contains("size")){
                if(f instanceof MyFileImpl)
                    write += ((MyFileImpl) f).getExtension() + "bytes ";
            }

            if(filter.contains("path"))
                write += f.getPath() + " ";

            if(filter.contains("dateCreated"))
                write += f.getDateCreated() + " ";

            if(filter.contains("dateModify"))
                write += f.getDateModify() + " ";

            System.out.println(write);
        }
    }

    private MyFile findDir(String path){
        MyFile myFile = null;
        //Moramo da proverimo da li je prosledjena putanja do dir-a ili do poddir-a
        if(path.contains("\\")) {
            //znaci imamo poddirektorijum - PodDir_Hey\\Hello
            String[] dirPaths = path.split("\\\\");

            for(int i=0; i < dirPaths.length; i++) {
                if (i == 0) {
                    for (MyFile file : MyRootDirImpl.getInstance().getListFiles()) {
                        if(file.getName().equalsIgnoreCase(dirPaths[i])) {
                            // Nasli smo prvi idemo dublje
                            myFile = file;
                        }
                    }
                }else{
                    //ako je dir ovo sto imamo
                    if(myFile instanceof MyDirImpl){
                        for (MyFile file : ((MyDirImpl)myFile).getListFiles()) {
                            if(file.getName().equalsIgnoreCase(dirPaths[i])) {
                                myFile = file;
                            }
                        }
                    }
                }
            }
        }else{
            // PodDir_Hey
            for(MyFile file : MyRootDirImpl.getInstance().getListFiles()){
                if(file.getName().equalsIgnoreCase(path)){
                    return file;
                }
            }
        }
        return myFile;
    }

    private MyFile findTargetFile(String path, String target){
        // Primamo sad PodDir_Hey\\Hello
        if(path.contains("\\")) {
            //znaci imamo poddirektorijum - PodDir_Hey\\Hello
            MyDirImpl currentPodDir = (MyDirImpl)findDir(path);
            for(MyFile file : currentPodDir.getListFiles()){
                //da li je ovo nas fajl?
                if(file.getName().equalsIgnoreCase(target)){
                    return currentPodDir;
                }
                //da li je ovo direktorijum?
                if(file instanceof MyDirImpl){
                    MyFile praviOwner = null;
                    praviOwner = findTargetFile(path + "\\" + file.getName(), target);
                    if(praviOwner != null){
                        //nasli smo target file u nekom od foldera unutar podDirBitchez
                        return praviOwner;
                    }
                }
            }
        }else{
            // PodDir_Hey
            for(MyFile file : MyRootDirImpl.getInstance().getListFiles()){
                if(file.getName().equalsIgnoreCase(path)){
                    //nasli smo podDirBitchez, hocemo da prodjemo kroz njegove price
                    if(file instanceof  MyDirImpl) {
                        for (MyFile podFile : ((MyDirImpl) file).getListFiles()) {
                            // da li smo ga vec nasli?
                            if (podFile.getName().equalsIgnoreCase(target)) {
                                return file;
                            }
                            //nismo ga nasli, ulazimo u taj dir
                            if (podFile instanceof MyDirImpl) {
                                MyFile praviOwner = null;
                                praviOwner = findTargetFile(path + "\\" + podFile.getName(), target);
                                if (praviOwner != null) {
                                    //nasli smo target file u nekom od foldera unutar podDirBitchez
                                    return praviOwner;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }

}
