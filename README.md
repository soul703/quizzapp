
# **Quiz App - Kiến trúc Monolith Hiệu suất cao & Dễ mở rộng**

Dự án này là một ứng dụng Web Quiz được xây dựng bằng Java và Spring Boot. Mục tiêu không chỉ là tạo ra một ứng dụng hoạt động, mà còn là xây dựng nó trên một nền tảng kiến trúc Monolith vững chắc, có cấu trúc tốt, đảm bảo hiệu suất, tính sẵn sàng cao và khả năng mở rộng theo chiều ngang (horizontal scaling) khi cần thiết.

## **Mục lục**

1.  [Stack Công nghệ](#1-stack-công-nghệ)
2.  [Sơ đồ Kiến trúc Triển khai](#2-sơ-đồ-kiến-trúc-triển-khai)
3.  [Phân tích Chi tiết các Thành phần Kiến trúc](#3-phân-tích-chi-tiết-các-thành-phần-kiến-trúc)
    *   [CDN (Content Delivery Network)](#cdn-content-delivery-network)
    *   [Load Balancer (Bộ cân bằng tải)](#load-balancer-bộ-cân-bằng-tải)
    *   [Application Instances (Các phiên bản ứng dụng)](#application-instances-các-phiên-bản-ứng-dụng---stateless-monolith)
    *   [Database Cluster (Cụm Cơ sở dữ liệu)](#database-cluster-cụm-cơ-sở-dữ-liệu---primary-replica-model)
    *   [Distributed Cache (Bộ nhớ đệm phân tán)](#distributed-cache-bộ-nhớ-đệm-phân-tán---redis)
    *   [Object Storage (Lưu trữ đối tượng)](#object-storage-lưu-trữ-đối-tượng---aws-s3--minio)
4.  [Lý do Lựa chọn Kiến trúc này](#4-lý-do-lựa-chọn-kiến-trúc-này)
5.  [Hướng dẫn Triển khai](#5-hướng-dẫn-triển-khai)
6.  [Hướng dẫn Cài đặt & Chạy Local](#6-hướng-dẫn-cài-đặt--chạy-local)

## **1. Stack Công nghệ**

*   **Backend:** Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
*   **Database:** PostgreSQL
*   **Cache:** Redis
*   **Build Tool:** Maven
*   **Containerization:** Docker
*   **Deployment:** Docker Compose (for local), Kubernetes/AWS ECS (for production)

## **2. Sơ đồ Kiến trúc Triển khai**

Kiến trúc này được thiết kế để xử lý lượng truy cập lớn và đảm bảo hệ thống không có điểm lỗi đơn (Single Point of Failure).

graph TD
    subgraph "Internet"
        %% Sử dụng ký tự Unicode để biểu diễn icon người dùng
        User["👤 User"]
    end

    subgraph "Edge Network"
        %% Sử dụng ký tự Unicode và xuống dòng bằng cách đặt text trong dấu ""
        CDN["☁️ CDN<br>(Cloudflare / CloudFront)"]
    end

    subgraph "Cloud Infrastructure (AWS, GCP, Azure)"
        LB["↔️ Load Balancer<br>(AWS ALB / Nginx)"]

        subgraph "Application Auto-Scaling Group"
            App1["⚙️ App Instance 1<br>(Docker Container)"]
            App2["⚙️ App Instance 2<br>(Docker Container)"]
            AppN["... App Instance N<br>(Docker Container)"]
        end

        subgraph "Data Tier"
            Cache["⚡ Distributed Cache<br>(Redis)"]
            DB_Primary["🗃️ PostgreSQL Primary<br>(Read/Write)"]
            DB_Replica["📋 PostgreSQL Replica<br>(Read-Only)"]
            Storage["📦 Object Storage<br>(AWS S3 / MinIO)"]
        end
    end

    User -- HTTPS --> CDN
    CDN -- HTTPS --> LB
    LB -- Distributes Traffic --> App1
    LB -- Distributes Traffic --> App2
    LB -- Distributes Traffic --> AppN

    App1 <--> Cache
    App2 <--> Cache
    AppN <--> Cache

    App1 -- Read/Write --> DB_Primary
    App2 -- Read/Write --> DB_Primary
    AppN -- Read/Write --> DB_Primary

    App1 -- Read-Only --> DB_Replica
    App2 -- Read-Only --> DB_Replica
    AppN -- Read-Only --> DB_Replica

    DB_Primary -- Replication --> DB_Replica

    App1 <--> Storage
    App2 <--> Storage
    AppN <--> Storage

## **3. Phân tích Chi tiết các Thành phần Kiến trúc**

#### **CDN (Content Delivery Network)**
*   **Vai trò:** Là lớp ngoài cùng, tiếp xúc đầu tiên với người dùng.
*   **Phân tích:**
    *   **Tăng tốc độ:** Cache các tài nguyên tĩnh (CSS, JS, hình ảnh) tại các máy chủ gần người dùng nhất trên toàn cầu, giảm độ trễ đáng kể.
    *   **Giảm tải:** Giảm lượng request trực tiếp đến server ứng dụng, giúp server tập trung xử lý logic nghiệp vụ.
    *   **Bảo mật:** Cung cấp các lớp bảo vệ cơ bản chống lại các cuộc tấn công như DDoS.

#### **Load Balancer (Bộ cân bằng tải)**
*   **Vai trò:** Phân phối traffic từ CDN đến các instance ứng dụng đang hoạt động.
*   **Phân tích:**
    *   **Scalability:** Cho phép chúng ta chạy nhiều bản sao (instance) của ứng dụng. Khi traffic tăng, chỉ cần thêm instance mới, Load Balancer sẽ tự động chia tải cho chúng. Đây là cốt lõi của **Horizontal Scaling**.
    *   **High Availability:** Thực hiện "Health Checks" liên tục. Nếu một instance bị lỗi, Load Balancer sẽ tự động ngừng gửi traffic đến nó, đảm bảo người dùng không bị ảnh hưởng và hệ thống luôn sẵn sàng.

#### **Application Instances (Các phiên bản ứng dụng) - Stateless Monolith**
*   **Vai trò:** Chứa code ứng dụng Monolith (đóng gói trong Docker container) để xử lý logic nghiệp vụ.
*   **Phân tích:**
    *   **Stateless (Phi trạng thái):** Đây là nguyên tắc thiết kế **bắt buộc** để scale. Mỗi instance ứng dụng không lưu trữ bất kỳ dữ liệu phiên (session) hay file nào trên local disk của nó. Mọi request từ cùng một người dùng có thể được xử lý bởi bất kỳ instance nào mà không có sự khác biệt.
    *   **Centralized State:** Trạng thái được đẩy ra các dịch vụ bên ngoài: session người dùng được lưu trong Redis, file upload được lưu trên S3/MinIO.
    *   **Consistency:** Tất cả các instance đều chạy cùng một Docker image, đảm bảo tính nhất quán trên toàn bộ hệ thống.

#### **Database Cluster (Cụm Cơ sở dữ liệu) - Primary-Replica Model**
*   **Vai trò:** Lưu trữ dữ liệu chính của ứng dụng một cách bền vững và hiệu quả.
*   **Phân tích:**
    *   **Tách biệt Đọc-Ghi:** Các ứng dụng web thường có lượng đọc (SELECT) cao hơn nhiều so với lượng ghi (INSERT, UPDATE). Kiến trúc này tối ưu cho kịch bản đó.
    *   **Primary (Master):** Xử lý tất cả các thao tác **ghi**. Đảm bảo tính nhất quán của dữ liệu.
    *   **Replica (Slave):** Là các bản sao chỉ đọc của Primary. Xử lý tất cả các thao tác **đọc**. Chúng ta có thể thêm nhiều Replica để scale khả năng đọc của hệ thống mà không ảnh hưởng đến database chính.
    *   **Giảm tải:** Giảm áp lực lên database chính, giúp các giao dịch ghi diễn ra nhanh hơn.

#### **Distributed Cache (Bộ nhớ đệm phân tán) - Redis**
*   **Vai trò:** Lưu trữ các dữ liệu hay được truy cập vào bộ nhớ RAM để truy xuất cực nhanh.
*   **Phân tích:**
    *   **User Sessions:** Lưu trữ session của người dùng, hỗ trợ cho kiến trúc stateless của application instance.
    *   **Data Caching:** Cache kết quả của các câu query DB tốn kém hoặc dữ liệu ít thay đổi (ví dụ: chi tiết một bài quiz, bảng xếp hạng). Điều này giảm đáng kể số lần truy vấn xuống database, tăng tốc độ phản hồi của API.

#### **Object Storage (Lưu trữ đối tượng) - AWS S3 / MinIO**
*   **Vai trò:** Lưu trữ các file có dung lượng lớn (binary files) như ảnh đại diện, hình ảnh trong câu hỏi.
*   **Phân tích:**
    *   **Chuyên dụng:** Các dịch vụ này được thiết kế để lưu trữ và truy xuất file một cách hiệu quả, bền bỉ và rẻ hơn so với việc lưu chúng trong database hoặc trên file system của server.
    *   **Stateless Support:** Giúp các application instance duy trì trạng thái stateless.

## **4. Lý do Lựa chọn Kiến trúc này**

Kiến trúc "Well-Structured Monolith" này được chọn vì nó mang lại sự **cân bằng hoàn hảo giữa tốc độ phát triển và khả năng mở rộng** cho giai đoạn đầu và giai đoạn tăng trưởng của một sản phẩm.

1.  **Đơn giản trong Phát triển & Vận hành (ban đầu):**
    *   **Một Codebase duy nhất:** Dễ dàng hơn cho việc phát triển, gỡ lỗi (debug) và kiểm thử (testing) so với việc quản lý nhiều repositories trong kiến trúc Microservices.
    *   **Triển khai đơn giản hơn:** Chỉ cần build và triển khai một artifact duy nhất. Giảm độ phức tạp về DevOps so với việc phải điều phối việc triển khai nhiều services.

2.  **Hiệu năng cao:**
    *   Giao tiếp giữa các module trong Monolith là các lời gọi phương thức (in-process calls), có độ trễ cực thấp so với các lời gọi mạng (network calls) giữa các Microservices.

3.  **Chi phí thấp hơn (ban đầu):**
    *   Yêu cầu ít tài nguyên hạ tầng và chi phí vận hành hơn so với một hệ thống Microservices hoàn chỉnh.

4.  **Không phải là ngõ cụt - Lộ trình Scale rõ ràng:**
    *   Nhờ thiết kế **stateless** và tách biệt các thành phần dữ liệu (Database, Cache, Storage), chúng ta có thể dễ dàng **scale theo chiều ngang** bằng cách thêm các application instance.
    *   Khi ứng dụng thực sự trở nên quá lớn và phức tạp, cấu trúc module rõ ràng bên trong monolith (theo domain) sẽ là nền tảng vững chắc để **tách dần ra thành Microservices** một cách có kiểm soát.

Tóm lại, kiến trúc này giúp chúng ta **nhanh chóng đưa sản phẩm ra thị trường (Time-to-Market)** mà không phải hy sinh khả năng phục vụ lượng người dùng lớn trong tương lai.

## **5. Hướng dẫn Triển khai**

1.  **Build Application:**
    ```bash
    mvn clean package
    ```
    Lệnh này sẽ tạo ra file `quiz-app-0.0.1-SNAPSHOT.jar` trong thư mục `target/`.

2.  **Containerize (Đóng gói Docker):**
    Sử dụng `Dockerfile` sau để tạo image cho ứng dụng:
    ```dockerfile
    # Sử dụng base image Java 17 chính thức
    FROM eclipse-temurin:17-jdk-alpine

    # Thiết lập thư mục làm việc
    WORKDIR /app

    # Copy file JAR đã được build vào container
    COPY target/quiz-app-0.0.1-SNAPSHOT.jar app.jar

    # Expose port mà ứng dụng Spring Boot sẽ chạy
    EXPOSE 8080

    # Lệnh để chạy ứng dụng khi container khởi động
    ENTRYPOINT ["java", "-jar", "app.jar"]
    ```
    Build Docker image:
    ```bash
    docker build -t quiz-app:latest .
    ```

3.  **Thiết lập Hạ tầng:**
    *   Cài đặt một cụm PostgreSQL với 1 Primary và ít nhất 1 Replica.
    *   Cài đặt một instance Redis.
    *   Cấu hình Load Balancer để trỏ đến các IP/port của các container ứng dụng.

4.  **Chạy Ứng dụng:**
    Triển khai Docker image đã build lên một nền tảng điều phối container như **Kubernetes** hoặc **AWS ECS**. Nền tảng này sẽ quản lý việc chạy nhiều instance, tự động scale và thực hiện rolling updates.

## **6. Hướng dẫn Cài đặt & Chạy Local**

1.  **Yêu cầu:**
    *   Java 17
    *   Maven 3.8+
    *   Docker & Docker Compose

2.  **Clone a project:**
    ```bash
    git clone <repository-url>
    cd quiz-app
    ```

3.  **Cấu hình:**
    Sao chép file `application.yml.example` thành `application.yml` và cập nhật thông tin kết nối đến PostgreSQL và Redis của bạn.

4.  **Chạy các dịch vụ phụ thuộc bằng Docker Compose:**
    Sử dụng file `docker-compose.yml` để khởi chạy PostgreSQL và Redis.
    ```bash
    docker-compose up -d
    ```

5.  **Chạy ứng dụng Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```

Ứng dụng sẽ có thể truy cập tại `http://localhost:8080`.
