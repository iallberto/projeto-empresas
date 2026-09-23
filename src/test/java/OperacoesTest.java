import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OperacoesTest
 *
 * Testa a regra de negócio da classe Operacoes — os itens 3.2 a 3.12 do
 * enunciado original — usando os métodos "obter*" (que retornam valor),
 * já que são esses que dá para verificar de forma automática. Os métodos
 * "imprimir*" só chamam os "obter*" e mandam para o console, então
 * testando o "obter*" já cobrimos a parte que realmente importa (o
 * cálculo); a impressão em si é só formatação de texto.
 *
 * @BeforeEach roda ANTES de cada teste, montando um cenário novo e
 * conhecido (3 funcionários fixos). Isso garante ISOLAMENTO entre os
 * testes: um teste nunca deve depender do que outro teste fez antes —
 * cada @Test começa do zero.
 */
class OperacoesTest {

    private Operacoes operacoes;

    @BeforeEach
    void configurarCenario() {
        operacoes = new Operacoes("EmpresaTeste");
        // Inserido fora de ordem alfabética de propósito, para o teste de
        // ordenação realmente provar que o método reordena a lista.
        operacoes.inserirFuncionarios(List.of(
                new Funcionario("Carla", LocalDate.of(1995, 12, 25), new BigDecimal("2000.00"), "Analista"),
                new Funcionario("Ana", LocalDate.of(1990, 3, 15), new BigDecimal("3000.00"), "Analista"),
                new Funcionario("Bruno", LocalDate.of(1985, 10, 20), new BigDecimal("5000.00"), "Gerente")
        ));
    }

    @Test
    void deveRemoverFuncionarioPeloNome() {
        operacoes.removerFuncionario("Ana");

        List<String> nomesRestantes = operacoes.obterFuncionarios().stream()
                .map(Funcionario::getNome)
                .toList();

        assertEquals(List.of("Carla", "Bruno"), nomesRestantes);
    }

    @Test
    void removerNomeInexistenteNaoDeveAlterarALista() {
        operacoes.removerFuncionario("NomeQueNaoExiste");

        assertEquals(3, operacoes.obterFuncionarios().size());
    }

    @Test
    void deveAplicarAumentoDeDezPorCentoATodosOsFuncionarios() {
        operacoes.aplicarAumento(new BigDecimal("0.10")); // 10%

        Map<String, BigDecimal> salariosPorNome = operacoes.obterFuncionarios().stream()
                .collect(java.util.stream.Collectors.toMap(Funcionario::getNome, Funcionario::getSalario));

        assertEquals(new BigDecimal("2200.00"), salariosPorNome.get("Carla")); // 2000 + 10%
        assertEquals(new BigDecimal("3300.00"), salariosPorNome.get("Ana"));   // 3000 + 10%
        assertEquals(new BigDecimal("5500.00"), salariosPorNome.get("Bruno")); // 5000 + 10%
    }

    @Test
    void deveAgruparFuncionariosPorFuncao() {
        Map<String, List<Funcionario>> agrupado = operacoes.agruparPorFuncao();

        assertEquals(2, agrupado.get("Analista").size()); // Carla e Ana
        assertEquals(1, agrupado.get("Gerente").size());  // Bruno
    }

    @Test
    void deveEncontrarAniversariantesDosMesesInformados() {
        // Bruno nasceu em outubro (10), Carla em dezembro (12), Ana em março (3)
        List<Funcionario> aniversariantes = operacoes.obterAniversariantes(10, 12);

        List<String> nomes = aniversariantes.stream().map(Funcionario::getNome).toList();
        assertEquals(2, aniversariantes.size());
        assertTrue(nomes.contains("Bruno"));
        assertTrue(nomes.contains("Carla"));
    }

    @Test
    void deveEncontrarOFuncionarioComMaiorIdade() {
        // Bruno (1985) é o mais antigo dos três -> maior idade
        Funcionario maisVelho = operacoes.obterFuncionarioMaisVelho();

        assertEquals("Bruno", maisVelho.getNome());
    }

    @Test
    void obterFuncionarioMaisVelhoDeveLancarExcecaoQuandoListaVazia() {
        Operacoes operacoesVazia = new Operacoes("EmpresaSemFuncionarios");

        assertThrows(IllegalStateException.class, operacoesVazia::obterFuncionarioMaisVelho);
    }

    @Test
    void deveOrdenarFuncionariosPorOrdemAlfabetica() {
        List<String> nomesOrdenados = operacoes.obterOrdemAlfabetica().stream()
                .map(Funcionario::getNome)
                .toList();

        assertEquals(List.of("Ana", "Bruno", "Carla"), nomesOrdenados);
    }

    @Test
    void deveSomarOTotalDosSalarios() {
        BigDecimal total = operacoes.obterTotalSalarios();

        assertEquals(new BigDecimal("10000.00"), total); // 2000 + 3000 + 5000
    }

    @Test
    void deveCalcularQuantosSalariosMinimosCadaFuncionarioGanha() {
        Map<String, BigDecimal> salariosMinimos = operacoes.obterSalariosMinimosPorFuncionario();

        assertEquals(new BigDecimal("1.65"), salariosMinimos.get("Carla")); // 2000 / 1212
        assertEquals(new BigDecimal("2.48"), salariosMinimos.get("Ana"));   // 3000 / 1212
        assertEquals(new BigDecimal("4.13"), salariosMinimos.get("Bruno")); // 5000 / 1212
    }

    @Test
    void calcularIdadeDeveSerDeterministicoComDataDeReferenciaFixa() {
        // Usamos uma data de referência FIXA (em vez de LocalDate.now()) para
        // o teste dar sempre o mesmo resultado, não importa em que dia rodar.
        LocalDate nascimento = LocalDate.of(2000, 6, 15);
        LocalDate dataReferencia = LocalDate.of(2026, 6, 14); // um dia antes de completar 26 anos

        int idade = Operacoes.calcularIdade(nascimento, dataReferencia);

        assertEquals(25, idade);
    }
}
