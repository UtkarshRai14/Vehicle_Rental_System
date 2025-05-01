module com.example.demo_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demo_1 to javafx.fxml;
    exports com.example.demo_1;
    exports com.example.demo_utkarsh;
    opens com.example.demo_utkarsh to javafx.fxml;
}