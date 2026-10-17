/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elham
 */
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;



public class CinemaBooking extends Application {
    // Database settings are read from environment variables so credentials are
    // never committed to source control. The defaults target a local demo setup.
    private static final String DB_URL_1 = env("CINEMA_USERS_DB_URL", "jdbc:mysql://localhost:3306/userdb?useSSL=false");
    private static final String DB_URL = env("CINEMA_BOOKINGS_DB_URL", "jdbc:mysql://localhost:3306/bookingdb?useSSL=false");
    private static final String DB_USER = env("CINEMA_DB_USER", "root");
    private static final String DB_PASSWORD = env("CINEMA_DB_PASSWORD", "");
    
    private GridPane movieGrid; // الجريد الخاص بالأفلام المتاحة حالياً
    private GridPane upcomingMovieGrid; // الجريد الخاص بالأفلام القادمة
    private Set<String> reservedMovies = new HashSet<>(); // مجموعة لتتبع الأفلام المحجوزة
    private TextField searchField; // مربع البحث
  private TableView<ObservableList<String>> tableView = new TableView<>();
private ObservableList<ObservableList<String>> bookingList = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) {
        // البداية من صفحة التفاصيل
        mainPage(primaryStage);
    }
    
    private void mainPage(Stage primaryStage) {
        // إعداد جريد الأفلام
        movieGrid = new GridPane();
        movieGrid.setHgap(20); // المسافة بين الأعمدة
        movieGrid.setVgap(20); // المسافة بين الصفوف

        // إضافة الأفلام المتاحة مع الصور
        addAllMovies(primaryStage);  // تم تعديل الاستدعاء لتمرير primaryStage

        // إعداد جريد الأفلام القادمة
        upcomingMovieGrid = new GridPane();
        upcomingMovieGrid.setHgap(20); // المسافة بين الأعمدة
        upcomingMovieGrid.setVgap(20); // المسافة بين الصفوف

        // إضافة الأفلام القادمة مع الصور
        addUpcomingMovies(primaryStage);  // تم تعديل الاستدعاء لتمرير primaryStage

        // إعداد صندوق اختيار الفلاتر
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Most Viewed", "Genre Filter");

        // إعداد صندوق اختيار الأنواع
        ComboBox<String> genreComboBox = new ComboBox<>();
        genreComboBox.getItems().addAll("Comedy", "Thriller", "Horror", "Animation", "Drama");

        // حدث عند اختيار نوع الفيلم
        genreComboBox.setOnAction(event -> {
            String selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();
            showMoviesByGenre(selectedGenre, primaryStage);  // تم تعديل الاستدعاء لتمرير primaryStage
        });

        // حدث عند اختيار الفلتر
        filterComboBox.setOnAction(event -> {
            String selected = filterComboBox.getSelectionModel().getSelectedItem();
            if ("Most Viewed".equals(selected)) {
                showMostViewedMovies(primaryStage);  // تم تعديل الاستدعاء لتمرير primaryStage
            } else {
                movieGrid.getChildren().clear();
            }
        });

        // مربع البحث
        searchField = new TextField();
        searchField.setPromptText("Search for a movie...");
        searchField.setMaxWidth(300);  // تحديد عرض أكبر لحقول البحث
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMovies(newValue, primaryStage));  // تم تعديل الاستدعاء لتمرير primaryStage

        // إعداد HBox للأزرار والبحث
        HBox controls = new HBox(10);
        controls.getChildren().addAll(searchField);

        // إعداد Layout العمودي VBox
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-background-color:#e6ccb3;");
        vbox.getChildren().addAll(
                new Label("List of Available Movies:"),
                movieGrid,
                new Label("List of Upcoming Movies:"),
                upcomingMovieGrid,
                new Label("Movie Filter:"),
                filterComboBox,
                new Label("Genre Filter:"),
                genreComboBox,
                controls // إضافة أزرار التحكم (البحث)
        );

        Scene scene = new Scene(vbox, 780, 780); // تغيير حجم النافذة
        scene.setFill(Color.web("#996633"));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Movie Reservation System");
        primaryStage.show();
    }

    private void addAllMovies(Stage primaryStage) {
        movieGrid.getChildren().clear(); // مسح المحتويات السابقة للجريد
        movieGrid.add(createMovieItem("Wolf Man", "movie1.jpg", primaryStage), 0, 0);
        movieGrid.add(createMovieItem("Moana 2", "movie3.jpg", primaryStage), 1, 0);
        movieGrid.add(createMovieItem("Into the Deep", "movie4.jpg", primaryStage), 2, 0);
        movieGrid.add(createMovieItem("Mandoob", "movie5.jpg", primaryStage), 3, 0);
    }

    private void showMostViewedMovies(Stage primaryStage) {
        movieGrid.getChildren().clear(); // مسح المحتويات السابقة
        movieGrid.add(createMovieItem("Moana 2", "movie3.jpg", primaryStage), 0, 0);
        movieGrid.add(createMovieItem("Mandoob", "movie5.jpg", primaryStage), 1, 0);
    }

    private void addUpcomingMovies(Stage primaryStage) {
        upcomingMovieGrid.getChildren().clear(); // مسح المحتويات السابقة للأفلام القادمة
        upcomingMovieGrid.add(createMovieItem("The Day the Earth Blew Up", "movie6.jpg", primaryStage), 0, 0);
        upcomingMovieGrid.add(createMovieItem("Outbreak", "movie7.jpg", primaryStage), 1, 0);
        upcomingMovieGrid.add(createMovieItem("Se7en", "movie8.jpg", primaryStage), 2, 0);
        upcomingMovieGrid.add(createMovieItem("Kobam", "movie7.jpg", primaryStage), 3, 0);
    }

    private void showMoviesByGenre(String genre, Stage primaryStage) {
        movieGrid.getChildren().clear(); // مسح الجريد قبل إضافة الأفلام حسب النوع
        switch (genre) {
            case "Comedy":
                movieGrid.add(createMovieItem("Mandoob", "movie5.jpg", primaryStage), 0, 0);
                movieGrid.add(createMovieItem("The Day the Earth Blew Up", "movie6.jpg", primaryStage), 1, 0);
                break;
            case "Thriller":
                movieGrid.add(createMovieItem("Into the Deep", "movie4.jpg", primaryStage), 0, 0);
                movieGrid.add(createMovieItem("Outbreak", "movie7.jpg", primaryStage), 1, 0);
                break;
            case "Horror":
                movieGrid.add(createMovieItem("Wolf Man", "movie1.jpg", primaryStage), 0, 0);
                movieGrid.add(createMovieItem("Se7en", "movie8.jpg", primaryStage), 1, 0);
                break;
            case "Animation":
                movieGrid.add(createMovieItem("Moana 2", "movie3.jpg", primaryStage), 0, 0);
                break;
            case "Drama":
                movieGrid.add(createMovieItem("Kobam", "movie7.jpg", primaryStage), 0, 0);
                break;
        }
    }

    private void filterMovies(String query, Stage primaryStage) {
        movieGrid.getChildren().clear(); // مسح الجريد قبل البدء في الفلترة
        // إضافة منطق للبحث عن الأفلام بناءً على النص المدخل في مربع البحث
        if (query.isEmpty()) {
            addAllMovies(primaryStage); // عرض جميع الأفلام إذا كان مربع البحث فارغ
            return;
        }
        
        // تحقق من المطابقة مع عناوين الأفلام
        if ("Wolf Man".toLowerCase().contains(query.toLowerCase())) {
            movieGrid.add(createMovieItem("Wolf Man", "movie1.jpg", primaryStage), 0, 0);
        }
        if ("Moana 2".toLowerCase().contains(query.toLowerCase())) {
            movieGrid.add(createMovieItem("Moana 2", "movie2.jpg", primaryStage), 1, 0);
        }
        // إضافة باقي الأفلام حسب الحاجة
    }

    private VBox createMovieItem(String title, String imagePath, Stage primaryStage) {
        Image image = new Image(resourcePath(imagePath)); // تحميل الصورة للفيلم
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(180); 
        imageView.setFitHeight(250); 
        
        Label movieLabel = new Label(title); // إنشاء التسمية الخاصة بالفيلم
        
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(imageView, movieLabel);
        
        vbox.setOnMouseClicked(event -> {
            showMovieDetails(title, primaryStage); // عرض تفاصيل الفيلم عند النقر عليه
        });

        return vbox; // إعادة العنصر الجاهز
    }

    private void showAlert(String title, String message, Stage primaryStage, Stage detailStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // إنشاء نافذة تنبيه
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.setOnHidden(e -> {
            detailStage.close();  // إغلاق نافذة التفاصيل بعد إغلاق الرسالة
            showOffersPage(primaryStage);  // فتح نافذة العروض بعد إغلاق نافذة التفاصيل
        });

        alert.showAndWait(); // عرض الرسالة وانتظار إغلاقها
    }
// هذا الميثود يقوم بعرض تفاصيل الفيلم في نافذة جديدة
private void showMovieDetails(String title, Stage primaryStage) {
    // إنشاء نافذة جديدة لعرض تفاصيل الفيلم
    Stage detailStage = new Stage();
    detailStage.initModality(Modality.APPLICATION_MODAL); // تعيين النافذة لتكون نافذة منبثقة
    detailStage.setTitle(title + " Details"); // تعيين عنوان النافذة

    // إعداد تخطيط الـ VBox لعرض التفاصيل
    VBox detailLayout = new VBox(10);
    detailLayout.setPadding(new javafx.geometry.Insets(20));

    // تغيير خلفية الـ VBox إلى اللون المحدد (#e6ccb3)
    BackgroundFill bgFill = new BackgroundFill(Color.web("#e6ccb3"), CornerRadii.EMPTY, Insets.EMPTY);
    detailLayout.setBackground(new Background(bgFill));

    // إضافة التفاصيل: العنوان، النوع، المدة، التقييم، الملخص
    Label titleLabel = new Label("Title: " + title);
    Label genreLabel = new Label("Genre: " + getGenre(title));
    Label durationLabel = new Label("Duration: " + getDuration(title));
    Label ratingLabel = new Label("Rating: " + getRating(title));
    Label summaryLabel = new Label("Summary: " + getSummary(title));

    // زر الحجز: إذا كان الفيلم محجوزًا، يظهر نص "إلغاء الحجز"، وإذا لم يكن محجوزًا يظهر "حجز الفيلم"
    Button reserveButton = new Button(reservedMovies.contains(title) ? "Cancel Reservation" : "Reserve Movie");
    reserveButton.setOnAction(event -> {
        // إذا كان الفيلم محجوزًا، قم بإلغاء الحجز
        if (reservedMovies.contains(title)) {
            reservedMovies.remove(title); // إزالة الفيلم من قائمة المحجوزات
            reserveButton.setText("Reserve Movie"); // تغيير النص إلى "حجز الفيلم"
            showAlert("Reservation Cancelled", "You have cancelled the reservation for " + title, primaryStage, detailStage);
        } else {
            reservedMovies.add(title); // إضافة الفيلم إلى قائمة المحجوزات
            reserveButton.setText("Cancel Reservation"); // تغيير النص إلى "إلغاء الحجز"
            showAlert("Reservation Confirmed", "You have reserved " + title, primaryStage, detailStage);
        }
    });

    // إضافة كل العناصر (التفاصيل والزِر) إلى الـ VBox
    detailLayout.getChildren().addAll(titleLabel, genreLabel, durationLabel, ratingLabel, summaryLabel, reserveButton);

    // إنشاء مشهد يحتوي على الـ VBox وعرضه في النافذة
    Scene detailScene = new Scene(detailLayout, 300, 300);
    detailStage.setScene(detailScene);
    detailStage.showAndWait(); // عرض النافذة وانتظار التفاعل
}

// هذه الدالة تُرجع النوع بناءً على اسم الفيلم
private String getGenre(String title) {
    switch (title) {
        case "Wolf Man":
            return "Horror";
        case "Moana 2":
            return "Animation";
        case "Into the Deep":
            return "Thriller";
        case "Mandoob":
            return "Comedy";
        case "The Day the Earth Blew Up":
            return "Animation";
        case "Outbreak":
            return "Thriller";
        case "Se7en":
            return "Horror";
        case "Kobam":
            return "Drama";
        default:
            return "Unknown"; // إذا كان الفيلم غير موجود في القائمة، يرجع "غير معروف"
    }
}

// هذه الدالة تُرجع المدة بناءً على اسم الفيلم
private String getDuration(String title) {
    switch (title) {
        case "Wolf Man":
            return "1h 50m";
        case "Moana 2":
            return "1h 43m";
        case "Into the Deep":
            return "2h 10m";
        case "Mandoob":
            return "1h 35m";
        case "The Day the Earth Blew Up":
            return "1h 40m";
        case "Outbreak":
            return "2h 5m";
        case "Se7en":
            return "2h 7m";
        case "Kobam":
            return "2h 15m";
        default:
            return "Unknown";
    }
}

// هذه الدالة تُرجع التقييم بناءً على اسم الفيلم
private String getRating(String title) {
    switch (title) {
        case "Wolf Man":
            return "8.5/10";
        case "Moana 2":
            return "7.9/10";
        case "Into the Deep":
            return "8.0/10";
        case "Mandoob":
            return "6.8/10";
        case "The Day the Earth Blew Up":
            return "7.5/10";
        case "Outbreak":
            return "7.0/10";
        case "Se7en":
            return "8.6/10";
        case "Kobam":
            return "7.2/10";
        default:
            return "Unknown";
    }
}

// هذه الدالة تُرجع الملخص بناءً على اسم الفيلم
private String getSummary(String title) {
    switch (title) {
        case "Wolf Man":
            return "A man discovers his werewolf lineage and must confront his fate to protect his loved ones.";
        case "Moana 2":
            return "Moana embarks on a journey to save her people, guided by the ocean and her ancestors.";
        case "Into the Deep":
            return "An underwater exploration turns dangerous as secrets unfold.";
        case "Mandoob":
            return "A comedic drama about the life of a sales representative in the city.";
        case "The Day the Earth Blew Up":
            return "Aliens threaten to destroy the Earth, and a team of heroes must save it.";
        case "Outbreak":
            return "A deadly virus outbreak threatens humanity as a team races to find a cure.";
        case "Se7en":
            return "Two detectives hunt a serial killer who uses the seven deadly sins as motives.";
        case "Kobam":
            return "A dramatic story of love and family struggles in India.";
        default:
            return "No summary available."; // في حالة عدم وجود ملخص، يرجع "لا يوجد ملخص"
    }
}

// هذا الميثود يعرض صفحة العروض الخاصة
private void showOffersPage(Stage primaryStage) {
    VBox root = new VBox(20); // إنشاء VBox لترتيب العناصر في الصفحة
    root.setPadding(new Insets(20)); // إضافة حواف للمسافات حول المحتوى
    root.setAlignment(Pos.TOP_CENTER); // محاذاة العناصر في أعلى المركز

    // تغيير خلفية الصفحة إلى اللون #e6ccb3
    BackgroundFill bgFill = new BackgroundFill(Color.web("#e6ccb3"), CornerRadii.EMPTY, Insets.EMPTY);
    root.setBackground(new Background(bgFill));

    // إضافة عنوان "Offers" مع تنسيق خاص
    Label title = new Label("Offers");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    // إضافة عنوان "Special Offers"
    Label offersLabel = new Label("Special Offers");
    offersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true;");

    // إعداد الأزرار الخاصة بالعروض
    Button offerButton1 = new Button("Monday Offer 2025: Buy one ticket,\n get one free.\nExpires: 31/03/2025");
    offerButton1.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
    offerButton1.setGraphic(new ImageView(new Image(resourcePath("img1.png"), 200, 0, true, true)));
    offerButton1.setOnAction(e -> showLoginPage(primaryStage)); // عند النقر، يتم عرض صفحة تسجيل الدخول

    Button offerButton2 = new Button("Monday Delight: Includes 2 tickets,\n 2 refreshing slushies, medium salted\n popcorn, and small nachos.\nExpires: 31/03/2025");
    offerButton2.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
    offerButton2.setGraphic(new ImageView(new Image(resourcePath("img2.png"), 200, 0, true, true)));
    offerButton2.setOnAction(e -> showLoginPage(primaryStage));

    Button offerButton3 = new Button("Group Fun: Get 4 tickets,\n 4 small salted popcorns, 4 refreshing \n slushies, and 2 small nachos. Gather\n family and friends and enjoy!");
    offerButton3.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
    offerButton3.setGraphic(new ImageView(new Image(resourcePath("img3.png"), 200, 0, true, true)));
    offerButton3.setOnAction(e -> showLoginPage(primaryStage));

    // ترتيب الأزرار في VBox
    VBox offersBox = new VBox(10, offersLabel, offerButton1, offerButton2, offerButton3);
    VBox buttonsBox = new VBox(10);
    offersBox.setAlignment(Pos.CENTER_LEFT);
    buttonsBox.setAlignment(Pos.CENTER);

    root.getChildren().addAll(title, offersBox, buttonsBox);

    // إنشاء مشهد يحتوي على الـ VBox وعرضه في الصفحة
    Scene scene = new Scene(root, 500, 600);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Offers & Events Page"); // تعيين عنوان الصفحة
    primaryStage.show(); // عرض الصفحة
}

    // الدالة لعرض صفحة الدخول
private void showLoginPage(Stage primaryStage) {
        // هنا نقوم بإنشاء مشهد تسجيل الدخول
        Scene loginScene = createLoginScene(primaryStage);
        primaryStage.setScene(loginScene); // نحدد المشهد الحالي
        primaryStage.setTitle("Authentication"); // نحدد عنوان النافذة
        primaryStage.show(); // نعرض النافذة
    }

    private Scene createLoginScene(Stage primaryStage) {
        // هنا نبدأ نجهز التصميم باستخدام VBox لصفحة تسجيل الدخول
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20)); // تحديد الحواف
        loginLayout.setAlignment(Pos.CENTER); // تحديد المحاذاة للمنتصف

        // تعيين الخلفية للصفحة بلون معين
        BackgroundFill bgFill = new BackgroundFill(Color.web("#e6ccb3"), CornerRadii.EMPTY, Insets.EMPTY);
        loginLayout.setBackground(new Background(bgFill));

        // إضافة عنوان صفحة الدخول
        Label loginTitle = new Label("Login");
        loginTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // إضافة حقول إدخال البيانات (اسم المستخدم وكلمة المرور)
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // إضافة زر تسجيل الدخول
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText(); // الحصول على اسم المستخدم
            String password = passwordField.getText(); // الحصول على كلمة المرور

            // التحقق من صحة بيانات المستخدم
            if (authenticateUser(username, password)) {
                showAlert("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);
                showTicketPage(primaryStage);  // الانتقال إلى صفحة حجز التذاكر بعد النجاح
            } else {
                showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
            }
        });

        // زر الانتقال إلى صفحة إنشاء حساب جديد
        Button switchToSignUpButton = new Button("Create Account");
        switchToSignUpButton.setOnAction(e -> primaryStage.setScene(createSignUpScene(primaryStage)));

        // إضافة كل العناصر إلى الصفحة
        loginLayout.getChildren().addAll(loginTitle, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, switchToSignUpButton);

        return new Scene(loginLayout, 300, 400); // إعادة المشهد
    }

   private Scene createSignUpScene(Stage primaryStage) {
    // تصميم صفحة التسجيل باستخدام VBox
    VBox signUpLayout = new VBox(10);
    signUpLayout.setPadding(new Insets(20)); // تحديد الحواف
    signUpLayout.setAlignment(Pos.CENTER); // تحديد المحاذاة للمنتصف
    BackgroundFill bgFill = new BackgroundFill(Color.web("#e6ccb3"), CornerRadii.EMPTY, Insets.EMPTY);
    signUpLayout.setBackground(new Background(bgFill));

        // إضافة عنوان صفحة التسجيل
        Label signUpTitle = new Label("Create Account");
        signUpTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // إضافة حقول إدخال البيانات (الاسم الأول، الاسم الأخير، اسم المستخدم، كلمة المرور)
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // زر التسجيل
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            // التحقق من أن الحقول غير فارغة
            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            } else {
                // إنشاء حساب جديد
                if (createAccount(firstName, lastName, username, password)) {
                    showAlert("Success", "Account created successfully!", Alert.AlertType.INFORMATION);
                    primaryStage.setScene(createLoginScene(primaryStage)); // العودة لصفحة الدخول
                } else {
                    showAlert("Error", "Account creation failed. Username may already exist.", Alert.AlertType.ERROR);
                }
            }
        });

        // زر العودة لصفحة الدخول
        Button switchToLoginButton = new Button("Back to Login");
        switchToLoginButton.setOnAction(e -> primaryStage.setScene(createLoginScene(primaryStage)));

        // إضافة كل العناصر إلى الصفحة
        signUpLayout.getChildren().addAll(signUpTitle, firstNameLabel, firstNameField, lastNameLabel, lastNameField, usernameLabel, usernameField, passwordLabel, passwordField, signUpButton, switchToLoginButton);

        return new Scene(signUpLayout, 300, 400); // إعادة المشهد
    }

    
private boolean authenticateUser(String username, String password) {
    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
    try (Connection connection = DriverManager.getConnection(DB_URL_1, DB_USER, DB_PASSWORD);  // استخدم DB_URL_1 أو DB_URL_2 هنا
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, username); // تحديد اسم المستخدم
        statement.setString(2, password); // تحديد كلمة المرور

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next(); // إذا تم العثور على المستخدم
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

private boolean createAccount(String firstName, String lastName, String username, String password) {
    String query = "INSERT INTO users (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
    try (Connection connection = DriverManager.getConnection(DB_URL_1, DB_USER, DB_PASSWORD);  // استخدم DB_URL_1 أو DB_URL_2 هنا
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, firstName); // تحديد الاسم الأول
        statement.setString(2, lastName);  // تحديد الاسم الأخير
        statement.setString(3, username);  // تحديد اسم المستخدم
        statement.setString(4, password);  // تحديد كلمة المرور

        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;  // إذا تم إدراج الحساب بنجاح
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    private void showTicketPage(Stage primaryStage) {
    // تصميم صفحة حجز التذاكر
    VBox root = new VBox(20);
    root.setPadding(new Insets(20)); // تحديد الحواف
    root.setAlignment(Pos.TOP_CENTER); // تحديد المحاذاة للمنتصف
    root.setStyle("-fx-background-color: #e6ccb3;"); // تعيين اللون للواجهة

    // إضافة عنوان الصفحة
    Label title = new Label("Ticket Booking Form");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    // حقل اختيار نوع التذكرة (بالأزرار الراديو)
    Label ticketTypeLabel = new Label("Ticket Type:");
    ToggleGroup ticketGroup = new ToggleGroup();
    RadioButton adultRadio = new RadioButton("Adult");
    RadioButton childRadio = new RadioButton("Child");
    RadioButton seniorRadio = new RadioButton("Senior");
    adultRadio.setToggleGroup(ticketGroup);
    childRadio.setToggleGroup(ticketGroup);
    seniorRadio.setToggleGroup(ticketGroup);
    HBox ticketBox = new HBox(15, adultRadio, childRadio, seniorRadio);
    ticketBox.setAlignment(Pos.CENTER);

    // حقل إدخال اسم الفيلم
    Label movieLabel = new Label("Movie Name:");
    TextField movieField = new TextField();
    movieField.setPromptText("Enter the name of the movie");

    // حقل اختيار عدد التذاكر
    Label ticketCountLabel = new Label("Number of Tickets:");
    Spinner<Integer> ticketCountSpinner = new Spinner<>(1, 10, 1);
    ticketCountSpinner.setEditable(true);

    // حقل اختيار الوقت المفضل
    Label timeLabel = new Label("Preferred Time to Attend:");
    ComboBox<String> timeComboBox = new ComboBox<>();
    timeComboBox.getItems().addAll("Morning", "Afternoon", "Evening");
    timeComboBox.setValue("Morning");

    // زر إرسال الحجز
    Button submitButton = new Button("Book Tickets");
    submitButton.setOnAction(e -> {
        // جمع المعلومات المدخلة
        String ticketType = adultRadio.isSelected() ? "Adult" : (childRadio.isSelected() ? "Child" : "Senior");
        String movieName = movieField.getText();
        int ticketCount = ticketCountSpinner.getValue();
        String time = timeComboBox.getValue();

        // عرض تنبيه بتفاصيل الحجز
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmation");
        alert.setHeaderText("Booking Successful!");
        alert.setContentText("\nTicket Type: " + ticketType + "\nMovie: " + movieName + "\nNumber of Tickets: " +
                ticketCount + "\nPreferred Time: " + time);
        alert.showAndWait();

        // بعد الحجز، عرض صفحة الحجوزات
        showBookingPage(primaryStage);
    });

    // إضافة كل العناصر إلى الصفحة
    root.getChildren().addAll(title, ticketTypeLabel, ticketBox, movieLabel, movieField, ticketCountLabel, ticketCountSpinner, timeLabel, timeComboBox, submitButton);

    // عرض المشهد
    Scene scene = new Scene(root, 400, 500);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ticket Booking Form");
    primaryStage.show();
}

private void showBookingPage(Stage primaryStage) {
    // تصميم صفحة عرض الحجوزات
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-background-color: #e6ccb3;"); // تعيين اللون للواجهة

    setupTableView();

    Button loadButton = new Button("Load Bookings");
    loadButton.setOnAction(e -> loadBookings());

    Button addButton = new Button("Add Booking");
    addButton.setOnAction(e -> addBooking());

    layout.getChildren().addAll(loadButton, addButton, tableView);

    // تعديل حجم نافذة التطبيق
    Scene scene = new Scene(layout, 600, 500);  // زيادة الأبعاد هنا
    primaryStage.setScene(scene);
    primaryStage.setTitle("Booking List");
    primaryStage.show();
}

private void setupTableView() {
    // إعداد الأعمدة
    TableColumn<ObservableList<String>, String> bookingIDCol = new TableColumn<>("Booking ID");
    bookingIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
    bookingIDCol.setPrefWidth(100);  // تحديد عرض العمود بشكل ثابت

    TableColumn<ObservableList<String>, String> userIDCol = new TableColumn<>("User ID");
    userIDCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
    userIDCol.setPrefWidth(100);

    TableColumn<ObservableList<String>, String> showtimeIdCol = new TableColumn<>("Showtime ID");
    showtimeIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
    showtimeIdCol.setPrefWidth(100);

    TableColumn<ObservableList<String>, String> seatsCol = new TableColumn<>("Seats");
    seatsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
    seatsCol.setPrefWidth(100);

    TableColumn<ObservableList<String>, String> bookingDateCol = new TableColumn<>("Booking Date");
    bookingDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
    bookingDateCol.setPrefWidth(150);  // زيادة عرض العمود

    // إضافة الأعمدة إلى الجدول
    tableView.getColumns().addAll(bookingIDCol, userIDCol, showtimeIdCol, seatsCol, bookingDateCol);
}

private void loadBookings() {
    // تحميل بيانات الحجوزات من قاعدة البيانات
    bookingList.clear();
    String query = "SELECT * FROM booking";  // تأكد من اسم الجدول
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {

        while (resultSet.next()) {
            ObservableList<String> bookingData = FXCollections.observableArrayList();
            bookingData.add(String.valueOf(resultSet.getInt("bookingID")));
            bookingData.add(String.valueOf(resultSet.getInt("userID")));
            bookingData.add(String.valueOf(resultSet.getInt("showtimeId")));
            bookingData.add(String.valueOf(resultSet.getInt("seats")));
            String bookingDate = resultSet.getString("BookingDate");

            // تحويل التاريخ إلى أرقام إنجليزية
            bookingDate = convertToEnglishNumbers(bookingDate);
            bookingData.add(bookingDate);
            bookingList.add(bookingData);
        }

        tableView.setItems(bookingList);  // تحديث الجدول ليعرض البيانات
    } catch (SQLException e) {
        showAlert("Error", "Failed to load bookings: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

private void addBooking() {
    // إضافة حجز جديد
    Stage addBookingStage = new Stage();
    VBox addLayout = new VBox(10);
    addLayout.setPadding(new Insets(20));
    addLayout.setAlignment(Pos.CENTER);
    addLayout.setStyle("-fx-background-color: #e6ccb3;"); // تعيين اللون للواجهة

    TextField userIDField = new TextField();
    userIDField.setPromptText("User ID");

    TextField showtimeIdField = new TextField();
    showtimeIdField.setPromptText("Showtime ID");

    TextField seatsField = new TextField();
    seatsField.setPromptText("Number of Seats");

    TextField bookingDateField = new TextField();
    bookingDateField.setPromptText("Booking Date (YYYY-MM-DD)");

    Button saveButton = new Button("Save Booking");
    saveButton.setOnAction(e -> {
        String[] fields = {
            userIDField.getText(),
            showtimeIdField.getText(),
            seatsField.getText(),
            bookingDateField.getText()
        };

        List<String> fieldNames = Arrays.asList("User ID", "Showtime ID", "Seats", "Booking Date");
        StringBuilder errorMessage = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isEmpty()) {
                errorMessage.append(fieldNames.get(i)).append(" is required.\n");
            }
        }

        if (errorMessage.length() > 0) {
            showAlert("Error", errorMessage.toString(), Alert.AlertType.ERROR);
        } else {
            try {
                int userID = Integer.parseInt(fields[0]);
                int showtimeId = Integer.parseInt(fields[1]);
                int seats = Integer.parseInt(fields[2]);

                // تحويل التاريخ إلى أرقام إنجليزية قبل تخزينه
                String bookingDate = convertToEnglishNumbers(fields[3]);

                if (createBooking(userID, showtimeId, seats, bookingDate)) {
                    showAlert("Success", "Booking created successfully!", Alert.AlertType.INFORMATION);
                    addBookingStage.close();
                    loadBookings(); // تحديث القائمة بعد إضافة الحجز
                } else {
                    showAlert("Error", "Booking creation failed.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "User ID, Showtime ID, and Seats must be numbers.", Alert.AlertType.ERROR);
            }
        }
    });

    addLayout.getChildren().addAll(userIDField, showtimeIdField, seatsField, bookingDateField, saveButton);
    Scene scene = new Scene(addLayout, 300, 250);
    addBookingStage.setScene(scene);
    addBookingStage.setTitle("Add Booking");
    addBookingStage.show();
}

private boolean createBooking(int userID, int showtimeId, int seats, String bookingDate) {
    // إنشاء الحجز في قاعدة البيانات
    String query = "INSERT INTO booking (userID, showtimeId, seats, BookingDate) VALUES (?, ?, ?, ?)";  // تأكد من اسم الجدول
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setInt(1, userID);
        statement.setInt(2, showtimeId);
        statement.setInt(3, seats);
        statement.setString(4, bookingDate);

        statement.executeUpdate();
        return true;
    } catch (SQLException e) {
        showAlert("Error", "Failed to create booking: " + e.getMessage(), Alert.AlertType.ERROR);
        return false;
    }
}

private String convertToEnglishNumbers(String input) {
    // تحويل الأرقام العربية إلى الإنجليزية
    String arabicNumbers = "٠١٢٣٤٥٦٧٨٩";
    String englishNumbers = "0123456789";
    for (int i = 0; i < arabicNumbers.length(); i++) {
        input = input.replace(arabicNumbers.charAt(i), englishNumbers.charAt(i));
    }
    return input;
}

private void showAlert(String title, String message, Alert.AlertType alertType) {
    // عرض التنبيه للمستخدم
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

    public static void main(String[] args) {
        launch(args);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private String resourcePath(String fileName) {
        var resource = getClass().getResource("/images/" + fileName);
        if (resource == null) {
            throw new IllegalArgumentException("Missing image resource: " + fileName);
        }
        return resource.toExternalForm();
    }
}
