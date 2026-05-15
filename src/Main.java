package src;

import src.generator.KotlinGenerator;
import src.lexer.Lexer;
import src.lexer.Token;
import src.parser.Parser;
import src.tree.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Comando para rodar: java src.Main <arquivo> [--tokens] [--ast]");
            return;
        }

        String code = new String(Files.readAllBytes(Paths.get(args[0])));

        boolean printTokens = false;
        String astMode = null;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--ast", "--ast=tree" -> astMode = "tree";
                case "--ast=preorder" -> astMode = "preorder";
                case "--ast=code" -> astMode = "code";
                case "--tokens" -> printTokens = true;
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

            String outputFile = args[0].replaceAll("\\.[^.]+$", "") + ".kt";
            KotlinGenerator generator = new KotlinGenerator(tree);
            generator.generate(outputFile);
        }
    }
}
