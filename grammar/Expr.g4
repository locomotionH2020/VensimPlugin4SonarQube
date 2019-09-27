grammar Expr;

expr:   Id '(' exprList? ')'              # Call
    |   lookupCall # exprLookupCall
    |   '-' expr                          # Negative
    |   '+' expr                          # Positive
    |   expr '^' expr                     # Power
    |   expr op=('*'|'/') expr            # MulDiv
    |   expr op=('+'|'-') expr            # AddSub
    |   expr op=('<'|'>'|'<='|'>=') expr  # Relational
    |   expr op=('='|'<>') expr           # Equality
    |   expr ':AND:' expr                 # And
    |   expr ':OR:' expr                  # Or
    |   Id (subscript)?                   # Var
    |   constVensim                             # const
    |   Keyword expr?                        # Keyword
    |   lookup                            # LookupArg
    |   '(' expr ')'                      # Parens
    |    Star                             # WildCard       
    ;

macroHeader: Id '(' macroArguments? ')';  
macroArguments: exprList (':' exprList)?;
lookupCall: Id (subscript)? '(' expr ')' ;
exprList : expr (',' expr)* ;
subscriptIdList : subscriptId (',' subscriptId)* ;
subscript: '[' subscriptId (',' subscriptId)* ']'; 
lookup : '(' lookupRange? lookupPointList ')' ;
lookupRange : '[' lookupPoint '-' lookupPoint referenceLine? ']' ',' ;
lookupPointList : lookupPoint (',' lookupPoint)* ;
referenceLine: ',' lookupPointList;
lookupPoint : '(' expr ',' expr ')' ;
constList : ( expr ( ',' expr )+ | ( expr ( ',' expr )+ ';' )+ ) ;

Star : '*' ;
Div : '/' ;
Plus : '+' ;
Minus : '-' ;
Less : '<' ;
LessEqual : '<=' ;
Greater : '>' ;
GreaterEqual : '>=' ;
Equal : '=' ;
TwoEqual : '==' ;
NotEqual : '<>' ;
Exclamation : '!' ;
EquationOp: ':=';
StringAssignOp: ':IS:';


subscriptId : Id  Exclamation?;
Id: ( ( Nondigit IdChar*  ) | ( Nondigit ( IdChar | ' ' )* IdChar ) | StringLiteral );

fragment
IdChar : [a-zA-Z0-9_$'&%\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;


fragment
Nondigit : [a-zA-Z_] ;

fragment
Digit
    :   [0-9]
    ;

constVensim
    :   IntegerConst
    |   FloatingConst
    |   StringConst
    ;


IntegerConst
    :   Digit+
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;


FloatingConst
    :   FractionalConstant ExponentPart?
    |   DigitSeq ExponentPart
    ;

fragment
FractionalConstant
    :   DigitSeq? '.' DigitSeq
    |   DigitSeq '.'
    ;

fragment
ExponentPart
    :   'e' Sign? DigitSeq
    |   'E' Sign? DigitSeq
    ;

fragment
Sign
    :   '+' | '-'
    ;

fragment
DigitSeq
    :   Digit+
    ;

StringLiteral
    :   ["](~["\\]|[\\].)*["]
    ;

StringConst
    :    ['](~['\\]|[\\].)*[']
    ;


Keyword
    :   ':'[a-zA-Z ]*':'
    ;

Whitespace : [ \t\n\r]+ -> skip ;
// Backslashes are used as line continuators, so they can be ignored.
Backslash: [\\] -> skip;
OtherCaracter: .;


unitsDoc : UNITOS ;
UNITOS: '~' .*? '|';