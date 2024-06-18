public class UpravljanjeSklExporter {

    /**
     * @serial   upravljanjeSkladistem instanca odabranog skladista
     */
    private static UpravljanjeSkladistem upravljanjeSkladistem;

    /**
     *
     * @param upravljanjeSKL implementacija za odredjeno skladiste
     */
    public static void kreirajUpravljanjeSkladistem(UpravljanjeSkladistem upravljanjeSKL){
        upravljanjeSkladistem = upravljanjeSKL;
    }

    /**
     *
     * @return vraca odgovarajuce skladiste
     */
    public static UpravljanjeSkladistem getUpravljanjeSkladistem() {
        return upravljanjeSkladistem;
    }
}
