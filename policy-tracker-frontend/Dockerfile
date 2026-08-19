# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

# 1. Bağımlılık dosyalarını kopyala
COPY package.json package-lock.json ./

# 2. --ignore-scripts ile postinstall (quasar prepare) çökmesini engelle
RUN npm ci --legacy-peer-deps --ignore-scripts

# 3. Kaynak kodları kopyala
COPY . .

# 4. Kodlar kopyalandıktan sonra Quasar prepare adımını manuel çalıştır
RUN npx quasar prepare

# 5. Ortam değişkenlerini yaz
ARG QCLI_BASE_URL=http://localhost:8080
ARG QCLI_WS_URL=ws://localhost:8080/ws

RUN echo "QCLI_BASE_URL=${QCLI_BASE_URL}" > .env && \
    echo "QCLI_WS_URL=${QCLI_WS_URL}" >> .env

# 6. Global CLI kurmadan doğrudan npx ile build al
RUN npx quasar build

# ─────────────────────────────────────────────
# Stage 2: Serve with Nginx
# ─────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Özel nginx konfigürasyonu
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Build çıktısını nginx'e kopyala
COPY --from=builder /app/dist/spa /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
