FROM node:20-alpine
WORKDIR /app
COPY frontshoppingmall/package*.json ./
RUN npm install
COPY frontshoppingmall/ .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
