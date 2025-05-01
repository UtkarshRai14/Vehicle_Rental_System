package com.example.demo_utkarsh;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class VehicleRentalSystem extends Application {

    // Model classes (unchanged)
    private static class User {
        private String username, password, fullName, contactNumber, email, userType;
        public User(String username, String password, String fullName, String contactNumber, String email, String userType) {
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.contactNumber = contactNumber;
            this.email = email;
            this.userType = userType;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getFullName() { return fullName; }
        public String getUserType() { return userType; }
    }

    private static class Vehicle {
        private String id, make, model, color, type, ownerUsername, imageUrl;
        private int year;
        private double rentalRate;
        private boolean isAvailable;
        public Vehicle(String id, String make, String model, int year, String color, String type, double rentalRate, String ownerUsername) {
            this.id = id;
            this.make = make;
            this.model = model;
            this.year = year;
            this.color = color;
            this.type = type;
            this.rentalRate = rentalRate;
            this.isAvailable = true;
            this.ownerUsername = ownerUsername;
            this.imageUrl = "default_vehicle.png";
        }
        public String getId() { return id; }
        public String getMake() { return make; }
        public String getModel() { return model; }
        public int getYear() { return year; }
        public String getColor() { return color; }
        public String getType() { return type; }
        public double getRentalRate() { return rentalRate; }
        public boolean isAvailable() { return isAvailable; }
        public String getOwnerUsername() { return ownerUsername; }
        public void setMake(String make) { this.make = make; }
        public void setModel(String model) { this.model = model; }
        public void setYear(int year) { this.year = year; }
        public void setColor(String color) { this.color = color; }
        public void setType(String type) { this.type = type; }
        public void setRentalRate(double rentalRate) { this.rentalRate = rentalRate; }
        public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
        @Override
        public String toString() { return make + " " + model + " (" + year + ")"; }
    }

    private static class Booking {
        private String id, vehicleId, renterUsername, status;
        private LocalDate startDate, endDate;
        private double totalCost;
        public Booking(String id, String vehicleId, String renterUsername, LocalDate startDate, LocalDate endDate, double totalCost) {
            this.id = id;
            this.vehicleId = vehicleId;
            this.renterUsername = renterUsername;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalCost = totalCost;
            this.status = "pending";
        }
        public String getId() { return id; }
        public String getVehicleId() { return vehicleId; }
        public String getRenterUsername() { return renterUsername; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public double getTotalCost() { return totalCost; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // Data storage
    private List<User> users = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private User currentUser = null;

    // UI components
    private Stage primaryStage;
    private Scene loginScene, registerScene, ownerDashboardScene, renterDashboardScene;
    private static final String BG_STYLE = "-fx-background-color: #f5f5f5;";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        addSampleData();
        loginScene = createScene("login");
        registerScene = createScene("register");
        primaryStage.setTitle("Vehicle Rental System");
        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private void addSampleData() {
        users.add(new User("owner1", "pass123", "John Owner", "123-456-7890", "john@example.com", "owner"));
        users.add(new User("renter1", "pass123", "Alice Renter", "234-567-8901", "alice@example.com", "renter"));
        vehicles.add(new Vehicle("V001", "Toyota", "Camry", 2020, "Silver", "Sedan", 50.0, "owner1"));
        vehicles.add(new Vehicle("V002", "Honda", "Civic", 2019, "Blue", "Sedan", 45.0, "owner1"));
        vehicles.add(new Vehicle("V003", "Ford", "Explorer", 2021, "Black", "SUV", 70.0, "owner1"));
        vehicles.add(new Vehicle("V004", "Harley-Davidson", "Sportster", 2018, "Red", "Motorcycle", 60.0, "owner1"));
        Booking booking = new Booking("B001", "V001", "renter1", LocalDate.now(), LocalDate.now().plusDays(3), 150.0);
        booking.setStatus("confirmed");
        bookings.add(booking);
    }

    private Scene createScene(String type) {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(20));
        pane.setStyle(BG_STYLE);

        if (type.equals("login")) {
            Label titleLabel = createLabel("Vehicle Rental System", 24, true, Color.DARKBLUE);
            Label loginLabel = createLabel("Login", 18, false, Color.BLACK);
            GridPane grid = createFormGrid(Arrays.asList(
                    new FormField("Username:", new TextField()),
                    new FormField("Password:", new PasswordField())
            ));

            Button loginButton = createButton("Login", "#4CAF50", e -> {
                TextField usernameField = (TextField) grid.getChildren().get(1);
                PasswordField passwordField = (PasswordField) grid.getChildren().get(3);
                Optional<User> user = users.stream()
                        .filter(u -> u.getUsername().equals(usernameField.getText()) && u.getPassword().equals(passwordField.getText()))
                        .findFirst();
                if (user.isPresent()) {
                    currentUser = user.get();
                    Scene dashboard = currentUser.getUserType().equals("owner") ? (ownerDashboardScene == null ? ownerDashboardScene = createScene("owner") : ownerDashboardScene)
                            : (renterDashboardScene == null ? renterDashboardScene = createScene("renter") : renterDashboardScene);
                    primaryStage.setScene(dashboard);
                } else {
                    showAlert("Login Failed", "Invalid username or password");
                }
            });

            Button registerButton = createButton("Register New User", "#2196F3", e -> primaryStage.setScene(registerScene));
            pane.getChildren().addAll(titleLabel, loginLabel, grid, loginButton, registerButton);
        } else if (type.equals("register")) {
            Label titleLabel = createLabel("Register New User", 20, true, Color.DARKBLUE);
            GridPane grid = createFormGrid(Arrays.asList(
                    new FormField("Username:", new TextField()),
                    new FormField("Password:", new PasswordField()),
                    new FormField("Confirm Password:", new PasswordField()),
                    new FormField("Full Name:", new TextField()),
                    new FormField("Contact Number:", new TextField()),
                    new FormField("Email:", new TextField())
            ));

            ToggleGroup userTypeGroup = new ToggleGroup();
            RadioButton ownerRadio = new RadioButton("Vehicle Owner");
            RadioButton renterRadio = new RadioButton("Renter");
            ownerRadio.setToggleGroup(userTypeGroup);
            renterRadio.setToggleGroup(userTypeGroup);
            renterRadio.setSelected(true);
            HBox radioBox = new HBox(20, ownerRadio, renterRadio);
            grid.add(new Label("User Type:"), 0, 6);
            grid.add(radioBox, 1, 6);

            Button registerButton = createButton("Register", "#4CAF50", e -> {
                TextField[] fields = grid.getChildren().stream()
                        .filter(n -> n instanceof TextField || n instanceof PasswordField)
                        .map(n -> (TextField) n)
                        .toArray(TextField[]::new);
                String username = fields[0].getText();
                String password = fields[1].getText();
                String confirmPassword = fields[2].getText();
                String fullName = fields[3].getText();
                String contact = fields[4].getText();
                String email = fields[5].getText();
                String userType = ((RadioButton) userTypeGroup.getSelectedToggle()).getText().contains("Owner") ? "owner" : "renter";

                if (Arrays.stream(fields).anyMatch(f -> f.getText().isEmpty())) {
                    showAlert("Registration Failed", "All fields are required");
                } else if (!password.equals(confirmPassword)) {
                    showAlert("Registration Failed", "Passwords do not match");
                } else if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
                    showAlert("Registration Failed", "Username already exists");
                } else {
                    users.add(new User(username, password, fullName, contact, email, userType));
                    showAlert("Registration Successful", "You can now login with your credentials");
                    primaryStage.setScene(loginScene);
                }
            });

            Button backButton = createButton("Back to Login", "#2196F3", e -> primaryStage.setScene(loginScene));
            HBox buttonBox = new HBox(20, registerButton, backButton);
            buttonBox.setAlignment(Pos.CENTER);
            pane.getChildren().addAll(titleLabel, grid, buttonBox);
        } else {
            // Dashboard (owner or renter)
            BorderPane borderPane = new BorderPane();
            borderPane.setPadding(new Insets(20));
            borderPane.setStyle(BG_STYLE);

            VBox topSection = new VBox(10);
            String title = type.equals("owner") ? "Vehicle Owner Dashboard" : "Renter Dashboard";
            topSection.getChildren().addAll(
                    createLabel(title, 24, true, Color.DARKBLUE),
                    createLabel("Welcome, " + currentUser.getFullName(), 16, false, Color.BLACK)
            );
            borderPane.setTop(topSection);

            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            if (type.equals("owner")) {
                // My Vehicles Tab
                Tab vehiclesTab = new Tab("My Vehicles");
                VBox vehiclesPane = createVBox(20);
                ListView<Vehicle> vehiclesListView = new ListView<>();
                ObservableList<Vehicle> ownerVehicles = FXCollections.observableArrayList(
                        vehicles.stream().filter(v -> v.getOwnerUsername().equals(currentUser.getUsername())).collect(Collectors.toList())
                );
                vehiclesListView.setItems(ownerVehicles);

                Button updateButton = createButton("Update Selected Vehicle", "#2196F3", e -> {
                    Vehicle selected = vehiclesListView.getSelectionModel().getSelectedItem();
                    if (selected != null) showVehicleDialog("update", selected, ownerVehicles);
                    else showAlert("No Selection", "Please select a vehicle to update");
                });

                Button addButton = createButton("Add New Vehicle", "#4CAF50", e -> showVehicleDialog("add", null, ownerVehicles));
                HBox buttonsBox = new HBox(20, updateButton, addButton);
                buttonsBox.setAlignment(Pos.CENTER);
                vehiclesPane.getChildren().addAll(new Label("Your Vehicles:"), vehiclesListView, buttonsBox);
                vehiclesTab.setContent(vehiclesPane);

                // Rental History Tab
                Tab historyTab = new Tab("Rental History");
                historyTab.setContent(createBookingTable(
                        bookings.stream()
                                .filter(b -> vehicles.stream().anyMatch(v -> v.getId().equals(b.getVehicleId()) && v.getOwnerUsername().equals(currentUser.getUsername())))
                                .collect(Collectors.toList())
                ));
                tabPane.getTabs().addAll(vehiclesTab, historyTab);
            } else {
                // Search Vehicles Tab
                Tab searchTab = new Tab("Search Vehicles");
                VBox searchPane = createVBox(20);
                HBox searchFilters = new HBox(15);
                ComboBox<String> vehicleTypeCombo = new ComboBox<>();
                vehicleTypeCombo.getItems().addAll("All Types", "Sedan", "SUV", "Truck", "Motorcycle");
                vehicleTypeCombo.setValue("All Types");
                TextField searchField = new TextField();
                searchField.setPromptText("Search by make or model");
                searchField.setPrefWidth(200);
                Button searchButton = createButton("Search", "#2196F3", null);
                searchFilters.getChildren().addAll(new Label("Type:"), vehicleTypeCombo, searchField, searchButton);

                TableView<Vehicle> vehiclesTable = createVehicleTable(vehicles.stream().filter(Vehicle::isAvailable).collect(Collectors.toList()));
                Button bookButton = createButton("Book Selected Vehicle", "#4CAF50", e -> {
                    Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
                    if (selected != null) showVehicleDialog("book", selected, null);
                    else showAlert("No Selection", "Please select a vehicle to book");
                });

                searchButton.setOnAction(e -> {
                    String searchText = searchField.getText().toLowerCase();
                    String selectedType = vehicleTypeCombo.getValue();
                    List<Vehicle> filtered = vehicles.stream()
                            .filter(Vehicle::isAvailable)
                            .filter(v -> (selectedType.equals("All Types") || v.getType().equals(selectedType)) &&
                                    (searchText.isEmpty() || v.getMake().toLowerCase().contains(searchText) || v.getModel().toLowerCase().contains(searchText)))
                            .collect(Collectors.toList());
                    vehiclesTable.setItems(FXCollections.observableArrayList(filtered));
                });

                searchPane.getChildren().addAll(searchFilters, vehiclesTable, bookButton);
                searchTab.setContent(searchPane);

                // Booking History Tab
                Tab historyTab = new Tab("Booking History");
                historyTab.setContent(createBookingTable(
                        bookings.stream().filter(b -> b.getRenterUsername().equals(currentUser.getUsername())).collect(Collectors.toList())
                ));
                tabPane.getTabs().addAll(searchTab, historyTab);
            }

            borderPane.setCenter(tabPane);

            Button logoutButton = createButton("Logout", "#f44336", e -> {
                currentUser = null;
                primaryStage.setScene(loginScene);
            });
            HBox bottomBox = new HBox(logoutButton);
            bottomBox.setAlignment(Pos.CENTER_RIGHT);
            bottomBox.setPadding(new Insets(10, 0, 0, 0));
            borderPane.setBottom(bottomBox);

            return new Scene(borderPane, 800, 600);
        }

        return new Scene(pane, 800, 600);
    }

    private VBox createBookingTable(List<Booking> bookingList) {
        VBox pane = createVBox(20);
        TableView<Booking> table = new TableView<>();
        table.getColumns().addAll(
                createTableColumn("Vehicle", b -> {
                    Vehicle v = vehicles.stream().filter(veh -> veh.getId().equals(b.getVehicleId())).findFirst().orElse(null);
                    return v != null ? v.getMake() + " " + v.getModel() : "Unknown";
                }),
                createTableColumn("Renter", b -> {
                    User u = users.stream().filter(user -> user.getUsername().equals(b.getRenterUsername())).findFirst().orElse(null);
                    return u != null ? u.getFullName() : "Unknown";
                }, true),
                createTableColumn("Start Date", b -> b.getStartDate().toString()),
                createTableColumn("End Date", b -> b.getEndDate().toString()),
                createTableColumn("Total Cost", b -> "$" + b.getTotalCost()),
                createTableColumn("Status", Booking::getStatus)
        );
        table.setItems(FXCollections.observableArrayList(bookingList));
        pane.getChildren().addAll(new Label(bookingList.isEmpty() ? "Rental History:" : "Your Booking History:"), table);
        return pane;
    }

    private TableView<Vehicle> createVehicleTable(List<Vehicle> vehicleList) {
        TableView<Vehicle> table = new TableView<>();
        table.getColumns().addAll(
                createTableColumn("Make & Model", v -> v.getMake() + " " + v.getModel()),
                createTableColumn("Year", v -> String.valueOf(v.getYear())),
                createTableColumn("Type", Vehicle::getType),
                createTableColumn("Daily Rate", v -> "$" + v.getRentalRate())
        );
        table.setItems(FXCollections.observableArrayList(vehicleList));
        return table;
    }

    private <T> TableColumn<T, String> createTableColumn(String title, java.util.function.Function<T, String> valueExtractor) {
        return createTableColumn(title, valueExtractor, false);
    }

    private <T> TableColumn<T, String> createTableColumn(String title, java.util.function.Function<T, String> valueExtractor, boolean hideForRenter) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> Bindings.createStringBinding(() -> valueExtractor.apply(cellData.getValue())));
        if (hideForRenter && currentUser != null && currentUser.getUserType().equals("renter")) {
            column.setVisible(false);
        }
        return column;
    }

    private void showVehicleDialog(String dialogType, Vehicle vehicle, ObservableList<Vehicle> ownerVehicles) {
        Dialog<Object> dialog = new Dialog<>();
        String title = dialogType.equals("add") ? "Add New Vehicle" : dialogType.equals("update") ? "Update Vehicle" : "Book Vehicle";
        dialog.setTitle(title);
        dialog.setHeaderText(dialogType.equals("book") ? "Book " + vehicle.getMake() + " " + vehicle.getModel() : "Enter vehicle details");

        ButtonType actionButtonType = new ButtonType(dialogType.equals("book") ? "Book" : dialogType.equals("add") ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(actionButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        if (dialogType.equals("add") || dialogType.equals("update")) {
            TextField makeField = new TextField(dialogType.equals("update") ? vehicle.getMake() : "");
            TextField modelField = new TextField(dialogType.equals("update") ? vehicle.getModel() : "");
            TextField yearField = new TextField(dialogType.equals("update") ? String.valueOf(vehicle.getYear()) : "");
            TextField colorField = new TextField(dialogType.equals("update") ? vehicle.getColor() : "");
            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll("Sedan", "SUV", "Truck", "Motorcycle", "Van");
            typeComboBox.setValue(dialogType.equals("update") ? vehicle.getType() : "Sedan");
            TextField rateField = new TextField(dialogType.equals("update") ? String.valueOf(vehicle.getRentalRate()) : "");
            CheckBox availableCheckBox = new CheckBox("Available for rental");
            if (dialogType.equals("update")) availableCheckBox.setSelected(vehicle.isAvailable());

            grid.add(new Label("Make:"), 0, 0);
            grid.add(makeField, 1, 0);
            grid.add(new Label("Model:"), 0, 1);
            grid.add(modelField, 1, 1);
            grid.add(new Label("Year:"), 0, 2);
            grid.add(yearField, 1, 2);
            grid.add(new Label("Color:"), 0, 3);
            grid.add(colorField, 1, 3);
            grid.add(new Label("Type:"), 0, 4);
            grid.add(typeComboBox, 1, 4);
            grid.add(new Label("Daily Rate ($):"), 0, 5);
            grid.add(rateField, 1, 5);
            if (dialogType.equals("update")) grid.add(availableCheckBox, 0, 6, 2, 1);

            dialog.setResultConverter(button -> {
                if (button == actionButtonType) {
                    try {
                        String make = makeField.getText();
                        String model = modelField.getText();
                        int year = Integer.parseInt(yearField.getText());
                        String color = colorField.getText();
                        String type = typeComboBox.getValue();
                        double rate = Double.parseDouble(rateField.getText());

                        if (make.isEmpty() || model.isEmpty() || color.isEmpty()) {
                            showAlert("Invalid Input", "All fields must be completed");
                            return null;
                        }

                        if (dialogType.equals("add")) {
                            String vehicleId = "V" + String.format("%03d", vehicles.size() + 1);
                            Vehicle newVehicle = new Vehicle(vehicleId, make, model, year, color, type, rate, currentUser.getUsername());
                            vehicles.add(newVehicle);
                            ownerVehicles.add(newVehicle);
                            showAlert("Success", "Vehicle added successfully");
                        } else {
                            vehicle.setMake(make);
                            vehicle.setModel(model);
                            vehicle.setYear(year);
                            vehicle.setColor(color);
                            vehicle.setType(type);
                            vehicle.setRentalRate(rate);
                            vehicle.setAvailable(availableCheckBox.isSelected());
                            ownerVehicles.setAll(vehicles.stream()
                                    .filter(v -> v.getOwnerUsername().equals(currentUser.getUsername()))
                                    .collect(Collectors.toList()));
                            showAlert("Success", "Vehicle updated successfully");
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Invalid Input", "Year and rate must be valid numbers");
                    }
                }
                return null;
            });
        } else {
            DatePicker startDatePicker = new DatePicker(LocalDate.now());
            DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));
            Label costLabel = new Label("Total Cost: $0.00");

            grid.add(new Label("Start Date:"), 0, 0);
            grid.add(startDatePicker, 1, 0);
            grid.add(new Label("End Date:"), 0, 1);
            grid.add(endDatePicker, 1, 1);
            grid.add(costLabel, 0, 2, 2, 1);

            Runnable updateCost = () -> {
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();
                if (start != null && end != null && !start.isAfter(end)) {
                    long days = ChronoUnit.DAYS.between(start, end) + 1;
                    double totalCost = days * vehicle.getRentalRate();
                    costLabel.setText(String.format("Total Cost: $%.2f", totalCost));
                }
            };

            startDatePicker.valueProperty().addListener((obs, old, newVal) -> updateCost.run());
            endDatePicker.valueProperty().addListener((obs, old, newVal) -> updateCost.run());
            updateCost.run();

            dialog.setResultConverter(button -> {
                if (button == actionButtonType) {
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();
                    if (startDate == null || endDate == null) {
                        showAlert("Invalid Dates", "Please select valid dates");
                        return null;
                    }
                    if (startDate.isAfter(endDate)) {
                        showAlert("Invalid Dates", "Start date must be before or equal to end date");
                        return null;
                    }
                    long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    double totalCost = days * vehicle.getRentalRate();
                    String bookingId = "B" + String.format("%03d", bookings.size() + 1);
                    Booking booking = new Booking(bookingId, vehicle.getId(), currentUser.getUsername(), startDate, endDate, totalCost);
                    bookings.add(booking);
                    vehicle.setAvailable(false);
                    showAlert("Success", "Vehicle booked successfully");
                }
                return null;
            });
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private Label createLabel(String text, int size, boolean bold, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        label.setTextFill(color);
        return label;
    }

    private Button createButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setPrefWidth(text.contains("Register") ? 150 : 100);
        if (handler != null) button.setOnAction(handler);
        return button;
    }

    private VBox createVBox(int spacing) {
        VBox vbox = new VBox(spacing);
        vbox.setPadding(new Insets(20));
        return vbox;
    }

    private static class FormField {
        String label;
        TextField field;
        FormField(String label, TextField field) {
            this.label = label;
            this.field = field;
            field.setPrefWidth(200);
        }
    }

    private GridPane createFormGrid(List<FormField> fields) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < fields.size(); i++) {
            grid.add(new Label(fields.get(i).label), 0, i);
            grid.add(fields.get(i).field, 1, i);
        }
        return grid;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}