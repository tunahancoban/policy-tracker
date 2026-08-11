<template>
  <div class="error-page">
    <div class="error-page__code">404</div>
    <div class="error-page__message">Aradığınız sayfa bulunamadı</div>
    <!-- Description paragraph — styled via .error-page__description-text in app.css -->
    <p class="error-page__description-text">
      Bu sayfa taşınmış, silinmiş veya hiç var olmamış olabilir. Lütfen adresi kontrol edin.
    </p>
    <q-btn color="primary" unelevated to="/dashboard" label="Ana Sayfaya Dön" icon="home" no-caps class="q-px-lg" />
  </div>
</template>

<style scoped>
/* --- 404 Page --- */
.error-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  background-color: #f9fafb;
  padding: 24px;
}

.error-page__code {
  font-size: clamp(6rem, 20vw, 10rem);
  font-weight: 800;
  color: #e5e7eb;
  line-height: 1;
  letter-spacing: -4px;
}

.error-page__message {
  font-size: 1.25rem;
  font-weight: 600;
  color: #6b7280;
  margin-top: 8px;
  margin-bottom: 24px;
}
</style>