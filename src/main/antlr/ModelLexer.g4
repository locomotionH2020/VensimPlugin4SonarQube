lexer grammar ModelLexer;

channels{
    VIEWS
}

NewLine: [\n\r]+ -> channel(VIEWS);

// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.

CommentOrEncoding: '{' .*? '}' -> skip;

GroupDelimiter : '***' Star+;
GroupEndDelimiter : GroupDelimiter '~';

Star : '*' ;
Div : '/' ;
Pow : '^';
Minus: '-';
Plus: '+';
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
TwoDots: ':';
OpenBracket: '(';
CloseBracket: ')';
OpenSquareBracket: '[';
CloseSquareBracket: ']';
RightArrow: '->';
TwoSidesArrow: '<->';
Comma: ',';
Semicolon: ';';
VerticalBar: '|';
Dolar: '$';
At: '@';

Ignore: ':IGNORE:';
Except:':EXCEPT:';
Id: (Nondigit (( IdChar | ' ' )* IdChar)?)| (Digit+ NondigitIdChar (( IdChar | ' ' )* IdChar )?) | StringLiteral;

//TODO una comilla hace petar también.
fragment
IdChar : [a-zA-Z0-9_$'&%\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;

fragment
NondigitIdChar : [a-zA-Z_$'&%\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;
fragment
Nondigit : [a-zA-Z_] ;

fragment
Digit
    :   [0-9]
    ;


fragment
NonzeroDigit
    :   [1-9]
    ;

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


Whitespace : [ \t]+ -> skip;
// Backslashes are used as line continuators, so they can be ignored.
Backslash: [\\] -> skip;
INFO_UNIT: '~' ~('~'|'|')*;
OtherCaracter: .;


SketchesDelimiter: '///---';



Condition : ':THE CONDITION:' ;
Implies : ':IMPLIES:' ;
Test_input : ':TEST INPUT:' ;
Macro : ':MACRO:' ;
EndMacro : ':END OF MACRO:' ;
And : ':AND:' ;
Or : ':OR:' ;
Delayp : 'DELAYP(' ;
Tabbed_array : 'TABBED ARRAY(' ;
Graph : ':GRAPH' ;
Title : ':TITLE' ;
Xaxis : ':X-AXIS' ;
Xlabel : ':X-LABEL' ;
Xdiv : ':X-DIV' ;
Yaxis : ':Y-AXIS' ;
Ylabel : ':Y-LABEL' ;
Ydiv : ':Y-DIV' ;
Xmin : ':X-MIN' ;
Xmax : ':X-MAX' ;
No_legend : ':NO-LEGEND' ;
Scale : ':SCALE' ;
Var : ':VAR' ;
Ymin : ':Y-MIN' ;
Ymax : ':Y-MAX' ;
Line_width : ':LINE-WIDTH' ;
Metada_separator : ':L<%^E!@' ;
ViewDelimier : '---///' ;
Sketch_phrase : 'Sketch information - do not modify anything except names' ;
Sketch_version : 'V300  Do not put anything below this section - it will be ignored' ;


Keyword
    :   ':'[a-zA-Z ]*':'
    ;
