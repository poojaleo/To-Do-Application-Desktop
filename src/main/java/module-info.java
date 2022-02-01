module com.example.thingstodo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.thingstodo to javafx.fxml;
    exports com.example.thingstodo;
}