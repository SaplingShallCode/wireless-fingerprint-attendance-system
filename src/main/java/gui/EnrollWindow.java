package gui;

import core.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utility.Const;


@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EnrollWindow {
    private final Stage enroll_stage;
    private Scene main_scene;

    private String first_name;
    private String middle_name;
    private String last_name;
    private String age;
    private String gender;
    private String phone_number;
    private String address;
    private int fingerprint_id;

    private boolean valid_age;
    private boolean valid_phone_number;
    private boolean valid_fingerprint_id;
    private boolean valid_gender_selection;
    private boolean isSubmitted;

    private VBox root_pane;
    private GridPane name_grid;
    private GridPane apg_grid;
    private GridPane address_grid;
    private HBox last_group;
    private Label enrollment_label;
    private Label first_name_label;
    private Label middle_name_label;
    private Label last_name_label;
    private Label age_label;
    private Label gender_label;
    private Label phone_number_label;
    private Label address_label;
    private Label fingerprint_id_label;
    private Button submit_button;

    private TextField first_name_field;
    private TextField middle_name_field;
    private TextField last_name_field;
    private TextField age_field;
    private ChoiceBox<String> gender_field;
    private TextField phone_number_field;
    private TextField address_field;
    private TextField fingerprint_id_field;

    private final DatabaseManager database_manager;


    public EnrollWindow(Stage stage) {
        database_manager = new DatabaseManager();
        isSubmitted = false;
        enroll_stage = stage;
        enroll_stage.setAlwaysOnTop(true);
        enroll_stage.setResizable(false);
        enroll_stage.setTitle("Fill out Form.");
        enroll_stage.setWidth(800);
        for (Const.Icons i : Const.Icons.values()) {
            enroll_stage.getIcons().add(i.getIconImage());
        }
        initHeaderUI();
        initBodyUI();
        initFooterUI();
        initLayout();

        enroll_stage.showAndWait();
    }


    private void initHeaderUI() {
        enrollment_label = new Label("Enrollment Form");
    }


    private void initBodyUI() {
        first_name_label = new Label("First Name");
        middle_name_label = new Label("Middle Name");
        last_name_label = new Label("Last Name");
        age_label = new Label("Age");
        gender_label = new Label("Gender");
        phone_number_label = new Label("Phone Number");
        address_label = new Label("Address");

        first_name_field = new TextField();
        middle_name_field = new TextField();
        last_name_field = new TextField();
        age_field = new TextField();
        age_field.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if input is a number and a valid age (from 1 to 99).
            valid_age = age_field.getText().matches(
                    "^([1-9]|[1-9][0-9])$"
            );
            updateButtonState();
        });
        gender_field = new ChoiceBox<>();
        gender_field.setValue("Select an option...");
        gender_field.getItems().addAll(
                "Male",
                "Female",
                "Other"
        );
        gender_field.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            valid_gender_selection = !gender_field.getValue()
                                                  .equals("Select an option...");
            updateButtonState();
        });
        phone_number_field = new TextField();
        phone_number_field.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if valid PH phone number.
            valid_phone_number = phone_number_field.getText().matches(
                    "^(09|\\+639)\\d{9}$"
            );
            updateButtonState();
        });
        address_field = new TextField();
    }


    private void initFooterUI() {
        fingerprint_id_label = new Label("Fingerprint ID: ");
        fingerprint_id_field = new TextField();
        fingerprint_id_field.textProperty().addListener((observable, old_value, new_value) -> {
            String fingerprint_id_unparsed = fingerprint_id_field.getText();
            valid_fingerprint_id = false;
            boolean validInput = fingerprint_id_unparsed.matches(
                    // 0-255
                    "^(2[0-5][0-5]|" +
                            "1[0-9][0-9]|" +
                            "[1-9][0-9]|" +
                            "[1-9]" +
                            ")$"
            );
            if (validInput) {
                int fingerprint_id = Integer.parseInt(fingerprint_id_unparsed);
                valid_fingerprint_id = !database_manager.checkFingerIDExists(fingerprint_id);
            }
            updateButtonState();
        });

        submit_button = new Button("Submit");
        submit_button.setDisable(true);
        submit_button.setOnAction(this::submitValues);
    }


    private void initLayout() {
        root_pane = new VBox();
        root_pane.setFillWidth(false);
        root_pane.setPadding(new Insets(10));
        root_pane.setSpacing(25);

        name_grid = new GridPane();
        name_grid.setHgap(10);
        name_grid.prefWidthProperty().bind(root_pane.widthProperty());
        name_grid.add(first_name_field, 0, 0);
        name_grid.add(middle_name_field, 1, 0);
        name_grid.add(last_name_field, 2, 0);
        name_grid.add(first_name_label, 0, 1);
        name_grid.add(middle_name_label, 1, 1);
        name_grid.add(last_name_label, 2, 1);

        apg_grid = new GridPane();
        apg_grid.setHgap(10);
        apg_grid.prefWidthProperty().bind(root_pane.widthProperty());
        apg_grid.add(age_field, 0, 0);
        apg_grid.add(phone_number_field, 1, 0);
        apg_grid.add(gender_field, 2, 0);
        apg_grid.add(age_label, 0, 1);
        apg_grid.add(phone_number_label, 1, 1);
        apg_grid.add(gender_label, 2, 1);

        address_grid = new GridPane();
        address_grid.add(address_field, 0, 0);
        address_grid.add(address_label, 0, 1);
        address_grid.prefWidthProperty().bind(root_pane.widthProperty());

        last_group = new HBox();
        last_group.setSpacing(10);
        last_group.getChildren().addAll(
                fingerprint_id_label,
                fingerprint_id_field,
                submit_button
        );

        root_pane.getChildren().addAll(
                enrollment_label,
                name_grid,
                apg_grid,
                address_grid,
                last_group
        );
        GridPane.setHgrow(first_name_field, Priority.ALWAYS);
        GridPane.setHgrow(middle_name_field, Priority.ALWAYS);
        GridPane.setHgrow(last_name_field, Priority.ALWAYS);
        GridPane.setHgrow(age_field, Priority.ALWAYS);
        GridPane.setHgrow(phone_number_field, Priority.ALWAYS);
        GridPane.setHgrow(gender_field, Priority.ALWAYS);
        GridPane.setHgrow(address_field, Priority.ALWAYS);
        main_scene = new Scene(root_pane);
        enroll_stage.setScene(main_scene);
    }


    private void submitValues(ActionEvent event) {

        first_name = first_name_field.getText();
        middle_name = middle_name_field.getText();
        last_name = last_name_field.getText();
        age = age_field.getText();
        gender = gender_field.getValue();
        phone_number = phone_number_field.getText();
        address = address_field.getText();
        fingerprint_id = Integer.parseInt(fingerprint_id_field.getText());

        enroll_stage.close();
        isSubmitted = true;
    }


    private void updateButtonState() {
        submit_button.setDisable(true);
        if (valid_fingerprint_id && valid_age && valid_phone_number && valid_gender_selection) {
            submit_button.setDisable(false);
        }
    }


    public String getFirstName() {
        return first_name;
    }


    public String getMiddleName() {
        return middle_name;
    }


    public String getLastName() {
        return last_name;
    }


    public String getAge() {
        return age;
    }


    public String getGender() {
        return gender;
    }


    public String getPhoneNumber() {
        return phone_number;
    }


    public String getAddress() {
        return address;
    }


    public int getFingerprintId() {
        return fingerprint_id;
    }


    public boolean getIsSubmitted() { return isSubmitted; }
}
