import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * LeitorFuncionariosTest
 *
 * Aqui o teste usa um arquivo .xlsx de verdade — "EmpresaTeste.xlsx", em
 * src/test/resources — com apenas 2 funcionários fixos e conhecidos.
 * É um exemplo de TESTE DE INTEGRAÇÃO disfarçado de teste unitário: ele
 * não testa só uma unidade isolada de código, mas a integração entre o
 * LeitorFuncionarios e um arquivo real do disco. Por isso o arquivo de
 * teste é pequeno e fixo — o objetivo não é testar "os dados da empresa",
 * e sim "o leitor entende corretamente o formato de arquivo esperado".
 */
class LeitorFuncionariosTest {

    @Test
    void deveLerTodosOsFuncionariosDoArquivo() {
        List<Funcionario> funcionarios = LeitorFuncionarios.carregar(caminhoDoArquivoDeTeste());

        assertEquals(2, funcionarios.size());
    }

    @Test
    void devePreencherCorretamenteOsCamposDoPrimeiroFuncionario() {
        List<Funcionario> funcionarios = LeitorFuncionarios.carregar(caminhoDoArquivoDeTeste());

        Funcionario ana = funcionarios.get(0);
        assertEquals("Ana", ana.getNome());
        assertEquals(LocalDate.of(1990, 3, 15), ana.getDataNascimento());
        assertEquals(new BigDecimal("3000.00"), ana.getSalario());
        assertEquals("Analista", ana.getFuncao());
    }

    @Test
    void devePreencherCorretamenteOsCamposDoSegundoFuncionario() {
        List<Funcionario> funcionarios = LeitorFuncionarios.carregar(caminhoDoArquivoDeTeste());

        Funcionario bruno = funcionarios.get(1);
        assertEquals("Bruno", bruno.getNome());
        assertEquals(LocalDate.of(1985, 10, 20), bruno.getDataNascimento());
        assertEquals(new BigDecimal("5000.00"), bruno.getSalario());
        assertEquals("Gerente", bruno.getFuncao());
    }

    private String caminhoDoArquivoDeTeste() {
        URL recurso = getClass().getClassLoader().getResource("EmpresaTeste.xlsx");
        if (recurso == null) {
            throw new IllegalStateException("Arquivo de teste EmpresaTeste.xlsx não encontrado no classpath.");
        }
        return recurso.getPath();
    }
}
