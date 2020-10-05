grammar Model;




// A Vensim model is a sequence of equations and subscript ranges.

file: model EOF;
model: ( symbolWithDoc | macroDefinition)* sketchesGraphsAndMetadata?;
sketchesGraphsAndMetadata: sketches graphs* metadata; //Separating equations and sketches&graphs allows to test sample files with just a few lines.
                                                                      //For example, a problematic equation.
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
    
graphs: graph title xaxis? xlabel? xdiv? yaxis? ylabel? ydiv? xmin? xmax? nolegend? scale graphvar*;
graph: ':GRAPH' .*?;
title: ':TITLE' .*?;
xaxis: ':X-AXIS' .*?;
xlabel: ':X-LABEL' Id;
xdiv: ':X-DIV' DigitSeq;
yaxis: ':Y-AXIS' .*?;
ylabel: ':Y-LABEL' Id;
ydiv: ':Y-DIV' DigitSeq;
xmin: ':X-MIN' .*?;
xmax: ':X-MAX' .*?;
nolegend: ':NO-LEGEND' DigitSeq;
scale: ':SCALE';
graphvar: gvar ymin? ymax? linewidthgraph? scale?;
gvar: ':VAR' .*?;
ymin: ':Y-MIN' .*?;
ymax: ':Y-MAX' .*?;
linewidthgraph: ':LINE-WIDTH' .*?;
metadata: ':L<%^E!@' metadataLine+;
metadataLine:DigitSeq':'.*?;
  
// Backslash tokens are ignored, so this rule doesn't take them into account.
sketches: viewInfo* sketchesDelimiter;
sketchesDelimiter: '///---';
viewInfo:   sketchInfo versionCode viewName viewVariables;
sketchInfo: '---///' 'Sketch information - do not modify anything except names' ;
versionCode: 'V300  Do not put anything below this section - it will be ignored'; 
//Vensim versions 5,4 and 3 all use the same version code (300).
viewName: '*' .*?; //All view names are preceeded by an '*'
viewSettings: '$' ('-'|DigitSeq)* ',' (Id|'-'|DigitSeq)* ',' (Id|'-'|DigitSeq)* '|' (Id|'-'|DigitSeq)* '|' //REVISAR PARA CLARIFICARLO
    (Id|'-'|DigitSeq)* '|' (Id|'-'|DigitSeq)* '|' (Id|'-'|DigitSeq)* '|' (Id|'-'|DigitSeq)* '|' (Id|'-'|DigitSeq)* '|' 
    ((Id|'-'|DigitSeq)* '|')? (DigitSeq ',')? (DigitSeq ',')? (DigitSeq ',')? (DigitSeq)?; //USUALLY, The settings of each view always will have 2 commas separating
                                                                                                            //fields, then 8 '|' and then again 3 commas.
                                                                                                            //Sometimes, some fields are not necessary.
viewVariables: viewSettings (arrow|shadowVariable|textVariable|rawText|objectVariable)*;


shadowVariable: (integerConst) (','(rawTextObjects|Id|integerConst|floatingConst|(DigitSeq'-'DigitSeq'-'DigitSeq)|('-'DigitSeq'-''-'DigitSeq'-''-'DigitSeq)))* lastShadowPart;
                                 //Variables that do not belong to any view and do not depend on any other variables. Besides, other variables can depend on shadow variables.
lastShadowPart: ',' '|'(integerConst|floatingConst)'|'(DigitSeq|Id)*'|'(DigitSeq'-'DigitSeq'-'DigitSeq);

textVariable: (integerConst) (','(rawTextObjects|Id|integerConst|floatingConst|(DigitSeq'-'DigitSeq'-'DigitSeq)|('-'DigitSeq'-''-'DigitSeq'-''-'DigitSeq)))* lastTextVarPart; 
                                                                                                //Object variables that its format has been modified(font, color...)
lastTextVarPart: '|'(integerConst|floatingConst)'|'(DigitSeq|Id)*'|'(DigitSeq'-'DigitSeq'-'DigitSeq);

objectVariable: (integerConst) (','(integerConst|floatingConst|rawTextObjects))*; //Variables, Valves, Comments, Bitmaps and Metafiles will have an undetermined
                                                                                       //set of fields, always separated by commas.
arrow: DigitSeq ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','
    (Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','
    (Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(Id|'-'|DigitSeq)* ','(points);  //Arrows always will have 13 fields and a last field that contains 
                                                                                                        //the number of points of the object and where they are located.
points: DigitSeq ('|''('integerConst','integerConst')')+'|';
rawText: ('\''|'"'|Id|StringConst|'.'|'-'|'+'|'='|Less|Greater|'('|')'|'->'|Star|Div|'?'|'!'|'|'|'&'|'%'|'$'|'@'|':'|';'|','|'['|']'|link)+; 
 //Symbols that may affect the grammar. Those are contained in comments or variable names. They must be controlled.
rawTextObjects: ('\''|Id|StringConst|'.'|'-'|'+'|'='|Less|Greater|'('|')'|'->'|Star|Div|'?'|'!'|'|'|'&'|'%'|'$'|'@'|':'|';'|'['|']'|link)+; 
 //Symbols that may affect the grammar. Those are contained in objects. It cannot contain any commas. They must be controlled.
link: ('http://'|'https://'|': https://'| ': http://') .*?;


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
IdChar : [a-zA-Z0-9_$'"&%\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;


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
    :   ["](~["\\]|[\\].)*?["\r\n]
    ;

StringConst
    :    ['](~['\\]|[\\].)*?['\r\n]
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
