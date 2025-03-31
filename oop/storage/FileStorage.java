public class FileStorage implements DataStorage {
    @Override
    public void save(String data){
        System.out.println("Saving \""+data+"\" to a file...");
    }
}
