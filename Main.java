import java.math.BigDecimal;

/**
 * Classe Main
 * Ponto de entrada do programa. Instancia um objeto de Operacoes e chama
 * cada método na ordem do enunciado.
 */
public class Main {

    public static void main(String[] args) {

        Operacoes operacoes = new Operacoes();

        operacoes.inserirFuncionarios();
        operacoes.removerFuncionario("João");

        System.out.println("===== 3.3 - Lista de funcionários =====");
        operacoes.imprimirFuncionarios();

        operacoes.aplicarAumento(new BigDecimal("0.10"));
        System.out.println("\n===== 3.4 - Funcionários após aumento de 10% =====");
        operacoes.imprimirFuncionarios();

        System.out.println("\n===== 3.6 - Funcionários agrupados por função =====");
        operacoes.imprimirAgrupadosPorFuncao();

        System.out.println("\n===== 3.8 - Aniversariantes de outubro e dezembro =====");
        operacoes.imprimirAniversariantes(10, 12);

        System.out.println("\n===== 3.9 - Funcionário com maior idade =====");
        operacoes.imprimirFuncionarioMaisVelho();

        System.out.println("\n===== 3.10 - Funcionários em ordem alfabética =====");
        operacoes.imprimirOrdemAlfabetica();

        System.out.println("\n===== 3.11 - Total dos salários =====");
        operacoes.imprimirTotalSalarios();

        System.out.println("\n===== 3.12 - Salários mínimos por funcionário =====");
        operacoes.imprimirSalariosMinimos();
    }
}
