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

    public Lexer(String code) {
        tokens = new ArrayList<>();
        this.code = new StringCharacterIterator(code);
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
            code.next();
        }
    }

    private void error() {
        throw new RuntimeException("Erro - token nâo reconhecido: " + code.current());
    }

    private Token searchNextToken() {
        int pos = code.getIndex();
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) return t;
            code.setIndex(pos);
        }
        return null;
    }

    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace();
            t = searchNextToken();
            if (t == null) error();
            if (!t.tipo.equals("comentario")) {
                tokens.add(t);
            }
        } while (!t.tipo.equals("EOF"));
        return tokens;
    }
}
