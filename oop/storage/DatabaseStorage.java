public class DatabaseStorage implements DataStorage {
    @Override
    public void save(String data) {
        System.out.println("Saving \"" + data + "\" to database...");
    }
}
