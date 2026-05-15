package src.generator;

import src.tree.Node;
import src.tree.Tree;

public class JavaGenerator extends CodeGenerator {

    public JavaGenerator(Tree tree) {
        super(tree);
    }

    @Override
    public String getFileExtension() {
        return ".java";
    }

    @Override
    public String getLanguageName() {
        return "Java";
    }

    @Override
    protected void generatePrograma(Node node) {
        appendLine("import java.util.Scanner;");
        appendLine("");
        appendLine("public class Main {");
        indentLevel++;
        appendLine("public static void main(String[] args) {");
        indentLevel++;
        appendLine("Scanner scanner = new Scanner(System.in);");
        for (Node child : node.getNodes()) {
            generateNode(child);
        }
        indentLevel--;
        appendLine("}");
        indentLevel--;
        appendLine("}");
    }

    @Override
    protected void generateDeclaracao(Node node) {
        String tipo = node.getNodes().get(0).getNome();
        String id = node.getNodes().get(1).getNome();
        symbolTable.put(id, tipo);
        appendLine(translateType(tipo) + " " + id + " = " + getDefaultValue(tipo) + ";");
    }

    @Override
    protected void generateDeclaracaoAtribuicao(Node node) {
        String tipo = node.getNodes().get(0).getNome();
        String id = node.getNodes().get(1).getNome();
        symbolTable.put(id, tipo);
        currentTargetType = tipo;
        String expr = generateExpressao(node.getNodes().get(2));
        currentTargetType = null;
        appendLine(translateType(tipo) + " " + id + " = " + expr + ";");
    }

    @Override
    protected void generateAtribuicao(Node node) {
        String id = node.getNodes().get(0).getNome();
        currentTargetType = symbolTable.get(id);
        String expr = generateExpressao(node.getNodes().get(1));
        currentTargetType = null;
        appendLine(id + " = " + expr + ";");
    }

    @Override
    protected void generateCondicional(Node node) {
        int idx = 0;
        Node condicaoNode = node.getNodes().get(idx);
        idx++;

        appendLine("if (" + generateCondicao(condicaoNode) + ") {");
        indentLevel++;

        while (idx < node.getNodes().size()) {
            Node child = node.getNodes().get(idx);
            if (child.getNome().equals("senao")) break;
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

    @Override
    protected void generateEnquanto(Node node) {
        appendLine("while (" + generateCondicao(node.getNodes().getFirst()) + ") {");
        indentLevel++;
        for (int i = 1; i < node.getNodes().size(); i++) {
            generateNode(node.getNodes().get(i));
        }
        indentLevel--;
        appendLine("}");
    }

    @Override
    protected void generateDurante(Node node) {
        Node initNode = node.getNodes().get(0);
        Node condicaoNode = node.getNodes().get(1);
        Node stepNode = node.getNodes().get(2);

        appendLine(initNode.getNodes().get(0).getNome() + " = " + generateExpressao(initNode.getNodes().get(1)) + ";");

        appendLine("while (" + generateCondicao(condicaoNode) + ") {");
        indentLevel++;
        for (int i = 3; i < node.getNodes().size(); i++) {
            generateNode(node.getNodes().get(i));
        }
        appendLine(stepNode.getNodes().get(0).getNome() + " = " + generateExpressao(stepNode.getNodes().get(1)) + ";");
        indentLevel--;
        appendLine("}");
    }

    @Override
    protected void generateSaida(Node node) {
        appendLine("System.out.println(" + generateExpressao(node.getNodes().getFirst()) + ");");
    }

    @Override
    protected String generateExpressao(Node node) {
        if (!node.getNome().equals("expressao")) return generateValue(node);
        StringBuilder sb = new StringBuilder();
        for (Node child : node.getNodes()) {
            sb.append(generateTermo(child));
        }
        return sb.toString();
    }

    private String generateTermo(Node node) {
        if (!node.getNome().equals("termo")) return " " + node.getNome() + " ";
        StringBuilder sb = new StringBuilder();
        for (Node child : node.getNodes()) {
            sb.append(generateFator(child));
        }
        return sb.toString();
    }

    private String generateFator(Node node) {
        switch (node.getNome()) {
            case "negativo" -> { return "-" + generateFator(node.getNodes().getFirst()); }
            case "entrada" -> { return generateEntrada(node); }
            case "termo" -> { return generateTermo(node); }
            case "expressao" -> { return "(" + generateExpressao(node) + ")"; }
            default -> {
                if (node.getNome().equals("*") || node.getNome().equals("/"))
                    return " " + node.getNome() + " ";
                return generateValue(node);
            }
        }
    }

    @Override
    protected String generateEntrada(Node node) {
        if (!node.getNodes().isEmpty()) {
            String prompt = node.getNodes().getFirst().getNome();
            appendLine("System.out.print(" + prompt + ");");
        }
        if (currentTargetType != null) {
            return switch (currentTargetType) {
                case "inteiro" -> "scanner.nextInt()";
                case "decimal" -> "scanner.nextDouble()";
                case "logico" -> "scanner.nextBoolean()";
                default -> "scanner.nextLine()";
            };
        }
        return "scanner.nextLine()";
    }

    private String generateValue(Node node) {
        return switch (node.getNome()) {
            case "verdade" -> "true";
            case "falso" -> "false";
            default -> node.getNome();
        };
    }

    @Override
    protected String generateCondicao(Node node) {
        if (!node.getNome().equals("condicao")) return generateTermoLogico(node);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Node child : node.getNodes()) {
            if (!first) sb.append(" || ");
            sb.append(generateTermoLogico(child));
            first = false;
        }
        return sb.toString();
    }

    private String generateTermoLogico(Node node) {
        if (!node.getNome().equals("termo_logico")) return generateFatorLogico(node);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Node child : node.getNodes()) {
            if (!first) sb.append(" && ");
            sb.append(generateFatorLogico(child));
            first = false;
        }
        return sb.toString();
    }

    private String generateFatorLogico(Node node) {
        return switch (node.getNome()) {
            case "negacao_logica" -> "!" + generateFatorLogico(node.getNodes().getFirst());
            case "expressao_relacional" -> generateExpressaoRelacional(node);
            case "condicao" -> "(" + generateCondicao(node) + ")";
            case "termo_logico" -> generateTermoLogico(node);
            case "verdade" -> "true";
            case "falso" -> "false";
            default -> generateValue(node);
        };
    }

    private String generateExpressaoRelacional(Node node) {
        if (node.getNodes().size() == 1) return generateExpressao(node.getNodes().getFirst());
        String left = generateExpressao(node.getNodes().get(0));
        String op = translateOperator(node.getNodes().get(1).getNome());
        String right = generateExpressao(node.getNodes().get(2));
        return left + " " + op + " " + right;
    }

    private String translateOperator(String op) {
        return switch (op) {
            case "<>" -> "!=";
            default -> op;
        };
    }

    @Override
    protected String translateType(String tipo) {
        return switch (tipo) {
            case "inteiro" -> "int";
            case "decimal" -> "double";
            case "texto" -> "String";
            case "logico" -> "boolean";
            default -> tipo;
        };
    }

    @Override
    protected String getDefaultValue(String tipo) {
        return switch (tipo) {
            case "inteiro" -> "0";
            case "decimal" -> "0.0";
            case "texto" -> "\"\"";
            case "logico" -> "false";
            default -> "null";
        };
    }
}

