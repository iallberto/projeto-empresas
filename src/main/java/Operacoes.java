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
 * público para cada item do enunciado (3.1 a 3.12). Assim, quem for usar
 * a classe (ex.: a Main) instancia um objeto e chama os métodos na ordem
 * e combinação que quiser, sem precisar ficar passando a lista de
 * funcionários como parâmetro toda hora.
 */
public class Operacoes {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATO_NUMERO = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    static {
        FORMATO_NUMERO.setMinimumFractionDigits(2);
        FORMATO_NUMERO.setMaximumFractionDigits(2);
    }

    // Estado interno do objeto: cada instância de Operacoes tem sua própria lista.
    private final List<Funcionario> funcionarios;
    private final String nomeEmpresa;

    public Operacoes(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
        this.funcionarios = new ArrayList<>();
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    /**
     * 3.1 - Inserir todos os funcionários, lendo-os de um arquivo .xlsx.
     *
     * O método não sabe nada sobre "Excel" ou "POI" — isso é delegado à
     * classe LeitorFuncionarios. Aqui a gente só recebe a lista já pronta
     * e adiciona ao estado interno. Por receber o caminho como parâmetro,
     * o mesmo objeto Operacoes pode carregar QUALQUER arquivo que siga o
     * formato esperado — outra empresa, outro arquivo, mesmo código.
     */
    public void inserirFuncionarios(String caminhoArquivo) {
        funcionarios.addAll(LeitorFuncionarios.carregar(caminhoArquivo));
    }

    /**
     * 3.2 - Remover um funcionário da lista pelo nome.
     */
    public void removerFuncionario(String nome) {
        funcionarios.removeIf(f -> f.getNome().equals(nome));
    }

    /**
     * 3.3 - Imprimir todos os funcionários com todas as informações,
     * data em dd/mm/aaaa e valores com separador de milhar/decimal em pt-BR.
     */
    public void imprimirFuncionarios() {
        for (Funcionario f : funcionarios) {
            System.out.println(formatarFuncionario(f));
        }
    }

    /**
     * 3.4 - Aplicar um percentual de aumento sobre o salário de todos os
     * funcionários, atualizando a lista. Recebe o percentual como parâmetro
     * (ex.: new BigDecimal("0.10") para 10%) em vez de fixo no código,
     * assim o método fica reutilizável para qualquer percentual.
     */
    public void aplicarAumento(BigDecimal percentual) {
        BigDecimal fator = BigDecimal.ONE.add(percentual);
        for (Funcionario f : funcionarios) {
            BigDecimal novoSalario = f.getSalario()
                    .multiply(fator)
                    .setScale(2, RoundingMode.HALF_UP);
            f.setSalario(novoSalario);
        }
    }

    /**
     * 3.5 - Agrupar os funcionários por função em um Map (chave = função,
     * valor = lista de funcionários). Devolve o Map para quem quiser usar
     * os dados sem necessariamente imprimir (ex.: 3.6 usa este método).
     */
    public Map<String, List<Funcionario>> agruparPorFuncao() {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getFuncao,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    /**
     * 3.6 - Imprimir os funcionários agrupados por função.
     */
    public void imprimirAgrupadosPorFuncao() {
        Map<String, List<Funcionario>> agrupado = agruparPorFuncao();
        for (Map.Entry<String, List<Funcionario>> entrada : agrupado.entrySet()) {
            System.out.println(entrada.getKey() + ":");
            for (Funcionario f : entrada.getValue()) {
                System.out.println(formatarFuncionario(f));
            }
        }
    }

    /**
     * 3.8 - Imprimir os funcionários que fazem aniversário nos meses informados.
     * Recebe os meses como parâmetro em vez de fixar 10 e 12 no código.
     */
    public void imprimirAniversariantes(int mes1, int mes2) {
        funcionarios.stream()
                .filter(f -> f.getDataNascimento().getMonthValue() == mes1
                        || f.getDataNascimento().getMonthValue() == mes2)
                .forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    /**
     * 3.9 - Imprimir o funcionário com a maior idade (nome e idade).
     * Maior idade = data de nascimento mais ANTIGA.
     */
    public void imprimirFuncionarioMaisVelho() {
        Funcionario maisVelho = funcionarios.stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .orElseThrow();
        int idade = calcularIdade(maisVelho.getDataNascimento());
        System.out.println("Nome: " + maisVelho.getNome() + " | Idade: " + idade);
    }

    /**
     * 3.10 - Imprimir a lista de funcionários em ordem alfabética (por nome).
     */
    public void imprimirOrdemAlfabetica() {
        funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getNome))
                .forEach(f -> System.out.println(formatarFuncionario(f)));
    }

    /**
     * 3.11 - Imprimir o total dos salários dos funcionários.
     */
    public void imprimirTotalSalarios() {
        BigDecimal total = funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("R$ " + FORMATO_NUMERO.format(total));
    }

    /**
     * 3.12 - Imprimir quantos salários mínimos ganha cada funcionário.
     */
    public void imprimirSalariosMinimos() {
        for (Funcionario f : funcionarios) {
            BigDecimal quantidade = f.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
            System.out.println(f.getNome() + ": " + FORMATO_NUMERO.format(quantidade));
        }
    }

    /**
     * Getter de leitura: devolve uma view não-modificável da lista interna.
     * Assim quem usa o objeto pode CONSULTAR os funcionários sem conseguir
     * alterar a lista por fora (isso quebraria o encapsulamento).
     */
    public List<Funcionario> getFuncionarios() {
        return Collections.unmodifiableList(funcionarios);
    }

    // ----- métodos privados de apoio (detalhe de implementação, não fazem parte do "contrato" da classe) -----

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
