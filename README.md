# Dashboard Service - Inteligência de Performance para Gestão de Objetivos

**Transformando dados de tarefas em decisões estratégicas, produtividade real e resultados mensuráveis.**
<img width="1536" height="1024" alt="Generatedimage_1774911582331" src="https://github.com/user-attachments/assets/43fb93b9-168a-4944-9cb8-47ce2d9b876a" />

## Introdução e Visão Geral

Em ambientes com múltiplos objetivos e tarefas em andamento, líderes e usuários frequentemente enfrentam um problema crítico: **falta de visibilidade clara sobre progresso, gargalos e motivos de baixa execução**.  
Sem análises consolidadas, decisões acabam sendo tomadas com base em percepção, e não em evidências.

Este projeto resolve esse desafio ao entregar um microserviço robusto de dashboard analítico, capaz de:

- consolidar dados de categorias, objetivos e tarefas;
- calcular métricas de produtividade e distribuição de status;
- mapear padrões de cancelamento;
- disponibilizar insights via API para consumo por front-end, apps e outros serviços.

Além disso, a solução integra uma camada de IA para geração de recomendações acionáveis, elevando o valor percebido do produto para usuários finais e para o negócio.

### Benefícios diretos para o usuário e para o negócio

- **Visão 360 do desempenho:** leitura rápida do que está sendo entregue e do que está bloqueando resultados.
- **Decisão orientada por dados:** métricas e percentuais que facilitam priorização e correção de rota.
- **Escalabilidade por arquitetura de microserviços:** integração com serviços de autenticação, tarefas e categorias.
- **Experiência premium com IA:** recomendações personalizadas para aumento de produtividade e foco.

## Funcionalidades Principais

1. **Dashboard analítico consolidado por usuário**
   - Retorna estrutura completa de categorias, objetivos e indicadores de tarefas.
   - Agrega valor ao centralizar dados dispersos em uma única resposta de alto valor estratégico.

2. **Análise detalhada por categoria e objetivo**
   - Permite aprofundar a leitura de performance por contexto de negócio.
   - Ajuda a identificar quais áreas estão performando bem e quais exigem intervenção.

3. **Métricas de produtividade por status de tarefa**
   - Calcula totais e percentuais de tarefas `TODO`, `IN_PROGRESS`, `DONE`, `LATE` e `CANCELLED`.
   - Entrega indicadores objetivos para acompanhamento de evolução e tomada de decisão.

4. **Análise de motivos de cancelamento**
   - Consolida e contabiliza as razões mais frequentes de cancelamento de tarefas.
   - Gera inteligência prática para atacar causas-raiz e reduzir retrabalho.

5. **Listagem de tarefas canceladas por objetivo**
   - Expõe quais tarefas foram canceladas em cada objetivo.
   - Facilita auditoria, retrospectiva e melhoria contínua de processos.

6. **Validação de segurança via token**
   - Todos os endpoints críticos validam autenticação antes de processar dados.
   - Reforça governança, proteção de dados e aderência a boas práticas de API.

7. **Integração com IA para recomendações acionáveis**
   - Endpoint específico envia contexto analítico para modelo de IA e retorna orientação prática.
   - Diferencial comercial: converte dados operacionais em recomendações de alto impacto.

8. **Documentação automática de API com OpenAPI/Swagger**
   - Facilita onboarding técnico, testes rápidos e integração com consumidores da API.
   - Reduz atrito entre times e acelera entregas.

## Tecnologias Utilizadas

### Back-end e Arquitetura

- **Java 21**
- **Spring Boot 3.4.6**
- **Spring Cloud 2024.0.1**
- **Spring Web**
- **Spring Cloud OpenFeign**
- **Spring Cloud Config Client**
- **Spring Boot Actuator**
- **Springdoc OpenAPI (Swagger UI)**

### Segurança e Integrações

- **JWT (`java-jwt`)** para validações de token em fluxos de autenticação.
- **Integrações HTTP/REST** com serviços externos (`category-service`, `task-service`, `auth-for-ms-service` e `email-service`).
- **OpenRouter (Claude)** para camada de recomendação inteligente baseada em dados reais.

### Qualidade e Testes

- **Spring Boot Test**
- **Rest Assured (API testing)**

### DevOps e Execução

- **Maven** para build e gerenciamento de dependências.
- **Docker Compose** para execução containerizada do serviço.

### Justificativas técnicas estratégicas

- **Spring Boot + OpenFeign**: acelera desenvolvimento de microserviços e padroniza comunicação entre serviços.
- **Java 21**: ganho em performance, modernidade da linguagem e suporte de longo prazo.
- **Actuator + OpenAPI**: melhora observabilidade, governança e produtividade do time técnico.

## Demonstração Visual

- **Screenshot do Swagger UI**
   ![Swagger UI - Endpoints do Dashboard](./docs/images/swagger-dashboard.png)

## Contribuição

Contribuições são bem-vindas para evolução contínua da solução.

1. Faça um fork do projeto.
2. Crie uma branch para sua feature:
   - `git checkout -b feature/minha-melhoria`
3. Commit suas alterações:
   - `git commit -m "feat: adiciona melhoria X"`
4. Envie para seu repositório remoto:
   - `git push origin feature/minha-melhoria`
5. Abra um Pull Request com contexto técnico e impacto esperado.

## Licença

Este projeto está distribuído sob a licença **MIT**.  
Caso deseje outro modelo (proprietário/comercial), ajuste este bloco conforme a estratégia do portfólio.

## Agradecimentos

- Ao ecossistema Spring, pela produtividade e robustez.
- À comunidade open source, por ferramentas que aceleram inovação real.
- Aos profissionais e mentores que inspiram padrões elevados de engenharia de software.

## Contato

Apresente seus canais profissionais para oportunidades:

- **LinkedIn:** [https://www.linkedin.com/in/victor-teixeira-354a131a3/]
- **GitHub:** [https://github.com/victorteixeirasilva]
- **E-mail:** [victor.teixeira@inovasoft.tech]

---

**Projeto desenvolvido para demonstrar domínio em arquitetura de microserviços, integração entre APIs, análise de dados operacionais e aplicação de IA para geração de valor de negócio.**
