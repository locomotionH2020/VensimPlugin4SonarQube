// Generated from Model.g4 by ANTLR 4.7.2
package es.uva.medeas.parser;
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, CommentOrEncoding=26, Group=27, Star=28, Div=29, Plus=30, Minus=31, 
		Less=32, LessEqual=33, Greater=34, GreaterEqual=35, Equal=36, TwoEqual=37, 
		NotEqual=38, Exclamation=39, DataEquationOp=40, StringAssignOp=41, Id=42, 
		DigitSeq=43, StringLiteral=44, StringConst=45, Keyword=46, Whitespace=47, 
		Backslash=48, OtherCaracter=49, COMMENTS=50;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"CommentOrEncoding", "Group", "Star", "Div", "Plus", "Minus", "Less", 
			"LessEqual", "Greater", "GreaterEqual", "Equal", "TwoEqual", "NotEqual", 
			"Exclamation", "DataEquationOp", "StringAssignOp", "Id", "IdChar", "Nondigit", 
			"Digit", "NonzeroDigit", "DigitSeq", "StringLiteral", "StringConst", 
			"Keyword", "Whitespace", "Backslash", "OtherCaracter", "COMMENTS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "':'", "'('", "')'", "'->'", "','", "':IGNORE:'", "':EXCEPT:'", 
			"'<->'", "':THE CONDITION:'", "':IMPLIES:'", "':TEST INPUT:'", "':MACRO:'", 
			"':END OF MACRO:'", "'---///'", "'Sketch information - do not modify anything except names'", 
			"'^'", "':AND:'", "':OR:'", "'TABBED ARRAY('", "'['", "']'", "';'", "'.'", 
			"'e'", "'E'", null, null, "'*'", "'/'", "'+'", "'-'", "'<'", "'<='", 
			"'>'", "'>='", "'='", "'=='", "'<>'", "'!'", "':='", "':IS:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "CommentOrEncoding", "Group", "Star", "Div", "Plus", "Minus", 
			"Less", "LessEqual", "Greater", "GreaterEqual", "Equal", "TwoEqual", 
			"NotEqual", "Exclamation", "DataEquationOp", "StringAssignOp", "Id", 
			"DigitSeq", "StringLiteral", "StringConst", "Keyword", "Whitespace", 
			"Backslash", "OtherCaracter", "COMMENTS"
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
	public String getGrammarFileName() { return "Model.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u01c9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3"+
		"\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\7\33\u0138"+
		"\n\33\f\33\16\33\u013b\13\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3"+
		"\34\3\34\7\34\u0147\n\34\f\34\16\34\u014a\13\34\3\34\3\34\3\34\3\34\3"+
		"\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3$\3"+
		"%\3%\3&\3&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3)\3*\3*\3*\3*\3*\3+\3+\7+\u0176"+
		"\n+\f+\16+\u0179\13+\3+\3+\3+\7+\u017e\n+\f+\16+\u0181\13+\3+\3+\3+\5"+
		"+\u0186\n+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\6\60\u0191\n\60\r\60\16\60\u0192"+
		"\3\61\3\61\3\61\3\61\7\61\u0199\n\61\f\61\16\61\u019c\13\61\3\61\3\61"+
		"\3\62\3\62\3\62\3\62\7\62\u01a4\n\62\f\62\16\62\u01a7\13\62\3\62\3\62"+
		"\3\63\3\63\7\63\u01ad\n\63\f\63\16\63\u01b0\13\63\3\63\3\63\3\64\6\64"+
		"\u01b5\n\64\r\64\16\64\u01b6\3\64\3\64\3\65\3\65\3\65\3\65\3\66\3\66\3"+
		"\67\3\67\7\67\u01c3\n\67\f\67\16\67\u01c6\13\67\3\67\3\67\5\u0139\u0148"+
		"\u01c4\28\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W\2Y\2[\2]\2_-a.c/e\60g\61i\62"+
		"k\63m\64\3\2\r\t\2&)\62;C\\aac|\u00a3\u0251\u1e04\u1ef5\5\2C\\aac|\3\2"+
		"\62;\3\2\63;\3\2$$\4\2$$^^\3\2^^\3\2))\4\2))^^\5\2\"\"C\\c|\5\2\13\f\17"+
		"\17\"\"\2\u01d3\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2"+
		"\2\2S\3\2\2\2\2U\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
		"g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\3o\3\2\2\2\5q\3\2\2\2\7s\3"+
		"\2\2\2\tu\3\2\2\2\13x\3\2\2\2\rz\3\2\2\2\17\u0083\3\2\2\2\21\u008c\3\2"+
		"\2\2\23\u0090\3\2\2\2\25\u00a0\3\2\2\2\27\u00aa\3\2\2\2\31\u00b7\3\2\2"+
		"\2\33\u00bf\3\2\2\2\35\u00ce\3\2\2\2\37\u00d5\3\2\2\2!\u010e\3\2\2\2#"+
		"\u0110\3\2\2\2%\u0116\3\2\2\2\'\u011b\3\2\2\2)\u0129\3\2\2\2+\u012b\3"+
		"\2\2\2-\u012d\3\2\2\2/\u012f\3\2\2\2\61\u0131\3\2\2\2\63\u0133\3\2\2\2"+
		"\65\u0135\3\2\2\2\67\u0140\3\2\2\29\u014f\3\2\2\2;\u0151\3\2\2\2=\u0153"+
		"\3\2\2\2?\u0155\3\2\2\2A\u0157\3\2\2\2C\u0159\3\2\2\2E\u015c\3\2\2\2G"+
		"\u015e\3\2\2\2I\u0161\3\2\2\2K\u0163\3\2\2\2M\u0166\3\2\2\2O\u0169\3\2"+
		"\2\2Q\u016b\3\2\2\2S\u016e\3\2\2\2U\u0185\3\2\2\2W\u0187\3\2\2\2Y\u0189"+
		"\3\2\2\2[\u018b\3\2\2\2]\u018d\3\2\2\2_\u0190\3\2\2\2a\u0194\3\2\2\2c"+
		"\u019f\3\2\2\2e\u01aa\3\2\2\2g\u01b4\3\2\2\2i\u01ba\3\2\2\2k\u01be\3\2"+
		"\2\2m\u01c0\3\2\2\2op\7<\2\2p\4\3\2\2\2qr\7*\2\2r\6\3\2\2\2st\7+\2\2t"+
		"\b\3\2\2\2uv\7/\2\2vw\7@\2\2w\n\3\2\2\2xy\7.\2\2y\f\3\2\2\2z{\7<\2\2{"+
		"|\7K\2\2|}\7I\2\2}~\7P\2\2~\177\7Q\2\2\177\u0080\7T\2\2\u0080\u0081\7"+
		"G\2\2\u0081\u0082\7<\2\2\u0082\16\3\2\2\2\u0083\u0084\7<\2\2\u0084\u0085"+
		"\7G\2\2\u0085\u0086\7Z\2\2\u0086\u0087\7E\2\2\u0087\u0088\7G\2\2\u0088"+
		"\u0089\7R\2\2\u0089\u008a\7V\2\2\u008a\u008b\7<\2\2\u008b\20\3\2\2\2\u008c"+
		"\u008d\7>\2\2\u008d\u008e\7/\2\2\u008e\u008f\7@\2\2\u008f\22\3\2\2\2\u0090"+
		"\u0091\7<\2\2\u0091\u0092\7V\2\2\u0092\u0093\7J\2\2\u0093\u0094\7G\2\2"+
		"\u0094\u0095\7\"\2\2\u0095\u0096\7E\2\2\u0096\u0097\7Q\2\2\u0097\u0098"+
		"\7P\2\2\u0098\u0099\7F\2\2\u0099\u009a\7K\2\2\u009a\u009b\7V\2\2\u009b"+
		"\u009c\7K\2\2\u009c\u009d\7Q\2\2\u009d\u009e\7P\2\2\u009e\u009f\7<\2\2"+
		"\u009f\24\3\2\2\2\u00a0\u00a1\7<\2\2\u00a1\u00a2\7K\2\2\u00a2\u00a3\7"+
		"O\2\2\u00a3\u00a4\7R\2\2\u00a4\u00a5\7N\2\2\u00a5\u00a6\7K\2\2\u00a6\u00a7"+
		"\7G\2\2\u00a7\u00a8\7U\2\2\u00a8\u00a9\7<\2\2\u00a9\26\3\2\2\2\u00aa\u00ab"+
		"\7<\2\2\u00ab\u00ac\7V\2\2\u00ac\u00ad\7G\2\2\u00ad\u00ae\7U\2\2\u00ae"+
		"\u00af\7V\2\2\u00af\u00b0\7\"\2\2\u00b0\u00b1\7K\2\2\u00b1\u00b2\7P\2"+
		"\2\u00b2\u00b3\7R\2\2\u00b3\u00b4\7W\2\2\u00b4\u00b5\7V\2\2\u00b5\u00b6"+
		"\7<\2\2\u00b6\30\3\2\2\2\u00b7\u00b8\7<\2\2\u00b8\u00b9\7O\2\2\u00b9\u00ba"+
		"\7C\2\2\u00ba\u00bb\7E\2\2\u00bb\u00bc\7T\2\2\u00bc\u00bd\7Q\2\2\u00bd"+
		"\u00be\7<\2\2\u00be\32\3\2\2\2\u00bf\u00c0\7<\2\2\u00c0\u00c1\7G\2\2\u00c1"+
		"\u00c2\7P\2\2\u00c2\u00c3\7F\2\2\u00c3\u00c4\7\"\2\2\u00c4\u00c5\7Q\2"+
		"\2\u00c5\u00c6\7H\2\2\u00c6\u00c7\7\"\2\2\u00c7\u00c8\7O\2\2\u00c8\u00c9"+
		"\7C\2\2\u00c9\u00ca\7E\2\2\u00ca\u00cb\7T\2\2\u00cb\u00cc\7Q\2\2\u00cc"+
		"\u00cd\7<\2\2\u00cd\34\3\2\2\2\u00ce\u00cf\7/\2\2\u00cf\u00d0\7/\2\2\u00d0"+
		"\u00d1\7/\2\2\u00d1\u00d2\7\61\2\2\u00d2\u00d3\7\61\2\2\u00d3\u00d4\7"+
		"\61\2\2\u00d4\36\3\2\2\2\u00d5\u00d6\7U\2\2\u00d6\u00d7\7m\2\2\u00d7\u00d8"+
		"\7g\2\2\u00d8\u00d9\7v\2\2\u00d9\u00da\7e\2\2\u00da\u00db\7j\2\2\u00db"+
		"\u00dc\7\"\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7p\2\2\u00de\u00df\7h\2"+
		"\2\u00df\u00e0\7q\2\2\u00e0\u00e1\7t\2\2\u00e1\u00e2\7o\2\2\u00e2\u00e3"+
		"\7c\2\2\u00e3\u00e4\7v\2\2\u00e4\u00e5\7k\2\2\u00e5\u00e6\7q\2\2\u00e6"+
		"\u00e7\7p\2\2\u00e7\u00e8\7\"\2\2\u00e8\u00e9\7/\2\2\u00e9\u00ea\7\"\2"+
		"\2\u00ea\u00eb\7f\2\2\u00eb\u00ec\7q\2\2\u00ec\u00ed\7\"\2\2\u00ed\u00ee"+
		"\7p\2\2\u00ee\u00ef\7q\2\2\u00ef\u00f0\7v\2\2\u00f0\u00f1\7\"\2\2\u00f1"+
		"\u00f2\7o\2\2\u00f2\u00f3\7q\2\2\u00f3\u00f4\7f\2\2\u00f4\u00f5\7k\2\2"+
		"\u00f5\u00f6\7h\2\2\u00f6\u00f7\7{\2\2\u00f7\u00f8\7\"\2\2\u00f8\u00f9"+
		"\7c\2\2\u00f9\u00fa\7p\2\2\u00fa\u00fb\7{\2\2\u00fb\u00fc\7v\2\2\u00fc"+
		"\u00fd\7j\2\2\u00fd\u00fe\7k\2\2\u00fe\u00ff\7p\2\2\u00ff\u0100\7i\2\2"+
		"\u0100\u0101\7\"\2\2\u0101\u0102\7g\2\2\u0102\u0103\7z\2\2\u0103\u0104"+
		"\7e\2\2\u0104\u0105\7g\2\2\u0105\u0106\7r\2\2\u0106\u0107\7v\2\2\u0107"+
		"\u0108\7\"\2\2\u0108\u0109\7p\2\2\u0109\u010a\7c\2\2\u010a\u010b\7o\2"+
		"\2\u010b\u010c\7g\2\2\u010c\u010d\7u\2\2\u010d \3\2\2\2\u010e\u010f\7"+
		"`\2\2\u010f\"\3\2\2\2\u0110\u0111\7<\2\2\u0111\u0112\7C\2\2\u0112\u0113"+
		"\7P\2\2\u0113\u0114\7F\2\2\u0114\u0115\7<\2\2\u0115$\3\2\2\2\u0116\u0117"+
		"\7<\2\2\u0117\u0118\7Q\2\2\u0118\u0119\7T\2\2\u0119\u011a\7<\2\2\u011a"+
		"&\3\2\2\2\u011b\u011c\7V\2\2\u011c\u011d\7C\2\2\u011d\u011e\7D\2\2\u011e"+
		"\u011f\7D\2\2\u011f\u0120\7G\2\2\u0120\u0121\7F\2\2\u0121\u0122\7\"\2"+
		"\2\u0122\u0123\7C\2\2\u0123\u0124\7T\2\2\u0124\u0125\7T\2\2\u0125\u0126"+
		"\7C\2\2\u0126\u0127\7[\2\2\u0127\u0128\7*\2\2\u0128(\3\2\2\2\u0129\u012a"+
		"\7]\2\2\u012a*\3\2\2\2\u012b\u012c\7_\2\2\u012c,\3\2\2\2\u012d\u012e\7"+
		"=\2\2\u012e.\3\2\2\2\u012f\u0130\7\60\2\2\u0130\60\3\2\2\2\u0131\u0132"+
		"\7g\2\2\u0132\62\3\2\2\2\u0133\u0134\7G\2\2\u0134\64\3\2\2\2\u0135\u0139"+
		"\7}\2\2\u0136\u0138\13\2\2\2\u0137\u0136\3\2\2\2\u0138\u013b\3\2\2\2\u0139"+
		"\u013a\3\2\2\2\u0139\u0137\3\2\2\2\u013a\u013c\3\2\2\2\u013b\u0139\3\2"+
		"\2\2\u013c\u013d\7\177\2\2\u013d\u013e\3\2\2\2\u013e\u013f\b\33\2\2\u013f"+
		"\66\3\2\2\2\u0140\u0141\7,\2\2\u0141\u0142\7,\2\2\u0142\u0143\7,\2\2\u0143"+
		"\u0144\7,\2\2\u0144\u0148\3\2\2\2\u0145\u0147\13\2\2\2\u0146\u0145\3\2"+
		"\2\2\u0147\u014a\3\2\2\2\u0148\u0149\3\2\2\2\u0148\u0146\3\2\2\2\u0149"+
		"\u014b\3\2\2\2\u014a\u0148\3\2\2\2\u014b\u014c\7~\2\2\u014c\u014d\3\2"+
		"\2\2\u014d\u014e\b\34\2\2\u014e8\3\2\2\2\u014f\u0150\7,\2\2\u0150:\3\2"+
		"\2\2\u0151\u0152\7\61\2\2\u0152<\3\2\2\2\u0153\u0154\7-\2\2\u0154>\3\2"+
		"\2\2\u0155\u0156\7/\2\2\u0156@\3\2\2\2\u0157\u0158\7>\2\2\u0158B\3\2\2"+
		"\2\u0159\u015a\7>\2\2\u015a\u015b\7?\2\2\u015bD\3\2\2\2\u015c\u015d\7"+
		"@\2\2\u015dF\3\2\2\2\u015e\u015f\7@\2\2\u015f\u0160\7?\2\2\u0160H\3\2"+
		"\2\2\u0161\u0162\7?\2\2\u0162J\3\2\2\2\u0163\u0164\7?\2\2\u0164\u0165"+
		"\7?\2\2\u0165L\3\2\2\2\u0166\u0167\7>\2\2\u0167\u0168\7@\2\2\u0168N\3"+
		"\2\2\2\u0169\u016a\7#\2\2\u016aP\3\2\2\2\u016b\u016c\7<\2\2\u016c\u016d"+
		"\7?\2\2\u016dR\3\2\2\2\u016e\u016f\7<\2\2\u016f\u0170\7K\2\2\u0170\u0171"+
		"\7U\2\2\u0171\u0172\7<\2\2\u0172T\3\2\2\2\u0173\u0177\5Y-\2\u0174\u0176"+
		"\5W,\2\u0175\u0174\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2\2\2\u0177"+
		"\u0178\3\2\2\2\u0178\u0186\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017f\5Y"+
		"-\2\u017b\u017e\5W,\2\u017c\u017e\7\"\2\2\u017d\u017b\3\2\2\2\u017d\u017c"+
		"\3\2\2\2\u017e\u0181\3\2\2\2\u017f\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180"+
		"\u0182\3\2\2\2\u0181\u017f\3\2\2\2\u0182\u0183\5W,\2\u0183\u0186\3\2\2"+
		"\2\u0184\u0186\5a\61\2\u0185\u0173\3\2\2\2\u0185\u017a\3\2\2\2\u0185\u0184"+
		"\3\2\2\2\u0186V\3\2\2\2\u0187\u0188\t\2\2\2\u0188X\3\2\2\2\u0189\u018a"+
		"\t\3\2\2\u018aZ\3\2\2\2\u018b\u018c\t\4\2\2\u018c\\\3\2\2\2\u018d\u018e"+
		"\t\5\2\2\u018e^\3\2\2\2\u018f\u0191\5[.\2\u0190\u018f\3\2\2\2\u0191\u0192"+
		"\3\2\2\2\u0192\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193`\3\2\2\2\u0194"+
		"\u019a\t\6\2\2\u0195\u0199\n\7\2\2\u0196\u0197\t\b\2\2\u0197\u0199\13"+
		"\2\2\2\u0198\u0195\3\2\2\2\u0198\u0196\3\2\2\2\u0199\u019c\3\2\2\2\u019a"+
		"\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019d\3\2\2\2\u019c\u019a\3\2"+
		"\2\2\u019d\u019e\t\6\2\2\u019eb\3\2\2\2\u019f\u01a5\t\t\2\2\u01a0\u01a4"+
		"\n\n\2\2\u01a1\u01a2\t\b\2\2\u01a2\u01a4\13\2\2\2\u01a3\u01a0\3\2\2\2"+
		"\u01a3\u01a1\3\2\2\2\u01a4\u01a7\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5\u01a6"+
		"\3\2\2\2\u01a6\u01a8\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a8\u01a9\t\t\2\2\u01a9"+
		"d\3\2\2\2\u01aa\u01ae\7<\2\2\u01ab\u01ad\t\13\2\2\u01ac\u01ab\3\2\2\2"+
		"\u01ad\u01b0\3\2\2\2\u01ae\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01b1"+
		"\3\2\2\2\u01b0\u01ae\3\2\2\2\u01b1\u01b2\7<\2\2\u01b2f\3\2\2\2\u01b3\u01b5"+
		"\t\f\2\2\u01b4\u01b3\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6\u01b4\3\2\2\2\u01b6"+
		"\u01b7\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01b9\b\64\2\2\u01b9h\3\2\2\2"+
		"\u01ba\u01bb\t\b\2\2\u01bb\u01bc\3\2\2\2\u01bc\u01bd\b\65\2\2\u01bdj\3"+
		"\2\2\2\u01be\u01bf\13\2\2\2\u01bfl\3\2\2\2\u01c0\u01c4\7\u0080\2\2\u01c1"+
		"\u01c3\13\2\2\2\u01c2\u01c1\3\2\2\2\u01c3\u01c6\3\2\2\2\u01c4\u01c5\3"+
		"\2\2\2\u01c4\u01c2\3\2\2\2\u01c5\u01c7\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c7"+
		"\u01c8\7~\2\2\u01c8n\3\2\2\2\21\2\u0139\u0148\u0177\u017d\u017f\u0185"+
		"\u0192\u0198\u019a\u01a3\u01a5\u01ae\u01b6\u01c4\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}