package kth.desireetong.lab1databas.View;

import kth.desireetong.lab1databas.Model.Author;
import kth.desireetong.lab1databas.Model.Book;
import kth.desireetong.lab1databas.Model.BooksDbInterface;
import kth.desireetong.lab1databas.Model.SearchMode;
import java.util.ArrayList;
import java.util.List;
import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private static BooksPane booksView; // view
    private static BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        // ...
                        break;
                    case Author:
                        // ...
                        break;
                    default:
                        result= new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
    public static void addBook(Book book) {
        try {
            booksDb.addBook(book);
            booksView.showAlertAndWait("Book added successfully.", INFORMATION);
            // Refresh the view if necessary
        } catch (Exception e) {
            booksView.showAlertAndWait("Error adding book to database.", ERROR);
        }
    }
    public static void removeBook(int bookID) {
        try {
            booksDb.deleteBook(bookID);
            booksView.showAlertAndWait("Book removed successfully.", INFORMATION);
            // Refresh the view if necessary
        } catch (Exception e) {
            booksView.showAlertAndWait("Error removing book from database.", ERROR);
        }
    }
    public static void addAuthor(Author author) {
        try {
            booksDb.addAuthor(author);
            booksView.showAlertAndWait("Author added successfully.", INFORMATION);
            // Refresh the view if necessary
        } catch (Exception e) {
            booksView.showAlertAndWait("Error adding author to database.", ERROR);
        }
    }
    


}
