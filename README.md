# Projeto compiladores - Portugol Diferenciado

Linguagem de programação própria, inspirada no Portugol, porém, com algumas diferenças implementadas. <br>
Compila/Traduz a linguagem para Kotlin ou Java

## Integrantes

- Allan Donetti Calen - RA: 221240021
- Luca Munhoz Rossi - RA: 221240039
- Rodrigo Akira Rosado Yatate - RA: 221240930
- Renato Leonardo Baxmann Júnior - RA: 221240401 
- Felipe Franco Falcon - RA: 221250731

## Tokens / Palavras Reservadas

```
INICIO      → inicio->
FIM         → <-fim
INTEIRO     → inteiro
TEXTO       → texto
DECIMAL     → decimal
LOGICO      → logico
SE          → se
SENAO       → senao
ENQUANTO    → enquanto
DURANTE     → durante
ENTRADA     → entrada
SAIDA       → saida
VERDADEIRO  → verdade
FALSO       → falso

ID              → [a-zA-Z_][a-zA-Z0-9_]*
NUMERO_INT      → [0-9]+ 
NUMERO_DECIMAL  → [0-9]+\.[0-9]+ 
LITERAL_TEXTO   → "[^"]*"
IGUAL           → ==
DIFERENTE       → <>
MAIOR           → >
MENOR           → <
MAIOR_IGUAL     → >=
MENOR_IGUAL     → <=
E_LOGICO        → &&
OU_LOGICO       → ||
NAO_LOGICO      → !!
COMENTARIO_LINHA → //--.*--//

PONTO_VIRGULA → ;
ABRE_PARENTESE  → (
FECHA_PARENTESE → )
ABRE_CHAVE      → {
FECHA_CHAVE     → }
ASPAS           → "

MAIS        → +
MENOS       → -
VEZES       → *
DIVISAO     → /
OP_ATRIBUICAO → <<
OP_DECLARACAO → :
```

## GLC

```
S → programa

programa → INICIO lista_comandos FIM

lista_comandos → comando
               | comando lista_comandos


comando → declaracao
         | atribuicao
         | declaracao_atribuicao
         | condicional
         | laco
         | saida


declaracao → tipo OP_DECLARACAO ID PONTO_VIRGULA

declaracao_atribuicao → tipo OP_DECLARACAO ID OP_ATRIBUICAO expressao PONTO_VIRGULA

tipo → INTEIRO
      | TEXTO
      | DECIMAL
      | LOGICO


atribuicao → ID OP_ATRIBUICAO expressao PONTO_VIRGULA

atribuicao_loop → ID OP_ATRIBUICAO expressao


expressao → termo
          | expressao op_aditivo termo

expressao_relacional → expressao
                    | expressao op_comparacao expressao

condicao → termo_logico
      | condicao OU_LOGICO termo_logico

termo_logico → fator_logico
          | termo_logico E_LOGICO fator_logico

fator_logico → NAO_LOGICO fator_logico
          | ABRE_PARENTESE condicao FECHA_PARENTESE
          | expressao_relacional
          | booleano

termo → fator
      | termo op_multiplicativo fator

fator → ID
      | numero
      | LITERAL_TEXTO
      | booleano
      | entrada
      | MENOS fator
      | ABRE_PARENTESE expressao FECHA_PARENTESE

numero → NUMERO_INT
       | NUMERO_DECIMAL

booleano → VERDADEIRO
         | FALSO

op_aditivo → MAIS | MENOS

op_multiplicativo → VEZES | DIVISAO

op_comparacao → IGUAL
              | DIFERENTE
              | MAIOR
              | MENOR
              | MAIOR_IGUAL
              | MENOR_IGUAL


condicional → SE ABRE_PARENTESE condicao FECHA_PARENTESE ABRE_CHAVE lista_comandos FECHA_CHAVE bloco_senao

bloco_senao → SENAO ABRE_CHAVE lista_comandos FECHA_CHAVE
            | ε


laco → enquanto
     | durante

enquanto → ENQUANTO ABRE_PARENTESE condicao FECHA_PARENTESE ABRE_CHAVE lista_comandos FECHA_CHAVE

durante → DURANTE ABRE_PARENTESE atribuicao_loop PONTO_VIRGULA condicao PONTO_VIRGULA atribuicao_loop FECHA_PARENTESE ABRE_CHAVE lista_comandos FECHA_CHAVE


entrada → ENTRADA ABRE_PARENTESE FECHA_PARENTESE
      | ENTRADA ABRE_PARENTESE LITERAL_TEXTO FECHA_PARENTESE

saida → SAIDA ABRE_PARENTESE expressao FECHA_PARENTESE PONTO_VIRGULA
```

## Como Rodar

- Requisitos:
  - JDK instalado

Rode o comando:

``` bash
java src/Main.java <caminho_do_arquivo_de_entrada> [--lang=kotlin|java] [--ast=tree|preorder|code] [--tokens] [--run]
```

**Flags:**
- `--lang`: Especifica a linguagem de saída, podendo ser Kotlin (padrão) ou Java.
- `--ast`: Especifica o tipo de representação da árvore sintática a ser exibida. As opções são `tree` (padrão), `preorder` e `code`.
- `--tokens`: Exibe a lista de tokens gerados durante a análise léxica.
- `--run`: Compila e executa automaticamente o código gerado.

Depois disso, no mesmo lugar onde está o arquivo de entrada, será gerado um arquivo com a extensão `.kt` ou `.java`, dependendo da linguagem escolhida.

Então, basta compilar e rodar o arquivo gerado para ver o resultado.

## Exemplos de códigos

- Declaração de variável
```
inicio->

inteiro : contador << 0;
logico : isTrue << falso;
texto : nome << "jose";

<-fim
```

- Condicional (if else)

```
inicio->

inteiro : idade;
idade << entrada("Digite sua idade");

se (idade >= 18) {
      saida("Maior de idade");
} senao {
      saida("Menor de idade");
}

<-fim
```

- Laço de repetição

```
inicio->

inteiro : i << 0;

enquanto (i < 5) {
      saida(i);
      i << i + 1;
}

<-fim
```

## Traduções equivalentes

### KOTLIN

- Declaração de variável

```
var contador: Int = 0
var isTrue: Boolean = false
var nome: String = "jose"
```

- Condicional (if else)

```
var idade: Int = 0
idade = run {
    print("Digite sua idade");
    readln()
}.toInt()
if (idade >= 18) {
    println("Maior de idade")
} else {
    println("Menor de idade")
}
```

- Laço de repetição

```
var i: Int = 0
while (i < 5) {
    println(i)
    i = i + 1
}
```

### JAVA

- Declaração de variável

```
int contador = 0;
boolean isTrue = false;
String nome = "jose";
```

- Condicional (if else)

```
Scanner scanner = new Scanner(System.in); 

int idade;
idade = scanner.nextInt();

if (idade >= 18) {
    System.out.println("Maior de idade");
} else {
    System.out.println("Menor de idade");
}
```

- Laço de repetição

```
int i = 0;

while (i < 5) {
    System.out.println(i);
    i = i + 1;
}
```

### PYTHON

- Declaração de variável

```
contador: int = 0
isTrue: bool = False
nome: str = "jose"
```

- Condicional (if else)

```
idade: int = None
idade = input("Digite sua idade")

if idade >= 18:
    print("Maior de idade")
else:
    print("Menor de idade")
```

- Laço de repetição

```
i: int = 0

while i < 5:
    print(i)
    i = i + 1
```

### C

- Declaração de variável

```
int contador = 0;
bool isTrue = false;
char nome[] = "jose";
```

- Condicional (if else)

```
int idade;
scanf("%d", &idade);

if (idade >= 18) {
    printf("Maior de idade");
} else {
    printf("Menor de idade");
}
```

- Laço de repetição

```
int i = 0;

while (i < 5) {
    printf(i);
    i = i + 1;
}
```