# Foody Delivery

Sistema de acompanhamento de pedidos do Foody Delivery.

## Stack

### Backend

- Java 25
- Spring Boot
- Maven

### Database

- PostgreSQL
- H2

### Frontend

- React.js
- Vite

### Decisão sobre o H2:

O projeto utiliza **H2** como banco secundário para facilitar a execução local sem a necessidade de configurar um banco externo.

---

## Como executar

### 1. Backend

Entre na pasta:

```bash
cd backend
```

Por padrão, a aplicação utiliza PostgreSQL, informe as variáveis de ambiente do banco:

```bash
DB_NAME=foody_delivery \
DB_USERNAME=meu_usuario \
DB_PASSWORD=minha_senha \
JWT_SECRET=uuid \
./mvnw spring-boot:run
```

As variáveis são utilizadas para configurar a conexão com o banco PostgreSQL:

- `DB_NAME`: nome do banco de dados;
- `DB_USERNAME`: usuário do banco;
- `DB_PASSWORD`: senha do banco;
- `JWT_SECRET`: Secret do sistema JWT (Adicione um UUID aleatório como parâmetro);

Nesse caso, é necessário ter um PostgreSQL disponível e criar o banco:

```sql
CREATE DATABASE foody_delivery;
```

A API estará disponível em:

```text
http://localhost:8080
```

### Executando com H2

Para executar utilizando H2, use o profile `h2`:

```bash
JWT_SECRET=uuid \
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

O H2 é utilizado para simplificar a execução do projeto. O PostgreSQL permanece disponível como principal para execução com banco relacional externo.

---

### 2. Frontend

Entre na pasta:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Configure a URL da API no arquivo `.env`:

```bash
cp .env.example .env
```

```env
VITE_API_URL=http://localhost:8080
```

Inicie a aplicação:

```bash
npm run dev
```

A aplicação estará disponível em:

```text
http://localhost:5173
```

---
## Testes

Para executar os testes do backend:

```bash
cd backend
./mvnw test
```
