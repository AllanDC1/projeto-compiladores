package src;

import java.util.List;

public class Parser {
    List <Token> tokens;
    Token token;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.token = getNextToken();
    }

    public void runParser() {
        if (ifelse()) {
            if (matchTipo("EOF")) {
                System.out.println("SUCESSO!!!");
                return;
            }
            erro();
        }
        erro();
    }

    public Token getNextToken() {
        if (tokens.size() > 0) {
            return tokens.remove(0);
        }

        return null;
    }

    private void erro() {
        System.out.println("Token inválido: " + token.lexema);
        System.out.println("-----------------------------");
    }

    private boolean matchLexema(String palavra) {
        if (token.lexema.equals(palavra)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    private boolean matchTipo(String palavra) {
        if (token.tipo.equals(palavra)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    private boolean ifelse() {
        if (matchLexema("se") && condicao() && matchLexema("entao")
                && bloco() && matchLexema("senao") && bloco()) {
            return true;
        }

        return false;
    }

    private boolean bloco() {
        if (id() && operadorAtribuicao() && num()) {
            return true;
        }

        return false;
    }

    private boolean operadorAtribuicao() {
        if (matchLexema("=")) {
            return true;
        }

        return false;
    }

    private boolean condicao() {
        if (id() && operador() && num()) {
            return true;
        }
        return false;
    }

    private boolean operador() {
        if (matchLexema(">") || matchLexema("<") || matchLexema("==")) {
            return true;
        }

        return false;
    }

    private boolean id() {
        if (matchTipo("id")) {
            return true;
        }
        return false;
    }

    private boolean num() {
        if (matchTipo("num")) {
            return true;
        }
        return false;
    }
}
