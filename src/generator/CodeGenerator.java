package src.generator;

import src.tree.Node;
import src.tree.Tree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class CodeGenerator {

    protected final Tree tree;
    protected final StringBuilder output;
    protected int indentLevel;
    protected final Map<String, String> symbolTable;
    protected String currentTargetType;

    public CodeGenerator(Tree tree) {
        this.tree = tree;
        this.output = new StringBuilder();
        this.indentLevel = 0;
        this.symbolTable = new HashMap<>();
        this.currentTargetType = null;
    }

    public abstract String getFileExtension();
    public abstract String getLanguageName();

    protected abstract void generatePrograma(Node node);
    protected abstract void generateDeclaracao(Node node);
    protected abstract void generateDeclaracaoAtribuicao(Node node);
    protected abstract void generateAtribuicao(Node node);
    protected abstract void generateCondicional(Node node);
    protected abstract void generateEnquanto(Node node);
    protected abstract void generateDurante(Node node);
    protected abstract void generateSaida(Node node);
    protected abstract String generateEntrada(Node node);
    protected abstract String generateExpressao(Node node);
    protected abstract String generateCondicao(Node node);
    protected abstract String translateType(String tipo);
    protected abstract String getDefaultValue(String tipo);

    public void generate(String outputFile) throws IOException {
        generateNode(tree.getRoot());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.print(output);
        }
        System.out.println("Código " + getLanguageName() + " gerado em: " + outputFile);
    }

    protected void generateNode(Node node) {
        switch (node.getNome()) {
            case "programa" -> generatePrograma(node);
            case "declaracao" -> generateDeclaracao(node);
            case "declaracao_atribuicao" -> generateDeclaracaoAtribuicao(node);
            case "atribuicao" -> generateAtribuicao(node);
            case "condicional" -> generateCondicional(node);
            case "enquanto" -> generateEnquanto(node);
            case "durante" -> generateDurante(node);
            case "saida" -> generateSaida(node);
            default -> {
                for (Node child : node.getNodes()) {
                    generateNode(child);
                }
            }
        }
    }

    protected void appendLine(String line) {
        output.repeat("    ", indentLevel).append(line).append("\n");
    }
}
