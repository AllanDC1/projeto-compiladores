package src.parser;

import src.lexer.Token;

import java.util.List;

public class Parser {
    List<Token> tokens;
    Token token;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.token = getNextToken();
    }

    public void runParser() {
        Grammar grammar = new Grammar(this);

        if (grammar.programa()) {
            if (matchTipo("EOF")) {
                System.out.println("SUCESSO!!!");
                grammar.getTree().printTree();
                return;
            }
        }
        erro();
    }

    public Token getNextToken() {
        if (tokens.size() > 0) {
            return tokens.remove(0);
        }
        return null;
    }

    public void erro() {
        if (token != null) {
            System.out.println("Token inválido: " + token.lexema);
        } else {
            System.out.println("ERRO: Fim inesperado do arquivo");
        }
        System.out.println("-----------------------------");
    }

    public boolean matchLexema(String palavra) {
        if (token != null && token.lexema.equals(palavra)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    public boolean matchTipo(String palavra) {
        if (token != null && token.tipo.equals(palavra)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    public Token currentToken() {
        return token;
    }
}
