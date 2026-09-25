# ---- Estagio 1: build da aplicacao React (Vite) ----
FROM node:20-alpine AS build

WORKDIR /app

# Instala dependencias leveraging o cache de layers do Docker
COPY package*.json ./
RUN npm ci

# Codigo-fonte
COPY . .

# Gera os arquivos estaticos em /app/dist
RUN npm run build

# ---- Estagio 2: servidor estatico Nginx ----
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
