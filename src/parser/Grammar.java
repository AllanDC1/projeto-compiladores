package src.parser;

import src.tree.Tree;
import src.tree.Node;

public class Grammar {

    private final Parser parser;
    private final Tree tree;

    public Grammar(Parser parser) {
        this.parser = parser;
        this.tree = new Tree();
    }

    public boolean programa() {
        Node root = new Node("programa");
        if (parser.matchLexema("inicio->") && listaComandos(root) && parser.matchLexema("<-fim")) {
            tree.setRoot(root);
            return true;
        }
        return false;
    }

    public boolean listaComandos(Node pai) {
        if (!comando(pai)) {
            return false;
        }
        while (parser.currentToken() != null &&
               !parser.currentToken().lexema.equals("}") &&
               !parser.currentToken().lexema.equals("<-fim")) {
            if (!comando(pai)) {
                break;
            }
        }
        return true;
    }

    public boolean comando(Node pai) {
        if (parser.currentToken() == null) return false;

        String lexema = parser.currentToken().lexema;
        String tipo = parser.currentToken().tipo;

        if (lexema.equals("inteiro") || lexema.equals("texto") || lexema.equals("decimal") || lexema.equals("logico")) {
            return declaracaoOuDeclaracaoAtribuicao(pai);
        }
        if (tipo.equals("id")) {
            return atribuicao(pai);
        }
        if (lexema.equals("se")) {
            return condicional(pai);
        }
        if (lexema.equals("enquanto") || lexema.equals("durante")) {
            return laco(pai);
        }
        if (lexema.equals("saida")) {
            return saida(pai);
        }
        return false;
    }

    public boolean declaracaoOuDeclaracaoAtribuicao(Node pai) {
        Node tipoNode = new Node(parser.currentToken().lexema);
        if (!tipo()) return false;

        if (!parser.matchLexema(":")) return false;

        String nomeId = parser.currentToken().lexema;
        if (!parser.matchTipo("id")) return false;

        if (parser.matchLexema("<<")) {
            Node node = pai.addNode("declaracao_atribuicao");
            node.addNode(tipoNode);
            node.addNode(new Node(nomeId));
            if (!expressao(node)) return false;
            return parser.matchLexema(";");
        } else {
            Node node = pai.addNode("declaracao");
            node.addNode(tipoNode);
            node.addNode(new Node(nomeId));
            return parser.matchLexema(";");
        }
    }

    public boolean tipo() {
        return parser.matchLexema("inteiro") || parser.matchLexema("texto") ||
               parser.matchLexema("decimal") || parser.matchLexema("logico");
    }

    public boolean atribuicao(Node pai) {
        Node node = pai.addNode("atribuicao");
        node.addNode(new Node(parser.currentToken().lexema));
        if (!parser.matchTipo("id")) return false;
        if (!parser.matchLexema("<<")) return false;
        if (!expressao(node)) return false;
        return parser.matchLexema(";");
    }

    public boolean atribuicaoLoop(Node pai) {
        Node node = pai.addNode("atribuicao_loop");
        node.addNode(new Node(parser.currentToken().lexema));
        if (!parser.matchTipo("id")) return false;
        if (!parser.matchLexema("<<")) return false;
        return expressao(node);
    }

    public boolean expressao(Node pai) {
        Node node = pai.addNode("expressao");
        if (!termo(node)) return false;
        while (parser.currentToken() != null &&
               (parser.currentToken().lexema.equals("+") || parser.currentToken().lexema.equals("-"))) {
            node.addNode(new Node(parser.currentToken().lexema));
            parser.matchLexema(parser.currentToken().lexema);
            if (!termo(node)) return false;
        }
        return true;
    }

    public boolean expressaoRelacional(Node pai) {
        Node node = pai.addNode("expressao_relacional");
        if (!expressao(node)) return false;
        if (opComparacao(node)) {
            return expressao(node);
        }
        return true;
    }

    public boolean condicao(Node pai) {
        Node node = pai.addNode("condicao");
        if (!termoLogico(node)) return false;
        while (parser.matchLexema("||")) {
            if (!termoLogico(node)) return false;
        }
        return true;
    }

    public boolean termoLogico(Node pai) {
        Node node = pai.addNode("termo_logico");
        do {
            if (!fatorLogico(node)) return false;
        } while (parser.matchLexema("&&"));
        return true;
    }

    public boolean fatorLogico(Node pai) {
        if (parser.matchLexema("!!")) {
            Node node = pai.addNode("negacao_logica");
            return fatorLogico(node);
        }
        if (parser.matchLexema("(")) {
            return condicao(pai) && parser.matchLexema(")");
        }
        if (parser.currentToken() != null &&
            (parser.currentToken().lexema.equals("verdade") || parser.currentToken().lexema.equals("falso"))) {
            pai.addNode(new Node(parser.currentToken().lexema));
            parser.matchLexema(parser.currentToken().lexema);
            return true;
        }
        return expressaoRelacional(pai);
    }

    public boolean termo(Node pai) {
        Node node = pai.addNode("termo");
        if (!fator(node)) return false;
        while (parser.currentToken() != null &&
               (parser.currentToken().lexema.equals("*") || parser.currentToken().lexema.equals("/"))) {
            node.addNode(new Node(parser.currentToken().lexema));
            parser.matchLexema(parser.currentToken().lexema);
            if (!fator(node)) return false;
        }
        return true;
    }

    public boolean fator(Node pai) {
        if (parser.currentToken() == null) return false;

        switch (parser.currentToken().tipo) {
            case "id" -> {
                pai.addNode(new Node(parser.currentToken().lexema));
                parser.matchTipo("id");
                return true;
            }
            case "num_int", "num_decimal" -> {
                pai.addNode(new Node(parser.currentToken().lexema));
                parser.matchTipo(parser.currentToken().tipo);
                return true;
            }
            case "texto" -> {
                pai.addNode(new Node(parser.currentToken().lexema));
                parser.matchTipo("texto");
                return true;
            }
        }
        if (parser.currentToken().lexema.equals("verdade") || parser.currentToken().lexema.equals("falso")) {
            pai.addNode(new Node(parser.currentToken().lexema));
            parser.matchLexema(parser.currentToken().lexema);
            return true;
        }
        if (parser.matchLexema("-")) {
            Node node = pai.addNode("negativo");
            return fator(node);
        }
        if (parser.matchLexema("(")) {
            return expressao(pai) && parser.matchLexema(")");
        }
        if (parser.currentToken().lexema.equals("entrada")) {
            return entrada(pai);
        }
        return false;
    }

    public boolean entrada(Node pai) {
        Node node = pai.addNode("entrada");
        if (!parser.matchLexema("entrada")) return false;
        if (!parser.matchLexema("(")) return false;
        if (parser.currentToken() != null && parser.currentToken().tipo.equals("texto")) {
            node.addNode(new Node(parser.currentToken().lexema));
            parser.matchTipo("texto");
        }
        return parser.matchLexema(")");
    }

    public boolean opComparacao(Node pai) {
        if (parser.currentToken() == null) return false;
        String lex = parser.currentToken().lexema;
        if (lex.equals("==") || lex.equals("<>") || lex.equals(">") || lex.equals("<") ||
            lex.equals(">=") || lex.equals("<=")) {
            pai.addNode(new Node(lex));
            parser.matchLexema(lex);
            return true;
        }
        return false;
    }

    public boolean condicional(Node pai) {
        Node node = pai.addNode("condicional");
        if (!parser.matchLexema("se")) return false;
        if (!parser.matchLexema("(")) return false;
        if (!condicao(node)) return false;
        if (!parser.matchLexema(")")) return false;
        if (!parser.matchLexema("{")) return false;
        if (!listaComandos(node)) return false;
        if (!parser.matchLexema("}")) return false;
        blocoSenao(node);
        return true;
    }

    public boolean blocoSenao(Node pai) {
        if (parser.matchLexema("senao")) {
            Node node = pai.addNode("senao");
            if (!parser.matchLexema("{")) return false;
            if (!listaComandos(node)) return false;
            return parser.matchLexema("}");
        }
        return true;
    }

    public boolean laco(Node pai) {
        if (parser.currentToken().lexema.equals("enquanto")) {
            return enquanto(pai);
        }
        if (parser.currentToken().lexema.equals("durante")) {
            return durante(pai);
        }
        return false;
    }

    public boolean enquanto(Node pai) {
        Node node = pai.addNode("enquanto");
        if (!parser.matchLexema("enquanto")) return false;
        if (!parser.matchLexema("(")) return false;
        if (!condicao(node)) return false;
        if (!parser.matchLexema(")")) return false;
        if (!parser.matchLexema("{")) return false;
        if (!listaComandos(node)) return false;
        return parser.matchLexema("}");
    }

    public boolean durante(Node pai) {
        Node node = pai.addNode("durante");
        if (!parser.matchLexema("durante")) return false;
        if (!parser.matchLexema("(")) return false;
        if (!atribuicaoLoop(node)) return false;
        if (!parser.matchLexema(";")) return false;
        if (!condicao(node)) return false;
        if (!parser.matchLexema(";")) return false;
        if (!atribuicaoLoop(node)) return false;
        if (!parser.matchLexema(")")) return false;
        if (!parser.matchLexema("{")) return false;
        if (!listaComandos(node)) return false;
        return parser.matchLexema("}");
    }

    public boolean saida(Node pai) {
        Node node = pai.addNode("saida");
        if (!parser.matchLexema("saida")) return false;
        if (!parser.matchLexema("(")) return false;
        if (!expressao(node)) return false;
        if (!parser.matchLexema(")")) return false;
        return parser.matchLexema(";");
    }

    public Tree getTree() {
        return tree;
    }
}
