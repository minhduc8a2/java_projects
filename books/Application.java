import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n📚 Library Management System");
            System.out.println("1️⃣ Add Book");
            System.out.println("2️⃣ Remove Book");
            System.out.println("3️⃣ List All Books");
            System.out.println("4️⃣ Search Book");
            System.out.println("5️⃣ Issue Book");
            System.out.println("6️⃣ Return Book");
            System.out.println("7️⃣ Exit");
            System.out.print("👉 Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                System.out.print("📖 Enter book ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.print("📖 Enter book title: ");
                String title = scanner.nextLine();
                System.out.print("📖 Enter book author: ");
                String author = scanner.nextLine();
                Book book = new Book(id, title, author);
                library.addBook(book);
                    break;
                case 2:
                System.out.print("📖 Enter book ID to remove: ");
                int bookId = scanner.nextInt();
                library.removeBook(bookId);
                    break;
                case 3:
                library.listBooks();
                    break;
                case 4:
                System.out.print("📖 Enter book title or author to search: ");
                String query = scanner.nextLine();
                library.searchBook(query);
                    break;
                case 5:
                System.out.print("📖 Enter book ID to issue: ");
                int issueBookId = scanner.nextInt();
                library.issueBook(issueBookId);
                    break;
                case 6:
                System.out.print("📖 Enter book ID to return: ");
                int returnBookId = scanner.nextInt();
                library.returnBook(returnBookId);
                    break;
                case 7:
                System.out.println("👋 Thank you for using the Library Management System");
                scanner.close();
                System.exit(0);
                    break;
                default:
                System.out.println("❌ Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
