package src;

import src.generator.CodeGenerator;
import src.generator.JavaGenerator;
import src.generator.KotlinGenerator;
import src.lexer.Lexer;
import src.lexer.Token;
import src.parser.Parser;
import src.runner.Runner;
import src.tree.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Uso: java src.Main <arquivo> [--lang=kotlin|java] [--tokens] [--ast|--ast=tree|--ast=preorder|--ast=code] [--run]");
            return;
        }

        String code = new String(Files.readAllBytes(Paths.get(args[0])));

        boolean printTokens = false;
        boolean runGenerated = false;
        String astMode = null;
        String lang = "kotlin";
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--ast", "--ast=tree" -> astMode = "tree";
                case "--ast=preorder" -> astMode = "preorder";
                case "--ast=code" -> astMode = "code";
                case "--tokens" -> printTokens = true;
                case "--lang=kotlin" -> lang = "kotlin";
                case "--lang=java" -> lang = "java";
                case "--run" -> runGenerated = true;
            }
        }

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.getTokens();

        if (printTokens) {
            System.out.println("=== TOKENS ===");
            for (Token t : tokens) {
                System.out.println(t);
            }
            System.out.println();
        }

        if (lexer.temErros()) {
            System.out.println("========== ERROS LÉXICOS ==========");
            for (String erro : lexer.getErros()) {
                System.out.println("  " + erro);
            }
        }

        Parser parser = new Parser(tokens);
        Tree tree = parser.runParser();

        if (tree != null) {
            if (astMode != null) {
                switch (astMode) {
                    case "tree" -> tree.printTree();
                    case "preorder" -> tree.preOrder();
                    case "code" -> tree.printCode();
                }
            }

            CodeGenerator generator = switch (lang) {
                case "java" -> new JavaGenerator(tree);
                default -> new KotlinGenerator(tree);
            };

            String outputFile = args[0].replaceAll("\\.[^.]+$", "") + generator.getFileExtension();
            generator.generate(outputFile);

            if (runGenerated) {
                Runner.run(outputFile, lang);
            }
        }
    }
}
