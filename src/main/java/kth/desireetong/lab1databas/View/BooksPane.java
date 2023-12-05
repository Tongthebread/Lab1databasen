package kth.desireetong.lab1databas.View;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import kth.desireetong.lab1databas.Model.*;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view
    private ObservableList<Author> authors;

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private MenuBar menuBar;

    public BooksPane(BooksDbImpl booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
        this.authors = FXCollections.observableArrayList();
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }
    
    /**
     * Notify user on input error or exceptions.
     * 
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, Integer> bookIDCol = new TableColumn<>("BookID");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, Author> authorCol = new TableColumn<>("Author");
        TableColumn<Book, Integer> ratingCol = new TableColumn<>("Rating");
        booksTable.getColumns().addAll(titleCol, bookIDCol, isbnCol, publishedCol, authorCol, ratingCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        
        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");
        
        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        addItem.setOnAction(e -> showAddBookDialog());
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }

    private void showAddBookDialog() {
        // Skapa en ny dialog
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter Book Details");

        // Lägger till knappar
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Skapa fält för att samla in bokinformation
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bookIDField = new TextField();
        bookIDField.setPromptText("Book ID");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        DatePicker publishedDateField = new DatePicker();
        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (1-5)");
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        ListView<Author> authorListView = new ListView<>();
        authorListView.setItems(authors);

        Button addAuthorButton = new Button("Add Author");
        addAuthorButton.setOnAction(e -> {
            Author newAuthor = showAddAuthorDialog();
            if (newAuthor != null) {
                authors.add(newAuthor);
            }
        });

        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(bookIDField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Title:"), 0, 2);
        grid.add(titleField, 1, 2);
        grid.add(new Label("Published Date:"), 0, 3);
        grid.add(publishedDateField, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(ratingField, 1, 4);
        grid.add(new Label("Genre:"), 0, 5);
        grid.add(genreField, 1, 5);
        grid.add(new Label("Author:"), 0, 6);
        grid.add(authorListView, 1, 6);
        grid.add(addAuthorButton, 2, 6);

        dialog.getDialogPane().setContent(grid);

        // Konvertera resultaten när "Save"-knappen trycks
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int bookId = Integer.parseInt(bookIDField.getText());
                    String isbn = isbnField.getText();
                    String title = titleField.getText();
                    LocalDate publishedLocalDate = publishedDateField.getValue();
                    Date published = Date.valueOf(publishedLocalDate); // Konvertera LocalDate till java.sql.Date
                    int rating = Integer.parseInt(ratingField.getText());
                    Genre genre = Genre.valueOf(genreField.getText());

                    Book newBook = new Book(bookId, isbn, title, published, rating, genre);
                    for(Author author : authors){
                        newBook.setAuthors(author);
                    }
                    return newBook;

                } catch (Exception e) {
                    showAlertAndWait("Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            // Hantera det nya Book-objektet här
            List<Author> authors = authorListView.getItems();
            // Exempel: Lägg till i din ObservableList eller databas
            booksInTable.add(book);
        });
    }

    private Author showAddAuthorDialog() {
        // Skapa en dialog för att lägga till en ny författare
        Dialog<Author> dialog = new Dialog<>();
        dialog.setTitle("Add New Author");
        dialog.setHeaderText("Enter Author Details");

        // Lägger till knappar
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Skapa fält för att samla in författarinformation
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField authorIdField = new TextField();
        authorIdField.setPromptText("Author ID");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Birth Date (YYYY-MM-DD)");

        grid.add(new Label("Author ID:"), 0, 0);
        grid.add(authorIdField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Birth Date:"), 0, 3);
        grid.add(birthDateField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Konvertera resultaten när "Save"-knappen trycks
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int authorId = Integer.parseInt(authorIdField.getText());
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String birthDate = birthDateField.getText(); // Använd String för födelsedatum

                    return new Author(authorId, firstName, lastName, birthDate);
                } catch (Exception e) {
                    showAlertAndWait("Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Author> result = dialog.showAndWait();
        return result.orElse(null);
    }


}
