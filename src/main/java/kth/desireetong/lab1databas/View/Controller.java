package kth.desireetong.lab1databas.View;

import kth.desireetong.lab1databas.Model.*;

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

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

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
    public void addBookWithAuthors(Book book, List<Author> authors) {
        try {
            booksDb.addBook(book); // Lägger till boken i databasen
            for (Author author : authors) {
                booksDb.addAuthor(author); // Lägger till varje författare i databasen
            }
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("Error adding book or author to the database: " + e.getMessage(), ERROR);
        }
    }

}
