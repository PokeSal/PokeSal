# Checklist de Verificação Estática de Código (Revisão Manual) — Fase 02

**Instituição:** Universidade Católica do Salvador – UCSal  
**Curso:** Análise e Desenvolvimento de Sistemas  
**Disciplina:** Testes e Qualidade de Software  
**Professor:** Pedro Arthur de Melo Nascimento  

**Equipe:**
- Cauã Lopes
- Arivaldo Teixeira
- Ryan Abade
- Pedro Luiz

---

### 1. Todas as variáveis estão inicializadas antes do uso?
* **Resposta:** **SIM**
* **Justificativa:** Todos os atributos em `PokeSal`, `Mochila` e `GerenciadorDeBatalha` são inicializados via construtores. Variáveis locais na `Main.java` e `CalculadoraDano.java` possuem valores atribuídos antes de qualquer leitura ou operação.

---

### 2. Há variáveis declaradas e nunca usadas?
* **Resposta:** **NÃO**
* **Justificativa:** Varredura estática confirma que todos os campos e variáveis locais declarados são usados.

---

### 3. Existe código inacessível (*Dead Code*)?
* **Resposta:** **SIM (Identificado e removido durante a revisão)**
* **Justificativa:** O método `executarRodada()` na classe `GerenciadorDeBatalha.java` era um controle de turno obsoleto que não recebia chamadas. Foi devidamente removido.

---

### 4. Há código duplicado?
* **Resposta:** **NÃO**
* **Justificativa:** A lógica de dano elemental e bônus de terreno está isolada na `CalculadoraDano.java`, e as constantes do jogo estão centralizadas na `ConstantesJogo.java`.

---

### 5. Existem erros de sintaxe?
* **Resposta:** **NÃO**
* **Justificativa:** O projeto compila com sucesso e atende às regras do *Google Java Style Guide* via Checkstyle.

---

### 6. Há erros de lógica que quebram regras do negócio?
* **Resposta:** **SIM (Identificado e ajustado durante a revisão)**
* **Justificativa:** A classe `Mochila.java` retornava apenas `null` ao tentar consumir mais de 2 itens, sem disparar exceção. O comportamento foi corrigido para lançar `IllegalStateException`, atendendo ao requisito do teste JUnit `testUsoLimiteDeItensExcedido()`.

---

### 7. Há erros de tipagem?
* **Resposta:** **NÃO**
* **Justificativa:** Uso consistente de `int` para estatísticas e `double` para multiplicadores, com conversão explícita `(double)` em `CalculadoraDano.java` para evitar truncamento em divisões inteiras.

---

### 8. O fluxo de controle é válido (sem loops infinitos, condições impossíveis)?
* **Resposta:** **SIM**
* **Justificativa:** O loop `while` em `Main.java` avalia `isVivo()` para ambos os combatentes e captura `InputMismatchException` no scanner do teclado.

---

### 9. Outros tipos de erros identificados?
* **Resposta:** **CORRIGIDO**
* **Justificativa:** Constantes de mensagens e limites numéricos foram extraídos para `ConstantesJogo.java`.