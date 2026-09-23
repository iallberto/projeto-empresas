# Sistema de Gestão de Funcionários

Projeto desenvolvido em Java puro (JDK 21, sem dependências externas) a partir de um
teste prático de processo seletivo, evoluído além do escopo original para demonstrar
conceitos de Programação Orientada a Objetos, separação de responsabilidades e boas
práticas de design de software.

## Enunciado original

Dada uma lista de funcionários de uma indústria, o sistema deveria:

- Modelar `Pessoa` (nome, data de nascimento) e `Funcionario` (herda de `Pessoa`, com
  salário e função);
- Inserir, remover, listar, reajustar salários, agrupar por função, listar
  aniversariantes, encontrar o funcionário mais velho, ordenar alfabeticamente,
  somar salários e calcular quantos salários mínimos cada um recebe.

## Evolução do projeto

O histórico de commits deste repositório foi mantido propositalmente para mostrar a
evolução do raciocínio, do exercício básico até um mini-sistema:

1. **Versão 1** — solução direta do enunciado: três classes (`Pessoa`, `Funcionario`,
   `Principal`), tudo estático, dados fixos no código.
2. **Versão 2** — refatoração para POO de verdade: `Principal` vira `Operacoes`,
   uma classe **instanciável** que guarda o estado (lista de funcionários) por objeto,
   com um método público por item do enunciado. `Main` passa a ser só o orquestrador.
3. **Versão 3** — os dados deixam de ser fixos no código e passam a vir de um arquivo
   `.xlsx` de entrada, lido pela classe `LeitorFuncionarios` (sem depender de
   bibliotecas externas — um `.xlsx` é lido diretamente como o ZIP/XML que ele é,
   usando apenas `java.util.zip` e `javax.xml.parsers` do próprio JDK).
4. **Versão 4 (atual)** — o sistema passa a suportar **múltiplas empresas**
   simultaneamente (cada `Operacoes` é isolada, com seu próprio nome e lista) e ganha
   um **menu interativo** via console, com confirmação nas operações que alteram dados.

## Arquitetura

| Classe | Responsabilidade |
|---|---|
| `Pessoa` | Modelo base: nome e data de nascimento |
| `Funcionario` | Estende `Pessoa`: salário e função |
| `LeitorFuncionarios` | Lê um arquivo `.xlsx` e devolve `List<Funcionario>` |
| `Operacoes` | Regras de negócio; cada instância representa uma empresa isolada |
| `Main` | Interface de console: cadastra empresas e exibe o menu de operações |

## Como executar

```bash
javac *.java
java Main
```

O programa vai pedir o nome do arquivo da empresa (ex.: `EmpresaA`, sem a extensão —
ele procura `EmpresaA.xlsx` na mesma pasta), perguntar se há uma segunda empresa, e em
seguida exibir um menu com as operações 2 a 12 do enunciado (o item 1 corresponde à
carga dos dados, já feita nessa etapa inicial).

A planilha de entrada deve seguir o formato: coluna A = Nome, B = Data de Nascimento,
C = Salário, D = Função, com cabeçalho na primeira linha.

## Arquivos de exemplo

`EmpresaA.xlsx` e `EmpresaB.xlsx` são cópias da mesma base de dados de exemplo,
incluídas apenas para demonstrar o suporte a múltiplas empresas.
