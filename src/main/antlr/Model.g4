parser grammar Model;

options {
  superClass=MultiChannelBaseParser;
  tokenVocab = Tokens;
}

// A Vensim model is a sequence of equations and subscript ranges.

file: model EOF;
model: {disable(Tokens.VIEWS);}( symbolWithDoc | macroDefinition)* sketchesGraphsAndMetadata;
sketchesGraphsAndMetadata: sketches? graphsGroup? metadataDivisor?; //Separating equations and sketches&graphs allows to test sample files with just a few lines.
                                                                      //For example, a problematic equation.
symbolWithDoc: symbolWithDocDefinition unitsDoc;

symbolWithDocDefinition: ( lookupDefinition | subscriptRange | equation |constraint  | unchangeableConstant |
                         dataEquation| stringAssign |subscriptCopy |realityCheck);


// A subscript range definition names subscripts in a dimension.
subscriptRange : Id TwoDots (  subscriptSequence| subscriptValueList |call) subscriptMappingList? ;
subscriptSequence : OpenBracket start=Id  Minus end=Id  CloseBracket ;
subscriptMappingList : RightArrow subscriptMapping (  Comma subscriptMapping )* ;
subscriptMapping : Id | OpenBracket Id TwoDots indexList  CloseBracket ;

// An equation has a left-hand side and a right-hand side.
// The RHS is a formula expression, a constant list, or a Vensim lookup.
// The RHS is empty for data equations.
equation : lhs  Equal (expr|constList)  (Ignore exprList)? ;
lhs : Id ( indexes=subscript )? Keyword? ( Except subscript (  Comma subscript )* )? ;


// https://www.vensim.com/documentation/ref_subscript_mapping.htm
subscriptCopy: copy=Id TwoSidesArrow original=Id ;
unchangeableConstant: lhs TwoEqual (constList|call|Keyword) ;
dataEquation: lhs ( DataEquationOp ( expr | constList ) )? (Ignore exprList)? ;

lookupDefinition: lhs (lookup|(OpenBracket(call|numberList) CloseBracket)) ;
// lookup numberlist format: accomplishments per hour lookup(0,0.2,0.4,0.6,0.8,1,
//                                                          0,0.2,0.4,0.6,0.8,1)
// The call is needed for direct and xls lookups. For example: historic_demand_lt( GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' ))~~|


constraint: lhs Condition expr? Implies expr ;
realityCheck: lhs Test_input expr ;


stringAssign: lhs StringAssignOp StringConst  (Ignore exprList)? ;
macroDefinition: Macro macroHeader symbolWithDoc+ EndMacro;



expr:     expr op=(And|Pow|Star|Div| Minus| Plus|Less|Greater|LessEqual|GreaterEqual|Equal|NotEqual| Or) expr  # exprOperation
    |   constVensim                             # const
    |   Keyword expr?                        # Keyword
    |   lookup                            # LookupArg
    |    Star                             # WildCard
    |   ( Minus| Plus)* exprAllowSign      # signExpr
    ;

exprAllowSign:
     call             # CallExpr
     |Delayp expr   Comma expr TwoDots Id  CloseBracket                     # DelayPArg
     | Id (subscript)?                   # Var
     |  Tabbed_array constVensim*  CloseBracket   # tabbedArray
     |     OpenBracket expr  CloseBracket                      # Parens
     ;


call:  Id (subscript)? OpenBracket exprList?  CloseBracket;


macroHeader: Id OpenBracket macroArguments?  CloseBracket;
macroArguments: exprList (TwoDots exprList)?;
exprList : expr ( Comma expr)* ;
subscriptValueList : (subscriptId|subscriptSequence) ( Comma (subscriptId|subscriptSequence))* ;
indexList: subscriptId ( Comma subscriptId)*;
subscript: OpenSquareBracket indexList CloseSquareBracket;
lookup : OpenBracket (lookupRange? lookupPointList)  CloseBracket ;


lookupRange : OpenSquareBracket lookupPoint  Minus lookupPoint referenceLine? CloseSquareBracket  Comma ;
lookupPointList : lookupPoint ( Comma lookupPoint)* ;
referenceLine:  Comma lookupPointList;
lookupPoint : OpenBracket constVensim  Comma constVensim  CloseBracket ;
constantLine: ( constVensim (  Comma constVensim)*) ;
// Optional semi colon is required for two dimensional arrays https://www.vensim.com/documentation/22070.htm
constList : constantLine ((Semicolon constantLine)* Semicolon)?;

numberList: (integerConst | floatingConst) ( Comma ( integerConst | floatingConst))*;





subscriptId : Id  Exclamation?;

constVensim
    :   integerConst
    |   floatingConst
    |   StringConst
    ;


integerConst
    :  ( Plus| Minus)* DigitSeq
    ;


floatingConst:
    ( Plus| Minus)* FloatingConstNumber;


 unitsDoc: units=INFO_UNIT comment=INFO_UNIT supplementary=INFO_UNIT? VerticalBar;


graphsGroup: graphs+;
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
metadataLine:DigitSeq TwoDots.*?;

// Backslash tokens are ignored, so this rule doesn't take them into account.
sketches: {enable(Tokens.VIEWS);} viewInfo* sketchesDelimiter  {disable(Tokens.VIEWS);};
sketchesDelimiter: SketchesDelimiter NewLine;
viewInfo:   sketchInfo versionCode viewName viewVariables;
sketchInfo:  NewLine* ViewDelimier Sketch_phrase NewLine ;
versionCode: Sketch_version NewLine;
//Vensim versions 5,4 and 3 all use the same version code (300).
viewName: Star .*? NewLine; //All view names are preceeded by an '*'

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
viewSettings: Dolar  color Comma integerConst  Comma typography  VerticalBar? (ppix=integerConst  Comma ppiy=integerConst)? ( Comma zoom=integerConst  Comma tf=integerConst)? NewLine;
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
arrow: internalId=integerConst  Comma idInView=integerConst  Comma fromVariable=integerConst  Comma toVariable=integerConst  Comma arrowShape=integerConst  Comma hidden=integerConst  Comma  polarityChar=integerConst  Comma thickness=integerConst  Comma hasFont=integerConst  Comma delayType=integerConst  Comma  integerConst  Comma  color  Comma typography?  Comma numberOfPoints=integerConst  VerticalBar (arrowCoordinates  VerticalBar)+ NewLine;
arrowCoordinates: OpenBracket integerConst Comma integerConst CloseBracket;

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
viewVariable:  internalId=integerConst  Comma idInView=integerConst  Comma (name=Id | integerConst)   Comma x=integerConst  Comma y=integerConst  Comma width=integerConst  Comma height=integerConst  Comma shape=integerConst  Comma bits=integerConst  Comma hidden=integerConst  Comma hasFont=integerConst  Comma textPos=integerConst  Comma boxWidth=integerConst  Comma nav1=integerConst  Comma nav2=integerConst ( Comma boxColor=color  Comma fillColor=color  Comma typography)? ( Comma integerConst  Comma integerConst  Comma integerConst  Comma integerConst  Comma integerConst  Comma integerConst)?  visualInfo NewLine;

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
typography: typographyName? VerticalBar fontSize=integerConst VerticalBar textFormat=Id?  VerticalBar fontColor=color ( VerticalBar shapeColor=color  VerticalBar arrowColor=color  VerticalBar fillColor=color  VerticalBar backgroundColor=color)?;
typographyName: At? Id;

color: rgbColor | singleColor;
rgbColor: integerConst  Minus integerConst  Minus integerConst;
singleColor: integerConst;



