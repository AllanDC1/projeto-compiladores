package src;

import java.text.CharacterIterator;

public class MathOperator extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {

        switch (code.current()) {
            case '+' -> {
                code.next();
                return new Token("PLUS", "+");
            }
            case '-' -> {
                code.next();
                return new Token("MINUS", "-");
            }
            case '*' -> {
                code.next();
                return new Token("MUL", "*");
            }
            case '/' -> {
                code.next();
                return new Token("DIV", "/");
            }
            case '(' -> {
                code.next();
                return new Token("LPAREN", "(");
            }
            case ')' -> {
                code.next();
                return new Token("RPAREN", ")");
            }
            case CharacterIterator.DONE -> {
                return new Token("EOF", "$");
            }
            default -> {
                return null;
            }
        }
    }
}