public class Main {
    public static void main(String[] args) {
        String data = "MySecretPassword";
        StorageManager storageManager = new StorageManager(new DatabaseStorage());
        storageManager.storeData(data);

        StorageManager storageManager2 = new StorageManager(new FileStorage());
        storageManager2.storeData(data);
    }

}
