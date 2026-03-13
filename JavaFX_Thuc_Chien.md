# 🎓 JavaFX Thực Chiến – Học Nhanh, Đủ Dùng, Áp Dụng Ngay

> **Dành cho:** Lập trình viên đã biết Java Core, chưa biết GUI  
> **Mục tiêu:** Tự xây dựng ứng dụng CRUD hoàn chỉnh bằng JavaFX  
> **Phong cách:** Senior Dev – không lý thuyết dư thừa, học xong dùng được ngay

---

## 📋 Mục Lục

1. [Tổng quan JavaFX + Kiến trúc](#module-1-tổng-quan-javafx--kiến-trúc)
2. [Stage – Scene – Node](#module-2-stage--scene--node)
3. [Layout (VBox, HBox, BorderPane, GridPane)](#module-3-layout)
4. [Control (Button, TextField, Label, TableView...)](#module-4-control)
5. [Event Handling](#module-5-event-handling)
6. [FXML + Scene Builder](#module-6-fxml--scene-builder)
7. [MVC trong JavaFX](#module-7-mvc-trong-javafx)
8. [Binding & Observable](#module-8-binding--observable)
9. [TableView CRUD](#module-9-tableview-crud)
10. [CSS trong JavaFX](#module-10-css-trong-javafx)
11. [Chuyển Scene / Multiple Window](#module-11-chuyển-scene--multiple-window)
12. [Kết nối Database (Mini Project)](#module-12-kết-nối-database)
13. [Packaging Ứng dụng](#module-13-packaging-ứng-dụng)

---

## MODULE 1: Tổng Quan JavaFX + Kiến Trúc

### 📌 Khái niệm ngắn gọn

JavaFX là framework để xây dựng **desktop GUI** bằng Java. Nó thay thế Swing (cũ, xấu, khó) với mô hình hiện đại hơn.

Kiến trúc JavaFX hoạt động theo mô hình **cây (Scene Graph)**:

```
Application
    └── Stage  (Cửa sổ - window)
            └── Scene  (Màn hình - container chính)
                    └── Node  (UI components: Button, Label, VBox...)
```

> ⚡ **Tư duy quan trọng:** Mọi thứ hiển thị trên màn hình đều là **Node**. Node lồng vào nhau tạo thành cây. JavaFX render cái cây đó lên màn hình.

---

### 🎯 Khi nào dùng trong thực tế

- Xây app desktop: quản lý sinh viên, quản lý bán hàng, POS, tool nội bộ
- Thay thế web app khi khách hàng cần app cài máy tính
- Thi môn OOP/lập trình ứng dụng ở đại học

---

### 💻 Ví dụ code đơn giản – Hello JavaFX

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloApp extends Application {

    @Override
    public void start(Stage stage) {
        // Tạo Node
        Label label = new Label("Hello JavaFX!");

        // Đặt Node vào Scene (tham số: root node, width, height)
        Scene scene = new Scene(label, 400, 300);

        // Đặt Scene vào Stage
        stage.setScene(scene);
        stage.setTitle("App đầu tiên");
        stage.show(); // Hiện cửa sổ
    }

    public static void main(String[] args) {
        launch(args); // Bắt buộc – khởi động JavaFX thread
    }
}
```

---

### 🔧 Ví dụ nâng cao – Lifecycle đầy đủ

```java
public class LifecycleApp extends Application {

    @Override
    public void init() {
        // Chạy TRƯỚC start() – dùng để: load config, kết nối DB
        // KHÔNG chạy trên JavaFX thread → KHÔNG tạo UI ở đây
        System.out.println("1. init() – chuẩn bị tài nguyên");
    }

    @Override
    public void start(Stage stage) {
        // Chạy trên JavaFX thread – TẠO UI Ở ĐÂY
        System.out.println("2. start() – tạo UI");

        Label label = new Label("App đang chạy");
        stage.setScene(new Scene(label, 400, 300));
        stage.show();
    }

    @Override
    public void stop() {
        // Chạy khi đóng app – dùng để: đóng DB connection, lưu file
        System.out.println("3. stop() – dọn dẹp tài nguyên");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

---

### 🛠 Setup nhanh với Maven

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21</version>
</dependency>
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| `IllegalStateException: Not on FX thread` | Tạo UI trong `init()` hoặc thread phụ | Chỉ tạo UI trong `start()` hoặc dùng `Platform.runLater()` |
| App không hiện cửa sổ | Quên gọi `stage.show()` | Luôn gọi `show()` cuối `start()` |
| `NullPointerException` khi launch | Quên gọi `launch(args)` trong `main` | Copy đúng cấu trúc template |
| Module error (Java 11+) | Thiếu VM options | Thêm `--module-path` và `--add-modules javafx.controls,javafx.fxml` |

---

### 📝 Bài tập Module 1

Tạo một JavaFX app có:
1. Hiển thị cửa sổ 500x400, tiêu đề là tên bạn
2. Hiện một `Label` với nội dung: *"Xin chào, tôi đang học JavaFX!"*
3. In ra console: `"App đã khởi động"` trong `init()` và `"App đã đóng"` trong `stop()`
4. Chạy thử, đóng cửa sổ và xác nhận console in đúng thứ tự

---

## MODULE 2: Stage – Scene – Node

### 📌 Khái niệm ngắn gọn

| Thành phần | Tương đương thực tế | Vai trò |
|-----------|---------------------|---------|
| **Stage** | Cửa sổ (window) | Container ngoài cùng, do OS quản lý |
| **Scene** | Nội dung bên trong cửa sổ | Chứa toàn bộ UI tree |
| **Node** | Mọi thứ nhìn thấy được | Button, Label, TextField, VBox, ImageView... |

> ⚡ **Tư duy:** Stage = khung tranh, Scene = tờ giấy vẽ, Node = các hình vẽ trên giấy.

---

### 🎯 Khi nào dùng trong thực tế

- **Stage:** Mỗi cửa sổ = 1 Stage. App chính dùng Primary Stage. Popup/Dialog tạo Stage mới.
- **Scene:** Mỗi màn hình = 1 Scene. Chuyển màn hình = thay Scene trong Stage.
- **Node:** Mọi UI component bạn dùng đều là Node.

---

### 💻 Ví dụ Stage + Scene

```java
@Override
public void start(Stage primaryStage) {
    // Cấu hình Stage
    primaryStage.setTitle("Quản lý sinh viên");
    primaryStage.setWidth(800);
    primaryStage.setHeight(600);
    primaryStage.setResizable(false); // Không cho resize

    // Tạo layout root (VBox là một Node chứa các Node khác)
    VBox root = new VBox(10); // spacing = 10px
    root.setPadding(new Insets(20));

    Label title = new Label("Danh sách sinh viên");
    Button btnAdd = new Button("Thêm sinh viên");

    root.getChildren().addAll(title, btnAdd);

    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Xử lý khi đóng cửa sổ
    primaryStage.setOnCloseRequest(e -> {
        System.out.println("Đang đóng app...");
        // e.consume(); // Gọi cái này nếu muốn chặn đóng app
    });
}
```

---

### 🔧 Ví dụ nâng cao – Chuyển Scene

```java
public class SceneSwitchDemo extends Application {

    @Override
    public void start(Stage stage) {
        // Scene 1: Màn hình đăng nhập
        VBox loginLayout = new VBox(10);
        Button btnLogin = new Button("Đăng nhập");
        loginLayout.getChildren().addAll(new Label("Màn hình Login"), btnLogin);
        Scene loginScene = new Scene(loginLayout, 400, 300);

        // Scene 2: Màn hình chính
        VBox mainLayout = new VBox(10);
        Button btnLogout = new Button("Đăng xuất");
        mainLayout.getChildren().addAll(new Label("Màn hình chính"), btnLogout);
        Scene mainScene = new Scene(mainLayout, 400, 300);

        // Chuyển scene khi click
        btnLogin.setOnAction(e -> stage.setScene(mainScene));
        btnLogout.setOnAction(e -> stage.setScene(loginScene));

        stage.setScene(loginScene);
        stage.show();
    }
}
```

---

### 🔍 Node: Phân loại quan trọng

```
Node
├── Parent (có thể chứa Node con)
│   ├── Region (có CSS layout)
│   │   ├── Control (UI controls)
│   │   │   ├── Button, Label, TextField, ComboBox...
│   │   │   └── TableView, ListView, TreeView...
│   │   └── Pane (layout containers)
│   │       ├── VBox, HBox, BorderPane, GridPane...
│   │       └── AnchorPane, StackPane, FlowPane...
│   └── Group (không có layout)
└── Shape, ImageView, Canvas... (leaf nodes)
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| Node không hiển thị | Chưa add vào parent container | Luôn dùng `parent.getChildren().add(node)` |
| Kích thước sai | Stage size ≠ Scene size | Nên set size ở Stage, để Scene tự fit |
| Đóng app không được | Dùng `e.consume()` trong `setOnCloseRequest` | Chỉ dùng `consume()` khi cần xác nhận đóng |

---

### 📝 Bài tập Module 2

Tạo app có 2 Scene:
1. **Scene 1 (Login):** Có Label "Đăng nhập", Button "Vào hệ thống"
2. **Scene 2 (Main):** Có Label "Chào mừng!", Button "Quay lại"
3. Click button để chuyển qua lại giữa 2 Scene

---

## MODULE 3: Layout

### 📌 Khái niệm ngắn gọn

Layout = Container giúp **sắp xếp vị trí các Node con** tự động.

> ⚡ **Tư duy:** Không bao giờ đặt tọa độ tuyệt đối (x,y) cho UI component trong thực tế. Luôn dùng Layout để UI tự co dãn theo kích thước màn hình.

---

### 🗺 Bản đồ các Layout hay dùng

| Layout | Mô tả | Dùng khi nào |
|--------|-------|--------------|
| **VBox** | Xếp dọc (vertical) | Form nhập liệu, sidebar |
| **HBox** | Xếp ngang (horizontal) | Thanh toolbar, nhóm button |
| **BorderPane** | Chia 5 vùng: Top/Bottom/Left/Right/Center | Layout chính của app |
| **GridPane** | Lưới hàng × cột | Form phức tạp |
| **AnchorPane** | Gắn Node vào các cạnh | Dùng trong FXML/SceneBuilder |
| **StackPane** | Xếp chồng lên nhau | Loading overlay, splash screen |
| **FlowPane** | Tự động xuống dòng | Gallery, tag cloud |

---

### 💻 VBox – Xếp dọc

```java
VBox vbox = new VBox();
vbox.setSpacing(10);           // Khoảng cách giữa các phần tử
vbox.setPadding(new Insets(20)); // Padding bên trong
vbox.setAlignment(Pos.CENTER); // Căn giữa

Label title = new Label("Đăng nhập");
TextField txtUsername = new TextField();
txtUsername.setPromptText("Tên đăng nhập");
PasswordField txtPassword = new PasswordField();
txtPassword.setPromptText("Mật khẩu");
Button btnLogin = new Button("Đăng nhập");

vbox.getChildren().addAll(title, txtUsername, txtPassword, btnLogin);
```

---

### 💻 HBox – Xếp ngang

```java
HBox hbox = new HBox(10); // spacing = 10
hbox.setAlignment(Pos.CENTER_RIGHT);

Button btnSave = new Button("Lưu");
Button btnCancel = new Button("Hủy");
Button btnDelete = new Button("Xóa");

// Đẩy btnDelete sang trái, nhóm Save+Cancel ở phải
Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS); // Spacer đẩy về 2 phía

hbox.getChildren().addAll(btnDelete, spacer, btnCancel, btnSave);
```

---

### 💻 BorderPane – Layout chính của app

```java
BorderPane root = new BorderPane();

// TOP: Thanh menu/header
HBox topBar = new HBox(10);
topBar.setPadding(new Insets(10));
topBar.setStyle("-fx-background-color: #2c3e50;");
Label appTitle = new Label("Quản Lý Sinh Viên");
appTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
topBar.getChildren().add(appTitle);

// LEFT: Sidebar menu
VBox sidebar = new VBox(5);
sidebar.setPadding(new Insets(10));
sidebar.setPrefWidth(200);
sidebar.getChildren().addAll(
    new Button("Sinh viên"),
    new Button("Môn học"),
    new Button("Điểm số")
);

// CENTER: Nội dung chính
TableView<String> tableView = new TableView<>();

// BOTTOM: Status bar
HBox statusBar = new HBox();
statusBar.setPadding(new Insets(5, 10, 5, 10));
Label statusLabel = new Label("Sẵn sàng");

root.setTop(topBar);
root.setLeft(sidebar);
root.setCenter(tableView);
root.setBottom(statusBar);
```

---

### 💻 GridPane – Form nhập liệu

```java
GridPane grid = new GridPane();
grid.setHgap(10);  // Khoảng cách ngang giữa các cột
grid.setVgap(10);  // Khoảng cách dọc giữa các hàng
grid.setPadding(new Insets(20));

// add(node, colIndex, rowIndex)
grid.add(new Label("Họ tên:"), 0, 0);
grid.add(new TextField(), 1, 0);

grid.add(new Label("Email:"), 0, 1);
grid.add(new TextField(), 1, 1);

grid.add(new Label("Ngày sinh:"), 0, 2);
grid.add(new DatePicker(), 1, 2);

// Span nhiều cột: add(node, col, row, colspan, rowspan)
Button btnSubmit = new Button("Gửi");
grid.add(btnSubmit, 1, 3, 1, 1);

// Cấu hình độ rộng cột
ColumnConstraints col1 = new ColumnConstraints(100);
ColumnConstraints col2 = new ColumnConstraints();
col2.setHgrow(Priority.ALWAYS); // Cột 2 tự giãn
grid.getColumnConstraints().addAll(col1, col2);
```

---

### 🔧 Ví dụ nâng cao – Layout tổng hợp cho app thực tế

```java
public class MainLayout extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Header
        root.setTop(createHeader());

        // Body = Sidebar + Content
        SplitPane body = new SplitPane();
        body.getItems().addAll(createSidebar(), createContent());
        body.setDividerPositions(0.2); // Sidebar chiếm 20%
        root.setCenter(body);

        // Footer
        root.setBottom(createFooter());

        stage.setScene(new Scene(root, 1000, 700));
        stage.setTitle("Student Manager");
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #34495e;");
        Label title = new Label("🎓 Quản Lý Sinh Viên");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        header.getChildren().add(title);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #ecf0f1;");

        String[] menus = {"📋 Sinh Viên", "📚 Môn Học", "📊 Điểm Số", "⚙️ Cài Đặt"};
        for (String menu : menus) {
            Button btn = new Button(menu);
            btn.setMaxWidth(Double.MAX_VALUE); // Stretch full width
            btn.setAlignment(Pos.CENTER_LEFT);
            sidebar.getChildren().add(btn);
        }
        return sidebar;
    }

    private VBox createContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().add(new Label("Chọn menu bên trái"));
        return content;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(8, 15, 8, 15));
        footer.setStyle("-fx-background-color: #bdc3c7;");
        footer.getChildren().add(new Label("Phiên bản 1.0 | 2024"));
        return footer;
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| UI bị chồng lên nhau | Dùng AnchorPane không set anchor | Dùng VBox/HBox thay vì AnchorPane khi code tay |
| Button không giãn full width | Chưa set `setMaxWidth(Double.MAX_VALUE)` | Gọi method đó cho button trong VBox |
| GridPane bị lệch cột | Sai `colIndex` hoặc `rowIndex` | Đếm kỹ từ 0, vẽ bảng ra giấy trước |
| Spacing không có tác dụng | Dùng `setMargin` nhầm context | `VBox.setMargin(node, insets)` là static method |

---

### 📝 Bài tập Module 3

Tạo form đăng ký sinh viên với GridPane gồm:
1. Họ tên (Label + TextField)
2. Mã sinh viên (Label + TextField)
3. Khoa (Label + ComboBox với 3 khoa tùy chọn)
4. Ngày sinh (Label + DatePicker)
5. 2 Button: "Lưu" và "Hủy" nằm trong HBox, căn phải

Đặt toàn bộ form trong BorderPane (Center), thêm tiêu đề "Thêm Sinh Viên" ở Top.

---

## MODULE 4: Control

### 📌 Khái niệm ngắn gọn

Control = các UI component tương tác được với người dùng. Đây là những gì người dùng nhìn thấy và sử dụng hàng ngày.

---

### 🗺 Các Control quan trọng nhất

| Control | Dùng để | Lấy giá trị |
|---------|---------|-------------|
| `Label` | Hiển thị text tĩnh | `getText()` |
| `TextField` | Nhập text 1 dòng | `getText()` |
| `PasswordField` | Nhập mật khẩu | `getText()` |
| `TextArea` | Nhập text nhiều dòng | `getText()` |
| `Button` | Nút bấm | — (dùng event) |
| `CheckBox` | Checkbox | `isSelected()` |
| `RadioButton` | Chọn 1 trong nhiều | `isSelected()` |
| `ComboBox<T>` | Dropdown chọn 1 | `getValue()` |
| `ListView<T>` | Danh sách cuộn | `getSelectionModel().getSelectedItem()` |
| `TableView<T>` | Bảng dữ liệu | Module 9 riêng |
| `DatePicker` | Chọn ngày | `getValue()` (trả về `LocalDate`) |
| `Spinner<T>` | Số tăng/giảm | `getValue()` |
| `ProgressBar` | Thanh tiến trình | `setProgress(0.0-1.0)` |

---

### 💻 TextField & Validation

```java
TextField txtName = new TextField();
txtName.setPromptText("Nhập họ tên...");
txtName.setPrefWidth(300);

// Giới hạn ký tự
txtName.textProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal.length() > 50) {
        txtName.setText(oldVal); // Không cho nhập quá 50 ký tự
    }
});

// Chỉ cho nhập số
TextField txtAge = new TextField();
txtAge.textProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal.matches("\\d*")) {
        txtAge.setText(oldVal); // Rollback nếu không phải số
    }
});

// Lấy giá trị
String name = txtName.getText().trim();
if (name.isEmpty()) {
    // Hiện lỗi
}
```

---

### 💻 ComboBox

```java
// ComboBox với String
ComboBox<String> cboKhoa = new ComboBox<>();
cboKhoa.getItems().addAll("CNTT", "Kinh tế", "Kỹ thuật", "Ngoại ngữ");
cboKhoa.setValue("CNTT"); // Giá trị mặc định
cboKhoa.setPromptText("-- Chọn khoa --");

// Lấy giá trị đã chọn
String khoa = cboKhoa.getValue();

// ComboBox với Object (hay dùng trong thực tế)
ComboBox<SinhVien> cboSV = new ComboBox<>();
cboSV.setConverter(new StringConverter<SinhVien>() {
    @Override
    public String toString(SinhVien sv) {
        return sv == null ? "" : sv.getMaSV() + " - " + sv.getHoTen();
    }
    @Override
    public SinhVien fromString(String s) { return null; }
});
```

---

### 💻 CheckBox & RadioButton

```java
// CheckBox
CheckBox chkActive = new CheckBox("Còn hoạt động");
chkActive.setSelected(true); // Mặc định checked

boolean isActive = chkActive.isSelected();

// RadioButton – phải dùng ToggleGroup để chỉ chọn 1
ToggleGroup genderGroup = new ToggleGroup();
RadioButton rdoMale = new RadioButton("Nam");
RadioButton rdoFemale = new RadioButton("Nữ");
rdoMale.setToggleGroup(genderGroup);
rdoFemale.setToggleGroup(genderGroup);
rdoMale.setSelected(true);

// Lấy giá trị đã chọn
RadioButton selected = (RadioButton) genderGroup.getSelectedToggle();
String gender = selected.getText(); // "Nam" hoặc "Nữ"
```

---

### 💻 Alert – Hộp thoại thông báo

```java
// Thông báo thành công
Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
successAlert.setTitle("Thành công");
successAlert.setHeaderText(null);
successAlert.setContentText("Đã lưu thông tin sinh viên!");
successAlert.showAndWait();

// Xác nhận xóa
Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
confirmAlert.setTitle("Xác nhận xóa");
confirmAlert.setHeaderText("Bạn có chắc muốn xóa?");
confirmAlert.setContentText("Thao tác này không thể hoàn tác.");

Optional<ButtonType> result = confirmAlert.showAndWait();
if (result.isPresent() && result.get() == ButtonType.OK) {
    // Thực hiện xóa
    System.out.println("Đã xóa!");
}

// Thông báo lỗi
Alert errorAlert = new Alert(Alert.AlertType.ERROR);
errorAlert.setTitle("Lỗi");
errorAlert.setContentText("Email không đúng định dạng!");
errorAlert.showAndWait();
```

---

### 🔧 Ví dụ nâng cao – Form nhập liệu hoàn chỉnh

```java
public class StudentFormApp extends Application {
    private TextField txtMaSV, txtHoTen, txtEmail;
    private ComboBox<String> cboKhoa;
    private DatePicker dpNgaySinh;
    private Label lblStatus;

    @Override
    public void start(Stage stage) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(30));

        // Tạo controls
        txtMaSV = new TextField();
        txtHoTen = new TextField();
        txtEmail = new TextField();
        cboKhoa = new ComboBox<>();
        cboKhoa.getItems().addAll("CNTT", "Kinh tế", "Kỹ thuật");
        dpNgaySinh = new DatePicker();
        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: red;");

        // Thêm vào form
        form.add(new Label("Mã SV:"), 0, 0);
        form.add(txtMaSV, 1, 0);
        form.add(new Label("Họ tên:"), 0, 1);
        form.add(txtHoTen, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);
        form.add(new Label("Khoa:"), 0, 3);
        form.add(cboKhoa, 1, 3);
        form.add(new Label("Ngày sinh:"), 0, 4);
        form.add(dpNgaySinh, 1, 4);

        // Buttons
        HBox btnRow = new HBox(10);
        Button btnSave = new Button("💾 Lưu");
        Button btnClear = new Button("🗑 Xóa form");
        btnRow.getChildren().addAll(btnSave, btnClear);
        form.add(btnRow, 1, 5);
        form.add(lblStatus, 1, 6);

        // Event
        btnSave.setOnAction(e -> saveStudent());
        btnClear.setOnAction(e -> clearForm());

        stage.setScene(new Scene(form, 450, 400));
        stage.setTitle("Thêm Sinh Viên");
        stage.show();
    }

    private void saveStudent() {
        // Validate
        if (txtMaSV.getText().trim().isEmpty()) {
            lblStatus.setText("❌ Vui lòng nhập Mã SV!");
            return;
        }
        if (cboKhoa.getValue() == null) {
            lblStatus.setText("❌ Vui lòng chọn Khoa!");
            return;
        }

        // TODO: Lưu vào DB
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setText("✅ Đã lưu thành công!");
    }

    private void clearForm() {
        txtMaSV.clear();
        txtHoTen.clear();
        txtEmail.clear();
        cboKhoa.setValue(null);
        dpNgaySinh.setValue(null);
        lblStatus.setText("");
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| `NullPointerException` khi `getValue()` | ComboBox chưa chọn gì | Luôn check `getValue() != null` trước |
| DatePicker trả về `null` | Người dùng chưa chọn ngày | Check `getValue() != null` |
| RadioButton không exclusive | Chưa cùng `ToggleGroup` | Luôn set cùng 1 `ToggleGroup` |
| Alert block UI mãi | Dùng `show()` thay vì `showAndWait()` | Dùng `showAndWait()` cho dialog cần kết quả |

---

### 📝 Bài tập Module 4

Tạo form Đăng Nhập:
1. TextField cho username, PasswordField cho password
2. Button "Đăng nhập" – nếu username = `"admin"` và password = `"123"` → hiện Alert SUCCESS
3. Nếu sai → hiện Alert ERROR
4. Button "Xóa" – clear cả 2 field
5. CheckBox "Nhớ mật khẩu" – nếu check thì in ra console `"Đã lưu thông tin đăng nhập"`

---

## MODULE 5: Event Handling

### 📌 Khái niệm ngắn gọn

Event Handling = xử lý sự kiện khi người dùng tương tác (click, nhập liệu, di chuột...).

Trong JavaFX, event được xử lý theo 2 giai đoạn:
1. **Capture phase:** Event đi từ root → target node
2. **Bubble phase:** Event nổi từ target → root

> ⚡ **Tư duy:** 99% trường hợp chỉ cần xử lý ở **bubble phase** (mặc định). Chỉ dùng capture khi muốn "chặn" event trước khi nó đến đích.

---

### 💻 Các loại Event hay dùng

```java
Button btn = new Button("Click tôi");

// 1. ActionEvent – phổ biến nhất (Button click, MenuItem click)
btn.setOnAction(event -> {
    System.out.println("Button được click!");
    event.consume(); // Ngăn event lan tiếp (bubble)
});

// 2. Mouse Events
btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: lightblue;"));
btn.setOnMouseExited(e -> btn.setStyle(""));
btn.setOnMouseClicked(e -> {
    if (e.getClickCount() == 2) {
        System.out.println("Double click!");
    }
    if (e.getButton() == MouseButton.SECONDARY) {
        System.out.println("Chuột phải!");
    }
});

// 3. Key Events
TextField textField = new TextField();
textField.setOnKeyPressed(e -> {
    if (e.getCode() == KeyCode.ENTER) {
        System.out.println("Nhấn Enter: " + textField.getText());
    }
    if (e.getCode() == KeyCode.ESCAPE) {
        textField.clear();
    }
});

// 4. Focus Events
textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
    if (!isFocused) {
        // Validate khi rời khỏi field
        String text = textField.getText().trim();
        System.out.println("Đã rời field, giá trị: " + text);
    }
});
```

---

### 💻 Lambda vs Anonymous Class

```java
// Cách 1: Anonymous class (cũ, dài)
btn.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        System.out.println("Clicked");
    }
});

// Cách 2: Lambda (ngắn gọn, dùng khi chỉ có 1 dòng)
btn.setOnAction(e -> System.out.println("Clicked"));

// Cách 3: Method reference (khi logic phức tạp, tách riêng method)
btn.setOnAction(this::handleButtonClick);

private void handleButtonClick(ActionEvent e) {
    // Logic phức tạp ở đây
    validateAndSave();
    showSuccessMessage();
    refreshTable();
}
```

---

### 🔧 Ví dụ nâng cao – Tái sử dụng Event Handler

```java
public class EventDemo extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tìm kiếm...");

        Button btnSearch = new Button("🔍 Tìm");
        Button btnClear = new Button("✖ Xóa");
        Label lblResult = new Label();

        // Dùng cùng handler cho cả button và phím Enter
        Runnable searchAction = () -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                lblResult.setText("Vui lòng nhập từ khóa");
                return;
            }
            lblResult.setText("Kết quả tìm kiếm: " + keyword);
        };

        btnSearch.setOnAction(e -> searchAction.run());
        txtSearch.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) searchAction.run();
        });

        btnClear.setOnAction(e -> {
            txtSearch.clear();
            lblResult.setText("");
            txtSearch.requestFocus(); // Trả focus về text field
        });

        HBox searchBar = new HBox(10, txtSearch, btnSearch, btnClear);
        root.getChildren().addAll(searchBar, lblResult);

        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| Event bị xử lý 2 lần | Add handler 2 lần (trong loop) | Chỉ add handler 1 lần |
| UI đơ khi xử lý nặng | Chạy tác vụ nặng trên FX thread | Dùng `Task` + `Platform.runLater()` |
| `NullPointerException` trong event | Truy cập field chưa khởi tạo | Khởi tạo field trước khi add handler |
| Event không fire | Gọi nhầm `setOnAction` thay vì `addEventHandler` | `setOnAction` override, `addEventHandler` thêm mới |

---

### 📝 Bài tập Module 5

Tạo mini calculator đơn giản:
1. 2 TextField nhập số
2. 4 Button: +, -, ×, ÷
3. Label hiện kết quả
4. Xử lý: Enter ở TextField 2 = click nút "+"
5. Xử lý chia cho 0: hiện Alert ERROR thay vì crash

---

## MODULE 6: FXML + Scene Builder

### 📌 Khái niệm ngắn gọn

**FXML** = XML file mô tả giao diện (thay vì viết UI bằng code Java thuần).  
**Scene Builder** = phần mềm kéo thả để thiết kế FXML trực quan.

> ⚡ **Tư duy:** FXML = tách biệt UI (thiết kế) khỏi Logic (code). Giống HTML/CSS/JS trong web.

```
project/
├── src/
│   ├── main/java/.../
│   │   ├── MainApp.java          ← khởi động app
│   │   └── StudentController.java ← xử lý logic
│   └── main/resources/.../
│       └── student-view.fxml      ← mô tả giao diện
```

---

### 💻 File FXML cơ bản

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.control.*?>

<!-- fx:controller trỏ đến class Java xử lý logic -->
<VBox spacing="10" xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.example.StudentController"
      style="-fx-padding: 20;">

    <Label text="Quản Lý Sinh Viên" style="-fx-font-size: 18; -fx-font-weight: bold;"/>

    <HBox spacing="10">
        <!-- fx:id để inject vào Controller -->
        <TextField fx:id="txtHoTen" promptText="Nhập họ tên" prefWidth="250"/>
        <!-- onAction="#methodName" để gọi method trong Controller -->
        <Button text="Thêm" onAction="#handleAdd"/>
    </HBox>

    <TableView fx:id="tableView" prefHeight="400"/>

</VBox>
```

---

### 💻 Controller class

```java
package com.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    // @FXML inject tự động từ fx:id trong FXML
    @FXML private TextField txtHoTen;
    @FXML private TableView<Student> tableView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Chạy sau khi FXML load xong – dùng để setup bảng, load data
        setupTable();
        loadData();
    }

    // Method này được gọi từ onAction="#handleAdd" trong FXML
    @FXML
    private void handleAdd() {
        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập họ tên!");
            return;
        }
        // TODO: Thêm sinh viên
        txtHoTen.clear();
    }

    private void setupTable() {
        // Setup columns...
    }

    private void loadData() {
        // Load dữ liệu vào bảng...
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}
```

---

### 💻 Load FXML từ Main App

```java
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Cách 1: FXMLLoader cơ bản
        Parent root = FXMLLoader.load(
            getClass().getResource("/com/example/student-view.fxml")
        );

        // Cách 2: FXMLLoader nâng cao (lấy được Controller)
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/example/student-view.fxml")
        );
        Parent root = loader.load();
        StudentController controller = loader.getController();
        // Có thể truyền data vào controller trước khi hiện UI
        controller.setCurrentUser("admin");

        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}
```

---

### 🔧 Ví dụ nâng cao – Truyền data giữa các màn hình

```java
// Màn hình A mở màn hình B và truyền dữ liệu
@FXML
private void openEditWindow() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/edit-student.fxml")
        );
        Parent root = loader.load();

        // Lấy controller của màn hình B
        EditStudentController editCtrl = loader.getController();

        // Truyền student đang chọn sang màn hình B
        Student selected = tableView.getSelectionModel().getSelectedItem();
        editCtrl.setStudent(selected); // EditStudentController có method này

        Stage editStage = new Stage();
        editStage.setScene(new Scene(root, 400, 350));
        editStage.setTitle("Chỉnh sửa sinh viên");
        editStage.initModality(Modality.APPLICATION_MODAL); // Block màn hình chính
        editStage.showAndWait(); // Chờ đóng mới tiếp tục

        // Sau khi màn hình B đóng, refresh bảng
        loadData();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| `fx:id` bị null trong Controller | Sai tên id hoặc thiếu `@FXML` | Kiểm tra tên `fx:id` khớp 100% với field name |
| `Location is required` | Đường dẫn FXML sai | Dùng `/` đầu path, đặt file đúng trong resources |
| `NullPointerException` trong `initialize` | Dùng field chưa inject xong | `initialize()` chạy sau khi inject, dùng an toàn |
| Controller không có method | Method thiếu `@FXML` hoặc sai tên | Phải có `@FXML` và tên phải giống trong FXML |

---

### 📝 Bài tập Module 6

Tạo lại form đăng nhập từ Module 4 nhưng **dùng FXML**:
1. Tạo file `login-view.fxml` với VBox chứa: Label "Đăng nhập", TextField username, PasswordField password, Button "Đăng nhập"
2. Tạo `LoginController.java` với `@FXML` cho các field và method `handleLogin()`
3. Load FXML từ `MainApp.java`
4. Logic login giống Module 4 (username=admin, password=123)

---

## MODULE 7: MVC Trong JavaFX

### 📌 Khái niệm ngắn gọn

**MVC = Model – View – Controller**

| Tầng | Vai trò | File trong JavaFX |
|------|---------|-------------------|
| **Model** | Dữ liệu + business logic | `Student.java`, `StudentDAO.java` |
| **View** | Giao diện người dùng | `student-view.fxml` |
| **Controller** | Kết nối Model và View | `StudentController.java` |

> ⚡ **Tư duy:** Model không biết View tồn tại. View không xử lý business logic. Controller điều phối tất cả.

---

### 💻 Cấu trúc thư mục chuẩn

```
src/main/
├── java/com/example/
│   ├── model/
│   │   ├── Student.java          ← POJO/Entity
│   │   └── StudentDAO.java       ← Database access
│   ├── controller/
│   │   ├── MainController.java
│   │   └── StudentController.java
│   ├── service/                  ← (tùy chọn cho app lớn)
│   │   └── StudentService.java
│   └── MainApp.java
└── resources/com/example/
    ├── view/
    │   ├── main-view.fxml
    │   └── student-view.fxml
    └── css/
        └── style.css
```

---

### 💻 Model – Student.java

```java
package com.example.model;

import javafx.beans.property.*;

public class Student {
    // Dùng Property để hỗ trợ Binding (Module 8)
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty maSV = new SimpleStringProperty();
    private final StringProperty hoTen = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty khoa = new SimpleStringProperty();

    // Constructor
    public Student() {}

    public Student(int id, String maSV, String hoTen, String email, String khoa) {
        this.id.set(id);
        this.maSV.set(maSV);
        this.hoTen.set(hoTen);
        this.email.set(email);
        this.khoa.set(khoa);
    }

    // Getters/Setters thông thường
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getMaSV() { return maSV.get(); }
    public void setMaSV(String maSV) { this.maSV.set(maSV); }

    public String getHoTen() { return hoTen.get(); }
    public void setHoTen(String hoTen) { this.hoTen.set(hoTen); }

    // Property getters (bắt buộc cho TableView binding)
    public IntegerProperty idProperty() { return id; }
    public StringProperty maSVProperty() { return maSV; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty emailProperty() { return email; }
    public StringProperty khoaProperty() { return khoa; }

    @Override
    public String toString() {
        return maSV.get() + " - " + hoTen.get();
    }
}
```

---

### 💻 DAO – StudentDAO.java

```java
package com.example.model;

import java.util.*;

// DAO = Data Access Object – tầng truy xuất dữ liệu
public class StudentDAO {
    // Giả lập DB với List (Module 12 sẽ thay bằng MySQL)
    private static List<Student> database = new ArrayList<>();
    private static int nextId = 1;

    static {
        // Dữ liệu mẫu
        database.add(new Student(nextId++, "SV001", "Nguyễn Văn An", "an@email.com", "CNTT"));
        database.add(new Student(nextId++, "SV002", "Trần Thị Bình", "binh@email.com", "Kinh tế"));
    }

    public List<Student> getAll() {
        return new ArrayList<>(database);
    }

    public boolean add(Student student) {
        student.setId(nextId++);
        return database.add(student);
    }

    public boolean update(Student student) {
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getId() == student.getId()) {
                database.set(i, student);
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) {
        return database.removeIf(s -> s.getId() == id);
    }

    public List<Student> search(String keyword) {
        String kw = keyword.toLowerCase();
        return database.stream()
            .filter(s -> s.getHoTen().toLowerCase().contains(kw)
                      || s.getMaSV().toLowerCase().contains(kw))
            .toList();
    }
}
```

---

### 💻 Controller kết nối Model + View

```java
package com.example.controller;

public class StudentController implements Initializable {
    @FXML private TextField txtMaSV, txtHoTen, txtEmail;
    @FXML private ComboBox<String> cboKhoa;
    @FXML private TableView<Student> tableView;
    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;

    // Tham chiếu đến Model
    private StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadAllStudents();
        setupTableSelection(); // Click hàng → điền form
    }

    // ==================== CRUD ====================

    @FXML
    private void handleAdd() {
        if (!validateForm()) return;

        Student student = new Student(0,
            txtMaSV.getText().trim(),
            txtHoTen.getText().trim(),
            txtEmail.getText().trim(),
            cboKhoa.getValue()
        );

        if (studentDAO.add(student)) {
            loadAllStudents();
            clearForm();
            showStatus("✅ Thêm thành công!", "green");
        }
    }

    @FXML
    private void handleUpdate() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("❌ Chưa chọn sinh viên để sửa!", "red");
            return;
        }
        selected.setHoTen(txtHoTen.getText().trim());
        selected.setEmail(txtEmail.getText().trim());
        selected.setKhoa(cboKhoa.getValue());

        if (studentDAO.update(selected)) {
            loadAllStudents();
            showStatus("✅ Cập nhật thành công!", "green");
        }
    }

    @FXML
    private void handleDelete() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Xóa sinh viên: " + selected.getHoTen() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                studentDAO.delete(selected.getId());
                loadAllStudents();
                clearForm();
                showStatus("✅ Đã xóa!", "green");
            }
        });
    }

    // ==================== HELPER ====================

    private void loadAllStudents() {
        studentList.setAll(studentDAO.getAll());
        tableView.setItems(studentList);
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) fillForm(newVal);
            }
        );
    }

    private void fillForm(Student s) {
        txtMaSV.setText(s.getMaSV());
        txtHoTen.setText(s.getHoTen());
        txtEmail.setText(s.getEmail());
        cboKhoa.setValue(s.getKhoa());
    }

    private boolean validateForm() {
        if (txtMaSV.getText().trim().isEmpty()) {
            showStatus("❌ Mã SV không được trống!", "red");
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtMaSV.clear(); txtHoTen.clear(); txtEmail.clear();
        cboKhoa.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private void showStatus(String msg, String color) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + color + ";");
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| Controller quá béo | Nhét cả business logic vào Controller | Tách DAO/Service riêng |
| Model bị phụ thuộc JavaFX | Model dùng `StringProperty` nhưng cần dùng ngoài JavaFX | Tạo 2 lớp: POJO thuần và JavaFX wrapper |
| Khó test | Logic trong Controller | Đưa logic vào Service class, test Service |

---

### 📝 Bài tập Module 7

Tái cấu trúc app từ Module 6 theo MVC:
1. Tạo `Student.java` với đầy đủ fields
2. Tạo `StudentDAO.java` với List giả lập DB
3. Tách Controller ra riêng, chỉ chứa code UI
4. Kết nối 3 tầng lại với nhau

---

## MODULE 8: Binding & Observable

### 📌 Khái niệm ngắn gọn

**Binding** = liên kết 2 property với nhau. Khi 1 cái thay đổi, cái kia tự động cập nhật.

**Observable** = object có thể được "theo dõi" để nhận thông báo khi thay đổi.

> ⚡ **Tư duy:** Thay vì `label.setText(textField.getText())` mỗi lần thay đổi, dùng binding để tự động đồng bộ.

---

### 💻 Property Types

```java
// String Property
StringProperty name = new SimpleStringProperty("Nguyễn Văn A");
name.set("Trần Thị B");
String value = name.get();

// Integer/Double Property
IntegerProperty age = new SimpleIntegerProperty(20);
DoubleProperty score = new SimpleDoubleProperty(8.5);

// Boolean Property
BooleanProperty isActive = new SimpleBooleanProperty(true);

// List Property (quan trọng cho TableView)
ObservableList<Student> students = FXCollections.observableArrayList();
```

---

### 💻 Binding cơ bản

```java
TextField txtInput = new TextField();
Label lblOutput = new Label();

// Binding đơn giản: lblOutput.text luôn = txtInput.text
lblOutput.textProperty().bind(txtInput.textProperty());

// Binding có transform
Label lblUpper = new Label();
lblUpper.textProperty().bind(
    txtInput.textProperty().map(String::toUpperCase) // Java 19+
);

// Hoặc dùng Bindings utility
lblUpper.textProperty().bind(
    Bindings.createStringBinding(
        () -> txtInput.getText().toUpperCase(),
        txtInput.textProperty()
    )
);

// Binding số
TextField txtA = new TextField("0");
TextField txtB = new TextField("0");
Label lblSum = new Label();

lblSum.textProperty().bind(
    Bindings.createStringBinding(() -> {
        try {
            int a = Integer.parseInt(txtA.getText());
            int b = Integer.parseInt(txtB.getText());
            return "Tổng: " + (a + b);
        } catch (NumberFormatException e) {
            return "Tổng: (lỗi định dạng)";
        }
    }, txtA.textProperty(), txtB.textProperty())
);
```

---

### 💻 ObservableList – Dùng với TableView

```java
// ObservableList tự động notify TableView khi data thay đổi
ObservableList<Student> list = FXCollections.observableArrayList();
tableView.setItems(list);

// Thêm: TableView tự cập nhật
list.add(new Student(...));

// Xóa: TableView tự cập nhật
list.remove(student);

// Cập nhật toàn bộ
list.setAll(newDataFromDB);

// Lắng nghe thay đổi
list.addListener((ListChangeListener<Student>) change -> {
    while (change.next()) {
        if (change.wasAdded()) {
            System.out.println("Thêm " + change.getAddedSize() + " phần tử");
        }
        if (change.wasRemoved()) {
            System.out.println("Xóa " + change.getRemovedSize() + " phần tử");
        }
    }
});
```

---

### 🔧 Ví dụ nâng cao – Enable/Disable Button theo điều kiện

```java
TextField txtUsername = new TextField();
TextField txtPassword = new TextField();
Button btnLogin = new Button("Đăng nhập");

// Button chỉ enable khi cả 2 field không rỗng
btnLogin.disableProperty().bind(
    txtUsername.textProperty().isEmpty()
        .or(txtPassword.textProperty().isEmpty())
);

// Ví dụ phức tạp hơn: enable khi email hợp lệ
TextField txtEmail = new TextField();
BooleanBinding emailValid = Bindings.createBooleanBinding(
    () -> txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"),
    txtEmail.textProperty()
);
btnLogin.disableProperty().bind(emailValid.not());
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| `bound value cannot be set` | Set giá trị cho property đang bị bind | `unbind()` trước khi `set()` |
| TableView không cập nhật | Modify object trong list thay vì list | Dùng `list.set(index, newObj)` hoặc dùng Property trong Model |
| Memory leak | Listener không được remove | Dùng WeakReference hoặc remove listener khi không cần |

---

### 📝 Bài tập Module 8

Tạo màn hình tìm kiếm live:
1. `TextField` để nhập từ khóa
2. `ListView` hiển thị danh sách 10 tên sinh viên
3. Khi gõ vào TextField, ListView tự lọc (không cần nhấn button)
4. Dùng `FilteredList` kết hợp `ObservableList`

```java
// Gợi ý: Dùng FilteredList
ObservableList<String> allStudents = FXCollections.observableArrayList("...");
FilteredList<String> filtered = new FilteredList<>(allStudents);
listView.setItems(filtered);
txtSearch.textProperty().addListener((obs, old, kw) -> {
    filtered.setPredicate(name -> name.toLowerCase().contains(kw.toLowerCase()));
});
```

---

## MODULE 9: TableView CRUD

### 📌 Khái niệm ngắn gọn

`TableView<T>` = component hiển thị dữ liệu dạng bảng với:
- `TableColumn<T, CellType>` = định nghĩa từng cột
- `ObservableList<T>` = nguồn dữ liệu
- `SelectionModel` = quản lý row được chọn

> ⚡ **Tư duy:** TableView là component phức tạp nhất nhưng quan trọng nhất. Master TableView = có thể làm 90% app quản lý.

---

### 💻 Setup TableView cơ bản

```java
TableView<Student> tableView = new TableView<>();

// Cột 1: Mã SV (lấy từ property)
TableColumn<Student, String> colMaSV = new TableColumn<>("Mã SV");
colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
colMaSV.setPrefWidth(100);

// Cột 2: Họ tên
TableColumn<Student, String> colHoTen = new TableColumn<>("Họ Tên");
colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
colHoTen.setPrefWidth(200);

// Cột 3: Dùng lambda (linh hoạt hơn)
TableColumn<Student, String> colKhoa = new TableColumn<>("Khoa");
colKhoa.setCellValueFactory(data ->
    new SimpleStringProperty(data.getValue().getKhoa())
);

// Cột 4: Computed column (giá trị tính toán)
TableColumn<Student, String> colStatus = new TableColumn<>("Trạng thái");
colStatus.setCellValueFactory(data -> {
    double gpa = data.getValue().getGpa();
    String status = gpa >= 5.0 ? "Đạt" : "Không đạt";
    return new SimpleStringProperty(status);
});

tableView.getColumns().addAll(colMaSV, colHoTen, colKhoa, colStatus);

// Set dữ liệu
ObservableList<Student> list = FXCollections.observableArrayList(studentDAO.getAll());
tableView.setItems(list);
```

---

### 💻 Custom Cell – Nút bấm trong cột

```java
// Cột chứa Button Xóa
TableColumn<Student, Void> colAction = new TableColumn<>("Thao tác");
colAction.setCellFactory(col -> new TableCell<>() {
    private final Button btnDelete = new Button("🗑 Xóa");

    {
        btnDelete.setOnAction(e -> {
            Student student = getTableView().getItems().get(getIndex());
            handleDelete(student);
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btnDelete);
    }
});

// Cột với nhiều button
TableColumn<Student, Void> colActions = new TableColumn<>("Thao tác");
colActions.setCellFactory(col -> new TableCell<>() {
    private final Button btnEdit = new Button("✏️");
    private final Button btnDel = new Button("🗑");
    private final HBox box = new HBox(5, btnEdit, btnDel);

    {
        btnEdit.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
        btnDel.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : box);
    }
});
```

---

### 💻 TableView với Search + Sort + Pagination

```java
// Search + Filter
TextField txtSearch = new TextField();
ObservableList<Student> masterList = FXCollections.observableArrayList(dao.getAll());
FilteredList<Student> filteredList = new FilteredList<>(masterList, p -> true);
SortedList<Student> sortedList = new SortedList<>(filteredList);
sortedList.comparatorProperty().bind(tableView.comparatorProperty());
tableView.setItems(sortedList);

txtSearch.textProperty().addListener((obs, old, kw) -> {
    filteredList.setPredicate(student -> {
        if (kw == null || kw.isBlank()) return true;
        String lower = kw.toLowerCase();
        return student.getHoTen().toLowerCase().contains(lower)
            || student.getMaSV().toLowerCase().contains(lower);
    });
});

// Placeholder khi không có data
tableView.setPlaceholder(new Label("Không có dữ liệu"));

// Chọn row và điền form
tableView.getSelectionModel().selectedItemProperty().addListener(
    (obs, oldVal, newVal) -> {
        if (newVal != null) {
            txtMaSV.setText(newVal.getMaSV());
            txtHoTen.setText(newVal.getHoTen());
        }
    }
);
```

---

### 🔧 Ví dụ CRUD hoàn chỉnh với TableView

```java
public class StudentCRUDController implements Initializable {
    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colMaSV, colHoTen, colKhoa;
    @FXML private TableColumn<Student, Void> colActions;
    @FXML private TextField txtMaSV, txtHoTen, txtEmail, txtSearch;
    @FXML private ComboBox<String> cboKhoa;
    @FXML private Button btnSave;
    @FXML private Label lblStatus;

    private StudentDAO dao = new StudentDAO();
    private ObservableList<Student> masterList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredList;
    private Student editingStudent = null; // null = thêm mới, có giá trị = đang sửa

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboKhoa.getItems().addAll("CNTT", "Kinh tế", "Kỹ thuật", "Ngoại ngữ");
        setupColumns();
        setupSearch();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colKhoa.setCellValueFactory(new PropertyValueFactory<>("khoa"));

        // Nút Sửa + Xóa trong mỗi row
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️ Sửa");
            private final Button btnDel = new Button("🗑 Xóa");
            private final HBox box = new HBox(5, btnEdit, btnDel);
            {
                btnEdit.getStyleClass().add("btn-edit");
                btnDel.getStyleClass().add("btn-delete");
                btnEdit.setOnAction(e -> startEdit(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupSearch() {
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<Student> sorted = new SortedList<>(filteredList);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sorted);

        txtSearch.textProperty().addListener((obs, old, kw) ->
            filteredList.setPredicate(s -> kw.isBlank()
                || s.getHoTen().toLowerCase().contains(kw.toLowerCase())
                || s.getMaSV().toLowerCase().contains(kw.toLowerCase()))
        );
    }

    private void loadData() {
        masterList.setAll(dao.getAll());
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) return;

        if (editingStudent == null) {
            // THÊM MỚI
            Student s = new Student(0, txtMaSV.getText(), txtHoTen.getText(),
                                   txtEmail.getText(), cboKhoa.getValue());
            dao.add(s);
            showStatus("✅ Thêm thành công!", "green");
        } else {
            // CẬP NHẬT
            editingStudent.setHoTen(txtHoTen.getText());
            editingStudent.setEmail(txtEmail.getText());
            editingStudent.setKhoa(cboKhoa.getValue());
            dao.update(editingStudent);
            showStatus("✅ Cập nhật thành công!", "green");
        }
        loadData();
        clearForm();
    }

    private void startEdit(Student student) {
        editingStudent = student;
        txtMaSV.setText(student.getMaSV());
        txtMaSV.setDisable(true); // Không cho sửa mã khi đang edit
        txtHoTen.setText(student.getHoTen());
        txtEmail.setText(student.getEmail());
        cboKhoa.setValue(student.getKhoa());
        btnSave.setText("💾 Cập nhật");
    }

    private void handleDelete(Student student) {
        new Alert(Alert.AlertType.CONFIRMATION, "Xóa: " + student.getHoTen() + "?")
            .showAndWait()
            .filter(btn -> btn == ButtonType.OK)
            .ifPresent(btn -> {
                dao.delete(student.getId());
                loadData();
                showStatus("✅ Đã xóa!", "green");
            });
    }

    @FXML
    private void handleClear() { clearForm(); }

    private void clearForm() {
        txtMaSV.clear(); txtHoTen.clear(); txtEmail.clear();
        cboKhoa.setValue(null);
        txtMaSV.setDisable(false);
        editingStudent = null;
        btnSave.setText("➕ Thêm mới");
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (txtMaSV.getText().trim().isEmpty()) {
            showStatus("❌ Vui lòng nhập Mã SV!", "red"); return false;
        }
        if (txtHoTen.getText().trim().isEmpty()) {
            showStatus("❌ Vui lòng nhập Họ tên!", "red"); return false;
        }
        return true;
    }

    private void showStatus(String msg, String color) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + color + ";");
    }
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| Cột không hiển thị data | `PropertyValueFactory` sai tên field | Tên phải match getter: `"hoTen"` → `getHoTen()` |
| Button trong cell crash | `getIndex()` trả về -1 khi empty row | Kiểm tra `empty` trong `updateItem` |
| Sắp xếp không hoạt động | Chưa bind `comparatorProperty` | `sortedList.comparatorProperty().bind(table.comparatorProperty())` |
| Xóa xong bảng không cập nhật | Xóa trong DB nhưng không reload | Gọi `masterList.setAll(dao.getAll())` sau khi xóa |

---

### 📝 Bài tập Module 9

Xây dựng bảng quản lý sản phẩm với đầy đủ chức năng:
1. Cột: STT, Tên sản phẩm, Giá, Số lượng, Thao tác
2. Thêm sản phẩm qua form bên trên bảng
3. Click row → điền vào form để sửa
4. Nút Xóa trong cột Thao tác
5. Tìm kiếm theo tên sản phẩm

---

## MODULE 10: CSS Trong JavaFX

### 📌 Khái niệm ngắn gọn

JavaFX hỗ trợ CSS để style UI component. Cú pháp gần giống CSS web nhưng dùng prefix `-fx-`.

> ⚡ **Tư duy:** Tách CSS ra file riêng (đừng viết inline) để dễ maintain và tái sử dụng.

---

### 💻 3 cách áp dụng CSS

```java
// Cách 1: Inline style (nhanh, nhưng khó maintain)
button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

// Cách 2: StyleClass (khuyến nghị)
button.getStyleClass().add("primary-button");

// Cách 3: Load file CSS cho Scene (tốt nhất)
scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
```

---

### 💻 File CSS mẫu – style.css

```css
/* ====== GLOBAL ====== */
.root {
    -fx-font-family: "Segoe UI", Arial, sans-serif;
    -fx-font-size: 13px;
    -fx-background-color: #f5f6fa;
}

/* ====== BUTTONS ====== */
.button {
    -fx-padding: 8 18;
    -fx-cursor: hand;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

.btn-primary {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
    -fx-font-weight: bold;
}

.btn-primary:hover {
    -fx-background-color: #2980b9;
}

.btn-primary:pressed {
    -fx-background-color: #21618c;
}

.btn-danger {
    -fx-background-color: #e74c3c;
    -fx-text-fill: white;
}

.btn-danger:hover {
    -fx-background-color: #c0392b;
}

.btn-success {
    -fx-background-color: #2ecc71;
    -fx-text-fill: white;
}

/* ====== TEXT FIELD ====== */
.text-field {
    -fx-padding: 8 12;
    -fx-border-color: #ddd;
    -fx-border-radius: 4;
    -fx-background-radius: 4;
    -fx-background-color: white;
}

.text-field:focused {
    -fx-border-color: #3498db;
    -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 0);
}

.text-field-error {
    -fx-border-color: #e74c3c;
}

/* ====== LABEL ====== */
.title-label {
    -fx-font-size: 22px;
    -fx-font-weight: bold;
    -fx-text-fill: #2c3e50;
}

.section-header {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: #34495e;
    -fx-padding: 10 0 5 0;
}

.error-label {
    -fx-text-fill: #e74c3c;
    -fx-font-size: 11px;
}

/* ====== TABLE ====== */
.table-view {
    -fx-border-color: #ddd;
    -fx-border-radius: 5;
}

.table-view .column-header-background {
    -fx-background-color: #34495e;
}

.table-view .column-header .label {
    -fx-text-fill: white;
    -fx-font-weight: bold;
}

.table-row-cell:odd {
    -fx-background-color: #f8f9fa;
}

.table-row-cell:selected {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
}

/* ====== SIDEBAR ====== */
.sidebar {
    -fx-background-color: #2c3e50;
    -fx-padding: 10;
}

.sidebar-btn {
    -fx-background-color: transparent;
    -fx-text-fill: #ecf0f1;
    -fx-alignment: CENTER-LEFT;
    -fx-padding: 10 15;
    -fx-font-size: 14px;
    -fx-max-width: infinity;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

.sidebar-btn:hover {
    -fx-background-color: #34495e;
}

.sidebar-btn-active {
    -fx-background-color: #3498db;
}

/* ====== CARD ====== */
.card {
    -fx-background-color: white;
    -fx-background-radius: 8;
    -fx-border-radius: 8;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
    -fx-padding: 20;
}

/* ====== HEADER ====== */
.app-header {
    -fx-background-color: #2c3e50;
    -fx-padding: 15 20;
}

.app-title {
    -fx-text-fill: white;
    -fx-font-size: 20px;
    -fx-font-weight: bold;
}
```

---

### 💻 Dùng CSS trong Controller

```java
// Thêm/xóa class động
private void validateEmail(TextField field) {
    String email = field.getText();
    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
        field.getStyleClass().add("text-field-error");
    } else {
        field.getStyleClass().remove("text-field-error");
    }
}

// Pseudo-class (dùng cho state custom)
PseudoClass errorClass = PseudoClass.getPseudoClass("error");
field.pseudoClassStateChanged(errorClass, true); // Bật state "error"
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| CSS không có tác dụng | Thiếu load stylesheet vào Scene | `scene.getStylesheets().add(...)` |
| Đường dẫn CSS null | Sai path hoặc file không trong resources | Dùng `getClass().getResource(...)`, đặt file đúng chỗ |
| Property không hỗ trợ | Dùng CSS web thay vì JavaFX CSS | Xem tài liệu JavaFX CSS: dùng prefix `-fx-` |
| Style bị override | Specificity thấp hơn inline style | Tránh dùng `setStyle()` kết hợp với CSS file |

---

### 📝 Bài tập Module 10

Style lại app quản lý sinh viên:
1. Tạo file `style.css` với màu sắc nhất quán
2. Header màu tối, sidebar màu xám đậm, content nền xám sáng
3. Button có 3 màu: primary (xanh), danger (đỏ), default (xám)
4. TableView header tối, rows xen kẽ màu trắng/xám nhạt
5. TextField highlight khi focused

---

## MODULE 11: Chuyển Scene / Multiple Window

### 📌 Khái niệm ngắn gọn

Có 2 chiến lược chuyển màn hình:
1. **Thay Scene trong Stage** – dùng cho flow chính (login → main)
2. **Mở Stage mới** – dùng cho popup, dialog, secondary window

---

### 💻 Chiến lược 1: Thay Scene (SceneManager)

```java
// SceneManager.java – Quản lý tập trung các Scene
public class SceneManager {
    private static Stage primaryStage;
    private static Map<String, Scene> scenes = new HashMap<>();

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    // Load và cache Scene
    public static void loadScene(String name, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            SceneManager.class.getResource(fxmlPath)
        );
        Parent root = loader.load();
        scenes.put(name, new Scene(root, 1000, 700));
    }

    public static void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene != null) {
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        }
    }

    public static Stage getStage() { return primaryStage; }
}

// Trong MainApp.java
public void start(Stage stage) throws IOException {
    SceneManager.init(stage);
    SceneManager.loadScene("login", "/fxml/login-view.fxml");
    SceneManager.loadScene("main", "/fxml/main-view.fxml");
    SceneManager.switchTo("login");
    stage.show();
}

// Trong LoginController.java
@FXML
private void handleLogin() {
    if (authService.login(username, password)) {
        SceneManager.switchTo("main");
    }
}
```

---

### 💻 Chiến lược 2: Mở Stage mới (Dialog/Popup)

```java
// Mở cửa sổ modal (block màn hình chính)
public static <T> T openDialog(String fxmlPath, String title, Stage owner) {
    try {
        FXMLLoader loader = new FXMLLoader(
            SceneManager.class.getResource(fxmlPath)
        );
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner); // Gắn với parent stage
        dialogStage.setScene(new Scene(root));
        dialogStage.showAndWait(); // Block đến khi đóng

        return loader.getController(); // Trả về controller để lấy kết quả
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

// Sử dụng:
@FXML
private void openAddStudentDialog() {
    AddStudentController ctrl = SceneManager.openDialog(
        "/fxml/add-student.fxml",
        "Thêm Sinh Viên",
        (Stage) tableView.getScene().getWindow()
    );

    if (ctrl != null && ctrl.isSaved()) {
        // Người dùng đã lưu
        loadData(); // Refresh bảng
    }
}
```

---

### 💻 Truyền dữ liệu giữa các cửa sổ

```java
// Cách 1: Qua constructor/setter của Controller
FXMLLoader loader = new FXMLLoader(...);
Parent root = loader.load();
EditController ctrl = loader.getController();
ctrl.setData(selectedStudent); // Truyền data TRƯỚC khi show

// Cách 2: Callback – khi cần nhận kết quả từ dialog
@FXML
private void openEditDialog() {
    FXMLLoader loader = new FXMLLoader(...);
    Parent root = loader.load();
    EditController ctrl = loader.getController();
    ctrl.setStudent(selectedStudent);

    // Truyền callback để nhận kết quả khi lưu
    ctrl.setOnSaveCallback(updatedStudent -> {
        int idx = masterList.indexOf(selectedStudent);
        masterList.set(idx, updatedStudent);
    });

    Stage dialog = new Stage();
    dialog.setScene(new Scene(root));
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.show();
}

// EditController.java
private Consumer<Student> onSaveCallback;

public void setOnSaveCallback(Consumer<Student> callback) {
    this.onSaveCallback = callback;
}

@FXML
private void handleSave() {
    Student updated = getFormData();
    if (onSaveCallback != null) {
        onSaveCallback.accept(updated);
    }
    ((Stage) btnSave.getScene().getWindow()).close();
}
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| Dialog không block | Dùng `show()` thay vì `showAndWait()` | Dùng `showAndWait()` cho modal dialog |
| Màn hình cũ bị reset | Tạo Scene mới mỗi lần switch | Cache Scene trong Map, chỉ tạo 1 lần |
| Data không được truyền | `setData()` gọi trước `load()` | Load FXML trước, rồi mới gọi `setData()` |
| Owner null | Stage chưa hiển thị | Set owner sau `stage.show()` hoặc từ Node.getScene().getWindow() |

---

### 📝 Bài tập Module 11

Xây dựng flow hoàn chỉnh:
1. **Màn hình Login** → Đăng nhập thành công → mở **Màn hình Main**
2. Màn hình Main có bảng sinh viên
3. Click "Thêm" → mở **Dialog Add** (modal)
4. Lưu xong dialog → đóng dialog, bảng tự refresh
5. Đăng xuất → quay về màn hình Login

---

## MODULE 12: Kết Nối Database

### 📌 Khái niệm ngắn gọn

Kết nối JavaFX với MySQL qua JDBC. Pattern chuẩn: **Connection Pool + DAO Pattern**.

> ⚡ **Tư duy:** Không bao giờ gọi DB trực tiếp trong Controller. Controller → DAO → DB.

---

### 🛠 Setup dependencies (Maven)

```xml
<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

---

### 💻 DatabaseConnection.java – Singleton Pattern

```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    private static Connection connection;

    private DatabaseConnection() {} // Ngăn tạo instance bên ngoài

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try { connection.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
```

---

### 💻 SQL Setup

```sql
CREATE DATABASE student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_db;

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_sv VARCHAR(20) NOT NULL UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    khoa VARCHAR(50),
    ngay_sinh DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO students (ma_sv, ho_ten, email, khoa) VALUES
('SV001', 'Nguyễn Văn An', 'an@email.com', 'CNTT'),
('SV002', 'Trần Thị Bình', 'binh@email.com', 'Kinh tế');
```

---

### 💻 StudentDAO với JDBC đầy đủ

```java
public class StudentDAO {

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Student> search(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE ho_ten LIKE ? OR ma_sv LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Student s) {
        String sql = "INSERT INTO students (ma_sv, ho_ten, email, khoa) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getMaSV());
            stmt.setString(2, s.getHoTen());
            stmt.setString(3, s.getEmail());
            stmt.setString(4, s.getKhoa());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Student s) {
        String sql = "UPDATE students SET ho_ten=?, email=?, khoa=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getHoTen());
            stmt.setString(2, s.getEmail());
            stmt.setString(3, s.getKhoa());
            stmt.setInt(4, s.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Map ResultSet thành Student object
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("ma_sv"),
            rs.getString("ho_ten"),
            rs.getString("email"),
            rs.getString("khoa")
        );
    }
}
```

---

### 💻 Chạy query nặng trong background thread

```java
// KHÔNG làm thế này – block UI thread
@FXML
private void handleLoad() {
    List<Student> list = dao.getAll(); // ❌ Nếu query chậm → UI đơ
    tableView.getItems().setAll(list);
}

// ✅ Dùng Task để chạy background
@FXML
private void handleLoad() {
    Task<List<Student>> task = new Task<>() {
        @Override
        protected List<Student> call() {
            updateMessage("Đang tải dữ liệu...");
            return dao.getAll(); // Chạy trên background thread
        }
    };

    task.setOnSucceeded(e -> {
        // Chạy lại trên FX thread
        tableView.getItems().setAll(task.getValue());
        lblStatus.setText("Tải xong!");
    });

    task.setOnFailed(e -> {
        lblStatus.setText("Lỗi: " + task.getException().getMessage());
    });

    // Bind progress bar
    progressBar.progressProperty().bind(task.progressProperty());
    lblStatus.textProperty().bind(task.messageProperty());

    new Thread(task).start();
}
```

---

### 🗂 Mini Project – Cấu trúc hoàn chỉnh

```
student-manager/
├── src/main/java/com/example/
│   ├── MainApp.java
│   ├── model/
│   │   ├── Student.java
│   │   └── DatabaseConnection.java
│   ├── dao/
│   │   └── StudentDAO.java
│   └── controller/
│       ├── LoginController.java
│       ├── MainController.java
│       └── StudentController.java
└── src/main/resources/com/example/
    ├── fxml/
    │   ├── login-view.fxml
    │   ├── main-view.fxml
    │   └── student-view.fxml
    └── css/
        └── style.css
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| `ClassNotFoundException: com.mysql.jdbc.Driver` | Thiếu MySQL connector dependency | Kiểm tra `pom.xml` |
| `Connection refused` | MySQL server chưa chạy hoặc sai cổng | Kiểm tra MySQL service, default port 3306 |
| SQL Injection | Dùng `Statement` + string concat | Luôn dùng `PreparedStatement` |
| `Connection is already closed` | Dùng connection sau khi đóng | Luôn tạo connection mới trong try-with-resources |
| UI đơ khi load data | Chạy query trên FX thread | Dùng `Task` + background thread |

---

### 📝 Bài tập Module 12

Xây dựng mini app quản lý sản phẩm với MySQL:
1. Bảng `products(id, name, price, quantity, category)`
2. CRUD đầy đủ qua `ProductDAO`
3. Tìm kiếm theo tên sản phẩm
4. Hiện tổng số sản phẩm và tổng giá trị kho hàng (label ở dưới bảng)

---

## MODULE 13: Packaging Ứng Dụng

### 📌 Khái niệm ngắn gọn

Đóng gói app thành file chạy được trên máy người dùng **mà không cần cài Java**.

---

### 🛠 Phương pháp 1: jpackage (Java 14+) – Khuyến nghị

```bash
# Bước 1: Compile ra JAR trước
mvn clean package

# Bước 2: Dùng jpackage tạo installer
jpackage \
  --input target/ \
  --name "StudentManager" \
  --main-jar StudentManager-1.0.jar \
  --main-class com.example.MainApp \
  --type exe \
  --win-shortcut \
  --win-menu \
  --icon src/main/resources/icon.ico \
  --app-version "1.0.0" \
  --vendor "Your Company"
```

---

### 🛠 Phương pháp 2: Maven + JavaFX Maven Plugin

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.example/com.example.MainApp</mainClass>
        <launcher>StudentManager</launcher>
        <jlinkZipName>StudentManager</jlinkZipName>
        <jlinkImageName>StudentManager</jlinkImageName>
        <noManPages>true</noManPages>
        <stripDebug>true</stripDebug>
        <noHeaderFiles>true</noHeaderFiles>
    </configuration>
</plugin>
```

```bash
# Build + run
mvn javafx:run

# Tạo custom JRE image (nhỏ gọn)
mvn javafx:jlink
```

---

### 📦 Checklist trước khi đóng gói

```
✅ Kiểm tra tất cả resources (FXML, CSS, images) trong classpath
✅ Cấu hình DB connection string (production vs development)
✅ Ẩn thông tin nhạy cảm (password DB) ra file config
✅ Test trên máy sạch (không cài IDE)
✅ Đặt icon cho app (.ico cho Windows, .icns cho Mac)
✅ Tạo splash screen (tùy chọn)
```

---

### 💻 Đọc config từ file (tránh hardcode)

```java
// Đọc DB config từ file properties
public class AppConfig {
    private static Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/config.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDbUrl() { return props.getProperty("db.url"); }
    public static String getDbUser() { return props.getProperty("db.user"); }
    public static String getDbPassword() { return props.getProperty("db.password"); }
}
```

```properties
# src/main/resources/config.properties
db.url=jdbc:mysql://localhost:3306/student_db
db.user=root
db.password=yourpassword
app.name=Student Manager
app.version=1.0.0
```

---

### ⚠️ Lỗi phổ biến & cách tránh

| Lỗi | Nguyên nhân | Cách tránh |
|-----|-------------|------------|
| FXML không tìm thấy sau build | Path sai khi đóng gói | Dùng `getClass().getResource(...)` không dùng `new File(...)` |
| Thiếu native libraries | JavaFX libraries không được include | Dùng `--add-modules` đầy đủ khi jpackage |
| App chạy được trên máy dev nhưng không trên máy khác | Thiếu JavaFX runtime | Dùng `jlink` để bundle runtime vào app |

---

## 🏆 Tổng Kết – Roadmap Xây Dựng App CRUD Hoàn Chỉnh

### Checklist kỹ năng sau khóa học

```
Module 1  ✅ Khởi tạo app, hiểu lifecycle, setup Maven
Module 2  ✅ Stage/Scene/Node, chuyển scene cơ bản
Module 3  ✅ Layout đúng cách, BorderPane làm khung chính
Module 4  ✅ Dùng thành thạo các Control, validate form
Module 5  ✅ Xử lý sự kiện, lambda, method reference
Module 6  ✅ FXML + SceneBuilder, tách UI khỏi code
Module 7  ✅ MVC pattern, DAO, tổ chức project
Module 8  ✅ Binding, ObservableList, FilteredList
Module 9  ✅ TableView CRUD, custom cell, search/sort
Module 10 ✅ CSS style, StyleClass, theme nhất quán
Module 11 ✅ Multi-window, modal dialog, truyền data
Module 12 ✅ Kết nối MySQL, PreparedStatement, Task
Module 13 ✅ Đóng gói app, deploy cho end-user
```

### Stack công nghệ đề xuất cho project thực tế

```
JavaFX 21         ← UI Framework
Java 17/21        ← Language
FXML              ← View layer
MVC Pattern       ← Architecture
MySQL 8.0         ← Database
JDBC              ← DB Connection
Maven             ← Build tool
Scene Builder     ← UI Design tool
CSS               ← Styling
```

### Kiến trúc app CRUD hoàn chỉnh

```
MainApp.java
    ├── SceneManager (quản lý scenes)
    ├── AppConfig (cấu hình)
    │
    ├── View Layer (FXML files)
    │   ├── login-view.fxml
    │   ├── main-view.fxml
    │   └── student-view.fxml
    │
    ├── Controller Layer
    │   ├── LoginController
    │   ├── MainController
    │   └── StudentController
    │
    ├── Model Layer
    │   ├── Student.java (entity)
    │   └── StudentDAO.java (data access)
    │
    └── Database
        └── DatabaseConnection.java (singleton)
```

---

> 📌 **Lời khuyên cuối:** Cách học tốt nhất là **làm project thực tế ngay**. Bắt đầu với app quản lý bất kỳ (sách, sản phẩm, nhân viên), áp dụng từng module vào project đó. Mỗi khi gặp vấn đề mới, quay lại module tương ứng để tra cứu.

**Chúc bạn thành công! 🚀**
