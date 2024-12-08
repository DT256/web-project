# E-Commerce Website for Laptop Sales

## Mô tả dự án
Dự án **E-Commerce Website for Laptop Sales** là một ứng dụng web được xây dựng bằng **Spring Boot**, **Thymeleaf**, và **MySQL**, với mục tiêu tạo ra một nền tảng thương mại điện tử để người dùng có thể dễ dàng mua sắm các sản phẩm laptop.

Website hỗ trợ người dùng từ việc duyệt danh mục sản phẩm, xem chi tiết sản phẩm, thêm vào giỏ hàng, đến thanh toán đơn hàng. Quản trị viên có thể quản lý sản phẩm, đơn hàng và thông tin khách hàng.

---  

## Tính năng chính

### Người dùng (Khách hàng)
- **Xem sản phẩm**: Duyệt danh mục các sản phẩm laptop kèm thông tin chi tiết như giá, thông số kỹ thuật, mô tả.
- **Tìm kiếm sản phẩm**: Tìm kiếm laptop theo tên, giá cả, hãng sản xuất, hoặc cấu hình.
- **Thêm vào giỏ hàng**: Quản lý giỏ hàng (thêm, sửa, xóa sản phẩm).
- **Thanh toán**: Tiến hành đặt hàng với thông tin giao hàng và thanh toán an toàn.
- **Lịch sử đơn hàng**: Xem lại các đơn hàng đã đặt.

### Quản trị viên
- **Quản lý sản phẩm**: Thêm, sửa, xóa và cập nhật thông tin sản phẩm.
- **Quản lý danh mục**: Quản lý các danh mục sản phẩm như hãng sản xuất hoặc loại sản phẩm.
- **Quản lý đơn hàng**: Theo dõi, xác nhận hoặc hủy các đơn hàng.
- **Quản lý người dùng**: Xem danh sách khách hàng và thông tin đăng ký.

---  

## Công nghệ sử dụng

### Backend
- **Spring Boot**: Framework Java mạnh mẽ để phát triển ứng dụng web.
    - Spring Data JPA: Quản lý tương tác cơ sở dữ liệu.
    - JSON Web Token (JWT): Xác thực và phân quyền người dùng.
    - RESTful API: Cung cấp các endpoint để giao tiếp với frontend hoặc các ứng dụng khác.

### Frontend
- **Thymeleaf**: Công cụ template engine để xây dựng giao diện động.
- **Bootstrap**: Tạo giao diện thân thiện và đáp ứng trên mọi thiết bị.

### Database
- **MySQL**: Lưu trữ dữ liệu sản phẩm, người dùng, đơn hàng và các thông tin khác.

### Công cụ khác
- **Maven**: Quản lý dependencies và build project.
- **Lombok**: Giảm boilerplate code trong các entity.

---  

## Cách chạy dự án

### 1. Yêu cầu môi trường
- **Java 21** hoặc cao hơn.
- **Maven 3**.
- **MySQL Server**.

### 2. Cấu hình cơ sở dữ liệu
- Tạo một cơ sở dữ liệu MySQL mới, ví dụ: `laptop_store`.
- Cập nhật thông tin kết nối trong file `application.properties`:

```properties  
spring.application.name=web-project

spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.locale=vn

spring.datasource.url=jdbc:mysql:
spring.datasource.username=
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
```
### 3. Build và chạy ứng dụng
- Mở terminal và điều hướng đến thư mục dự án.
- Chạy lệnh sau để build và chạy ứng dụng:
```bash
mvn spring-boot:run  
```
- Ứng dụng sẽ chạy tại http://localhost:8080.

## Đóng góp

Nếu bạn muốn đóng góp vào dự án, hãy làm theo các bước sau:

1. **Fork repository**.
2. **Tạo nhánh mới**:

```bash  
git checkout -b feature/your-feature-name  
```
3. **Tạo nhánh mới**:
```bash  
git commit -m "Add your feature"  
```
4. **Push nhánh**:
```bash  
git push origin feature/your-feature-name  
```
5. **Tạo Pull Request**

## Danh sách thành viên
1. **Bùi Đức Thắng - 22110422**
2. **Nguyễn Hoài Tân - 22110413**
3. **Trần Phi Thắng - 22110424**
4. **Võ Nguyễn Xuân Thịnh - 22110429**