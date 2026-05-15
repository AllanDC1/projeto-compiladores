package src.parser;

import src.lexer.Token;
import src.tree.Tree;

import java.util.List;

public class Parser {
    List<Token> tokens;
    Token token;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.token = getNextToken();
    }

    public Tree runParser() {
        Grammar grammar = new Grammar(this);

        if (grammar.programa()) {
            if (matchTipo("EOF")) {
                return grammar.getTree();
            }
        }
        erro();
        return null;
    }

    public Token getNextToken() {
        if (!tokens.isEmpty()) {
            return tokens.removeFirst();
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
