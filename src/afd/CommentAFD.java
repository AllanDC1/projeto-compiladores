package src.afd;

import src.lexer.Token;

import java.text.CharacterIterator;

public class CommentAFD extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() != '/') {
            return null;
        }

        int startIndex = code.getIndex();

        if (!matchChar(code, '/') || !matchChar(code, '/') || !matchChar(code, '-') || !matchChar(code, '-')) {
            code.setIndex(startIndex);
            return null;
        }

        StringBuilder comment = new StringBuilder("//--");
        while (code.current() != CharacterIterator.DONE) {
            comment.append(code.current());
            if (comment.length() >= 8 && comment.substring(comment.length() - 4).equals("--//")) {
                code.next();
                return new Token("comentario", comment.toString());
            }
            code.next();
        }

        code.setIndex(startIndex);
        return null;
    }

    private boolean matchChar(CharacterIterator code, char expected) {
        if (code.current() == expected) {
            code.next();
            return true;
        }
        return false;
    }
}
