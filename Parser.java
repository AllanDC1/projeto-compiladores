
import java.util.List;

public class Parser {
    List <Token> tokens;
    Token token;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.token = getNextToken();
    }

    public void runParser() {        
        if (ifelse() && matchToken("EOF")) {
            System.out.println("SUCESSO!!!");
            return;
        }
        erro();
    }

    public Token getNextToken() {
        if (tokens.size() > 0) {
            return tokens.remove(0);
        }

        return null;
    }

    private void erro() {
        System.out.println("Token inválido: " + token.lexema);
        System.out.println("-----------------------------");
    }

    private boolean matchToken(String tipo) {
        if (tipo.equals(token.tipo)) {
            token = getNextToken();
            return true;
        }
        return false;
    }

    private boolean ifelse() {
        return (
            matchToken("reservada_if")
            && condicao()
            && matchToken("reservada_then")
            && expressao()
            && matchToken("reservada_else")
            && expressao()
        );
    }

    private boolean condicao() {
        return (
            matchToken("id")
            && matchToken("operador_condicional")
            && matchToken("num")
        );
    }

    private boolean expressao() {
        return (
            matchToken("id")
            && matchToken("operador_atribuicao")
            && matchToken("num")
        );
    }

    private boolean operador() {
        return (
            matchToken(">")
            || matchToken("<")
            || matchToken("==")
        );
    }

    private boolean enquanto() {
        return (
            matchToken("reservada_enquanto")
            && condicao()
            && matchToken(":")
            && expressao()
        );
    }
}
