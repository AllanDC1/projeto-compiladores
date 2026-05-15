package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;

public class SymbolAFD extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '<' -> {
                code.next();
                if (code.current() == '-') {
                    int mark = code.getIndex();
                    String rest = "-fim";
                    boolean match = true;
                    for (int i = 0; i < rest.length(); i++) {
                        if (code.current() != rest.charAt(i)) {
                            match = false;
                            break;
                        }
                        code.next();
                    }
                    if (match && isTokenSeparator(code)) {
                        return new Token("fim", "<-fim");
                    }
                    code.setIndex(mark);
                }
                if (code.current() == '<') {
                    code.next();
                    return new Token("op_atribuicao", "<<");
                }
                if (code.current() == '>') {
                    code.next();
                    return new Token("op_comparacao", "<>");
                }
                if (code.current() == '=') {
                    code.next();
                    return new Token("op_comparacao", "<=");
                }
                return new Token("op_comparacao", "<");
            }
            case '>' -> {
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("op_comparacao", ">=");
                }
                return new Token("op_comparacao", ">");
            }
            case '=' -> {
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("op_comparacao", "==");
                }
                return null;
            }
            case '&' -> {
                code.next();
                if (code.current() == '&') {
                    code.next();
                    return new Token("e_logico", "&&");
                }
                return null;
            }
            case '|' -> {
                code.next();
                if (code.current() == '|') {
                    code.next();
                    return new Token("ou_logico", "||");
                }
                return null;
            }
            case '!' -> {
                code.next();
                if (code.current() == '!') {
                    code.next();
                    return new Token("nao_logico", "!!");
                }
                return null;
            }
            default -> {
                return null;
            }
        }
    }
}

