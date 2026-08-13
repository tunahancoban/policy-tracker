# 🔍 Policy Tracker — Kapsamlı Proje Analiz Raporu

---

## 1. Genel İyileştirme Alanları

### 1.1 Backend

| # | Alan | Dosya | Sorun | Öneri |
|---|------|-------|-------|-------|
| 1 | **Hardcoded Yıl** | [useDashboardData.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/composables/useDashboardData.ts#L35) | `getCharts(2026)` yılı hardcoded | Dinamik `new Date().getFullYear()` kullanılmalı |
| 2 | **System.out Kullanımı** | `DashboardService.java`, `GlobalExceptionHandler.java` | `System.out.println` / `System.err.println` ile loglama yapılıyor | SLF4J Logger (`@Slf4j`) kullanılmalı |
| 3 | **switch-case yerine Map/Strategy** | `PolicyService.updatePolicy()`, `UserService.updateUser()` | `Map<String, Object>` ile gelen update'ler switch-case ile parse ediliyor, type-safe değil | DTO tabanlı partial update veya `UpdatePolicyRequest` DTO'su kullanılmalı |
| 4 | **`getPolicyPrefix` switch-case** | `IdGeneratorService.java` | Eski `switch` sözdizimi kullanılıyor | Java 21'de `switch expression` (`->`) kullanılmalı |
| 5 | **URL'lerde Trailing Space** | [customerService.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/restservices/customerService.ts#L23), [policyService.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/restservices/policyService.ts#L23), [userService.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/restservices/userService.ts#L16) | `/create-customer `, `/create-policy `, `/create-user ` sonunda boşluk var | Trailing space'ler kaldırılmalı — potansiyel 404 hatasına neden olabilir |
| 6 | **Hardcoded BaseURL** | [axios.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/boot/axios.ts#L4) | `http://localhost:8080` sabit kodlanmış | Environment variable (`import.meta.env.VITE_API_URL`) kullanılmalı |
| 7 | **Hardcoded WebSocket URL** | [useWebSocket.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/composables/useWebSocket.ts#L10) | `ws://localhost:8080/ws` sabit kodlanmış | Env variable ile konfigüre edilmeli |
| 8 | **Test Eksikliği** | `src/test/` | Backend test klasörü boş veya minimum | Unit + Integration testleri eklenmeli |
| 9 | **pom.xml Metadata** | [pom.xml](file:///home/rache/Documents/Codes/policy-tracker/backend/policy-tracker/pom.xml#L14-L16) | `<name/>`, `<description/>`, `<url/>` alanları boş | Proje bilgileri doldurulmalı |
| 10 | **spring-boot-starter-aop Versiyonu** | [pom.xml](file:///home/rache/Documents/Codes/policy-tracker/backend/policy-tracker/pom.xml#L47) | `3.2.5` olarak hardcoded, parent `4.1.0` | Parent BOM'dan versiyon miras alınmalı, `<version>` kaldırılmalı |
| 11 | **`RenewalPoliciesTable.vue` Kullanılmıyor** | [RenewalPoliciesTable.vue](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/components/RenewalPoliciesTable.vue) | Hiçbir yerde import edilmiyor | Ölü kod — kaldırılmalı veya entegre edilmeli |
| 12 | **`EssentialLink.vue` Kullanılmıyor** | `EssentialLink.vue` | Quasar scaffold'dan kalan bileşen | Kaldırılmalı |
| 13 | **userService Method Adı** | [userService.ts](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/restservices/userService.ts#L7) | `getCustomer()` adı ile User listesi çekiliyor | `getUsers()` olarak yeniden adlandırılmalı |
| 14 | **`@CrossOrigin` Tutarsızlığı** | `RestUserController.java` | Sadece UserController'da `@CrossOrigin(origins = "*")` var, diğerlerinde yok | CORS ayarları `SecurityConfig`'de merkezi yönetilmeli |
| 15 | **Pagination Eksikliği** | `RestUserController.java` | User listesi paginated değil, tüm kullanıcılar bir seferde dönüyor | `Pageable` destekli endpoint'e geçilmeli |

### 1.2 Frontend Mimari

| # | Alan | Sorun | Öneri |
|---|------|-------|-------|
| 16 | **Composable Tutarsızlığı** | `useDashboardData` doğrudan service çağırıyor ama `useCustomerList` / `usePolicyList` store üzerinden çalışıyor | Tek bir pattern seçilmeli (Service → Store → Composable) |
| 17 | **CSS Neredeyse Boş** | [app.css](file:///home/rache/Documents/Codes/policy-tracker/frontend/policy-tracker-frontend/src/css/app.css) sadece 21 byte | Global CSS değişkenleri, animasyonlar, ve tema tanımları bu dosyada olmalı |
| 18 | **`fade-in-up` CSS Sınıfı Tanımsız** | `DashboardPage.vue`, `PolicyPage.vue` | Template'de `fade-in-up` kullanılıyor ama CSS'de tanımlı değil | `app.css`'de animasyon tanımlanmalı |

---

## 2. Kullanıcı Deneyimi (UX) İyileştirmeleri

| # | Alan | Mevcut Durum | Öneri |
|---|------|-------------|-------|
| 1 | **Loading Skeleton** | Sayfa yüklenirken sadece spinner gösteriliyor | Skeleton loader (shimmer efektli placeholder) kullanılmalı — özellikle Dashboard kartları ve tablolar için |
| 2 | **Empty State Tasarımı** | Sadece ikon + tek satır metin var | Anlamlı illüstrasyon, açıklayıcı metin ve "Yeni Ekle" CTA butonu eklenmeli |
| 3 | **Tablo Satır Tıklama Geri Bildirimi** | `CustomerPage` tablosunda satırlar cursor-pointer gösteriyor ama `PolicyPage`'de CSS yok | Tüm tıklanabilir tablolarda hover efekti ve cursor-pointer tutarlı olmalı |
| 4 | **Arama UX'i** | PolicyPage'de arama `@clear` event'i dinlemiyor, "Temizle" butonu sadece `outline` stili | Her iki sayfada da arama alanında `@clear` desteklenmeli, temizle butonu tutarlı olmalı |
| 5 | **Başarı/Hata Bildirimleri** | Notify kullanımı var ama ikon ve pozisyon tutarsız | Tüm Notify çağrılarında tutarlı `icon`, `position: 'top-right'`, ve `timeout` kullanılmalı |
| 6 | **Form Validation Feedback** | Bazı modal formlarda inline validation var, bazılarında yok | Tüm formlarda gerçek zamanlı inline validation eklenmeli |
| 7 | **Tarih Formatı Tutarsızlığı** | Bazı yerlerde ham ISO tarih, bazılarında formatlanmış | Tüm tarihleri `formatDate` helper ile tutarlı göstermeli (özellikle PolicyDetailPage'de `policy.startDate` ham) |
| 8 | **Prim Tutarı Formatı** | `57 TL` gibi ham sayı gösteriliyor | `toLocaleString('tr-TR')` ile binlik ayraçlı formatlama (ör: `57.000,00 TL`) |
| 9 | **Dashboard Kartlarında İkon Eksikliği** | `DashboardSummaryCard` kartlarında ikon yok, düz metin | Her kart için temalı ikon ve arka plan rengi eklenmeli |
| 10 | **Sidebar Aktif Sayfa Vurgusu** | Mevcut ama basic | Aktif öğede sol kenarda renkli çizgi (border-left indicator) eklenmeli |
| 11 | **Mobile Responsive** | Sidebar drawer mevcut ama mobile optimizasyon minimum | Küçük ekranlarda tablo yerine kart bazlı liste, bottom-sheet modal kullanılmalı |
| 12 | **Poliçe Detay Sayfasında Düzenleme Yok** | PolicyDetailPage'de sadece görüntüleme var | Detay sayfasına düzenleme ve silme butonları eklenmeli |
| 13 | **Breadcrumb Navigasyon** | Yok | `Dashboard > Müşteriler > Ahmet Yılmaz` şeklinde breadcrumb eklenmeli |
| 14 | **Keyboard Shortcuts** | Yok | `Ctrl+N` (yeni kayıt), `Esc` (modal kapat) gibi kısayollar eklenmeli |
| 15 | **Veri Yenileme Göstergesi** | WebSocket bağlantı durumu kullanıcıya gösterilmiyor | Bağlantı durumu için header'da küçük bir gösterge eklenmeli |
| 16 | **Taksit Ödeme İşlemi** | Taksitler sadece görüntüleniyor, ödeme işaretleme yok | "Ödendi Olarak İşaretle" butonu eklenmeli |
| 17 | **Müşteri Silme — İlişkili Poliçe Uyarısı** | Müşteri silinirken ilişkili poliçeler hakkında uyarı yok | Silmeden önce ilişkili kayıt sayısı gösterilmeli |

---

## 3. Hata Yönetimi Sorunları

### 3.1 Backend

| # | Dosya | Sorun | Etki | Öneri |
|---|-------|-------|------|-------|
| 1 | **Genel** | Tüm business exception'lar `RuntimeException` fırlatıyor | Farklı hata tipleri ayırt edilemiyor, HTTP status code her zaman 400 | Özel exception sınıfları: `ResourceNotFoundException` (404), `ConflictException` (409), `ValidationException` (422) tanımlanmalı |
| 2 | `GlobalExceptionHandler` | Sadece `RuntimeException` ve `ConstraintViolationException` handle ediliyor | `NullPointerException`, `ClassCastException` vs. yakalanmıyor | Genel `Exception.class` handler + `MethodArgumentNotValidException` handler eklenmeli |
| 3 | `PolicyService.updatePolicy()` | `Map<String, Object>` üzerinde type-unsafe casting: `(String) value`, `((Number) value)` | `ClassCastException` riski, kötü input'larda 500 hatası | DTO kullanılmalı veya her cast try-catch ile sarılmalı |
| 4 | `UserService.updateUser()` | Aynı `Map<String, Object>` pattern'i, type-unsafe | Aynı riskler | Aynı çözüm |
| 5 | `DashboardService.getCharts()` | Exception catch ediliyor ama `System.err.println` ile loglanıp 0 değer dönüyor | Hata sessizce yutulur, kullanıcı farkına varmaz | Uygun loglama + partial failure response dönmeli |
| 6 | `PolicyService.getPolicyById()` | `existsByPolicyId` + `findByPolicyId` iki ayrı DB sorgusu (race condition riski) | Sorgular arası kayıt silinebilir | `findByPolicyId` + null check + `Optional` pattern kullanılmalı |
| 7 | `CustomerService` | Aynı exist + find double-query pattern | Aynı risk | Aynı çözüm |

### 3.2 Frontend

| # | Dosya | Sorun | Etki | Öneri |
|---|-------|-------|------|-------|
| 8 | `axios.ts` Interceptor | 401 (Unauthorized) durumunda özel handling yok | Token süresi dolduğunda kullanıcı belirsiz bir hata mesajı alır | 401'de otomatik olarak login sayfasına yönlendirme + auth state temizleme eklenmeli |
| 9 | `axios.ts` Interceptor | Network hatalarında (ağ kesintisi) `error.response` undefined olur | Hata mesajı `undefined` dönebilir | Network error kontrolü: `if (!error.response) return reject(new Error('Sunucuya ulaşılamıyor'))` |
| 10 | Store'lar (`customer.ts`, `policy.ts`) | Hata durumunda `console.error` + throw/return — tutarsız | Bazı hatalarda throw, bazılarında sessizce yutma | Tutarlı error handling stratejisi: her zaman throw veya bir `error` ref tutulmalı |
| 11 | `NewPolicyModal.onSubmit()` | `try-catch` var ama `emit('created', payload)` senkron — async hata yakalanamaz | Parent'taki API hatası modal'da gösterilmez | Parent'tan sonuç callback'i beklenmeli veya store üzerinden hata yönetimi |
| 12 | `EditPolicyModal.onSubmit()` | Aynı senkron emit sorunu | Aynı etki | Aynı çözüm |
| 13 | `CustomerModal.saveCustomer()` | Validation sadece 3 zorunlu alan kontrolü (form rules bypass edilebilir) | `@click` handler'ı form submit yerine `saveCustomer` çağırıyor, `q-form` validation tetiklenmez | `@submit` event kullanılmalı |
| 14 | `useWebSocket.ts` | `stompClient.onStompError` handler tanımlı değil | WS bağlantı hataları sessizce yutulur | `onStompError` ve `onWebSocketError` callback'leri eklenmeli |
| 15 | `useDashboardData.loadDashboard()` | Hata throw ediyor ama `DashboardPage.onMounted` hatayı yakalamıyor | Uncaught promise rejection | `refreshAllData` etrafına try-catch eklenmeli |

---

## 4. SOLID Prensip İhlalleri

### 4.1 Single Responsibility Principle (SRP) İhlalleri

| # | Dosya | İhlal | Açıklama |
|---|-------|-------|----------|
| 1 | `PolicyService.java` | Hem iş mantığı, hem ID üretimi koordinasyonu, hem taksit oluşturma, hem WebSocket bildirim gönderiyor | Her bir sorumluluk ayrı bir service'e devredilmeli. `PolicyCreationOrchestrator` gibi bir üst katman servis oluşturulabilir |
| 2 | `PolicyStore` (frontend) | Hem genel poliçe listesi, hem müşteri poliçeleri, hem müşteri özeti, hem seçili poliçe yönetimi tek store'da | `customerPolicies` ve `summary` ayrı bir store'a (ör: `useCustomerPolicyStore`) taşınmalı |
| 3 | `UserManagementPage.vue` | Sayfa bileşeni hem liste, hem form modal, hem CRUD iş mantığı, hem validation kurallarını barındırıyor (234 satır) | Modal bileşen (`UserModal.vue`), composable (`useUserManagement.ts`) olarak ayrılmalı |
| 4 | `LoggingAspect.java` | `result instanceof Customer` kontrolü ile Customer'a özel loglama yapıyor | Aspect genel olmalı, entity-spesifik loglama bilgisi annotation parametresinden gelmeli |
| 5 | `RestProfileController.java` | Profil güncelleme, profil getirme ve mevcut kullanıcı bilgisi getirme — üçü bir controller'da problem değil ama `getCurrentUser()` `AuthService` kullanırken diğerleri `UserService` kullanıyor | Tutarlılık sağlanmalı |

### 4.2 Open/Closed Principle (OCP) İhlalleri

| # | Dosya | İhlal | Açıklama |
|---|-------|-------|----------|
| 6 | `PolicyService.updatePolicy()` | Yeni bir alan eklemek için switch-case'e yeni bir `case` eklenmeli | Reflection-based veya DTO-based mapping kullanılmalı |
| 7 | `UserService.updateUser()` | Aynı switch-case sorunu | Aynı çözüm |
| 8 | `IdGeneratorService.getPolicyPrefix()` | Yeni `PolicyType` eklendiğinde bu metoda case eklenmeli | `PolicyType` enum'ına `getPrefix()` metodu eklenebilir (enum'ın kendi sorumluluğu) |
| 9 | `policyHelper.ts` | `policyColorMap`, `typeBackgroundMap`, `typeTextMap` hepsi ayrı ayrı yeni tip eklenmesini gerektiriyor | Tek bir `PolicyTypeConfig` map'i oluşturulabilir |

### 4.3 Liskov Substitution Principle (LSP) İhlalleri

| # | Dosya | İhlal |
|---|-------|-------|
| 10 | `GlobalExceptionHandler` | `RuntimeException` handler'ı **tüm** unchecked exception'ları yakalıyor (NPE dahil). Bir `NullPointerException` da 400 BAD_REQUEST döndürüyor — bu yanlış, 500 olmalı |

### 4.4 Interface Segregation Principle (ISP) İhlalleri

| # | Dosya | İhlal | Açıklama |
|---|-------|-------|----------|
| 11 | `PolicyTable.vue` | 10+ prop alıyor, birçok emit tanımlıyor | Her kullanım senaryosunda farklı prop kombinasyonları gerekiyor. Daha granüler alt bileşenler veya slot-based composition tercih edilmeli |
| 12 | `usePolicyStore` | Store'u kullanan her bileşen tüm state'e erişiyor (genel poliçeler, müşteri poliçeleri, summary) | Ayrı store'lara bölünmeli |

### 4.5 Dependency Inversion Principle (DIP) İhlalleri

| # | Dosya | İhlal | Açıklama |
|---|-------|-------|----------|
| 13 | Backend Service Katmanı | Service'ler interface kullanmadan doğrudan concrete class'lara bağımlı | `PolicyService` → `ICustomerService`, `IIdGeneratorService` gibi interface'ler tanımlanmalı |
| 14 | Frontend Store'lar | Store'lar doğrudan REST service modüllerine bağımlı | Service'ler inject edilebilir hale getirilmeli (test edilebilirlik için) |
| 15 | `NewPolicyModal.vue` | Doğrudan `useCustomerStore()` çağırıyor | Müşteri listesi prop veya composable ile inject edilmeli |

---

## 5. UI İyileştirme Alanları

### 5.1 Görsel Tasarım

| # | Alan | Mevcut Durum | Öneri |
|---|------|-------------|-------|
| 1 | **Renk Paleti** | Quasar varsayılan mavi tonu, görsel kimlik yok | Özel marka renk paleti tanımlanmalı (`quasar.config.ts` → `brand` colors) |
| 2 | **Dashboard Kartları** | Düz beyaz kartlar, sadece sayı ve metin | Gradient arka plan, büyük ikon, trend göstergesi (↑ %5 gibi), ve hover animasyonu eklenmeli |
| 3 | **Login Sayfası** | Temel card + form, Quasar logosu kullanılıyor | Özel logo, split-screen layout (sol: branding, sağ: form), veya glassmorphism efektli kart |
| 4 | **Sidebar** | Varsayılan Quasar drawer, stil yok | Renkli ikon badge'leri, bölüm ayırıcıları, kullanıcı avatar + rol gösterimi eklenmeli |
| 5 | **Tablo Tasarımı** | Varsayılan `q-table` görünümü | Zebra-striping, satır hover vurgusu, daha iyi header stili |
| 6 | **Chip/Badge Tutarlılığı** | Bazı yerlerde `dense`, bazılarında `square`, renk paleti tutarsız | Tüm chip/badge'ler için ortak bir stil rehberi oluşturulmalı |
| 7 | **Typography** | Quasar varsayılan font (Roboto) | Google Fonts'tan `Inter` veya `Outfit` ile değiştirilip heading hierarchy belirginleştirilmeli |

### 5.2 Bileşen Bazlı İyileştirmeler

| # | Bileşen | Sorun | Öneri |
|---|---------|-------|-------|
| 8 | `DashboardSummaryCard` | 4 kartın hepsi aynı stilde, fark edilemiyor | Her kart farklı vurgu rengi + ikon. Örn: Müşteri = mavi/people, Aktif = yeşil/check, Yakında = turuncu/warning, Süresi Dolmuş = kırmızı/error |
| 9 | `CustomerProfileCard` | Dış `div`'de `col-12 col-md-4` grid sınıfı var — bu parent'ın sorumluluğu | Grid sınıfı bileşenin dışına, parent'a taşınmalı |
| 10 | `PolicySummaryCard` | `text-h4` başlıklar küçük ekranlarda taşıyor | Responsive font-size ve `toLocaleString` ile prim formatlaması |
| 11 | `RecentActivitiesTimeline` | Liste tabanlı, gerçek timeline değil | Quasar `q-timeline` bileşeni kullanılmalı — sol kenar çizgisi, zaman noktaları ile |
| 12 | `DashboardCharts` | Sabit `height: 320px` | Responsive yükseklik (`aspect-ratio` veya `min-height`) kullanılmalı |
| 13 | Modal'lar | `style="min-width: 450px"` inline stil | CSS sınıfına çevrilmeli, responsive olmalı (mobilde `100%` genişlik) |
| 14 | `ErrorNotFound.vue` | Minimal — sadece metin | Anlamlı 404 sayfası: illüstrasyon, "Ana Sayfaya Dön" butonu |

### 5.3 Mikro-Etkileşimler & Animasyon

| # | Alan | Öneri |
|---|------|-------|
| 15 | **Sayfa Geçişleri** | `<router-view>` etrafına `<transition>` eklenip fade/slide animasyonu |
| 16 | **Kart Hover Efektleri** | Summary kartlarında `transform: translateY(-2px)` + `box-shadow` artışı |
| 17 | **Tablo Satır Animasyonu** | Yeni kayıt eklendiğinde highlight animasyonu |
| 18 | **Buton Ripple Efekti** | Quasar'ın `v-ripple` direktifi tutarlı kullanılmıyor |
| 19 | **Chart Animasyonları** | `useStaggeredChart` mevcut ama tüm chartlarda tutarlı kullanılmıyor |
| 20 | **Modal Geçişleri** | Varsayılan Quasar dialog animasyonu — özelleştirilebilir (`transition-show="slide-up"`) |

---

## 6. Özet Öncelik Matrisi

| Öncelik | Kategori | Örnek Başlıklar |
|---------|----------|-----------------|
| 🔴 **Kritik** | Hata Yönetimi | URL trailing space bug, 401 handling eksikliği, unsafe type casting |
| 🟠 **Yüksek** | SOLID / Mimari | Store ayrışması, custom exception sınıfları, DIP uyumu |
| 🟡 **Orta** | UX | Loading skeleton, breadcrumb, tutarlı notify, tarih/para formatı |
| 🟢 **Düşük** | UI / Görsel | Renk paleti, animasyonlar, typography, 404 sayfası |

> [!IMPORTANT]
> Bu rapor yalnızca analiz amaçlıdır — hiçbir kodda değişiklik yapılmamıştır.
