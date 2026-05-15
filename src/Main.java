package src;

import src.lexer.Lexer;
import src.lexer.Token;
import src.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Uso: java src.Main <arquivo>");
            return;
        }

        String code = new String(Files.readAllBytes(Paths.get(args[0])));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.getTokens();

        System.out.println("=== TOKENS ===");
        for (Token t : tokens) {
            System.out.println(t);
        }
        System.out.println("===============\n");

        Parser parser = new Parser(tokens);
        parser.runParser();
    }
}
