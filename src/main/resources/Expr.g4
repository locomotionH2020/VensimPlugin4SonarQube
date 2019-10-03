grammar Expr;




expr:   call             # CallExpr
    |   lookupCall # exprLookupCall
    |   expr '^' expr                     # Power
    |   expr op=('*'|'/') expr            # MulDiv
    |   expr op=(Plus|Minus) expr            # AddSub
    |   expr op=(Less|Greater|LessEqual|GreaterEqual) expr  # Relational
    |   expr op=(Equal|NotEqual) expr           # Equality
    |   expr ':AND:' expr                 # And
    |   expr ':OR:' expr                  # Or
    |   Id (subscript)?                   # Var
    |   constVensim                             # const
    |   Keyword expr?                        # Keyword
    |   lookup                            # LookupArg
    |   '(' expr ')'                      # Parens
    |    Star                             # WildCard   
    |   expr ':' expr                     # DelayPArg
    |  'TABBED ARRAY(' constVensim* ')'   # tabbedArray
    |   sign expr                          # signExpr
    ;


call:  Id '(' exprList? ')';
macroHeader: Id '(' macroArguments? ')';  
macroArguments: exprList (':' exprList)?;
lookupCall: Id (subscript)? '(' (expr  | numberList) ')' ;
exprList : expr (',' expr)* ;
subscriptIdList : (subscriptId|subscriptSequence) (',' (subscriptId|subscriptSequence))* ;
subscript: '[' subscriptId (',' subscriptId)* ']'; 
lookup : '(' ((lookupRange? lookupPointList)|numberList|expr )')' ;
lookupRange : '[' lookupPoint Minus lookupPoint referenceLine? ']' ',' ;
lookupPointList : lookupPoint (',' lookupPoint)* ;
referenceLine: ',' lookupPointList;
lookupPoint : '(' expr ',' expr ')' ;
constList : ( expr ( ',' expr )+ | ( expr ( ',' expr )+ ';' )+ ) ;

numberList: (integerConst | floatingConst) (',' ( integerConst | floatingConst))*;
    
    

Star : '*' ;
Div : '/' ;
Plus : '+' ;
Minus: '-';
Less : '<' ;
LessEqual : '<=' ;
Greater : '>' ;
GreaterEqual : '>=' ;
Equal : '=' ;
TwoEqual : '==' ;
NotEqual : '<>' ;
Exclamation : '!' ;
DataEquationOp: ':=';
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
    :   integerConst
    |   floatingConst
    |   StringConst
    ;


integerConst
    :  sign? DigitSeq
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;


floatingConst
:  sign? fractionalConstant exponentPart?
|   sign? DigitSeq exponentPart
;


fractionalConstant
    :   DigitSeq? '.' DigitSeq
    |   DigitSeq '.'
    ;


exponentPart
    :   'e' sign? DigitSeq
    |   'E' sign? DigitSeq
    ;

sign
    :   Plus | Minus
    ;


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




unitsDoc : COMMENTS ;
COMMENTS: '~' .*? '|';