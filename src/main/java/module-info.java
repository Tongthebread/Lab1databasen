module kth.desireetong.lab1databas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires javafx.base;


    opens kth.desireetong.lab1databas to javafx.fxml;
    opens kth.desireetong.lab1databas.Model to javafx.base;
    exports kth.desireetong.lab1databas;
}