// Generated from D:/juanh/Documents/Proyectos/tfg/src/main/antlr\ModelLexer.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ModelLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NewLine=1, CommentOrEncoding=2, GroupDelimiter=3, GroupEndDelimiter=4, 
		Star=5, Div=6, Pow=7, Minus=8, Plus=9, Less=10, LessEqual=11, Greater=12, 
		GreaterEqual=13, Equal=14, TwoEqual=15, NotEqual=16, Exclamation=17, DataEquationOp=18, 
		StringAssignOp=19, TwoDots=20, OpenBracket=21, CloseBracket=22, OpenSquareBracket=23, 
		CloseSquareBracket=24, RightArrow=25, TwoSidesArrow=26, Comma=27, Semicolon=28, 
		VerticalBar=29, Dolar=30, At=31, Ignore=32, Except=33, Id=34, FloatingConstNumber=35, 
		FractionalConstant=36, ExponentPart=37, DigitSeq=38, StringLiteral=39, 
		StringConst=40, Whitespace=41, Backslash=42, INFO_UNIT=43, OtherCaracter=44, 
		SketchesDelimiter=45, Condition=46, Implies=47, Test_input=48, Macro=49, 
		EndMacro=50, And=51, Or=52, Delayp=53, Tabbed_array=54, Graph=55, Title=56, 
		Xaxis=57, Xlabel=58, Xdiv=59, Yaxis=60, Ylabel=61, Ydiv=62, Xmin=63, Xmax=64, 
		No_legend=65, Scale=66, Var=67, Ymin=68, Ymax=69, Line_width=70, Metada_separator=71, 
		ViewDelimier=72, Sketch_phrase=73, Sketch_version=74, Keyword=75;
	public static final int
		VIEWS=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN", "VIEWS"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NewLine", "CommentOrEncoding", "GroupDelimiter", "GroupEndDelimiter", 
			"Star", "Div", "Pow", "Minus", "Plus", "Less", "LessEqual", "Greater", 
			"GreaterEqual", "Equal", "TwoEqual", "NotEqual", "Exclamation", "DataEquationOp", 
			"StringAssignOp", "TwoDots", "OpenBracket", "CloseBracket", "OpenSquareBracket", 
			"CloseSquareBracket", "RightArrow", "TwoSidesArrow", "Comma", "Semicolon", 
			"VerticalBar", "Dolar", "At", "Ignore", "Except", "Id", "IdChar", "Nondigit", 
			"Digit", "NonzeroDigit", "FloatingConstNumber", "FractionalConstant", 
			"ExponentPart", "DigitSeq", "StringLiteral", "StringConst", "Whitespace", 
			"Backslash", "INFO_UNIT", "OtherCaracter", "SketchesDelimiter", "Condition", 
			"Implies", "Test_input", "Macro", "EndMacro", "And", "Or", "Delayp", 
			"Tabbed_array", "Graph", "Title", "Xaxis", "Xlabel", "Xdiv", "Yaxis", 
			"Ylabel", "Ydiv", "Xmin", "Xmax", "No_legend", "Scale", "Var", "Ymin", 
			"Ymax", "Line_width", "Metada_separator", "ViewDelimier", "Sketch_phrase", 
			"Sketch_version", "Keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'*'", "'/'", "'^'", "'-'", "'+'", "'<'", 
			"'<='", "'>'", "'>='", "'='", "'=='", "'<>'", "'!'", "':='", "':IS:'", 
			"':'", "'('", "')'", "'['", "']'", "'->'", "'<->'", "','", "';'", "'|'", 
			"'$'", "'@'", "':IGNORE:'", "':EXCEPT:'", null, null, null, null, null, 
			null, null, null, null, null, null, "'///---'", "':THE CONDITION:'", 
			"':IMPLIES:'", "':TEST INPUT:'", "':MACRO:'", "':END OF MACRO:'", "':AND:'", 
			"':OR:'", "'DELAYP('", "'TABBED ARRAY('", "':GRAPH'", "':TITLE'", "':X-AXIS'", 
			"':X-LABEL'", "':X-DIV'", "':Y-AXIS'", "':Y-LABEL'", "':Y-DIV'", "':X-MIN'", 
			"':X-MAX'", "':NO-LEGEND'", "':SCALE'", "':VAR'", "':Y-MIN'", "':Y-MAX'", 
			"':LINE-WIDTH'", "':L\u007F<%^E!@'", "'---///'", "'Sketch information - do not modify anything except names'", 
			"'V300  Do not put anything below this section - it will be ignored'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NewLine", "CommentOrEncoding", "GroupDelimiter", "GroupEndDelimiter", 
			"Star", "Div", "Pow", "Minus", "Plus", "Less", "LessEqual", "Greater", 
			"GreaterEqual", "Equal", "TwoEqual", "NotEqual", "Exclamation", "DataEquationOp", 
			"StringAssignOp", "TwoDots", "OpenBracket", "CloseBracket", "OpenSquareBracket", 
			"CloseSquareBracket", "RightArrow", "TwoSidesArrow", "Comma", "Semicolon", 
			"VerticalBar", "Dolar", "At", "Ignore", "Except", "Id", "FloatingConstNumber", 
			"FractionalConstant", "ExponentPart", "DigitSeq", "StringLiteral", "StringConst", 
			"Whitespace", "Backslash", "INFO_UNIT", "OtherCaracter", "SketchesDelimiter", 
			"Condition", "Implies", "Test_input", "Macro", "EndMacro", "And", "Or", 
			"Delayp", "Tabbed_array", "Graph", "Title", "Xaxis", "Xlabel", "Xdiv", 
			"Yaxis", "Ylabel", "Ydiv", "Xmin", "Xmax", "No_legend", "Scale", "Var", 
			"Ymin", "Ymax", "Line_width", "Metada_separator", "ViewDelimier", "Sketch_phrase", 
			"Sketch_version", "Keyword"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ModelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ModelLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2M\u02f4\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\3\2\6\2\u00a3\n\2\r\2\16"+
		"\2\u00a4\3\2\3\2\3\3\3\3\7\3\u00ab\n\3\f\3\16\3\u00ae\13\3\3\3\3\3\3\3"+
		"\3\3\3\4\3\4\3\4\3\4\3\4\6\4\u00b9\n\4\r\4\16\4\u00ba\3\5\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\37\3\37\3 \3 \3!\3!\3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3#\3#\7#\u0115\n#\f#\16#\u0118\13#\3#\3#\3#\7#\u011d\n"+
		"#\f#\16#\u0120\13#\3#\3#\3#\5#\u0125\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3"+
		"(\5(\u0131\n(\3(\3(\3(\5(\u0136\n(\3)\5)\u0139\n)\3)\3)\3)\3)\3)\5)\u0140"+
		"\n)\3*\3*\5*\u0144\n*\3*\3*\3*\5*\u0149\n*\3*\5*\u014c\n*\3+\6+\u014f"+
		"\n+\r+\16+\u0150\3,\3,\3,\3,\7,\u0157\n,\f,\16,\u015a\13,\3,\3,\3-\3-"+
		"\3-\3-\7-\u0162\n-\f-\16-\u0165\13-\3-\3-\3.\6.\u016a\n.\r.\16.\u016b"+
		"\3.\3.\3/\3/\3/\3/\3\60\3\60\7\60\u0176\n\60\f\60\16\60\u0179\13\60\3"+
		"\61\3\61\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3"+
		"\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3"+
		"\64\3\64\3\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3"+
		"\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3"+
		"\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3"+
		"\67\38\38\38\38\38\38\39\39\39\39\39\3:\3:\3:\3:\3:\3:\3:\3:\3;\3;\3;"+
		"\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3="+
		"\3=\3=\3>\3>\3>\3>\3>\3>\3>\3>\3?\3?\3?\3?\3?\3?\3?\3?\3?\3@\3@\3@\3@"+
		"\3@\3@\3@\3A\3A\3A\3A\3A\3A\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B\3B\3C\3C\3C"+
		"\3C\3C\3C\3C\3D\3D\3D\3D\3D\3D\3D\3E\3E\3E\3E\3E\3E\3E\3F\3F\3F\3F\3F"+
		"\3F\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3G\3H\3H\3H\3H\3H\3I\3I\3I\3I\3I"+
		"\3I\3I\3J\3J\3J\3J\3J\3J\3J\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\3L\3L"+
		"\3L\3L\3L\3L\3L\3L\3L\3L\3M\3M\3M\3M\3M\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N"+
		"\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N"+
		"\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N"+
		"\3N\3N\3N\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O"+
		"\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O"+
		"\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O"+
		"\3P\3P\7P\u02ee\nP\fP\16P\u02f1\13P\3P\3P\5\u00ac\u0158\u0163\2Q\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G\2I\2K\2M\2O%Q&S\'U(W)Y*[+],_-a.c/e\60g\61i\62k\63m\64o\65q\66"+
		"s\67u8w9y:{;}<\177=\u0081>\u0083?\u0085@\u0087A\u0089B\u008bC\u008dD\u008f"+
		"E\u0091F\u0093G\u0095H\u0097I\u0099J\u009bK\u009dL\u009fM\3\2\20\4\2\f"+
		"\f\17\17\t\2&)\62;C\\aac|\u00a3\u0251\u1e04\u1ef5\5\2C\\aac|\3\2\62;\3"+
		"\2\63;\4\2--//\3\2$$\4\2$$^^\3\2^^\3\2))\4\2))^^\4\2\13\13\"\"\4\2~~\u0080"+
		"\u0080\5\2\"\"C\\c|\2\u0306\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3"+
		"\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2"+
		"\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C"+
		"\3\2\2\2\2E\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2"+
		"\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2"+
		"\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q"+
		"\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2"+
		"\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087"+
		"\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2"+
		"\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099"+
		"\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\3\u00a2\3\2\2"+
		"\2\5\u00a8\3\2\2\2\7\u00b3\3\2\2\2\t\u00bc\3\2\2\2\13\u00bf\3\2\2\2\r"+
		"\u00c1\3\2\2\2\17\u00c3\3\2\2\2\21\u00c5\3\2\2\2\23\u00c7\3\2\2\2\25\u00c9"+
		"\3\2\2\2\27\u00cb\3\2\2\2\31\u00ce\3\2\2\2\33\u00d0\3\2\2\2\35\u00d3\3"+
		"\2\2\2\37\u00d5\3\2\2\2!\u00d8\3\2\2\2#\u00db\3\2\2\2%\u00dd\3\2\2\2\'"+
		"\u00e0\3\2\2\2)\u00e5\3\2\2\2+\u00e7\3\2\2\2-\u00e9\3\2\2\2/\u00eb\3\2"+
		"\2\2\61\u00ed\3\2\2\2\63\u00ef\3\2\2\2\65\u00f2\3\2\2\2\67\u00f6\3\2\2"+
		"\29\u00f8\3\2\2\2;\u00fa\3\2\2\2=\u00fc\3\2\2\2?\u00fe\3\2\2\2A\u0100"+
		"\3\2\2\2C\u0109\3\2\2\2E\u0124\3\2\2\2G\u0126\3\2\2\2I\u0128\3\2\2\2K"+
		"\u012a\3\2\2\2M\u012c\3\2\2\2O\u0135\3\2\2\2Q\u013f\3\2\2\2S\u014b\3\2"+
		"\2\2U\u014e\3\2\2\2W\u0152\3\2\2\2Y\u015d\3\2\2\2[\u0169\3\2\2\2]\u016f"+
		"\3\2\2\2_\u0173\3\2\2\2a\u017a\3\2\2\2c\u017c\3\2\2\2e\u0183\3\2\2\2g"+
		"\u0193\3\2\2\2i\u019d\3\2\2\2k\u01aa\3\2\2\2m\u01b2\3\2\2\2o\u01c1\3\2"+
		"\2\2q\u01c7\3\2\2\2s\u01cc\3\2\2\2u\u01d4\3\2\2\2w\u01e2\3\2\2\2y\u01e9"+
		"\3\2\2\2{\u01f0\3\2\2\2}\u01f8\3\2\2\2\177\u0201\3\2\2\2\u0081\u0208\3"+
		"\2\2\2\u0083\u0210\3\2\2\2\u0085\u0219\3\2\2\2\u0087\u0220\3\2\2\2\u0089"+
		"\u0227\3\2\2\2\u008b\u022e\3\2\2\2\u008d\u0239\3\2\2\2\u008f\u0240\3\2"+
		"\2\2\u0091\u0245\3\2\2\2\u0093\u024c\3\2\2\2\u0095\u0253\3\2\2\2\u0097"+
		"\u025f\3\2\2\2\u0099\u0269\3\2\2\2\u009b\u0270\3\2\2\2\u009d\u02a9\3\2"+
		"\2\2\u009f\u02eb\3\2\2\2\u00a1\u00a3\t\2\2\2\u00a2\u00a1\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a6\3\2"+
		"\2\2\u00a6\u00a7\b\2\2\2\u00a7\4\3\2\2\2\u00a8\u00ac\7}\2\2\u00a9\u00ab"+
		"\13\2\2\2\u00aa\u00a9\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00ad\3\2\2\2"+
		"\u00ac\u00aa\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b0"+
		"\7\177\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\b\3\3\2\u00b2\6\3\2\2\2\u00b3"+
		"\u00b4\7,\2\2\u00b4\u00b5\7,\2\2\u00b5\u00b6\7,\2\2\u00b6\u00b8\3\2\2"+
		"\2\u00b7\u00b9\5\13\6\2\u00b8\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba"+
		"\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\b\3\2\2\2\u00bc\u00bd\5\7\4\2"+
		"\u00bd\u00be\7\u0080\2\2\u00be\n\3\2\2\2\u00bf\u00c0\7,\2\2\u00c0\f\3"+
		"\2\2\2\u00c1\u00c2\7\61\2\2\u00c2\16\3\2\2\2\u00c3\u00c4\7`\2\2\u00c4"+
		"\20\3\2\2\2\u00c5\u00c6\7/\2\2\u00c6\22\3\2\2\2\u00c7\u00c8\7-\2\2\u00c8"+
		"\24\3\2\2\2\u00c9\u00ca\7>\2\2\u00ca\26\3\2\2\2\u00cb\u00cc\7>\2\2\u00cc"+
		"\u00cd\7?\2\2\u00cd\30\3\2\2\2\u00ce\u00cf\7@\2\2\u00cf\32\3\2\2\2\u00d0"+
		"\u00d1\7@\2\2\u00d1\u00d2\7?\2\2\u00d2\34\3\2\2\2\u00d3\u00d4\7?\2\2\u00d4"+
		"\36\3\2\2\2\u00d5\u00d6\7?\2\2\u00d6\u00d7\7?\2\2\u00d7 \3\2\2\2\u00d8"+
		"\u00d9\7>\2\2\u00d9\u00da\7@\2\2\u00da\"\3\2\2\2\u00db\u00dc\7#\2\2\u00dc"+
		"$\3\2\2\2\u00dd\u00de\7<\2\2\u00de\u00df\7?\2\2\u00df&\3\2\2\2\u00e0\u00e1"+
		"\7<\2\2\u00e1\u00e2\7K\2\2\u00e2\u00e3\7U\2\2\u00e3\u00e4\7<\2\2\u00e4"+
		"(\3\2\2\2\u00e5\u00e6\7<\2\2\u00e6*\3\2\2\2\u00e7\u00e8\7*\2\2\u00e8,"+
		"\3\2\2\2\u00e9\u00ea\7+\2\2\u00ea.\3\2\2\2\u00eb\u00ec\7]\2\2\u00ec\60"+
		"\3\2\2\2\u00ed\u00ee\7_\2\2\u00ee\62\3\2\2\2\u00ef\u00f0\7/\2\2\u00f0"+
		"\u00f1\7@\2\2\u00f1\64\3\2\2\2\u00f2\u00f3\7>\2\2\u00f3\u00f4\7/\2\2\u00f4"+
		"\u00f5\7@\2\2\u00f5\66\3\2\2\2\u00f6\u00f7\7.\2\2\u00f78\3\2\2\2\u00f8"+
		"\u00f9\7=\2\2\u00f9:\3\2\2\2\u00fa\u00fb\7~\2\2\u00fb<\3\2\2\2\u00fc\u00fd"+
		"\7&\2\2\u00fd>\3\2\2\2\u00fe\u00ff\7B\2\2\u00ff@\3\2\2\2\u0100\u0101\7"+
		"<\2\2\u0101\u0102\7K\2\2\u0102\u0103\7I\2\2\u0103\u0104\7P\2\2\u0104\u0105"+
		"\7Q\2\2\u0105\u0106\7T\2\2\u0106\u0107\7G\2\2\u0107\u0108\7<\2\2\u0108"+
		"B\3\2\2\2\u0109\u010a\7<\2\2\u010a\u010b\7G\2\2\u010b\u010c\7Z\2\2\u010c"+
		"\u010d\7E\2\2\u010d\u010e\7G\2\2\u010e\u010f\7R\2\2\u010f\u0110\7V\2\2"+
		"\u0110\u0111\7<\2\2\u0111D\3\2\2\2\u0112\u0116\5I%\2\u0113\u0115\5G$\2"+
		"\u0114\u0113\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117"+
		"\3\2\2\2\u0117\u0125\3\2\2\2\u0118\u0116\3\2\2\2\u0119\u011e\5I%\2\u011a"+
		"\u011d\5G$\2\u011b\u011d\7\"\2\2\u011c\u011a\3\2\2\2\u011c\u011b\3\2\2"+
		"\2\u011d\u0120\3\2\2\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0121"+
		"\3\2\2\2\u0120\u011e\3\2\2\2\u0121\u0122\5G$\2\u0122\u0125\3\2\2\2\u0123"+
		"\u0125\5W,\2\u0124\u0112\3\2\2\2\u0124\u0119\3\2\2\2\u0124\u0123\3\2\2"+
		"\2\u0125F\3\2\2\2\u0126\u0127\t\3\2\2\u0127H\3\2\2\2\u0128\u0129\t\4\2"+
		"\2\u0129J\3\2\2\2\u012a\u012b\t\5\2\2\u012bL\3\2\2\2\u012c\u012d\t\6\2"+
		"\2\u012dN\3\2\2\2\u012e\u0130\5Q)\2\u012f\u0131\5S*\2\u0130\u012f\3\2"+
		"\2\2\u0130\u0131\3\2\2\2\u0131\u0136\3\2\2\2\u0132\u0133\5U+\2\u0133\u0134"+
		"\5S*\2\u0134\u0136\3\2\2\2\u0135\u012e\3\2\2\2\u0135\u0132\3\2\2\2\u0136"+
		"P\3\2\2\2\u0137\u0139\5U+\2\u0138\u0137\3\2\2\2\u0138\u0139\3\2\2\2\u0139"+
		"\u013a\3\2\2\2\u013a\u013b\7\60\2\2\u013b\u0140\5U+\2\u013c\u013d\5U+"+
		"\2\u013d\u013e\7\60\2\2\u013e\u0140\3\2\2\2\u013f\u0138\3\2\2\2\u013f"+
		"\u013c\3\2\2\2\u0140R\3\2\2\2\u0141\u0143\7g\2\2\u0142\u0144\t\7\2\2\u0143"+
		"\u0142\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u014c\5U"+
		"+\2\u0146\u0148\7G\2\2\u0147\u0149\t\7\2\2\u0148\u0147\3\2\2\2\u0148\u0149"+
		"\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014c\5U+\2\u014b\u0141\3\2\2\2\u014b"+
		"\u0146\3\2\2\2\u014cT\3\2\2\2\u014d\u014f\5K&\2\u014e\u014d\3\2\2\2\u014f"+
		"\u0150\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151V\3\2\2\2"+
		"\u0152\u0158\t\b\2\2\u0153\u0157\n\t\2\2\u0154\u0155\t\n\2\2\u0155\u0157"+
		"\13\2\2\2\u0156\u0153\3\2\2\2\u0156\u0154\3\2\2\2\u0157\u015a\3\2\2\2"+
		"\u0158\u0159\3\2\2\2\u0158\u0156\3\2\2\2\u0159\u015b\3\2\2\2\u015a\u0158"+
		"\3\2\2\2\u015b\u015c\t\b\2\2\u015cX\3\2\2\2\u015d\u0163\t\13\2\2\u015e"+
		"\u0162\n\f\2\2\u015f\u0160\t\n\2\2\u0160\u0162\13\2\2\2\u0161\u015e\3"+
		"\2\2\2\u0161\u015f\3\2\2\2\u0162\u0165\3\2\2\2\u0163\u0164\3\2\2\2\u0163"+
		"\u0161\3\2\2\2\u0164\u0166\3\2\2\2\u0165\u0163\3\2\2\2\u0166\u0167\t\13"+
		"\2\2\u0167Z\3\2\2\2\u0168\u016a\t\r\2\2\u0169\u0168\3\2\2\2\u016a\u016b"+
		"\3\2\2\2\u016b\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016d\3\2\2\2\u016d"+
		"\u016e\b.\3\2\u016e\\\3\2\2\2\u016f\u0170\t\n\2\2\u0170\u0171\3\2\2\2"+
		"\u0171\u0172\b/\3\2\u0172^\3\2\2\2\u0173\u0177\7\u0080\2\2\u0174\u0176"+
		"\n\16\2\2\u0175\u0174\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2\2\2"+
		"\u0177\u0178\3\2\2\2\u0178`\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017b\13"+
		"\2\2\2\u017bb\3\2\2\2\u017c\u017d\7\61\2\2\u017d\u017e\7\61\2\2\u017e"+
		"\u017f\7\61\2\2\u017f\u0180\7/\2\2\u0180\u0181\7/\2\2\u0181\u0182\7/\2"+
		"\2\u0182d\3\2\2\2\u0183\u0184\7<\2\2\u0184\u0185\7V\2\2\u0185\u0186\7"+
		"J\2\2\u0186\u0187\7G\2\2\u0187\u0188\7\"\2\2\u0188\u0189\7E\2\2\u0189"+
		"\u018a\7Q\2\2\u018a\u018b\7P\2\2\u018b\u018c\7F\2\2\u018c\u018d\7K\2\2"+
		"\u018d\u018e\7V\2\2\u018e\u018f\7K\2\2\u018f\u0190\7Q\2\2\u0190\u0191"+
		"\7P\2\2\u0191\u0192\7<\2\2\u0192f\3\2\2\2\u0193\u0194\7<\2\2\u0194\u0195"+
		"\7K\2\2\u0195\u0196\7O\2\2\u0196\u0197\7R\2\2\u0197\u0198\7N\2\2\u0198"+
		"\u0199\7K\2\2\u0199\u019a\7G\2\2\u019a\u019b\7U\2\2\u019b\u019c\7<\2\2"+
		"\u019ch\3\2\2\2\u019d\u019e\7<\2\2\u019e\u019f\7V\2\2\u019f\u01a0\7G\2"+
		"\2\u01a0\u01a1\7U\2\2\u01a1\u01a2\7V\2\2\u01a2\u01a3\7\"\2\2\u01a3\u01a4"+
		"\7K\2\2\u01a4\u01a5\7P\2\2\u01a5\u01a6\7R\2\2\u01a6\u01a7\7W\2\2\u01a7"+
		"\u01a8\7V\2\2\u01a8\u01a9\7<\2\2\u01a9j\3\2\2\2\u01aa\u01ab\7<\2\2\u01ab"+
		"\u01ac\7O\2\2\u01ac\u01ad\7C\2\2\u01ad\u01ae\7E\2\2\u01ae\u01af\7T\2\2"+
		"\u01af\u01b0\7Q\2\2\u01b0\u01b1\7<\2\2\u01b1l\3\2\2\2\u01b2\u01b3\7<\2"+
		"\2\u01b3\u01b4\7G\2\2\u01b4\u01b5\7P\2\2\u01b5\u01b6\7F\2\2\u01b6\u01b7"+
		"\7\"\2\2\u01b7\u01b8\7Q\2\2\u01b8\u01b9\7H\2\2\u01b9\u01ba\7\"\2\2\u01ba"+
		"\u01bb\7O\2\2\u01bb\u01bc\7C\2\2\u01bc\u01bd\7E\2\2\u01bd\u01be\7T\2\2"+
		"\u01be\u01bf\7Q\2\2\u01bf\u01c0\7<\2\2\u01c0n\3\2\2\2\u01c1\u01c2\7<\2"+
		"\2\u01c2\u01c3\7C\2\2\u01c3\u01c4\7P\2\2\u01c4\u01c5\7F\2\2\u01c5\u01c6"+
		"\7<\2\2\u01c6p\3\2\2\2\u01c7\u01c8\7<\2\2\u01c8\u01c9\7Q\2\2\u01c9\u01ca"+
		"\7T\2\2\u01ca\u01cb\7<\2\2\u01cbr\3\2\2\2\u01cc\u01cd\7F\2\2\u01cd\u01ce"+
		"\7G\2\2\u01ce\u01cf\7N\2\2\u01cf\u01d0\7C\2\2\u01d0\u01d1\7[\2\2\u01d1"+
		"\u01d2\7R\2\2\u01d2\u01d3\7*\2\2\u01d3t\3\2\2\2\u01d4\u01d5\7V\2\2\u01d5"+
		"\u01d6\7C\2\2\u01d6\u01d7\7D\2\2\u01d7\u01d8\7D\2\2\u01d8\u01d9\7G\2\2"+
		"\u01d9\u01da\7F\2\2\u01da\u01db\7\"\2\2\u01db\u01dc\7C\2\2\u01dc\u01dd"+
		"\7T\2\2\u01dd\u01de\7T\2\2\u01de\u01df\7C\2\2\u01df\u01e0\7[\2\2\u01e0"+
		"\u01e1\7*\2\2\u01e1v\3\2\2\2\u01e2\u01e3\7<\2\2\u01e3\u01e4\7I\2\2\u01e4"+
		"\u01e5\7T\2\2\u01e5\u01e6\7C\2\2\u01e6\u01e7\7R\2\2\u01e7\u01e8\7J\2\2"+
		"\u01e8x\3\2\2\2\u01e9\u01ea\7<\2\2\u01ea\u01eb\7V\2\2\u01eb\u01ec\7K\2"+
		"\2\u01ec\u01ed\7V\2\2\u01ed\u01ee\7N\2\2\u01ee\u01ef\7G\2\2\u01efz\3\2"+
		"\2\2\u01f0\u01f1\7<\2\2\u01f1\u01f2\7Z\2\2\u01f2\u01f3\7/\2\2\u01f3\u01f4"+
		"\7C\2\2\u01f4\u01f5\7Z\2\2\u01f5\u01f6\7K\2\2\u01f6\u01f7\7U\2\2\u01f7"+
		"|\3\2\2\2\u01f8\u01f9\7<\2\2\u01f9\u01fa\7Z\2\2\u01fa\u01fb\7/\2\2\u01fb"+
		"\u01fc\7N\2\2\u01fc\u01fd\7C\2\2\u01fd\u01fe\7D\2\2\u01fe\u01ff\7G\2\2"+
		"\u01ff\u0200\7N\2\2\u0200~\3\2\2\2\u0201\u0202\7<\2\2\u0202\u0203\7Z\2"+
		"\2\u0203\u0204\7/\2\2\u0204\u0205\7F\2\2\u0205\u0206\7K\2\2\u0206\u0207"+
		"\7X\2\2\u0207\u0080\3\2\2\2\u0208\u0209\7<\2\2\u0209\u020a\7[\2\2\u020a"+
		"\u020b\7/\2\2\u020b\u020c\7C\2\2\u020c\u020d\7Z\2\2\u020d\u020e\7K\2\2"+
		"\u020e\u020f\7U\2\2\u020f\u0082\3\2\2\2\u0210\u0211\7<\2\2\u0211\u0212"+
		"\7[\2\2\u0212\u0213\7/\2\2\u0213\u0214\7N\2\2\u0214\u0215\7C\2\2\u0215"+
		"\u0216\7D\2\2\u0216\u0217\7G\2\2\u0217\u0218\7N\2\2\u0218\u0084\3\2\2"+
		"\2\u0219\u021a\7<\2\2\u021a\u021b\7[\2\2\u021b\u021c\7/\2\2\u021c\u021d"+
		"\7F\2\2\u021d\u021e\7K\2\2\u021e\u021f\7X\2\2\u021f\u0086\3\2\2\2\u0220"+
		"\u0221\7<\2\2\u0221\u0222\7Z\2\2\u0222\u0223\7/\2\2\u0223\u0224\7O\2\2"+
		"\u0224\u0225\7K\2\2\u0225\u0226\7P\2\2\u0226\u0088\3\2\2\2\u0227\u0228"+
		"\7<\2\2\u0228\u0229\7Z\2\2\u0229\u022a\7/\2\2\u022a\u022b\7O\2\2\u022b"+
		"\u022c\7C\2\2\u022c\u022d\7Z\2\2\u022d\u008a\3\2\2\2\u022e\u022f\7<\2"+
		"\2\u022f\u0230\7P\2\2\u0230\u0231\7Q\2\2\u0231\u0232\7/\2\2\u0232\u0233"+
		"\7N\2\2\u0233\u0234\7G\2\2\u0234\u0235\7I\2\2\u0235\u0236\7G\2\2\u0236"+
		"\u0237\7P\2\2\u0237\u0238\7F\2\2\u0238\u008c\3\2\2\2\u0239\u023a\7<\2"+
		"\2\u023a\u023b\7U\2\2\u023b\u023c\7E\2\2\u023c\u023d\7C\2\2\u023d\u023e"+
		"\7N\2\2\u023e\u023f\7G\2\2\u023f\u008e\3\2\2\2\u0240\u0241\7<\2\2\u0241"+
		"\u0242\7X\2\2\u0242\u0243\7C\2\2\u0243\u0244\7T\2\2\u0244\u0090\3\2\2"+
		"\2\u0245\u0246\7<\2\2\u0246\u0247\7[\2\2\u0247\u0248\7/\2\2\u0248\u0249"+
		"\7O\2\2\u0249\u024a\7K\2\2\u024a\u024b\7P\2\2\u024b\u0092\3\2\2\2\u024c"+
		"\u024d\7<\2\2\u024d\u024e\7[\2\2\u024e\u024f\7/\2\2\u024f\u0250\7O\2\2"+
		"\u0250\u0251\7C\2\2\u0251\u0252\7Z\2\2\u0252\u0094\3\2\2\2\u0253\u0254"+
		"\7<\2\2\u0254\u0255\7N\2\2\u0255\u0256\7K\2\2\u0256\u0257\7P\2\2\u0257"+
		"\u0258\7G\2\2\u0258\u0259\7/\2\2\u0259\u025a\7Y\2\2\u025a\u025b\7K\2\2"+
		"\u025b\u025c\7F\2\2\u025c\u025d\7V\2\2\u025d\u025e\7J\2\2\u025e\u0096"+
		"\3\2\2\2\u025f\u0260\7<\2\2\u0260\u0261\7N\2\2\u0261\u0262\7\u0081\2\2"+
		"\u0262\u0263\7>\2\2\u0263\u0264\7\'\2\2\u0264\u0265\7`\2\2\u0265\u0266"+
		"\7G\2\2\u0266\u0267\7#\2\2\u0267\u0268\7B\2\2\u0268\u0098\3\2\2\2\u0269"+
		"\u026a\7/\2\2\u026a\u026b\7/\2\2\u026b\u026c\7/\2\2\u026c\u026d\7\61\2"+
		"\2\u026d\u026e\7\61\2\2\u026e\u026f\7\61\2\2\u026f\u009a\3\2\2\2\u0270"+
		"\u0271\7U\2\2\u0271\u0272\7m\2\2\u0272\u0273\7g\2\2\u0273\u0274\7v\2\2"+
		"\u0274\u0275\7e\2\2\u0275\u0276\7j\2\2\u0276\u0277\7\"\2\2\u0277\u0278"+
		"\7k\2\2\u0278\u0279\7p\2\2\u0279\u027a\7h\2\2\u027a\u027b\7q\2\2\u027b"+
		"\u027c\7t\2\2\u027c\u027d\7o\2\2\u027d\u027e\7c\2\2\u027e\u027f\7v\2\2"+
		"\u027f\u0280\7k\2\2\u0280\u0281\7q\2\2\u0281\u0282\7p\2\2\u0282\u0283"+
		"\7\"\2\2\u0283\u0284\7/\2\2\u0284\u0285\7\"\2\2\u0285\u0286\7f\2\2\u0286"+
		"\u0287\7q\2\2\u0287\u0288\7\"\2\2\u0288\u0289\7p\2\2\u0289\u028a\7q\2"+
		"\2\u028a\u028b\7v\2\2\u028b\u028c\7\"\2\2\u028c\u028d\7o\2\2\u028d\u028e"+
		"\7q\2\2\u028e\u028f\7f\2\2\u028f\u0290\7k\2\2\u0290\u0291\7h\2\2\u0291"+
		"\u0292\7{\2\2\u0292\u0293\7\"\2\2\u0293\u0294\7c\2\2\u0294\u0295\7p\2"+
		"\2\u0295\u0296\7{\2\2\u0296\u0297\7v\2\2\u0297\u0298\7j\2\2\u0298\u0299"+
		"\7k\2\2\u0299\u029a\7p\2\2\u029a\u029b\7i\2\2\u029b\u029c\7\"\2\2\u029c"+
		"\u029d\7g\2\2\u029d\u029e\7z\2\2\u029e\u029f\7e\2\2\u029f\u02a0\7g\2\2"+
		"\u02a0\u02a1\7r\2\2\u02a1\u02a2\7v\2\2\u02a2\u02a3\7\"\2\2\u02a3\u02a4"+
		"\7p\2\2\u02a4\u02a5\7c\2\2\u02a5\u02a6\7o\2\2\u02a6\u02a7\7g\2\2\u02a7"+
		"\u02a8\7u\2\2\u02a8\u009c\3\2\2\2\u02a9\u02aa\7X\2\2\u02aa\u02ab\7\65"+
		"\2\2\u02ab\u02ac\7\62\2\2\u02ac\u02ad\7\62\2\2\u02ad\u02ae\7\"\2\2\u02ae"+
		"\u02af\7\"\2\2\u02af\u02b0\7F\2\2\u02b0\u02b1\7q\2\2\u02b1\u02b2\7\"\2"+
		"\2\u02b2\u02b3\7p\2\2\u02b3\u02b4\7q\2\2\u02b4\u02b5\7v\2\2\u02b5\u02b6"+
		"\7\"\2\2\u02b6\u02b7\7r\2\2\u02b7\u02b8\7w\2\2\u02b8\u02b9\7v\2\2\u02b9"+
		"\u02ba\7\"\2\2\u02ba\u02bb\7c\2\2\u02bb\u02bc\7p\2\2\u02bc\u02bd\7{\2"+
		"\2\u02bd\u02be\7v\2\2\u02be\u02bf\7j\2\2\u02bf\u02c0\7k\2\2\u02c0\u02c1"+
		"\7p\2\2\u02c1\u02c2\7i\2\2\u02c2\u02c3\7\"\2\2\u02c3\u02c4\7d\2\2\u02c4"+
		"\u02c5\7g\2\2\u02c5\u02c6\7n\2\2\u02c6\u02c7\7q\2\2\u02c7\u02c8\7y\2\2"+
		"\u02c8\u02c9\7\"\2\2\u02c9\u02ca\7v\2\2\u02ca\u02cb\7j\2\2\u02cb\u02cc"+
		"\7k\2\2\u02cc\u02cd\7u\2\2\u02cd\u02ce\7\"\2\2\u02ce\u02cf\7u\2\2\u02cf"+
		"\u02d0\7g\2\2\u02d0\u02d1\7e\2\2\u02d1\u02d2\7v\2\2\u02d2\u02d3\7k\2\2"+
		"\u02d3\u02d4\7q\2\2\u02d4\u02d5\7p\2\2\u02d5\u02d6\7\"\2\2\u02d6\u02d7"+
		"\7/\2\2\u02d7\u02d8\7\"\2\2\u02d8\u02d9\7k\2\2\u02d9\u02da\7v\2\2\u02da"+
		"\u02db\7\"\2\2\u02db\u02dc\7y\2\2\u02dc\u02dd\7k\2\2\u02dd\u02de\7n\2"+
		"\2\u02de\u02df\7n\2\2\u02df\u02e0\7\"\2\2\u02e0\u02e1\7d\2\2\u02e1\u02e2"+
		"\7g\2\2\u02e2\u02e3\7\"\2\2\u02e3\u02e4\7k\2\2\u02e4\u02e5\7i\2\2\u02e5"+
		"\u02e6\7p\2\2\u02e6\u02e7\7q\2\2\u02e7\u02e8\7t\2\2\u02e8\u02e9\7g\2\2"+
		"\u02e9\u02ea\7f\2\2\u02ea\u009e\3\2\2\2\u02eb\u02ef\7<\2\2\u02ec\u02ee"+
		"\t\17\2\2\u02ed\u02ec\3\2\2\2\u02ee\u02f1\3\2\2\2\u02ef\u02ed\3\2\2\2"+
		"\u02ef\u02f0\3\2\2\2\u02f0\u02f2\3\2\2\2\u02f1\u02ef\3\2\2\2\u02f2\u02f3"+
		"\7<\2\2\u02f3\u00a0\3\2\2\2\31\2\u00a4\u00ac\u00ba\u0116\u011c\u011e\u0124"+
		"\u0130\u0135\u0138\u013f\u0143\u0148\u014b\u0150\u0156\u0158\u0161\u0163"+
		"\u016b\u0177\u02ef\4\2\4\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}