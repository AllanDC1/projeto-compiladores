package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;
import java.util.Set;

public class WordAFD extends AFD {

    private static final Set<String> PALAVRAS_RESERVADAS = Set.of(
        "inteiro", "texto", "decimal", "logico",
        "se", "senao", "enquanto", "durante",
        "entrada", "saida", "verdade", "falso"
    );

    @Override
    public Token evaluate(CharacterIterator code) {
        if (!Character.isLetter(code.current()) && code.current() != '_') {
            return null;
        }

        StringBuilder word = new StringBuilder();
        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            word.append(code.current());
            code.next();
        }

        String lexema = word.toString();

        if (lexema.equals("inicio") && code.current() == '-') {
            code.next();
            if (code.current() == '>') {
                code.next();
                return new Token("inicio", "inicio->");
            }
        }

        if (PALAVRAS_RESERVADAS.contains(lexema)) {
            return new Token("palavra_reservada", lexema);
        }

        return new Token("id", lexema);
    }
}

