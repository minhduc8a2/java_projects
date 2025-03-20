import java.io.Serializable;

class Book implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private String author;
    private boolean isIssued;

    

    public Book(int id, String title, String author){
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void issueBook(){
        isIssued = true;
    }

    public void returnBook(){
        isIssued = false;
    }

    @Override
    public String toString() {
        return "Book [author=" + author + ", id=" + id + ", isIssued=" + isIssued + ", title=" + title + "]";
    }


    
}