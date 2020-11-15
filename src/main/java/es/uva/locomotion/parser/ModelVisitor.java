// Generated from Model.g4 by ANTLR 4.7.2
package es.uva.locomotion.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Model}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ModelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Model#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(Model.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(Model.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#sketchesGraphsAndMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSketchesGraphsAndMetadata(Model.SketchesGraphsAndMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#symbolWithDoc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbolWithDoc(Model.SymbolWithDocContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#symbolWithDocDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbolWithDocDefinition(Model.SymbolWithDocDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptRange(Model.SubscriptRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptSequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptSequence(Model.SubscriptSequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptMappingList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptMappingList(Model.SubscriptMappingListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptMapping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptMapping(Model.SubscriptMappingContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#equation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquation(Model.EquationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLhs(Model.LhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptCopy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptCopy(Model.SubscriptCopyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#unchangeableConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnchangeableConstant(Model.UnchangeableConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#dataEquation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataEquation(Model.DataEquationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lookupDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookupDefinition(Model.LookupDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraint(Model.ConstraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#realityCheck}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealityCheck(Model.RealityCheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#stringAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringAssign(Model.StringAssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#macroDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroDefinition(Model.MacroDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code const}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst(Model.ConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Keyword}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(Model.KeywordContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprOperation}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprOperation(Model.ExprOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code signExpr}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSignExpr(Model.SignExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WildCard}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildCard(Model.WildCardContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LookupArg}
	 * labeled alternative in {@link Model#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookupArg(Model.LookupArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CallExpr}
	 * labeled alternative in {@link Model#exprAllowSign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallExpr(Model.CallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DelayPArg}
	 * labeled alternative in {@link Model#exprAllowSign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelayPArg(Model.DelayPArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link Model#exprAllowSign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(Model.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tabbedArray}
	 * labeled alternative in {@link Model#exprAllowSign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTabbedArray(Model.TabbedArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link Model#exprAllowSign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(Model.ParensContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall(Model.CallContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#macroHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroHeader(Model.MacroHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#macroArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroArguments(Model.MacroArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(Model.ExprListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptValueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptValueList(Model.SubscriptValueListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#indexList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexList(Model.IndexListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscript}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscript(Model.SubscriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lookup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookup(Model.LookupContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lookupRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookupRange(Model.LookupRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lookupPointList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookupPointList(Model.LookupPointListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#referenceLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReferenceLine(Model.ReferenceLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#lookupPoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLookupPoint(Model.LookupPointContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#constantLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantLine(Model.ConstantLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#constList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstList(Model.ConstListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#numberList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberList(Model.NumberListContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#subscriptId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubscriptId(Model.SubscriptIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#constVensim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstVensim(Model.ConstVensimContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#integerConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerConst(Model.IntegerConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#floatingConst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatingConst(Model.FloatingConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#unitsDoc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnitsDoc(Model.UnitsDocContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#graphsGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphsGroup(Model.GraphsGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#graphs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphs(Model.GraphsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#graph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraph(Model.GraphContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#title}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTitle(Model.TitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#xaxis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXaxis(Model.XaxisContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#xlabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXlabel(Model.XlabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#xdiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXdiv(Model.XdivContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#yaxis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYaxis(Model.YaxisContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#ylabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYlabel(Model.YlabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#ydiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYdiv(Model.YdivContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#xmin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXmin(Model.XminContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#xmax}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXmax(Model.XmaxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#nolegend}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNolegend(Model.NolegendContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#scale}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScale(Model.ScaleContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#graphvar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphvar(Model.GraphvarContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#gvar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGvar(Model.GvarContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#ymin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYmin(Model.YminContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#ymax}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYmax(Model.YmaxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#linewidthgraph}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinewidthgraph(Model.LinewidthgraphContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#metadataDivisor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataDivisor(Model.MetadataDivisorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#metadataLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataLine(Model.MetadataLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#sketches}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSketches(Model.SketchesContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#sketchesDelimiter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSketchesDelimiter(Model.SketchesDelimiterContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#viewInfo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitViewInfo(Model.ViewInfoContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#sketchInfo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSketchInfo(Model.SketchInfoContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#versionCode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersionCode(Model.VersionCodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#viewName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitViewName(Model.ViewNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#viewSettings}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitViewSettings(Model.ViewSettingsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#viewVariables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitViewVariables(Model.ViewVariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#arrow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrow(Model.ArrowContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#arrowCoordinates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrowCoordinates(Model.ArrowCoordinatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#viewVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitViewVariable(Model.ViewVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#visualInfo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVisualInfo(Model.VisualInfoContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#typography}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypography(Model.TypographyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#typographyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypographyName(Model.TypographyNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#color}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColor(Model.ColorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#rgbColor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRgbColor(Model.RgbColorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Model#singleColor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleColor(Model.SingleColorContext ctx);
}