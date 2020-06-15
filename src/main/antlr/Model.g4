grammar Model;




// A Vensim model is a sequence of equations and subscript ranges.

file: model EOF;
model: ( symbolWithDoc | macroDefinition)* sketches;


symbolWithDoc: symbolWithDocDefinition unitsDoc;

symbolWithDocDefinition: ( lookupDefinition | subscriptRange | equation |constraint  | unchangeableConstant |
                         dataEquation| stringAssign |subscriptCopy |realityCheck); 


// A subscript range definition names subscripts in a dimension.
subscriptRange : Id ':' (  subscriptSequence| subscriptValueList |call) subscriptMappingList? ;
subscriptSequence : '(' start=Id '-' end=Id ')' ;
subscriptMappingList : '->' subscriptMapping ( ',' subscriptMapping )* ;
subscriptMapping : Id | '(' Id ':' indexList ')' ;

// An equation has a left-hand side and a right-hand side.
// The RHS is a formula expression, a constant list, or a Vensim lookup.
// The RHS is empty for data equations.
equation : lhs  Equal (expr|constList)  (':IGNORE:' exprList)? ;
lhs : Id ( indexes=subscript )? Keyword? ( ':EXCEPT:' subscript ( ',' subscript )* )? ;


// https://www.vensim.com/documentation/ref_subscript_mapping.htm
subscriptCopy: copy=Id '<->' original=Id ;
unchangeableConstant: lhs TwoEqual (constList|call|Keyword) ;
dataEquation: lhs ( DataEquationOp ( expr | constList ) )? (':IGNORE:' exprList)? ;

lookupDefinition: lhs (lookup|('('(call|numberList)')')) ;
// lookup numberlist format: accomplishments per hour lookup(0,0.2,0.4,0.6,0.8,1,
//                                                          0,0.2,0.4,0.6,0.8,1)
// The call is needed for direct and xls lookups. For example: historic_demand_lt( GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' ))~~|


constraint: lhs ':THE CONDITION:' expr? ':IMPLIES:' expr ;
realityCheck: lhs ':TEST INPUT:' expr ;


stringAssign: lhs StringAssignOp StringConst  (':IGNORE:' exprList)? ;
macroDefinition: ':MACRO:' macroHeader symbolWithDoc+ ':END OF MACRO:';



// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.

CommentOrEncoding: '{' .*? '}' -> skip;

Group : '********************************************************' .*? '|' -> skip ;


// Backslash tokens are ignored, so this rule doesn't take them into account.
sketchInfo: '---///' 'Sketch information - do not modify anything except names' .*? ;
sketches: sketchInfo*;





























expr:     expr op=('^'|'*'|'/'|'-'|'+'|Less|Greater|LessEqual|GreaterEqual|Equal|NotEqual| ':AND:' | ':OR:') expr  # exprOperation
    |   constVensim                             # const
    |   Keyword expr?                        # Keyword
    |   lookup                            # LookupArg
    |    Star                             # WildCard
    |   ('-'|'+')* exprAllowSign      # signExpr
    ;

exprAllowSign:
     call             # CallExpr
     |'DELAYP(' expr  ',' expr ':' Id ')'                     # DelayPArg
     | Id (subscript)?                   # Var
     |  'TABBED ARRAY(' constVensim* ')'   # tabbedArray
     |     '(' expr ')'                      # Parens
     ;


call:  Id (subscript)? '(' exprList? ')';


macroHeader: Id '(' macroArguments? ')';  
macroArguments: exprList (':' exprList)?;
exprList : expr (',' expr)* ;
subscriptValueList : (subscriptId|subscriptSequence) (',' (subscriptId|subscriptSequence))* ;
indexList: subscriptId (',' subscriptId)*;
subscript: '[' indexList ']'; 
lookup : '(' (lookupRange? lookupPointList) ')' ;


lookupRange : '[' lookupPoint '-' lookupPoint referenceLine? ']' ',' ;
lookupPointList : lookupPoint (',' lookupPoint)* ;
referenceLine: ',' lookupPointList;
lookupPoint : '(' constVensim ',' constVensim ')' ;
constantLine: ( constVensim ( ',' constVensim)*) ;
// Optional semi colon is required for two dimensional arrays https://www.vensim.com/documentation/22070.htm
constList : constantLine ((';' constantLine)* ';')?;

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
    :  ('+'|'-')* DigitSeq
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;


floatingConst:
    ('+'|'-')* FloatingConstNumber;
FloatingConstNumber
: FractionalConstant ExponentPart?
| DigitSeq ExponentPart
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
    :   ["](~["\\]|[\\].)*?["]
    ;

StringConst
    :    ['](~['\\]|[\\].)*?[']
    ;


Keyword
    :   ':'[a-zA-Z ]*':'
    ;

Whitespace : [ \t\n\r]+ -> skip ;
// Backslashes are used as line continuators, so they can be ignored.
Backslash: [\\] -> skip;
INFO_UNIT: '~' ~('~'|'|')*;
OtherCaracter: .;




 unitsDoc: units=INFO_UNIT comment=INFO_UNIT supplementary=INFO_UNIT?'|';
