grammar Model;

// A Vensim model is a sequence of equations and subscript ranges.

file: model EOF;
model: ( symbolWithDoc | macroDefinition)* sketchesGraphsAndMetadata?;
sketchesGraphsAndMetadata: sketches graphsGroup metadataDivisor; //Separating equations and sketches&graphs allows to test sample files with just a few lines.
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

graphsGroup: graphs*;
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
metadataDivisor: ':L<%^E!@' metadataLine+;
metadataLine:DigitSeq':'.*?;

// Backslash tokens are ignored, so this rule doesn't take them into account.
sketches: viewInfo* sketchesDelimiter;
sketchesDelimiter: SketchesDelimiter;
viewInfo:   sketchInfo versionCode viewName viewVariables;
sketchInfo: '---///' 'Sketch information - do not modify anything except names' ;
versionCode: 'V300  Do not put anything below this section - it will be ignored';
//Vensim versions 5,4 and 3 all use the same version code (300).
viewName: '*' .*?; //All view names are preceeded by an '*'
/**
All the information is at https://www.vensim.com/documentation/_mdl_model_files.htm
viewSettings have the following syntax:
1- (color) default color of the initial arrow.
2- (integerConst) must be 0.
3- (typography) default typography of the view.
4- (ppix) pixel per inch in x.
5- (ppiy) pixel per inch in y.
6- (zoom) zoom value.
7- (tf) template flag 0=normal, 1=dont use, 3= template view.
**/
viewSettings: '$'  color  ',' integerConst ',' typography ppix=integerConst ','ppiy=integerConst ',' zoom=integerConst ','tf=integerConst;
viewVariables: viewSettings (arrow | viewVariable)*;

/**
arrow have the following syntax:
1-  (internalId) id for the type of view object of vensim, arrow=1.
2-  (idInView) object id of the view, correlative number from 0.
3-  (fromVariable) idInView of the variable at the starting point of the arrow.
4-  (toVariable) idInView of the variable being pointed by the arrow.
5-  (arrowShape) arrow head type.
6-  (hidden) hide level from 0=unhide to n=hide level n<=16.
7-  (polarityChar) ASCII of the char being displayed as polarity.
8-  (thickness) line thickness.
9-  (hasFont) enumSet indicates usage of default data or custom like typography.
10- (delayType) Indicates delay marking of the arrow.
11- (integerConst) should be 0.
12- (color) color of the arrow.
13- (typography) typography of the polarity mark.
14- (numberOfPoints) number of intermediate points in the arrow, minimun is 1.
15- (arrowCoordinates) for eech intermediate point, its coordinates.
**/
arrow: internalId=integerConst ',' idInView=integerConst ','fromVariable=integerConst ','toVariable=integerConst ','arrowShape=integerConst ','hidden=integerConst ','polarityChar=integerConst ','thickness=integerConst ','hasFont=integerConst ','delayType=integerConst ','integerConst ','color ','typography? ',' numberOfPoints=integerConst '|' (arrowCoordinates '|')+;
arrowCoordinates: '('integerConst','integerConst')';

/**
viewVariable have the following syntax:
1-  (internalId) id for the type of view object of vensim, variable=10 valves=11 comment&ioObject=12  bitmaps=30 metafiles=31.
2-  (idInView) object id of the view, correlative number from 0.
3-  it is differect depending of the type of object:
      a) if variable: (name) the name of the variable.
      b) if comment: (name) of the bytemap to load or (integerCoonst) of a index to display as bytemap.
      c) anything else: (integerConst) unused.
4-  (x) horizontal position.
5-  (y) vertical position.
6-  (width) width of the variable.
7-  (height) height of the variable.
8-  (shape) outer line style.
9-  (bits) store a lot of information about the type of the object in a enumSet stored in base-10.
      *bit counting done with LSB*
        0th bit: let arrows in.
        1st bit: let arrows out.
        2nd bit: sets if next line have the text for the comment (1) / or not (0).
        3th bit: indicate if obect is IO.
        6th bit: difference setting "no cause" (1) / or not (0)
        7th bit: difference between variable having it's standar size (0) / have been resized (1).
10- (hidden)  hide level from 0=unhide to n=hide level n<=16. 5th bit sets comments that should only appear in that exact level
11- (hasFont) enumSet indicates usage of default data or custom like typography.
12- (textPos) position of the variable name or if IoObject: 0=slider 1=custom graph 3=workbench tool.
13- (boxWidth) thickness of the outer box.
14- (nav1) sketch number to navigate, also see nav2.
15- (nav2) special numver for navigation, if 251: nav1 = 0 go to previous sketch, nav = 1 go to next sketch.
16- (boxColor) color of the shape.
17- (fillColor) color of the fill.
18- (typhography) thypography of the variable name.
19/24- (integerConst) unknown.
25- (visualInfo) extra info written in the next line for example in comments or IoObjects.
**/
viewVariable:  internalId=integerConst ',' idInView=integerConst ',' (name=Id | integerConst)  ',' x=integerConst ',' y=integerConst ',' width=integerConst ',' height=integerConst ',' shape=integerConst ',' bits=integerConst ',' hidden=integerConst ',' hasFont=integerConst ',' textPos=integerConst ',' boxWidth=integerConst ',' nav1=integerConst ',' nav2=integerConst (','boxColor=color ',' fillColor=color ',' typography)? (',' integerConst ',' integerConst ',' integerConst ',' integerConst ',' integerConst ',' integerConst)? visualInfo;

visualInfo:  .*?;

/**
typography have the following syntax:
1- (typographyName) name of the typhography.
2- (fontSize) size of the font.
3- (textFormat) attributes like bolde, underline, etc.
4- (fontColor) color of the font.
4- (shapeColor) color of the shape.
4- (arrowColor) color of the arrows.
4- (fillColor) color of the filling.
4- (background Color) color of the background .
**/
typography: typographyName?'|' fontSize=integerConst'|' textFormat '|' fontColor=color ('|' shapeColor=color '|' arrowColor=color '|' fillColor=color '|' backgroundColor=color '|')?;
typographyName: '@'? Id;
textFormat: 'B'?'I'?'U'?'S'?'V'?'D'?;

color: rgbColor | singleColor;
rgbColor: integerConst '-' integerConst '-' integerConst;
singleColor: integerConst;


subscriptId : Id  Exclamation?;

constVensim
    :   integerConst
    |   floatingConst
    |   StringConst
    ;


integerConst
    :  ('+'|'-')* DigitSeq
    ;


floatingConst:
    ('+'|'-')* FloatingConstNumber;



 unitsDoc: units=INFO_UNIT comment=INFO_UNIT supplementary=INFO_UNIT?'|';

// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.

CommentOrEncoding: '{' .*? '}' -> skip;

Group : '********************************************************' .*? '|' -> skip ;


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

Id: ( ( Nondigit  IdChar*  ) | ( Nondigit ( IdChar | ' ' )* IdChar ) |StringLiteral);

fragment
IdChar : [a-zA-Z0-9_$'"&%\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;

fragment
IdCharWithDash : [a-zA-Z0-9_$'"&%\-\u00A1-\u00ff\u0100-\u017f\u0180-\u024f\u1e02-\u1ef3] ;

fragment
NonzeroDigit
    :   [1-9]
    ;

fragment
Nondigit : [a-zA-Z_] ;

fragment
Digit
    :   [0-9]
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

SketchesDelimiter: '///---';