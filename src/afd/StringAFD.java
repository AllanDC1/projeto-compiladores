package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;

public class StringAFD extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != '"') {
            return null;
        }

        code.next();
        StringBuilder str = new StringBuilder();
        str.append('"');

        while (code.current() != CharacterIterator.DONE && code.current() != '"') {
            str.append(code.current());
            code.next();
        }

        if (code.current() == '"') {
            str.append('"');
            code.next();
            return new Token("texto", str.toString());
        }

        return null;
    }
}

