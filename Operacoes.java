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
 * Diferente da antiga "Principal", esta classe é INSTANCIÁVEL: guarda a
 * lista de funcionários como estado interno (atributo) e expõe um método
 * público para cada item do enunciado (3.1 a 3.12).
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

    public Operacoes() {
        this.funcionarios = new ArrayList<>();
    }

    public void inserirFuncionarios() {
        funcionarios.add(new Funcionario("Maria", LocalDate.of(2000, 10, 18), new BigDecimal("2009.44"), "Operador"));
        funcionarios.add(new Funcionario("João", LocalDate.of(1990, 5, 15), new BigDecimal("2284.38"), "Operador"));
        funcionarios.add(new Funcionario("Caio", LocalDate.of(1961, 5, 2), new BigDecimal("9836.14"), "Coordenador"));
        funcionarios.add(new Funcionario("Miguel", LocalDate.of(1988, 10, 14), new BigDecimal("19119.88"), "Diretor"));
        funcionarios.add(new Funcionario("Alice", LocalDate.of(1995, 1, 5), new BigDecimal("2234.68"), "Recepcionista"));
        funcionarios.add(new Funcionario("Heitor", LocalDate.of(1999, 11, 19), new BigDecimal("1582.72"), "Operador"));
        funcionarios.add(new Funcionario("Arthur", LocalDate.of(1993, 3, 31), new BigDecimal("4071.84"), "Contador"));
        funcionarios.add(new Funcionario("Laura", LocalDate.of(1994, 7, 8), new BigDecimal("3017.45"), "Gerente"));
        funcionarios.add(new Funcionario("Heloísa", LocalDate.of(2003, 5, 24), new BigDecimal("1606.85"), "Eletricista"));
        funcionarios.add(new Funcionario("Helena", LocalDate.of(1996, 9, 2), new BigDecimal("2799.93"), "Gerente"));
    }

    public void removerFuncionario(String nome) {
        funcionarios.removeIf(f -> f.getNome().equals(nome));
    }

    public void imprimirFuncionarios() {
        for (Funcionario f : funcionarios) {
            System.out.println(formatarFuncionario(f));
        }
    }

    public void aplicarAumento(BigDecimal percentual) {
        BigDecimal fator = BigDecimal.ONE.add(percentual);
        for (Funcionario f : funcionarios) {
            BigDecimal novoSalario = f.getSalario().multiply(fator).setScale(2, RoundingMode.HALF_UP);
            f.setSalario(novoSalario);
        }
    }

    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao, LinkedHashMap::new, Collectors.toList()));
    }

    public void imprimirAgrupadosPorFuncao() {
        Map<String, List<Funcionario>> agrupado = agruparPorFuncao();
        for (Map.Entry<String, List<Funcionario>> entrada : agrupado.entrySet()) {
            System.out.println(entrada.getKey() + ":");
            for (Funcionario f : entrada.getValue()) {
                System.out.println(formatarFuncionario(f));
            }
        }
    }

    public void imprimirAniversariantes(int mes1, int mes2) {
        funcionarios.stream()
                .filter(f -> f.getDataNascimento().getMonthValue() == mes1
                        || f.getDataNascimento().getMonthValue() == mes2)
                .forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    public void imprimirFuncionarioMaisVelho() {
        Funcionario maisVelho = funcionarios.stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .orElseThrow();
        int idade = calcularIdade(maisVelho.getDataNascimento());
        System.out.println("Nome: " + maisVelho.getNome() + " | Idade: " + idade);
    }

    public void imprimirOrdemAlfabetica() {
        funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getNome))
                .forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    public void imprimirTotalSalarios() {
        BigDecimal total = funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("R$ " + FORMATO_NUMERO.format(total));
    }

    public void imprimirSalariosMinimos() {
        for (Funcionario f : funcionarios) {
            BigDecimal quantidade = f.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
            System.out.println(f.getNome() + ": " + FORMATO_NUMERO.format(quantidade));
        }
    }

    public List<Funcionario> getFuncionarios() {
        return Collections.unmodifiableList(funcionarios);
    }

    private String formatarFuncionario(Funcionario f) {
        return "Nome: " + f.getNome() +
                " | Data Nascimento: " + f.getDataNascimento().format(FORMATO_DATA) +
                " | Salário: R$ " + FORMATO_NUMERO.format(f.getSalario()) +
                " | Função: " + f.getFuncao();
    }

    private int calcularIdade(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}
