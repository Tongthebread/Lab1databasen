package kth.desireetong.lab1databas.Model;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private final List<Book> books;
    private Connection conn;

    public BooksDbImpl() {
        books = Arrays.asList(DATA);
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        String server = "jdbc:mysql://myplace.se:3306/" + database + "?UseClientEnc=UTF8";
        String user = "root";
        String pwd = "psyke456SONG";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
            return true;
        } catch (ClassNotFoundException e){
            throw new BooksDbException("MySQL JDBC driver not found");
        }catch (SQLException e){
            throw new BooksDbException("Error connection to database");
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error disconnecting from database");
        }
    }

    @Override
    public ArrayList<Book> searchBooksByTitle(String searchTitle)
            throws BooksDbException {
        ArrayList<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTitle)) {
                result.add(book);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public ArrayList<Book> searchBooksByISBN(String ISBN) throws BooksDbException {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books){
            if (book.getIsbn().equals(ISBN)){
                result.add(book);
            }
        }
        Collections.sort(result);
        return result;
    }
    @Override
    public ArrayList<Book> searchBooksByAuthor(String authorName) throws BooksDbException {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books){
            if (book.getAuthors().contains(authorName)){
                result.add(book);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public ArrayList<Book> searchBooksByGenre(String genre) throws BooksDbException {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books){
            if (book.getGenre().equals(genre)){
                result.add(book);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public ArrayList<Book> searchBooksByRating(int rating) throws BooksDbException {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books){
            if (book.getRating() == rating){
                result.add(book);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        String sql = "INSERT INTO Book (isbn, bookID, title, published, rating, genre) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setInt(2, book.getBookId());
            pstmt.setString(3, book.getTitle());
            pstmt.setDate(4, book.getPublished());
            pstmt.setInt(5, book.getRating());
            pstmt.setString(6, String.valueOf(book.getGenre()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book to database", e);
        }
    }
    @Override
    public void addAuthor(Author author) throws BooksDbException {
        String sql = "INSERT INTO Author (authorID, firstName, lastName, birthDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, author.getAuthorID());
            pstmt.setString(2, author.getFirstName());
            pstmt.setString(3, author.getLastName());
            pstmt.setDate(4, Date.valueOf(author.getBirthDate()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding author to database", e);
        }
    }

    @Override
    public void deleteBook(int bookID) throws BooksDbException {
        String sql = "DELETE FROM Book WHERE bookID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookID);

            int rowsFound = pstmt.executeUpdate();

            if (rowsFound == 0) {
                throw new BooksDbException("No book found with ID: " + bookID);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book from database", e);
        }
    }

    private static final Book[] DATA = {
            new Book(1, "1234567898", "Databases Illuminated", new Date(2018, 1, 1),2, Genre.SCIENCE),
            new Book(2, "2345678916", "Dark Databases", new Date(1990, 1, 1),3, Genre.SCIENCE),
            new Book(3, "4567890129", "The buried giant", new Date(2000, 1, 1),4, Genre.SCI_FI),
            new Book(4, "5678901232222", "Never let me go", new Date(2000, 1, 1),4, Genre.ACTION),
            new Book(5, "6789012348", "The remains of the day", new Date(2000, 1, 1),1, Genre.ROMANCE),
            new Book(6, "2345678906", "Alias Grace", new Date(2000, 1, 1),5, Genre.THRILLER),
            new Book(7, "3456789115", "The handmaids tale", new Date(2010, 1, 1),2, Genre.DRAMA),
            new Book(8, "3456789013", "Shuggie Bain", new Date(2020, 1, 1),5, Genre.COMEDY),
            new Book(9, "3456789121", "Microserfs", new Date(2000, 1, 1),2, Genre.COMEDY),
    };
}
