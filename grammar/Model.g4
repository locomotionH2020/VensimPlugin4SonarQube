grammar Model;
import Expr;

// A Vensim model is a sequence of equations and subscript ranges.
model: ( subscriptRange | equation | lookupCall|constraint | macroDefinition )+ ;

// A subscript range definition names subscripts in a dimension.
subscriptRange : Id ':' ( subscriptIdList | subscriptSequence ) subscriptMappingList? ;
subscriptSequence : '(' Id '-' Id ')' ;
subscriptMappingList : '->' subscriptMapping ( ',' subscriptMapping )* ;
subscriptMapping : Id | '(' Id ':' subscriptIdList ')' ;

// An equation has a left-hand side and a right-hand side.
// The RHS is a formula expression, a constant list, or a Vensim lookup.
// The RHS is empty for data equations.
equation : lhs ( assignment_operator ( expr | constList ) | lookup )? (':IGNORE:' exprList)?;
lhs : Id ( subscript )? Keyword? ( ':EXCEPT:' subscript ( ',' subscript )* )? ;
assignment_operator: TwoEqual | Equal | Equation | StringAssign;

constraint: Id ':THE CONDITION:' expr? ':IMPLIES:' expr;

macroDefinition: ':MACRO:' macroHeader equation+ ':END OF MACRO:';

// The lexer strips some tokens we are not interested in.
// The character encoding is given at the start of a Vensim file.
// The units and documentation sections and group markings are skipped for now.
// Line continuation characters and the sketch must be stripped by a preprocessor.
Encoding : '{' [A-Za-z0-9-]+ '}' -> skip ;
UnitsDoc : '~' ~('|')* '|'->skip ;
Group : '****' .*? '|' -> skip ;



SketchInfo: [\\]+[-]+[/]+ Whitespace* 'Sketch information - do not modify anything except names' (.)*? EOF;
