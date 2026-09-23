import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * FuncionarioTest
 *
 * Um teste unitário segue, quase sempre, o padrão "AAA":
 *   Arrange  -> prepara os dados de entrada (o "cenário" do teste)
 *   Act      -> executa a ação que se quer testar
 *   Assert   -> confere se o resultado foi o esperado
 *
 * Cada método de teste testa UMA coisa só, e o nome do método já explica
 * o que está sendo verificado — isso é importante porque, quando um teste
 * falha, o nome dele já te diz o que quebrou, sem precisar ler o corpo.
 */
class FuncionarioTest {

    @Test
    void deveHerdarNomeEDataDeNascimentoDePessoa() {
        // Arrange
        LocalDate nascimento = LocalDate.of(1995, 6, 10);

        // Act
        Funcionario funcionario = new Funcionario("Ana", nascimento, new BigDecimal("3000.00"), "Analista");

        // Assert
        assertEquals("Ana", funcionario.getNome());
        assertEquals(nascimento, funcionario.getDataNascimento());
    }

    @Test
    void deveArmazenarSalarioEFuncao() {
        Funcionario funcionario = new Funcionario(
                "Bruno", LocalDate.of(1985, 1, 1), new BigDecimal("5000.00"), "Gerente");

        assertEquals(new BigDecimal("5000.00"), funcionario.getSalario());
        assertEquals("Gerente", funcionario.getFuncao());
    }

    @Test
    void devePermitirAlterarSalarioAposCriacao() {
        Funcionario funcionario = new Funcionario(
                "Carla", LocalDate.of(2000, 1, 1), new BigDecimal("1000.00"), "Estagiária");

        funcionario.setSalario(new BigDecimal("1500.00"));

        assertEquals(new BigDecimal("1500.00"), funcionario.getSalario());
    }
}
