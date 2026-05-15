package src.lexer;

import src.afd.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final List<Token> tokens;
    private final List<AFD> afds;
    private final CharacterIterator code;
    private final List<String> erros;
    private int linhaAtual;

    public Lexer(String code) {
        tokens = new ArrayList<>();
        erros = new ArrayList<>();
        this.code = new StringCharacterIterator(code);
        linhaAtual = 1;
        afds = new ArrayList<>();
        afds.add(new CommentAFD());
        afds.add(new StringAFD());
        afds.add(new WordAFD());
        afds.add(new NumberAFD());
        afds.add(new SymbolAFD());
        afds.add(new MathOperatorAFD());
    }

    public void skipWhiteSpace() {
        while (code.current() == ' ' || code.current() == '\n' || code.current() == '\r' || code.current() == '\t') {
            if (code.current() == '\n') {
                linhaAtual++;
            }
            code.next();
        }
    }

    private Token searchNextToken() {
        int pos = code.getIndex();
        int linhaAntes = linhaAtual;
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) {
                t.linha = linhaAntes;
                return t;
            }
            code.setIndex(pos);
        }
        return null;
    }

    public List<Token> getTokens() {
        Token t;
        while (true) {
            skipWhiteSpace();
            t = searchNextToken();
            if (t == null) {
                if (code.current() != CharacterIterator.DONE) {
                    erros.add("Erro na linha " + linhaAtual + ": token não reconhecido '" + code.current() + "'");
                    code.next();
                    continue;
                } else {
                    t = new Token("EOF", "EOF", linhaAtual);
                    tokens.add(t);
                    break;
                }
            }
            if (!t.tipo.equals("comentario")) {
                tokens.add(t);
            }
            for (char c : t.lexema.toCharArray()) {
                if (c == '\n') linhaAtual++;
            }
            if (t.tipo.equals("EOF")) {
                break;
            }
        }
        return tokens;
    }

    public List<String> getErros() {
        return erros;
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }
}
