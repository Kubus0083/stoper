module com.example.stoper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.stoper to javafx.fxml;
    exports com.example.stoper;
}