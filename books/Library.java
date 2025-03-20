import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private final String FILE_NAME = "library_data.ser";

    public Library() {
        this.books = new ArrayList<>();
        loadBooks();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added successfully");
        saveBooks();
    }

    public void removeBook(int bookId) {
        books.removeIf(book -> book.getId() == bookId);
        System.out.println("Book removed successfully");
        saveBooks();
    }

    public void listBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    public void searchBook(String query) {
        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                found = true;
                System.out.println("Found: " + book);

            }
        }
        if (!found) {
            System.out.println("Book not found");
        }

    }

    public void issueBook(int bookId) {
        for(Book book : books) {
            if(book.getId() == bookId){
                if(!book.isIssued()){
                    book.issueBook();
                    System.out.println("Book issued successfully");
                    saveBooks();
                    
                }
                else{
                    System.out.println("Book is already issued");
                }
                return;
            }
        }

        System.out.println("Book not found");
    }

    public void returnBook(int bookId) {
        for(Book book : books) {
            if(book.getId() == bookId){
                if(book.isIssued()){
                    book.returnBook();
                    System.out.println("Book returned successfully");
                    saveBooks();
                }
                else{
                    System.out.println("Book is not issued");
                }
                return;
            }
        }

        System.out.println("Book not found");
    }

    private void saveBooks(){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))){
            out.writeObject(books);
        } catch (Exception e) {
            System.out.println("Error saving books");
        }
    }

    private void loadBooks(){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))){
            books = (List<Book>) in.readObject();
        } catch (FileNotFoundException e) {
            books = new ArrayList<Book>();
        }
        catch(IOException | ClassNotFoundException e){
            System.out.println("Error loading data: "+e.getMessage());
        }
    }



}
