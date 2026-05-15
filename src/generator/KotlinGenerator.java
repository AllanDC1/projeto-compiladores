package src.generator;

import src.tree.Node;
import src.tree.Tree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class KotlinGenerator {

    private final Tree tree;
    private final StringBuilder output;
    private int indentLevel;
    private final Map<String, String> symbolTable;
    private String currentTargetType;

    public KotlinGenerator(Tree tree) {
        this.tree = tree;
        this.output = new StringBuilder();
        this.indentLevel = 0;
        this.symbolTable = new HashMap<>();
        this.currentTargetType = null;
    }

    public void generate(String outputFile) throws IOException {
        generateNode(tree.getRoot());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.print(output);
        }
        System.out.println("Código Kotlin gerado em: " + outputFile);
    }

    private void generateNode(Node node) {
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

    private void generatePrograma(Node node) {
        appendLine("fun main() {");
        indentLevel++;
        for (Node child : node.getNodes()) {
            generateNode(child);
        }
        indentLevel--;
        appendLine("}");
    }

    private void generateDeclaracao(Node node) {
        String tipo = node.getNodes().get(0).getNome();
        String id = node.getNodes().get(1).getNome();
        symbolTable.put(id, tipo);
        String kotlinType = translateType(tipo);
        String defaultValue = getDefaultValue(tipo);
        appendLine("var " + id + ": " + kotlinType + " = " + defaultValue);
    }

    private void generateDeclaracaoAtribuicao(Node node) {
        String tipo = node.getNodes().get(0).getNome();
        String id = node.getNodes().get(1).getNome();
        symbolTable.put(id, tipo);
        String kotlinType = translateType(tipo);
        currentTargetType = tipo;
        String expr = generateExpressao(node.getNodes().get(2));
        currentTargetType = null;
        appendLine("var " + id + ": " + kotlinType + " = " + expr);
    }

    private void generateAtribuicao(Node node) {
        String id = node.getNodes().get(0).getNome();
        currentTargetType = symbolTable.get(id);
        String expr = generateExpressao(node.getNodes().get(1));
        currentTargetType = null;
        appendLine(id + " = " + expr);
    }

    private void generateCondicional(Node node) {

        int idx = 0;
        Node condicaoNode = node.getNodes().get(idx);
        idx++;

        String condicao = generateCondicao(condicaoNode);
        appendLine("if (" + condicao + ") {");
        indentLevel++;

        while (idx < node.getNodes().size()) {
            Node child = node.getNodes().get(idx);
            if (child.getNome().equals("senao")) {
                break;
            }
            generateNode(child);
            idx++;
        }
        indentLevel--;

        if (idx < node.getNodes().size() && node.getNodes().get(idx).getNome().equals("senao")) {
            appendLine("} else {");
            indentLevel++;
            Node senaoNode = node.getNodes().get(idx);
            for (Node child : senaoNode.getNodes()) {
                generateNode(child);
            }
            indentLevel--;
        }
        appendLine("}");
    }

    private void generateEnquanto(Node node) {

        Node condicaoNode = node.getNodes().getFirst();
        String condicao = generateCondicao(condicaoNode);
        appendLine("while (" + condicao + ") {");
        indentLevel++;
        for (int i = 1; i < node.getNodes().size(); i++) {
            generateNode(node.getNodes().get(i));
        }
        indentLevel--;
        appendLine("}");
    }

    private void generateDurante(Node node) {

        Node initNode = node.getNodes().get(0);
        Node condicaoNode = node.getNodes().get(1);
        Node stepNode = node.getNodes().get(2);


        String initId = initNode.getNodes().get(0).getNome();
        String initExpr = generateExpressao(initNode.getNodes().get(1));
        appendLine(initId + " = " + initExpr);

        String condicao = generateCondicao(condicaoNode);
        appendLine("while (" + condicao + ") {");
        indentLevel++;
        for (int i = 3; i < node.getNodes().size(); i++) {
            generateNode(node.getNodes().get(i));
        }

        String stepId = stepNode.getNodes().get(0).getNome();
        String stepExpr = generateExpressao(stepNode.getNodes().get(1));
        appendLine(stepId + " = " + stepExpr);
        indentLevel--;
        appendLine("}");
    }

    private void generateSaida(Node node) {

        String expr = generateExpressao(node.getNodes().getFirst());
        appendLine("println(" + expr + ")");
    }

    private String generateExpressao(Node node) {
        if (!node.getNome().equals("expressao")) {
            return generateValue(node);
        }
        StringBuilder sb = new StringBuilder();
        for (Node child : node.getNodes()) {
            sb.append(generateTermo(child));
        }
        return sb.toString();
    }

    private String generateTermo(Node node) {
        if (!node.getNome().equals("termo")) {

            return " " + node.getNome() + " ";
        }
        StringBuilder sb = new StringBuilder();
        for (Node child : node.getNodes()) {
            sb.append(generateFator(child));
        }
        return sb.toString();
    }

    private String generateFator(Node node) {
        switch (node.getNome()) {
            case "negativo" -> {
                return "-" + generateFator(node.getNodes().getFirst());
            }
            case "entrada" -> {
                return generateEntrada(node);
            }
            case "termo" -> {
                return generateTermo(node);
            }
            case "expressao" -> {
                return generateExpressao(node);
            }
            default -> {

                if (node.getNome().equals("*") || node.getNome().equals("/")) {
                    return " " + node.getNome() + " ";
                }
                return generateValue(node);
            }
        }
    }

    private String generateEntrada(Node node) {
        String readExpr;
        if (!node.getNodes().isEmpty()) {
            String prompt = node.getNodes().getFirst().getNome();
            readExpr = "run { print(" + prompt + "); readln() }";
        } else {
            readExpr = "readln()";
        }

        if (currentTargetType != null) {
            switch (currentTargetType) {
                case "inteiro" -> readExpr += ".toInt()";
                case "decimal" -> readExpr += ".toDouble()";
                case "logico" -> readExpr += ".toBoolean()";
            }
        }
        return readExpr;
    }

    private String generateValue(Node node) {
        String nome = node.getNome();
        return switch (nome) {
            case "verdade" -> "true";
            case "falso" -> "false";
            default -> nome;
        };
    }

    private String generateCondicao(Node node) {
        if (!node.getNome().equals("condicao")) {
            return generateTermoLogico(node);
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Node child : node.getNodes()) {
            if (!first) {
                sb.append(" || ");
            }
            sb.append(generateTermoLogico(child));
            first = false;
        }
        return sb.toString();
    }

    private String generateTermoLogico(Node node) {
        if (!node.getNome().equals("termo_logico")) {
            return generateFatorLogico(node);
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Node child : node.getNodes()) {
            if (!first) {
                sb.append(" && ");
            }
            sb.append(generateFatorLogico(child));
            first = false;
        }
        return sb.toString();
    }

    private String generateFatorLogico(Node node) {
        switch (node.getNome()) {
            case "negacao_logica" -> {
                return "!" + generateFatorLogico(node.getNodes().getFirst());
            }
            case "expressao_relacional" -> {
                return generateExpressaoRelacional(node);
            }
            case "condicao" -> {
                return "(" + generateCondicao(node) + ")";
            }
            case "termo_logico" -> {
                return generateTermoLogico(node);
            }
            case "verdade" -> {
                return "true";
            }
            case "falso" -> {
                return "false";
            }
            default -> {
                return generateValue(node);
            }
        }
    }

    private String generateExpressaoRelacional(Node node) {
        if (node.getNodes().size() == 1) {
            return generateExpressao(node.getNodes().getFirst());
        }

        String left = generateExpressao(node.getNodes().get(0));
        String op = translateOperator(node.getNodes().get(1).getNome());
        String right = generateExpressao(node.getNodes().get(2));
        return left + " " + op + " " + right;
    }

    private String translateOperator(String op) {
        return switch (op) {
            case "<>" -> "!=";
            case "==" -> "==";
            default -> op;
        };
    }

    private String translateType(String tipo) {
        return switch (tipo) {
            case "inteiro" -> "Int";
            case "decimal" -> "Double";
            case "texto" -> "String";
            case "logico" -> "Boolean";
            default -> tipo;
        };
    }

    private String getDefaultValue(String tipo) {
        return switch (tipo) {
            case "inteiro" -> "0";
            case "decimal" -> "0.0";
            case "texto" -> "\"\"";
            case "logico" -> "false";
            default -> "null";
        };
    }

    private void appendLine(String line) {
        output.repeat("    ", indentLevel).append(line).append("\n");
    }
}
