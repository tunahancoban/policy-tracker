# Policy Tracker — Sigorta Poliçe Takip Sistemi

> Java Spring Boot, MongoDB ve Vue.js ile geliştirilmiş kapsamlı bir sigorta poliçesi takip ve yönetim sistemi.

---

##  İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Özellikler](#-özellikler)
- [Teknolojiler](#-teknoloji-yığını)
- [Mimari Yapı](#-mimari-yapı)
- [Kurulum ve Çalıştırma](#-kurulum-ve-çalıştırma)
  - [Ön Gereksinimler](#ön-gereksinimler)
  - [Backend Kurulumu](#backend-kurulumu)
  - [Frontend Kurulumu](#frontend-kurulumu)
- [Ortam Değişkenleri](#-ortam-değişkenleri)
- [API Uç Noktaları](#-api-uç-noktaları)
- [Proje Yapısı](#-proje-yapısı)
---

##  Proje Hakkında

**Policy Tracker**, sigorta acentelerine ve uzmanlarına yönelik geliştirilmiş, poliçe yaşam döngüsünü uçtan uca yönetmeyi sağlayan modern bir web uygulamasıdır. Müşteri kaydından taksit takibine, bildirim yönetiminden dashboard analizlerine kadar tüm süreçleri tek bir platformda bir araya getirir.

Sistem; güvenli kimlik doğrulama, rol tabanlı yetkilendirme, gerçek zamanlı bildirimler ve gelişmiş arama yetenekleri sunmaktadır.

---

##  Özellikler

###  Kimlik Doğrulama & Yetkilendirme
- JWT tabanlı güvenli oturum yönetimi
- Rol tabanlı erişim kontrolü (`ADMIN`, `USER`)
- Kullanıcı profil yönetimi

### Poliçe Yönetimi
- Poliçe oluşturma, düzenleme ve silme
- Çoklu poliçe türü desteği: `KASKO`, `TRAFIK`, `KONUT`, `SAGLIK`, `DASK`
- Poliçe durumu takibi: `AKTIF`, `PASIF`, `IPTAL`
- Taksit planı oluşturma ve takibi (aylık, 3 aylık, 6 aylık)
- Gelişmiş filtreleme ve sıralama

###  Müşteri Yönetimi
- Müşteri kayıt ve profil yönetimi
- Müşteri bazında poliçe listeleme
- Ad, soyad ve e-posta ile dinamik arama

### Taksit Takibi
- Ödeme durumu yönetimi: `ODENDI`, `BEKLEMEDE`, `GECIKTI`
- Taksit bazında ödeme kaydı
- Otomatik gecikme bildirimleri

### Bildirim Sistemi
- Gerçek zamanlı WebSocket bildirimleri (STOMP protokolü)
- Poliçe ve taksit olaylarına göre otomatik bildirim üretimi
- Bildirim okundu/okunmadı yönetimi

### Dashboard & Analitik
- Poliçe türlerine göre dağılım grafikleri
- Ödeme durumu özet kartları
- Yaklaşan taksit tarihleri

### Arama & Filtreleme
- Elasticsearch destekli tam metin arama
- Müşteri ve kullanıcı dinamik arama

###  Kullanıcı Yönetimi (Admin)
- Kullanıcı oluşturma, düzenleme ve silme
- Kullanıcı rol atama
- Kullanıcı listeleme ve dinamik arama

---

##  Teknoloji Yığını

### Backend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Java | 21 | Ana programlama dili |
| Spring Boot | 4.1.0 | Uygulama çerçevesi |
| Spring Security | — | Kimlik doğrulama & yetkilendirme |
| Spring WebSocket | — | Gerçek zamanlı iletişim |
| Spring Data MongoDB | — | MongoDB ORM |
| Spring Data Elasticsearch | — | Tam metin arama |
| JJWT | 0.13.0 | JWT token üretimi ve doğrulama |
| MapStruct | 1.6.3 | DTO ↔ Entity dönüşümü |
| Lombok | — | Boilerplate kod azaltma |
| Spring AOP | — | Kesişen ilgi alanları (loglama vb.) |
| Spring Actuator | — | Uygulama izleme |

### Frontend
| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| Vue.js | 3.x | UI çerçevesi (Composition API) |
| Quasar Framework | 2.x | Vue tabanlı UI bileşen kütüphanesi |
| TypeScript | 6.x | Tip güvenli geliştirme |
| Pinia | 3.x | Durum yönetimi |
| Vue Router | 5.x | SPA yönlendirme |
| Axios | 1.x | HTTP istemcisi |
| Chart.js + vue-chartjs | 4.x | Grafik ve analiz görselleştirme |
| @stomp/stompjs | 7.x | WebSocket / STOMP istemcisi |
| Vite | — | Build aracı |

### Veritabanı & Altyapı
| Teknoloji | Açıklama |
|-----------|----------|
| MongoDB | Ana NoSQL veritabanı |
| Elasticsearch | Tam metin arama motoru |

---

## ️ Mimari Yapı

```
policy-tracker/
├── backend/
│   └── policy-tracker/           # Spring Boot uygulaması
│       └── src/main/java/
│           └── com/tunahancoban/policy_tracker/
│               ├── annotation/   # Özel anotasyonlar
│               ├── aspect/       # AOP kesişenler (loglama)
│               ├── config/       # Spring konfigürasyonları (Security, WebSocket, CORS)
│               ├── controller/   # REST API kontrolcüleri
│               ├── exception/    # Global hata yönetimi
|               ├── init/         # Data initilazer
│               ├── listener/     # Olay dinleyicileri
│               ├── mapper/       # MapStruct mapper'ları
│               ├── model/
│               │   ├── DTO/      # Veri transfer nesneleri
│               │   ├── entity/   # MongoDB doküman modelleri
│               │   ├── enums/    # Enum sabitleri
│               │   └── indexes/  # Elasticsearch indeks modelleri
│               ├── repository/   # MongoDB & Elasticsearch repository'leri
│               ├── schedule/     # Zamanlanmış görevler
│               └── service/      # İş mantığı katmanı
│
└── policy-tracker-frontend/       # Vue.js / Quasar uygulaması
    └── src/
        ├── boot/                  # Quasar boot dosyaları (axios, vb.)
        ├── components/            # Yeniden kullanılabilir Vue bileşenleri
        ├── composables/           # Vue Composition API composable'ları
        ├── css/                   # Global CSS stilleri
        ├── layouts/               # Uygulama düzeni bileşenleri
        ├── pages/                 # Sayfa bileşenleri
        ├── restservices/          # API servis katmanı
        ├── router/                # Vue Router konfigürasyonu
        ├── stores/                # Pinia store'ları
        ├── types/                 # TypeScript tip tanımları
        └── utils/                 # Yardımcı fonksiyonlar
```

---

## Kurulum ve Çalıştırma

### Ön Gereksinimler

Başlamadan önce aşağıdaki araçların kurulu olduğundan emin olun:

- **Java** 21+
- **Maven** 3.8+
- **Node.js** 22.12+ veya 24+ veya 26+
- **pnpm** (önerilen paket yöneticisi)
- **MongoDB** 6.0+ (yerel veya uzak)
- **Elasticsearch** 8.x (yerel veya uzak)

---

### Backend Kurulumu

1. **Depoyu klonlayın:**
   ```bash
   git clone <https://github.com/tunahancoban/policy-tracker>
   cd policy-tracker/backend/policy-tracker
   ```

2. **Ortam değişkenlerini yapılandırın:**
   ```bash
   cp .env.example .env
   # .env dosyasını kendi ortamınıza göre düzenleyin
   ```

3. **Uygulamayı derleyin ve çalıştırın:**
   ```bash
   ./mvnw spring-boot:run
   ```

   Backend varsayılan olarak `http://localhost:8080` adresinde çalışır.

---

### Frontend Kurulumu

1. **Frontend dizinine gidin:**
   ```bash
   cd policy-tracker/policy-tracker-frontend
   ```

2. **Bağımlılıkları yükleyin:**
   ```bash
   pnpm install
   ```

3. **Geliştirme sunucusunu başlatın:**
   ```bash
   pnpm dev
   # veya: quasar dev
   ```

   Frontend varsayılan olarak `http://localhost:9000` adresinde çalışır.

4. **Üretim derlemesi (isteğe bağlı):**
   ```bash
   pnpm build
   # veya: quasar build
   ```

5. **Kod kalite kontrolleri:**
   ```bash
   # Otomatik düzeltme ile
   pnpm lint

   # Yalnızca kontrol (düzeltmesiz)
   pnpm lint:check

   # TypeScript tip kontrolü
   pnpm typecheck
   ```

---
## Docker ile Kurulumu
1. **Ana dizine gidin**   
```bash
   git clone <https://github.com/tunahancoban/policy-tracker>
   cd policy-tracker/
   ```
  2. **docker-compose.yml dosyasını düzenleyin**
  
  3. **Docker ile ayağa kaldırın**
   ```bash
  sudo docker compose up -d
   ```
   Frontend varsayılan olarak `http://localhost` adresinde çalışır.


## Ortam Değişkenleri

### Backend (`backend/policy-tracker/.env`)

| Değişken | Açıklama | Varsayılan |
|----------|----------|-----------|
| `SPRING_PROFILES_ACTIVE` | Aktif Spring profili | `dev` |
| `SERVER_PORT` | Sunucu port numarası | `8080` |
| `SPRING_DATA_MONGODB_URI` | MongoDB bağlantı URI'si | `mongodb://localhost:27017/policy-tracker-db` |
| `JWT_SECRET` | JWT imzalama anahtarı | — |
| `ELASTICSEARCH_HOST` | Elasticsearch sunucu adresi | `localhost` |
| `ELASTICSEARCH_PORT` | Elasticsearch port numarası | `9200` |
| `ELASTICSEARCH_URIS` | Elasticsearch bağlantı URI'si | `http://localhost:9200` |

### Frontend (`policy-tracker-frontend/.env`)

| Değişken | Açıklama |
|----------|----------|
| `VITE_API_BASE_URL` | Backend API temel adresi |

---

## API Uç Noktaları

| Kontrolcü | Temel Yol | Açıklama |
|-----------|-----------|----------|
| `RestAuthController` | `/api/auth` | Giriş / kayıt / token işlemleri |
| `RestPolicyController` | `/api/policies` | Poliçe CRUD işlemleri |
| `RestCustomerController` | `/api/customers` | Müşteri yönetimi |
| `RestInstallmentController` | `/api/installments` | Taksit yönetimi |
| `RestUserController` | `/api/users` | Kullanıcı yönetimi (Admin) |
| `RestProfileController` | `/api/profile` | Profil yönetimi |
| `RestNotificationController` | `/api/notifications` | Bildirim yönetimi |
| `RestDashboardController` | `/api/dashboard` | Dashboard ve analitik verileri |

### WebSocket
- **Bağlantı noktası:** `/ws`
- **Protokol:** STOMP over SockJS
- **Kullanım:** Gerçek zamanlı bildirimler

---

## Önemli Frontend Bileşenleri

### Sayfalar
| Sayfa | Açıklama |
|-------|----------|
| `LoginPage.vue` | JWT kimlik doğrulama ekranı |
| `DashboardPage.vue` | Genel bakış ve analitik |
| `PolicyPage.vue` | Poliçe listeleme ve yönetimi |
| `PolicyDetailPage.vue` | Poliçe detayı ve taksit tablosu |
| `CustomerPage.vue` | Müşteri listeleme ve yönetimi |
| `CustomerDetailPage.vue` | Müşteri detayı ve poliçe geçmişi |
| `UserManagementPage.vue` | Kullanıcı yönetimi (Admin) |
| `ProfilePage.vue` | Kullanıcı profili |

### Composable'lar
| Composable | Sorumluluk |
|-----------|------------|
| `usePolicyForm.ts` | Poliçe formu mantığı (oluşturma/düzenleme/silme) |
| `usePolicyDetail.ts` | Poliçe detay verisi ve taksit işlemleri |
| `usePolicyList.ts` | Poliçe listeleme ve filtreleme |
| `useCustomerSearch.ts` | Dinamik müşteri arama |
| `useUserSearch.ts` | Dinamik kullanıcı arama |
| `useQueryBuilder.ts` | Gelişmiş sorgu oluşturma |
| `useWebSocket.ts` | WebSocket bağlantısı ve bildirimler |
| `useConfirmDialog.ts` | Standart onay diyaloğu |
| `useNotify.ts` | Bildirim gösterimi |

### Pinia Store'ları
| Store | Açıklama |
|-------|----------|
| `auth.ts` | Oturum ve kullanıcı bilgisi |
| `policy.ts` | Poliçe durumu |
| `customer.ts` | Müşteri durumu |
| `user.ts` | Kullanıcı yönetimi durumu |
| `dashboard.ts` | Dashboard verileri |
| `installment.ts` | Taksit durumu |
| `notification.ts` | Bildirim durumu |

---

<div align="center">
  <sub>Java Spring Boot · MongoDB · Elasticsearch · Vue.js · Quasar ile geliştirilmiştir.</sub>
</div>
