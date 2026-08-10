<template>
  <div class="error-page">
    <div class="error-page__code">404</div>
    <div class="error-page__message">Aradığınız sayfa bulunamadı</div>
    <p style="color: #9ca3af; font-size: 0.9rem; max-width: 360px; text-align: center; margin-bottom: 28px;">
      Bu sayfa taşınmış, silinmiş veya hiç var olmamış olabilir. Lütfen adresi kontrol edin.
    </p>
    <q-btn
      color="primary"
      unelevated
      to="/dashboard"
      label="Ana Sayfaya Dön"
      icon="home"
      no-caps
      class="q-px-lg"
    />
  </div>
</template>
