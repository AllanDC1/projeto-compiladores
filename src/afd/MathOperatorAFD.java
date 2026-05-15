package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;

public class MathOperatorAFD extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {

        switch (code.current()) {
            case '+' -> {
                code.next();
                return new Token("op_aditivo", "+");
            }
            case '-' -> {
                code.next();
                return new Token("op_aditivo", "-");
            }
            case '*' -> {
                code.next();
                return new Token("op_multiplicativo", "*");
            }
            case '/' -> {
                code.next();
                return new Token("op_multiplicativo", "/");
            }
            case '(' -> {
                code.next();
                return new Token("abre_parentese", "(");
            }
            case ')' -> {
                code.next();
                return new Token("fecha_parentese", ")");
            }
            case '{' -> {
                code.next();
                return new Token("abre_chave", "{");
            }
            case '}' -> {
                code.next();
                return new Token("fecha_chave", "}");
            }
            case ';' -> {
                code.next();
                return new Token("ponto_virgula", ";");
            }
            case ':' -> {
                code.next();
                return new Token("op_declaracao", ":");
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