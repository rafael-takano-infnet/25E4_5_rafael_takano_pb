# Locadora de Filmes - TP Final

[![Backend CI](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/backend.yml/badge.svg?branch=main)](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/backend.yml)
[![Frontend CI](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/frontend.yml/badge.svg?branch=main)](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/frontend.yml)
[![Security](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/security.yml/badge.svg?branch=main)](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/security.yml)
[![Deploy](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/deploy.yml/badge.svg?branch=main)](https://github.com/rafael-takano-infnet/25E4_5_rafael_takano_pb_tp5/actions/workflows/deploy.yml)

Aplicação integrada de locadora com backend Spring Boot e frontend Vue.js, finalizada com CI/CD, validação pós-deploy, segurança automatizada e documentação de entrega.

## Ferramenta de Build

O enunciado final cita Gradle, mas este projeto mantém **Maven** de forma intencional.

Motivo:

- o backend já estava estabilizado e validado com Maven
- migrar para Gradle neste momento aumentaria risco sem agregar valor funcional
- os objetivos de CI/CD, segurança, deploy e cobertura foram atendidos mantendo a stack atual

## Tecnologias

- Backend: Spring Boot 3.2, Spring Data JPA, H2, Bean Validation, Maven, Java 21
- Frontend: Vue 3, Vite, Tailwind CSS v4
- Testes: JUnit 5, Mockito, JaCoCo, Selenium WebDriver
- CI/CD: GitHub Actions, GitHub Environments e runner self-hosted para execuções pós-deploy

## Arquitetura Final

- `backend/`: API REST de filmes
- `frontend/`: SPA integrada ao backend
- `.github/workflows/`: pipelines de CI, segurança, E2E e deploy
- `scripts/`: utilitários de inicialização, parada e verificação de saúde usados pelos workflows
- `docs/`: documentação final da entrega em PT-BR
- `.output/`: evidências locais e índices da validação final

## Como rodar localmente

### Backend

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Aplicação: `http://localhost:5173`

## Como validar localmente

### Backend, cobertura e evidências

```bash
cd backend
mvn verify
```

- relatório JaCoCo: `backend/target/site/jacoco/index.html`
- cobertura mínima exigida no TP5: 90%

### Frontend

```bash
cd frontend
npm install
npm run build
```

### Selenium com URL configurável

Com backend e frontend rodando:

```bash
cd backend
mvn test -Dtest="br.com.locadora.selenium.FilmeCrudSeleniumTest" -Dapp.base-url=http://localhost:5173
```

Também é possível usar a variável `APP_BASE_URL`.

## Workflows

### `backend.yml`

- executa compilação, testes e cobertura do backend
- usa Java 21 e Maven
- publica artefato do JaCoCo
- gera resumo em Markdown no GitHub Actions

### `frontend.yml`

- executa `npm ci` e `npm run build`
- publica o artefato `dist`
- gera resumo em Markdown

### `security.yml`

- executa CodeQL para Java e JavaScript
- executa dependency review em pull requests
- usa ferramentas nativas do ecossistema GitHub Actions

### `e2e-self-hosted.yml`

- usa runner `self-hosted`
- sobe backend e frontend localmente
- roda Selenium com URL configurável
- publica logs da execução

### `deploy.yml`

- pipeline de promoção para `dev`, `test` e `prod`
- usa GitHub Environments
- `dev`: verificações rápidas de saúde
- `test`: verificações rápidas de saúde do backend e frontend
- `prod`: promoção protegida por aprovação configurada no ambiente do GitHub

## Ambientes e Proteções

Configurar no repositório os ambientes:

- `dev`
- `test`
- `prod`

Para `prod`, habilitar reviewers obrigatórios em **Settings > Environments** para cumprir a exigência de aprovação manual.

## Segurança e Observabilidade

- SAST com CodeQL
- dependency review em pull requests
- logs e artefatos para depuração
- resumos em Markdown em todos os workflows principais
