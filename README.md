# ResourceMap 🤝

## 📋 Sobre o Projeto

**ResourceMap** é uma plataforma inovadora de coordenação de ajuda humanitária que utiliza inteligência artificial para conectar eficientemente doações com necessidades em tempo real. O sistema combina tecnologias modernas como Spring Boot, RabbitMQ e IA generativa para otimizar a distribuição de recursos humanitários.

### 🎯 Objetivo

Facilitar a coordenação entre doadores e organizações humanitárias, automatizando o processo de correspondência entre recursos disponíveis e necessidades urgentes através de algoritmos inteligentes e IA generativa.

## ✨ Funcionalidades Principais

### 🤖 Assistente IA Integrado
- **Geração Automática de Descrições**: IA cria descrições detalhadas para necessidades humanitárias
- **Otimização de Correspondências**: Sugestões inteligentes para melhor alocação de recursos
- **Relatórios de Impacto**: Análises automáticas de desempenho e eficiência
- **Chat Inteligente**: Conversação natural com assistente especializado em gestão humanitária

### 🎯 Sistema de Correspondências
- **Algoritmo de Matching**: Correspondência automática baseada em:
  - Categoria de recursos
  - Proximidade geográfica
  - Urgência/Prioridade
  - Quantidade disponível
- **Score de Compatibilidade**: Sistema de pontuação para qualificar correspondências
- **Notificações em Tempo Real**: Via RabbitMQ para atualizações instantâneas

### 📊 Gestão Completa
- **Dashboard Interativo**: Visão geral de estatísticas e métricas
- **Gestão de Necessidades**: CRUD completo com categorização e priorização
- **Gestão de Doações**: Controle de recursos disponíveis e status
- **Relatórios e Analytics**: Análises de eficiência e impacto

### 🔐 Segurança e Autenticação
- **Spring Security**: Autenticação robusta e autorização baseada em roles
- **OAuth2 Google**: Login social integrado
- **Gestão de Usuários**: Sistema de roles (Doador, Membro ONG, Admin)

### 🌐 Internacionalização
- **Suporte Multilíngue**: Português e Inglês
- **Interface Responsiva**: Design adaptável para dispositivos móveis

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**: Linguagem principal
- **Spring Boot 3.2.0**: Framework base
- **Spring Data JPA**: Persistência de dados
- **Spring Security**: Segurança e autenticação
- **Spring AMQP**: Integração com RabbitMQ
- **Spring AI**: Integração com modelos de IA

### Frontend
- **Thymeleaf**: Template engine
- **Bootstrap 5**: Framework CSS
- **Font Awesome**: Ícones
- **JavaScript**: Interatividade

### Banco de Dados
- **Oracle Database**: Banco principal
- **Hibernate**: ORM

### Mensageria
- **RabbitMQ**: Message broker para comunicação assíncrona

### Inteligência Artificial
- **Ollama**: Plataforma de IA local
- **Llama 3.2:1b**: Modelo de linguagem

### DevOps
- **Docker**: Containerização
- **Maven**: Gerenciamento de dependências

## 📋 Pré-requisitos

### Software Necessário
- **Java 17+**
- **Maven 3.6+**
- **Docker e Docker Compose**
- **Git**

### Serviços Externos
- **Conta Oracle Database** (ou instância local)
- **Conta Google OAuth2** (para login social)

## 🚀 Instalação e Configuração

### 1. Clone o Repositório
```bash
git clone https://github.com/seu-usuario/resourcemap.git
cd resourcemap
```

### 2. Configure o Docker (RabbitMQ)
```bash
# Inicie o RabbitMQ via Docker
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3-management

# Verificar se está rodando
docker ps
```


**Interface de Gerenciamento do RabbitMQ**: http://localhost:15672
- Usuário: `guest`
- Senha: `guest`

### 3. Configure o Ollama (IA)
```bash
# Instalar Ollama (Linux/macOS)
curl -fsSL https://ollama.ai/install.sh | sh

# Ou via Docker
docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama

# Baixar o modelo Llama 3.2:1b
ollama pull llama3.2:1b

# Verificar se está rodando
ollama list
```

### 4. Configure o Banco de Dados
Edite `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:oracle:thin:@//SEU_HOST:1521/SEU_SERVICE
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

### 5. Configure OAuth2 Google
1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um projeto ou selecione existente
3. Ative a Google+ API
4. Configure OAuth 2.0:
   - **Authorized redirect URIs**: `http://localhost:8080/login/oauth2/code/google`
5. Anote `CLIENT_ID` e `CLIENT_SECRET`

Atualize em `application.properties`:
```properties
spring.security.oauth2.client.registration.google.client-id=SEU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=SEU_CLIENT_SECRET
```

### 6. Compile e Execute
```bash
# Compile o projeto
mvn clean compile

# Execute a aplicação
mvn spring-boot:run
```

**Acesse a aplicação**: http://localhost:8080

## 📁 Estrutura do Projeto

```
resourcemap/
├── src/
│   ├── main/
│   │   ├── java/com/resourcemap/
│   │   │   ├── config/          # Configurações (Security, RabbitMQ, AI)
│   │   │   ├── controller/      # Controllers REST e Web
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   ├── service/         # Lógica de negócio
│   │   │   └── ResourcemapApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, imagens
│   │       ├── templates/       # Templates Thymeleaf
│   │       ├── messages/        # Arquivos de internacionalização
│   │       └── application.properties
│   └── test/                    # Testes unitários
├── target/                      # Arquivos compilados
├── pom.xml                      # Dependências Maven
└── README.md
```

## 🔧 Configuração Avançada

### RabbitMQ Customizado
```properties
# Configuração customizada do RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.virtual-host=/
```
![ResourceMap docker](https://github.com/bia98silva/resourcemap/blob/main/img/Docker.jpg)

### Ollama Customizado
```properties
# Configuração da IA
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.temperature=0.7
spring.ai.ollama.chat.options.model=llama3.2:1b
```
![ResourceMap Ollama](https://github.com/bia98silva/resourcemap/blob/main/img/Ollama.jpg)

### Profiles de Ambiente
```bash
# Desenvolvimento
mvn spring-boot:run -Dspring.profiles.active=dev

# Produção
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🎮 Como Usar

### 1. Acesso Inicial
- Acesse http://localhost:8080
- Crie uma conta ou faça login com Google
- Escolha seu perfil: **Doador** ou **Membro de ONG**

### 2. Para Doadores
1. **Dashboard**: Visualize estatísticas gerais
2. **Adicionar Doação**: Cadastre recursos disponíveis
3. **Ver Correspondências**: Acompanhe matches automáticos
4. **Assistente IA**: Use IA para otimizar descrições

### 3. Para ONGs
1. **Criar Necessidades**: Cadastre necessidades urgentes
2. **Gerenciar Prioridades**: Defina urgência dos recursos
3. **Acompanhar Matches**: Monitore correspondências
4. **Relatórios**: Analise eficiência da organização

### 4. Assistente IA
- **Geração de Descrições**: Digite título, categoria e localização
- **Otimização de Matches**: Forneça descrições para análise
- **Chat Inteligente**: Converse sobre estratégias
- **Relatórios**: Gere análises automáticas

## 📊 APIs Disponíveis

### Endpoints Principais
```
GET    /api/statistics           # Estatísticas gerais
GET    /api/needs               # Listar necessidades
GET    /api/donations           # Listar doações
GET    /api/matches             # Listar correspondências
POST   /api/matches/find        # Buscar novas correspondências
POST   /api/matches/{id}/confirm # Confirmar correspondência
```

### Endpoints IA
```
POST   /ai-assistant/generate-description    # Gerar descrição
POST   /ai-assistant/suggest-matching       # Sugerir estratégia
POST   /ai-assistant/chat                   # Chat com IA
POST   /ai-assistant/generate-report        # Relatório de impacto
POST   /ai-assistant/optimize               # Sugestões de otimização
```

## 🔍 Monitoramento

### RabbitMQ Management
- **URL**: http://localhost:15672
- **Usuário**: guest / guest
- **Filas Monitoradas**:
  - `matching.queue`: Correspondências
  - `notification.queue`: Notificações

### Logs da Aplicação
```bash
# Logs em tempo real
tail -f logs/resourcemap.log

# Logs específicos
grep "ERROR" logs/resourcemap.log
```

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Testes específicos
mvn test -Dtest=NeedServiceTest

# Cobertura de código
mvn jacoco:report
```

## 🚀 Deploy

### Build para Produção
```bash
# Criar JAR executável
mvn clean package -DskipTests

# Executar JAR
java -jar target/resourcemap-1.0.0.jar
```

### Docker Build
```bash
# Criar imagem Docker
docker build -t resourcemap:latest .

# Executar container
docker run -p 8080:8080 resourcemap:latest
```

## 👥 Equipe
- **Beatriz Silva RM552600
- **Vitor Onofre Ramos RM553241
- **Pedro Henrique soares araujo - RM553801

- **AI Integration**: Powered by Ollama & Llama 3.2
- **Infrastructure**: Docker & RabbitMQ

- **Link do Pitch: https://www.youtube.com/watch?v=Wzal2XSpM6s 
- **Link da aplicação rodando: https://youtu.be/O8ddFnr0Mxk

**ResourceMap** - *Conectando necessidades com doações através da tecnologia* 🤝✨

![ResourceMap Logo](https://github.com/bia98silva/resourcemap/blob/main/img/Captura%20de%20tela%202025-06-02%20133716.jpg)

> "Tecnologia a serviço da humanidade - transformando a gestão de recursos humanitários através da inteligência artificial."
