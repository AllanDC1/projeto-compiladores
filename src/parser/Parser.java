package src.parser;

import src.lexer.Token;
import src.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {
    List<Token> tokens;
    Token token;
    private final List<String> erros;

    private static final Set<String> SYNC_LEXEMAS = Set.of(
        ";", "}", "<-fim", "se", "enquanto", "durante", "saida",
        "inteiro", "texto", "decimal", "logico"
    );

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.erros = new ArrayList<>();
        this.token = getNextToken();
    }

    public Tree runParser() {
        Grammar grammar = new Grammar(this);

        if (grammar.programa()) {
            if (matchTipo("EOF")) {
                if (temErros()) {
                    printErros();
                    return null;
                }
                return grammar.getTree();
            }
        }

        if (erros.isEmpty()) {
            registrarErro();
        }
        printErros();
        return null;
    }

    public Token getNextToken() {
        if (!tokens.isEmpty()) {
            return tokens.removeFirst();
        }
        return null;
    }

    public void registrarErro() {
        String tokenInfo = (token != null) ? "Token inválido: '" + token.lexema + "'" : "Fim inesperado do arquivo";
        String linhaInfo = (token != null && token.linha > 0) ? "Linha " + token.linha + ": " : "";
        erros.add(linhaInfo + tokenInfo);
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }

    public void printErros() {
        System.out.println("========== ERROS SINTÁTICOS ==========");
        for (int i = 0; i < erros.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + erros.get(i));
        }
        System.out.println("Total: " + erros.size() + " erro(s) encontrado(s)");
    }

    public void sincronizar() {
        while (token != null && !token.tipo.equals("EOF")) {
            if (SYNC_LEXEMAS.contains(token.lexema)) {
                if (token.lexema.equals(";")) {
                    token = getNextToken();
                }
                return;
            }
            token = getNextToken();
        }
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
