
---

##  Implementação (`TqsStack`)
A classe `TqsStack<T>` foi implementada com base na interface de uma pilha (LIFO):

- `push(T item)` – insere elemento no topo.  
- `pop()` – remove e devolve o elemento do topo (lança exceção se vazio).  
- `peek()` – devolve o elemento do topo sem remover (lança exceção se vazio).  
- `size()` – devolve o número de elementos.  
- `isEmpty()` – verifica se a pilha está vazia.  
- `popTopN(int n)` – remove e devolve o **n-ésimo elemento** do topo, descartando os anteriores.

---

## Testes (`TqsStackTest`)
Os testes foram escritos com **JUnit 5** e **AssertJ**, garantindo clareza nas asserções.

### Casos de Teste Implementados
- **Construção inicial**
  - Stack vazia após construção (`isEmpty == true`, `size == 0`).  
- **Push**
  - Stack deixa de estar vazia após `push`.  
- **Pop**
  - Valor retornado é o mesmo que foi inserido.  
  - Ordem LIFO garantida com múltiplos elementos.  
  - Exceção lançada ao fazer `pop` numa stack vazia.  
- **Peek**
  - Retorna o valor do topo sem alterar o tamanho.  
  - Exceção lançada ao fazer `peek` numa stack vazia.  
- **Size & isEmpty**
  - Confirmam comportamento esperado em diferentes fases da pilha.  
- **PopTopN**
  - Retorna o elemento correto após descartar os anteriores.  
  - Lança exceção se `n` ≤ 0 ou `n > size`.

---

##  Mapa de Rastreabilidade
Todos os métodos públicos de `TqsStack` foram cobertos por testes unitários.  

 Método              Testes Correspondentes 

 `push`             `afterPushStackIsNotEmpty`, `pushThenPopReturnsSameValue`, `pushThenPeekReturnsSameValueButSizeUnchanged` 
 `pop`              `pushThenPopReturnsSameValue`, `multiplePushesAndPopsWorkAsExpected`, `poppingFromEmptyStackThrows` 
 `peek`             `pushThenPeekReturnsSameValueButSizeUnchanged`, `peekingIntoEmptyStackThrows` 
 `size`             `stackShouldBeEmptyOnConstruction`, `afterPushStackIsNotEmpty`, `multiplePushesAndPopsWorkAsExpected` 
 `isEmpty`          `stackShouldBeEmptyOnConstruction`, `afterPushStackIsNotEmpty`, `pushThenPopReturnsSameValue` 
 `popTopN`          `popTopNReturnsNthElement`, `popTopNWithInvalidNThrows` 

 **Cobertura manual considerada 100% (methods + statements).**

---

## Passos do Exercício

- **d)** Criar testes antes da implementação (Red → Green → Refactor).  
- **e)** Implementar métodos na `TqsStack` até todos os testes passarem.  
- **f)** Verificar cobertura manualmente e experimentar desativar testes (`@Disabled`).  
- **g)** Confirmar impacto na cobertura ao renomear classe de teste (`TqsStackTest` → `TqsStack_Test`).  
- **h)** Adicionar método `popTopN(int n)` e novos testes para garantir cobertura total.  
- **i)** Confirmar que todos os métodos estão cobertos por testes (100% manual).  

---

##  Como correr os testes

1. Compilar e executar os testes:
   ```bash
   mvn test
