public class StorageManager {
    private DataStorage dataStorage;

    public StorageManager(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public void storeData(String data) {
        dataStorage.save(data);
    }

}
