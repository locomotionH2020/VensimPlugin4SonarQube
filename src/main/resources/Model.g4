grammar Model;




// A Vensim model is a sequence of equations and subscript ranges.

file: model EOF;
model: ( lookupDefinition | subscriptRange | equation |constraint | macroDefinition | unchangeableConstant |
       dataEquation| stringAssign |subscriptCopy |realityCheck)* sketches;

// A subscript range definition names subscripts in a dimension.
subscriptRange : Id ':' ( subscriptIdList| call) subscriptMappingList? unitsDoc;
subscriptSequence : '(' Id '-' Id ')' ;
subscriptMappingList : '->' subscriptMapping ( ',' subscriptMapping )* ;
subscriptMapping : Id | '(' Id ':' subscriptIdList ')' ;

// An equation has a left-hand side and a right-hand side.
// The RHS is a formula expression, a constant list, or a Vensim lookup.
// The RHS is empty for data equations.
equation : lhs  Equal (expr|constList)  (':IGNORE:' exprList)? unitsDoc;
lhs : Id ( subscript )? Keyword? ( ':EXCEPT:' subscript ( ',' subscript )* )? ;


// https://www.vensim.com/documentation/ref_subscript_mapping.htm
subscriptCopy: Id '<->' Id unitsDoc;
unchangeableConstant: lhs TwoEqual ( expr | constList ) unitsDoc;
dataEquation: lhs ( DataEquationOp ( expr | constList ) )? (':IGNORE:' exprList)? unitsDoc;
lookupDefinition: lhs (lookup|'('call')') unitsDoc;


constraint: Id ':THE CONDITION:' expr? ':IMPLIES:' expr unitsDoc;
realityCheck: Id subscript? ':TEST INPUT:' expr unitsDoc;

stringAssign: lhs StringAssignOp StringConst  (':IGNORE:' exprList)? unitsDoc;
macroDefinition: ':MACRO:' macroHeader equation+ ':END OF MACRO:';



// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.

CommentOrEncoding: '{' .*? '}' -> skip;

Group : '****' .*? '|' -> skip ;


// Backslash tokens are ignored in Expr.g4, so this rule doesn't take them into account.
sketchInfo: '---///' 'Sketch information - do not modify anything except names' .*? ;
sketches: sketchInfo*;





























expr:   call             # CallExpr
    |   expr op=('^'|'*'|'/'|'-'|'+'|Less|Greater|LessEqual|GreaterEqual|Equal|NotEqual| ':AND:' | ':OR:') expr  # exprOperation
    |   Id (subscript)?                   # Var
    |   constVensim                             # const
    |   Keyword expr?                        # Keyword
    |   lookup                            # LookupArg
    |   '(' expr ')'                      # Parens
    |    Star                             # WildCard   
    |   'DELAYP(' expr  ',' expr ':' Id ')'                     # DelayPArg
    |  'TABBED ARRAY(' constVensim* ')'   # tabbedArray
    |   ('-'|'+') expr                          # signExpr
    ;


call:  Id (subscript)? '(' exprList? ')';


macroHeader: Id '(' macroArguments? ')';  
macroArguments: exprList (':' exprList)?;
exprList : expr (',' expr)* ;
subscriptIdList : (subscriptId|subscriptSequence) (',' (subscriptId|subscriptSequence))* ;
subscript: '[' subscriptId (',' subscriptId)* ']'; 
lookup : '(' ((lookupRange? lookupPointList)|numberList) ')' ;
lookupRange : '[' lookupPoint '-' lookupPoint referenceLine? ']' ',' ;
lookupPointList : lookupPoint (',' lookupPoint)* ;
referenceLine: ',' lookupPointList;
lookupPoint : '(' expr ',' expr ')' ;
constList : ( expr ( ',' expr )+ | ( expr ( ',' expr )+ ';' )+ ) ;

numberList: (integerConst | floatingConst) (',' ( integerConst | floatingConst))*;
    
    

Star : '*' ;
Div : '/' ;
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
    :  ('+'|'-')? DigitSeq
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;


floatingConst
:  ('+'|'-')?? FractionalConstant ExponentPart?
|   ('+'|'-')?? DigitSeq ExponentPart
;


FractionalConstant
    :   DigitSeq? '.' DigitSeq
    |   DigitSeq '.'
    ;


ExponentPart
    :   'e' [+-]? DigitSeq
    |   'E' [+-]? DigitSeq
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