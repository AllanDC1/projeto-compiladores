package src.lexer;

public class Token {
    public String tipo;
    public String lexema;
    public int linha;

    public Token(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = 0;
    }

    public Token(String tipo, String lexema, int linha) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = linha;
    }

    @Override
    public String toString() {
        return "< " + tipo + ", " + lexema + " >";
    }
}