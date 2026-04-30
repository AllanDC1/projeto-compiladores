package src;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private List<Token> tokens;
    private List<AFD> afds;
    private CharacterIterator code;

    public Lexer(String code) {
        tokens = new ArrayList<>();
        this.code = new StringCharacterIterator(code);
        afds = new ArrayList<>();
        afds.add(new MathOperator());
        afds.add(new Number());
    }

    public void skipWhiteSpace() {
        while (code.current() == ' ' || code.current() == '\n') {
            code.next();
        }
    }

    private void error() {
        throw new RuntimeException("Error: token not recognized: " + code.current());
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
            tokens.add(t);
        } while (!t.tipo.equals("EOF"));
        return tokens;
    }
}
