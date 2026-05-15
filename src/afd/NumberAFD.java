package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;

public class NumberAFD extends AFD {
    
    @Override
    public Token evaluate(CharacterIterator code) {
        
        if (Character.isDigit(code.current())) {
            String number = readNumber(code);
            if (code.current() == '.') {
                number += '.';
                code.next();
                number += readNumber(code);
                if (isTokenSeparator(code)) {
                    return new Token("num_decimal", number);
                }
            } else if (isTokenSeparator(code)) {
                return new Token("num_int", number);
            }
        }
        return null;
    }

    private String readNumber(CharacterIterator code) {
        StringBuilder number = new StringBuilder();
        while (Character.isDigit(code.current())) {
            number.append(code.current());
            code.next();
        }
        return number.toString();
    }

}
