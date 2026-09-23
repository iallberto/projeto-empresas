import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * LeitorFuncionarios
 *
 * Responsabilidade única: ler um arquivo .xlsx de um caminho qualquer e
 * transformar cada linha em um objeto Funcionario. Não sabe nada sobre
 * "aumento de salário", "agrupar por função" etc. — isso é problema da
 * classe Operacoes. Essa separação (cada classe cuidando de uma coisa só)
 * é o que permite trocar a origem dos dados (xlsx, csv, banco de dados...)
 * no futuro sem precisar tocar em Operacoes.
 *
 * Um arquivo .xlsx é, por baixo dos panos, um arquivo ZIP contendo vários
 * XMLs (é o formato OOXML). Em vez de depender de uma biblioteca externa
 * (como o Apache POI) só para ler quatro colunas simples, esta classe lê
 * o ZIP e o XML diretamente com as classes que já vêm no próprio JDK
 * (java.util.zip e javax.xml.parsers) — sem nenhuma dependência externa.
 *
 * Formato esperado da planilha (primeira aba, cabeçalho na linha 1):
 * Coluna A = Nome | Coluna B = Data de Nascimento | Coluna C = Salário | Coluna D = Função
 */
public class LeitorFuncionarios {

    // Excel guarda datas como um número de dias a partir desta data-base
    // (30/12/1899) — é assim que o valor "36817" da célula vira uma data de verdade.
    private static final LocalDate DATA_BASE_EXCEL = LocalDate.of(1899, 12, 30);

    public static List<Funcionario> carregar(String caminhoArquivo) {
        try (ZipFile zip = new ZipFile(caminhoArquivo)) {

            List<String> stringsCompartilhadas = lerStringsCompartilhadas(zip);
            return lerFuncionarios(zip, stringsCompartilhadas);

        } catch (Exception e) {
            throw new RuntimeException("Não foi possível ler o arquivo: " + caminhoArquivo, e);
        }
    }

    /**
     * O Excel não repete textos dentro do sheet1.xml: toda célula de texto
     * (nome, função, cabeçalho...) guarda apenas um ÍNDICE que aponta para
     * uma lista de strings únicas, armazenada em xl/sharedStrings.xml.
     * Por isso lemos essa lista primeiro.
     */
    private static List<String> lerStringsCompartilhadas(ZipFile zip) throws Exception {
        List<String> lista = new ArrayList<>();
        ZipEntry entrada = zip.getEntry("xl/sharedStrings.xml");
        if (entrada == null) {
            return lista; // planilha sem nenhuma célula de texto (improvável aqui, mas não quebra)
        }

        Document doc = lerXml(zip.getInputStream(entrada));
        NodeList itens = doc.getElementsByTagName("si");

        for (int i = 0; i < itens.getLength(); i++) {
            Element item = (Element) itens.item(i);
            NodeList textos = item.getElementsByTagName("t");
            StringBuilder texto = new StringBuilder();
            for (int j = 0; j < textos.getLength(); j++) {
                texto.append(textos.item(j).getTextContent());
            }
            lista.add(texto.toString());
        }
        return lista;
    }

    private static List<Funcionario> lerFuncionarios(ZipFile zip, List<String> stringsCompartilhadas) throws Exception {
        List<Funcionario> funcionarios = new ArrayList<>();

        ZipEntry entrada = zip.getEntry("xl/worksheets/sheet1.xml");
        Document doc = lerXml(zip.getInputStream(entrada));
        NodeList linhas = doc.getElementsByTagName("row");

        for (int i = 0; i < linhas.getLength(); i++) {
            Element linha = (Element) linhas.item(i);

            if ("1".equals(linha.getAttribute("r"))) {
                continue; // linha 1 = cabeçalho (Nome, Data de Nascimento, Salário, Função)
            }

            Map<String, String> valoresPorColuna = lerCelulasDaLinha(linha, stringsCompartilhadas);
            if (valoresPorColuna.get("A") == null) {
                continue; // linha em branco
            }

            String nome = valoresPorColuna.get("A");
            LocalDate dataNascimento = converterSerialParaData(Double.parseDouble(valoresPorColuna.get("B")));
            BigDecimal salario = new BigDecimal(valoresPorColuna.get("C")).setScale(2, RoundingMode.HALF_UP);
            String funcao = valoresPorColuna.get("D");

            funcionarios.add(new Funcionario(nome, dataNascimento, salario, funcao));
        }

        return funcionarios;
    }

    /**
     * Lê as células <c> de uma <row> e devolve um mapa "letra da coluna" -> "valor já resolvido"
     * (já traduzindo string compartilhada para o texto real, quando for o caso).
     */
    private static Map<String, String> lerCelulasDaLinha(Element linha, List<String> stringsCompartilhadas) {
        Map<String, String> valores = new LinkedHashMap<>();
        NodeList celulas = linha.getElementsByTagName("c");

        for (int i = 0; i < celulas.getLength(); i++) {
            Element celula = (Element) celulas.item(i);
            String referencia = celula.getAttribute("r");       // ex.: "A2", "B2"...
            String coluna = referencia.replaceAll("[0-9]", "");  // "A2" -> "A"
            String tipo = celula.getAttribute("t");              // "s" = string compartilhada

            NodeList nosValor = celula.getElementsByTagName("v");
            if (nosValor.getLength() == 0) {
                continue; // célula vazia
            }
            String valorBruto = nosValor.item(0).getTextContent();

            String valorFinal = "s".equals(tipo)
                    ? stringsCompartilhadas.get(Integer.parseInt(valorBruto))
                    : valorBruto;

            valores.put(coluna, valorFinal);
        }
        return valores;
    }

    private static LocalDate converterSerialParaData(double numeroSerial) {
        return DATA_BASE_EXCEL.plusDays((long) numeroSerial);
    }

    private static Document lerXml(InputStream entrada) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entrada);
    }
}
