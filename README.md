# 📱 SongXanh App

Ứng dụng Android giúp người dùng **quản lý sức khỏe, dinh dưỡng và luyện tập**. SongXanh hỗ trợ theo dõi bữa ăn, lập kế hoạch tập luyện, ghi nhận thành tựu, và tích hợp **Firebase** để đồng bộ dữ liệu, đồng thời cung cấp trải nghiệm hiện đại với giao diện trực quan.  

---

## 🚀 Tính năng chính  
- 🔐 **Xác thực người dùng** bằng Firebase Authentication.  
- 🥗 **Quản lý dinh dưỡng**: lưu trữ món ăn, thực đơn theo ngày, tính calo.  
- 🏋️ **Lịch tập luyện**: tạo và theo dõi kế hoạch tập luyện.  
- 🏆 **Thành tựu & hoạt động**: ghi nhận thói quen, thành tựu cá nhân.  
- 📊 **Báo cáo trực quan**: biểu đồ về calo và hoạt động.  
- ✨ **Giao diện hiện đại**: hỗ trợ animation, widget, navigation graph.  

---

## 🛠️ Công nghệ sử dụng  
- **Ngôn ngữ**: Java  
- **Kiến trúc**: phân tầng `data` – `ui` – `utils`  
- **Firebase**: Authentication, Firestore, Cloud Storage  
- **Jetpack**: LiveData, Navigation, ViewModel  
- **Glide**: tải ảnh động  
- **DataBinding**: kết nối dữ liệu với UI  

---

## 📂 Cấu trúc thư mục  
```
songxanh-app/
 ├── app/
 │   ├── src/main/
 │   │   ├── java/com/example/songxanh/
 │   │   │   ├── data/          # Repository, models, adapters
 │   │   │   ├── ui/            # Screens, animations, widgets
 │   │   │   └── utils/         # Global methods, Firebase constants, Glide module
 │   │   ├── res/               # Layout, drawable, values, navigation graph
 │   │   └── AndroidManifest.xml
 │   ├── build.gradle
 │   └── google-services.json   # Firebase config
 ├── build.gradle
 └── settings.gradle
```

---

## ⚙️ Cài đặt & chạy  
1. **Clone repo**  
   ```bash
   git clone https://github.com/thh1239/songxanh-app.git
   cd songxanh-app
   ```

2. **Mở bằng Android Studio**  
   - Cài **Android Studio** (Arctic Fox trở lên).  
   - Import project vào IDE.  

3. **Cấu hình Firebase**  
   - Tạo project Firebase.  
   - Tải file `google-services.json` về và đặt trong `app/`.  

4. **Build & Run**  
   - Chạy trên **AVD** hoặc thiết bị thật (yêu cầu Android 8.0+).  

---

## 📸 Screenshot (gợi ý)  
> *(Chèn ảnh minh họa màn hình Home, Meal Planner, Workout…)*  

---

## 👥 Nhóm phát triển  
- **Dương Chí Hải**  
- **Lê Thị Mỹ Linh**  
- **Tạ Hải Hoàn**  
