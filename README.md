# Sistema de Gestão de Funcionários

Projeto desenvolvido em Java (JDK 21) a partir de um teste prático de processo
seletivo, evoluído além do escopo original para demonstrar conceitos de
Programação Orientada a Objetos, separação de responsabilidades, leitura de
arquivos e testes automatizados.

O código de produção **não usa nenhuma biblioteca externa** — inclusive a
leitura de `.xlsx` é feita "na mão" (um `.xlsx` é um ZIP com XMLs dentro),
usando só classes do próprio JDK.

## Enunciado original

Dada uma lista de funcionários de uma indústria, o sistema deveria:

- Modelar `Pessoa` (nome, data de nascimento) e `Funcionario` (herda de `Pessoa`, com
  salário e função);
- Inserir, remover, listar, reajustar salários, agrupar por função, listar
  aniversariantes, encontrar o funcionário mais velho, ordenar alfabeticamente,
  somar salários e calcular quantos salários mínimos cada um recebe.

## Evolução do projeto

O histórico de commits foi mantido propositalmente para mostrar a evolução do
raciocínio, do exercício básico até um mini-sistema:

1. **Versão 1** — solução direta do enunciado: três classes (`Pessoa`, `Funcionario`,
   `Principal`), tudo estático, dados fixos no código.
2. **Versão 2** — refatoração para POO de verdade: `Principal` vira `Operacoes`,
   uma classe **instanciável** que guarda o estado (lista de funcionários) por objeto.
   `Main` passa a ser só o orquestrador.
3. **Versão 3** — os dados deixam de ser fixos e passam a vir de um arquivo `.xlsx`
   de entrada, lido pela classe `LeitorFuncionarios`.
4. **Versão 4** — suporte a **múltiplas empresas** simultâneas (cada `Operacoes` é
   isolada) e um **menu interativo** via console, com confirmação nas operações
   que alteram dados.
5. **Versão 5 (atual)** — projeto reorganizado no formato Maven padrão
   (`src/main/java`, `src/test/java`) e cobertura de **testes unitários com JUnit 5**.
   Isso exigiu separar, dentro de `Operacoes`, os métodos que **calculam** (retornam
   valor, testáveis) dos que **imprimem** (chamam o cálculo e exibem no console) —
   um método que só imprime é difícil de testar de forma automática.

## Arquitetura

| Classe | Responsabilidade |
|---|---|
| `Pessoa` | Modelo base: nome e data de nascimento |
| `Funcionario` | Estende `Pessoa`: salário e função |
| `LeitorFuncionarios` | Lê um arquivo `.xlsx` e devolve `List<Funcionario>` |
| `Operacoes` | Regras de negócio; cada instância representa uma empresa isolada |
| `Main` | Interface de console: cadastra empresas e exibe o menu de operações |

## Como executar

Com Maven instalado:

```bash
mvn compile exec:java -Dexec.mainClass=Main
```

Ou sem Maven, compilando manualmente:

```bash
cd src/main/java
javac *.java
java Main
```

O programa vai pedir o nome do arquivo da empresa (ex.: `EmpresaA`, sem a extensão —
ele procura `EmpresaA.xlsx` na mesma pasta), perguntar se há uma segunda empresa, e em
seguida exibir um menu com as operações 2 a 12 do enunciado (o item 1 corresponde à
carga dos dados, já feita na etapa inicial).

A planilha de entrada deve seguir o formato: coluna A = Nome, B = Data de Nascimento,
C = Salário, D = Função, com cabeçalho na primeira linha.

## Testes automatizados

```bash
mvn test
```

Cobertura atual: 17 testes (JUnit 5) sobre `Funcionario`, `Operacoes` e
`LeitorFuncionarios` — incluindo remoção, aumento de salário, agrupamento por
função, aniversariantes, funcionário mais velho, ordenação, soma de salários,
cálculo de salários mínimos e leitura de um arquivo `.xlsx` de teste
(`src/test/resources/EmpresaTeste.xlsx`).

## Arquivos de exemplo

`EmpresaA.xlsx` e `EmpresaB.xlsx` (na raiz do projeto) são cópias da mesma base de
dados de exemplo, incluídas para demonstrar o suporte a múltiplas empresas.
