import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main
 *
 * Ponto de entrada do sistema. Diferente das versões anteriores (fluxo
 * fixo, sempre a mesma sequência de operações), esta versão é interativa:
 *
 *   1) Pergunta o nome do arquivo da(s) empresa(s) e carrega os dados
 *      (item 3.1 do enunciado — por isso não aparece como opção de menu).
 *   2) Exibe um menu com as operações 2 a 12 do enunciado (o item 3.7
 *      não existe no enunciado original, por isso também não aparece).
 *   3) Repete o menu até o usuário escolher sair.
 *
 * A classe Main não conhece REGRA DE NEGÓCIO nenhuma — ela só conversa
 * com o usuário (Scanner/System.out) e delega tudo para os métodos
 * públicos de Operacoes. Essa é a mesma separação de responsabilidades
 * que vimos com LeitorFuncionarios: cada classe cuida de uma coisa.
 */
public class Main {

    private static final Scanner ENTRADA = new Scanner(System.in);

    // Pasta onde ficam as planilhas de cada empresa, para não misturar
    // dados com código-fonte na raiz do projeto.
    private static final String PASTA_DADOS = "dados/";

    public static void main(String[] args) {

        List<Operacoes> empresas = configurarEmpresas();
        System.out.println("\nTodas as empresas foram carregadas com sucesso!");

        executarMenuPrincipal(empresas);

        System.out.println("\nEncerrando o programa. Até logo!");
    }

    // ===================== Etapa 1: carregar empresa(s) (item 3.1) =====================

    private static List<Operacoes> configurarEmpresas() {
        List<Operacoes> empresas = new ArrayList<>();

        System.out.println("========================================");
        System.out.println(" CADASTRO DE EMPRESAS");
        System.out.println("========================================");

        String nomePrimeiraEmpresa = perguntarTexto("Nome do arquivo da 1ª empresa (ex.: EmpresaA): ");
        empresas.add(carregarEmpresa(nomePrimeiraEmpresa));

        if (perguntarSimNao("Existe uma segunda empresa? (S/N): ")) {
            String nomeSegundaEmpresa = perguntarTexto("Nome do arquivo da 2ª empresa (ex.: EmpresaB): ");
            empresas.add(carregarEmpresa(nomeSegundaEmpresa));
        }

        return empresas;
    }

    private static Operacoes carregarEmpresa(String nomeEmpresa) {
        String caminhoArquivo = PASTA_DADOS + nomeEmpresa + ".xlsx";
        Operacoes operacoes = new Operacoes(nomeEmpresa);
        operacoes.inserirFuncionarios(caminhoArquivo);
        System.out.println("-> " + nomeEmpresa + " carregada com sucesso a partir de \"" + caminhoArquivo + "\".");
        return operacoes;
    }

    // ===================== Etapa 2: menu principal (itens 2 a 12) =====================

    private static void executarMenuPrincipal(List<Operacoes> empresas) {
        boolean continuarPrograma = true;

        while (continuarPrograma) {
            Operacoes empresaSelecionada = empresas.size() > 1 ? escolherEmpresa(empresas) : empresas.get(0);

            executarMenuDaEmpresa(empresaSelecionada);

            continuarPrograma = empresas.size() > 1
                    && perguntarSimNao("Deseja operar em outra empresa? (S/N): ");
        }
    }

    private static void executarMenuDaEmpresa(Operacoes operacoes) {
        int opcao;
        do {
            exibirMenu(operacoes.getNomeEmpresa());
            opcao = perguntarInteiro("Escolha uma opção: ");
            System.out.println();

            if (opcao != 0) {
                executarOpcao(opcao, operacoes);
                System.out.println();
            }

        } while (opcao != 0);
    }

    private static Operacoes escolherEmpresa(List<Operacoes> empresas) {
        System.out.println("\nSobre qual empresa deseja operar?");
        for (int i = 0; i < empresas.size(); i++) {
            System.out.println((i + 1) + " - " + empresas.get(i).getNomeEmpresa());
        }

        int indice;
        do {
            indice = perguntarInteiro("Escolha uma opção: ") - 1;
            if (indice < 0 || indice >= empresas.size()) {
                System.out.println("Opção inválida.");
            }
        } while (indice < 0 || indice >= empresas.size());

        return empresas.get(indice);
    }

    private static void exibirMenu(String nomeEmpresa) {
        System.out.println("========================================");
        System.out.println(" MENU - " + nomeEmpresa);
        System.out.println("========================================");
        System.out.println(" 2  - Remover funcionário");
        System.out.println(" 3  - Imprimir todos os funcionários");
        System.out.println(" 4  - Aplicar aumento de salário");
        System.out.println(" 5  - Agrupar funcionários por função");
        System.out.println(" 6  - Imprimir funcionários agrupados por função");
        System.out.println(" 8  - Imprimir aniversariantes por mês");
        System.out.println(" 9  - Imprimir funcionário com maior idade");
        System.out.println(" 10 - Imprimir funcionários em ordem alfabética");
        System.out.println(" 11 - Imprimir total dos salários");
        System.out.println(" 12 - Imprimir salários mínimos por funcionário");
        System.out.println(" 0  - Sair");
        System.out.println("========================================");
    }

    private static void executarOpcao(int opcao, Operacoes operacoes) {
        switch (opcao) {

            case 2 -> { // 3.2 - Remover funcionário
                String nome = perguntarTexto("Nome do funcionário a remover (com acentos, se houver): ");
                if (perguntarSimNao("Confirma a remoção de \"" + nome + "\"? (S/N): ")) {
                    operacoes.removerFuncionario(nome);
                    System.out.println("Funcionário \"" + nome + "\" removido com sucesso.");
                } else {
                    System.out.println("Operação cancelada.");
                }
            }

            case 3 -> { // 3.3 - Imprimir todos os funcionários
                System.out.println("--- Funcionários de " + operacoes.getNomeEmpresa() + " ---");
                operacoes.imprimirFuncionarios();
            }

            case 4 -> { // 3.4 - Aumento de salário
                double percentualDigitado = perguntarDecimal("Percentual de aumento (ex.: 10 para 10%): ");
                if (perguntarSimNao("Confirma aumento de " + percentualDigitado + "% para TODOS os funcionários? (S/N): ")) {
                    operacoes.aplicarAumento(BigDecimal.valueOf(percentualDigitado / 100.0));
                    System.out.println("Aumento aplicado com sucesso.");
                } else {
                    System.out.println("Operação cancelada.");
                }
            }

            case 5 -> { // 3.5 - Agrupar por função (apenas monta a estrutura; a impressão é o item 6)
                operacoes.agruparPorFuncao();
                System.out.println("Funcionários agrupados por função (use a opção 6 para imprimir o resultado).");
            }

            case 6 -> { // 3.6 - Imprimir agrupados por função
                System.out.println("--- Funcionários de " + operacoes.getNomeEmpresa() + " agrupados por função ---");
                operacoes.imprimirAgrupadosPorFuncao();
            }

            case 8 -> { // 3.8 - Aniversariantes de dois meses
                int mes1 = perguntarInteiro("Primeiro mês (1-12): ");
                int mes2 = perguntarInteiro("Segundo mês (1-12): ");
                System.out.println("--- Aniversariantes dos meses " + mes1 + " e " + mes2 + " ---");
                operacoes.imprimirAniversariantes(mes1, mes2);
            }

            case 9 -> { // 3.9 - Funcionário mais velho
                System.out.println("--- Funcionário com maior idade ---");
                operacoes.imprimirFuncionarioMaisVelho();
            }

            case 10 -> { // 3.10 - Ordem alfabética
                System.out.println("--- Funcionários em ordem alfabética ---");
                operacoes.imprimirOrdemAlfabetica();
            }

            case 11 -> { // 3.11 - Total dos salários
                System.out.println("--- Total dos salários ---");
                operacoes.imprimirTotalSalarios();
            }

            case 12 -> { // 3.12 - Salários mínimos por funcionário
                System.out.println("--- Salários mínimos por funcionário ---");
                operacoes.imprimirSalariosMinimos();
            }

            default -> System.out.println("Opção inválida. Escolha um número entre 0 e 12 (não existem os itens 1 e 7).");
        }
    }

    // ===================== Utilitários de leitura do console =====================

    private static String perguntarTexto(String mensagem) {
        System.out.print(mensagem);
        return ENTRADA.nextLine().trim();
    }

    private static boolean perguntarSimNao(String mensagem) {
        System.out.print(mensagem);
        String resposta = ENTRADA.nextLine().trim();
        return resposta.equalsIgnoreCase("S") || resposta.equalsIgnoreCase("Sim");
    }

    private static int perguntarInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(ENTRADA.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static double perguntarDecimal(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Double.parseDouble(ENTRADA.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
