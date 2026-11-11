# 🤖 Declaração de Uso de Inteligência Artificial (AI_DECLARATION.md)

**Instituição:** Universidade Católica do Salvador – UCSal  
**Curso:** Análise e Desenvolvimento de Sistemas  
**Disciplina:** Testes e Qualidade de Software  
**Professor:** Pedro Arthur de Melo Nascimento  

**Equipe:**
- Cauã Lopes
- Arivaldo Teixeira (Neto)
- Ryan Abade
- Pedro Luiz

---

## 📌 Compromisso de Conformidade e Transparência

Declaramos que o uso de ferramentas de IA neste projeto atendeu estritamente aos limites fixados no documento do projeto (máximo de 10% da carga de trabalho total).

### 1. Escopo de Uso Permitido e Aplicado:
- **Fase 01 & 02:** Revisão ortográfica em relatórios Markdown, auxílio na interpretação de erros sintáticos/exceções de compilação, formatação visual de mensagens via console e apoio no entendimento de regras do SonarQube/JUnit 5.
- **Fase 03:** Auxílio pontual na adequação estrita ao Checkstyle (Google Java Style), quebras de linha longa, documentação Javadoc em métodos utilitários e suporte na formatação de tabelas em Markdown nos relatórios de cobertura do JaCoCo e SonarQube.

### 2. Isenção Absoluta de GenAI (Concepção Autônoma da Equipe):
- ❌ **Requisitos Autorais (Fase 03):** O design e implementação do Requisito Autoral 04 (Compilação Estatística do Torneio e Eleição do PokéSal MVP) e do Requisito Autoral 05 (Drop de Item de Recompensa Pós-Vitória) foram concepções 100% autorais da equipe.
- ❌ **Lógica de Negócio e Arquitetura:** O controle de chaveamento de semifinal/final (`GerenciadorTorneio`), o loop de combate (`GerenciadorDeBatalha`), os algoritmos de precisão/crítico e o desacoplamento do `GeradorAleatorio` foram desenvolvidos manualmente pelos membros do grupo.
- ❌ **Diagramas UML e Testes Mockito:** Todos os diagramas de classes e de sequência, bem como as suítes de testes isolados com `@Mock`, `@Captor`, `InOrder` e verificações comportamentais foram escritos inteiramente pelos integrantes da equipe.

---

## 📜 Histórico de Prompts
Todo o registro transparente de prompts utilizados encontra-se documentado no arquivo de texto público `prompts_ia.txt` na raiz deste repositório.