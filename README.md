# Taubatku - Aplikasi Waktu Sholat & Catatan Islami

**Taubatku** adalah aplikasi waktu sholat lengkap yang dirancang untuk membantu umat Muslim memantau jadwal sholat harian, membaca hadist, dan menjaga jurnal spiritual pribadi. Aplikasi ini memberikan jadwal sholat akurat berdasarkan lokasi pengguna serta hadir dengan antarmuka yang indah dan mudah digunakan.

---

## 🔑 Tentang File `google-services.json`

File **google-services.json** adalah file konfigurasi penting yang digunakan untuk menghubungkan aplikasi Android dengan layanan Firebase.  
File ini berisi informasi seperti:
- Project ID Firebase
- Application ID (ID aplikasi Android)
- API key (kunci untuk akses Firebase)
- Informasi konfigurasi lain yang dibutuhkan supaya aplikasi bisa memanfaatkan fitur Firebase seperti Authentication dan Firestore.

Di dalam repository ini, file `google-services.json` dapat ditemukan di:
📎 [app/google-services.json](https://github.com/Hidayattt24/Taubatku/blob/main/app/google-services.json)  
Pastikan file ini sudah terpasang di folder `app/` saat menjalankan atau membangun proyek.

---

## ✨ Fitur Utama

- 🕌 **Jadwal Sholat**
  - Update waktu sholat secara real-time
  - Jadwal sholat berbasis lokasi
  - Highlight dinamis untuk waktu sholat saat ini
  - Tampilan kalender bulanan jadwal sholat

- 📖 **Konten Islami**
  - Hadist harian
  - Kalimatullah harian
  - Pengingat Islami

- 📝 **Jurnal Pribadi**
  - Fitur jurnal Islami
  - Catatan refleksi harian
  - Catatan spiritual pribadi

- ⚙️ **Fitur Tambahan**
  - Sistem autentikasi pengguna (Firebase Authentication)
  - Layanan berbasis lokasi
  - Pengaturan yang dapat dikustomisasi
  - Desain antarmuka dengan gradasi warna yang indah

---

## 🔧 Detail Teknis

### Dibangun Dengan
- Native Android (Kotlin)
- Firebase Authentication
- Firebase Firestore
- Material Design Components
- API Waktu Sholat
- Google Location Services

### Arsitektur
- Pola **MVVM (Model-View-ViewModel)**
- Pola **Repository** untuk manajemen data
- **LiveData** untuk update UI secara reaktif
- **Coroutines** untuk operasi asynchronous

### Persyaratan
- Android 6.0 (API level 23) atau lebih tinggi
- Google Play Services terpasang
- Koneksi internet aktif
- Layanan lokasi diaktifkan

---

## 📲 Cara Instalasi

1️⃣ Clone repository ini:
```bash
git clone https://github.com/Hidayattt24/Taubatku.git
````

2️⃣ Buka proyek di Android Studio.
3️⃣ Sinkronisasi file Gradle.
4️⃣ Jalankan aplikasi di perangkat Android atau emulator.

---

## 👥 Tim Pengembang

Aplikasi ini dikembangkan oleh **Kelompok 4**:

* **Hidayat Nur Hakim**

  * Lead Developer
  * Backend 
  * UI/UX Designer

* **Fathiya Namira Fardhi**

  * Front End
  * Implementasi Fitur

📄 **Link Dokumentasi**

* GitHub: [https://github.com/Hidayattt24/Taubatku](https://github.com/Hidayattt24/Taubatku)
* Google Drive: [Dokumentasi Drive](https://drive.google.com/file/d/1Je9DUv8p7gmItQrAyJgQz11I9Dpkze-n/view?usp=sharing)

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah lisensi **MIT** 

---

© 2025 Taubatku. Semua hak dilindungi.

