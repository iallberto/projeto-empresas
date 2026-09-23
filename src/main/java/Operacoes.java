import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe Operacoes
 *
 * Guarda a lista de funcionários como estado interno (atributo) — cada
 * instância representa uma empresa isolada.
 *
 * Repare na divisão dos métodos em dois grupos:
 *   - "obter*"    -> calculam e RETORNAM um valor, sem imprimir nada.
 *   - "imprimir*" -> chamam o "obter*" correspondente e só cuidam de exibir.
 *
 * Essa separação existe por causa dos TESTES UNITÁRIOS: um método que só
 * imprime no console é difícil de testar (o teste teria que capturar a
 * saída padrão do programa). Um método que RETORNA um valor é trivial de
 * testar: você chama o método e compara o retorno com o valor esperado.
 * Isolar "calcular" de "exibir" é uma prática chamada Separation of
 * Concerns, e é o que torna a classe testável.
 */
public class Operacoes {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATO_NUMERO = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    static {
        FORMATO_NUMERO.setMinimumFractionDigits(2);
        FORMATO_NUMERO.setMaximumFractionDigits(2);
    }

    private final List<Funcionario> funcionarios;
    private final String nomeEmpresa;

    public Operacoes(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
        this.funcionarios = new ArrayList<>();
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    // ============================================================
    // 3.1 - Inserção
    // ============================================================

    public void inserirFuncionarios(String caminhoArquivo) {
        funcionarios.addAll(LeitorFuncionarios.carregar(caminhoArquivo));
    }

    /**
     * Sobrecarga usada pelos testes: insere uma lista já pronta, sem
     * precisar de um arquivo .xlsx real em disco.
     */
    public void inserirFuncionarios(List<Funcionario> listaPronta) {
        funcionarios.addAll(listaPronta);
    }

    // ============================================================
    // 3.2 - Remoção
    // ============================================================

    public void removerFuncionario(String nome) {
        funcionarios.removeIf(f -> f.getNome().equals(nome));
    }

    // ============================================================
    // 3.3 - Listagem completa
    // ============================================================

    public List<Funcionario> obterFuncionarios() {
        return Collections.unmodifiableList(funcionarios);
    }

    public void imprimirFuncionarios() {
        for (Funcionario f : obterFuncionarios()) {
            System.out.println(formatarFuncionario(f));
        }
    }

    // ============================================================
    // 3.4 - Aumento de salário
    // ============================================================

    public void aplicarAumento(BigDecimal percentual) {
        BigDecimal fator = BigDecimal.ONE.add(percentual);
        for (Funcionario f : funcionarios) {
            BigDecimal novoSalario = f.getSalario().multiply(fator).setScale(2, RoundingMode.HALF_UP);
            f.setSalario(novoSalario);
        }
    }

    // ============================================================
    // 3.5 / 3.6 - Agrupamento por função
    // ============================================================

    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao, LinkedHashMap::new, Collectors.toList()));
    }

    public void imprimirAgrupadosPorFuncao() {
        for (Map.Entry<String, List<Funcionario>> entrada : agruparPorFuncao().entrySet()) {
            System.out.println(entrada.getKey() + ":");
            for (Funcionario f : entrada.getValue()) {
                System.out.println(formatarFuncionario(f));
            }
        }
    }

    // ============================================================
    // 3.8 - Aniversariantes
    // ============================================================

    public List<Funcionario> obterAniversariantes(int mes1, int mes2) {
        return funcionarios.stream()
                .filter(f -> f.getDataNascimento().getMonthValue() == mes1
                        || f.getDataNascimento().getMonthValue() == mes2)
                .collect(Collectors.toList());
    }

    public void imprimirAniversariantes(int mes1, int mes2) {
        obterAniversariantes(mes1, mes2).forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    // ============================================================
    // 3.9 - Funcionário mais velho
    // ============================================================

    public Funcionario obterFuncionarioMaisVelho() {
        return funcionarios.stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .orElseThrow(() -> new IllegalStateException("Não há funcionários cadastrados."));
    }

    public void imprimirFuncionarioMaisVelho() {
        Funcionario maisVelho = obterFuncionarioMaisVelho();
        int idade = calcularIdade(maisVelho.getDataNascimento(), LocalDate.now());
        System.out.println("Nome: " + maisVelho.getNome() + " | Idade: " + idade);
    }

    // ============================================================
    // 3.10 - Ordem alfabética
    // ============================================================

    public List<Funcionario> obterOrdemAlfabetica() {
        return funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getNome))
                .collect(Collectors.toList());
    }

    public void imprimirOrdemAlfabetica() {
        obterOrdemAlfabetica().forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    // ============================================================
    // 3.11 - Total dos salários
    // ============================================================

    public BigDecimal obterTotalSalarios() {
        return funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void imprimirTotalSalarios() {
        System.out.println("R$ " + FORMATO_NUMERO.format(obterTotalSalarios()));
    }

    // ============================================================
    // 3.12 - Salários mínimos por funcionário
    // ============================================================

    public Map<String, BigDecimal> obterSalariosMinimosPorFuncionario() {
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        for (Funcionario f : funcionarios) {
            resultado.put(f.getNome(), f.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP));
        }
        return resultado;
    }

    public void imprimirSalariosMinimos() {
        obterSalariosMinimosPorFuncionario()
                .forEach((nome, quantidade) -> System.out.println(nome + ": " + FORMATO_NUMERO.format(quantidade)));
    }

    // ============================================================
    // Apoio
    // ============================================================

    private String formatarFuncionario(Funcionario f) {
        return "Nome: " + f.getNome() +
                " | Data Nascimento: " + f.getDataNascimento().format(FORMATO_DATA) +
                " | Salário: R$ " + FORMATO_NUMERO.format(f.getSalario()) +
                " | Função: " + f.getFuncao();
    }

    /**
     * Recebe a "data de referência" como parâmetro (em vez de usar
     * LocalDate.now() internamente) exatamente para ficar testável:
     * um teste pode passar uma data fixa e sempre obter o mesmo resultado,
     * independente do dia em que os testes rodarem.
     */
    public static int calcularIdade(LocalDate dataNascimento, LocalDate dataReferencia) {
        return Period.between(dataNascimento, dataReferencia).getYears();
    }
}
