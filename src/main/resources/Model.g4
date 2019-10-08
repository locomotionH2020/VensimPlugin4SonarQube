grammar Model;
import Expr;



// A Vensim model is a sequence of equations and subscript ranges.

file: model? EOF;
model: ( subscriptRange | equation |
      lookupCallEquation|constraint | macroDefinition | unchangeableConstant |
       dataEquation| lookupDefinition | stringAssign |subscriptCopy |realityCheck)+ sketches ;

// A subscript range definition names subscripts in a dimension.
subscriptRange : Id ':' ( subscriptIdList | subscriptSequence | call) subscriptMappingList? unitsDoc;
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
lookupDefinition: lhs lookup unitsDoc;
constraint: Id ':THE CONDITION:' expr? ':IMPLIES:' expr unitsDoc;
realityCheck: Id subscript? ':TEST INPUT:' Id subscript? '=' expr unitsDoc; 

stringAssign: lhs StringAssignOp StringConst  (':IGNORE:' exprList)? unitsDoc;
macroDefinition: ':MACRO:' macroHeader equation+ ':END OF MACRO:';
lookupCallEquation: lookupCall unitsDoc;



// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.

CommentOrEncoding: '{' .*? '}' -> skip;

Group : '****' .*? '|' -> skip ;


// Backslash tokens are ignored in Expr.g4, so this rule doesn't take them into account.
sketchInfo: '---///' 'Sketch information - do not modify anything except names' .*? ;
sketches: sketchInfo+;
