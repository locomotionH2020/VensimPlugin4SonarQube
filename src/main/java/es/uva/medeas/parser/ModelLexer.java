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
		T__24=25, T__25=26, CommentOrEncoding=27, Group=28, Star=29, Div=30, Plus=31, 
		Minus=32, Less=33, LessEqual=34, Greater=35, GreaterEqual=36, Equal=37, 
		TwoEqual=38, NotEqual=39, Exclamation=40, DataEquationOp=41, StringAssignOp=42, 
		Id=43, DigitSeq=44, StringLiteral=45, StringConst=46, Keyword=47, Whitespace=48, 
		Backslash=49, OtherCaracter=50, COMMENTS=51;
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
			"T__25", "CommentOrEncoding", "Group", "Star", "Div", "Plus", "Minus", 
			"Less", "LessEqual", "Greater", "GreaterEqual", "Equal", "TwoEqual", 
			"NotEqual", "Exclamation", "DataEquationOp", "StringAssignOp", "Id", 
			"IdChar", "Nondigit", "Digit", "NonzeroDigit", "DigitSeq", "StringLiteral", 
			"StringConst", "Keyword", "Whitespace", "Backslash", "OtherCaracter", 
			"COMMENTS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "':'", "'('", "')'", "'->'", "','", "':IGNORE:'", "':EXCEPT:'", 
			"'<->'", "':THE CONDITION:'", "':IMPLIES:'", "':TEST INPUT:'", "':MACRO:'", 
			"':END OF MACRO:'", "'---///'", "'Sketch information - do not modify anything except names'", 
			"'^'", "':AND:'", "':OR:'", "'DELAYP('", "'TABBED ARRAY('", "'['", "']'", 
			"';'", "'.'", "'e'", "'E'", null, null, "'*'", "'/'", "'+'", "'-'", "'<'", 
			"'<='", "'>'", "'>='", "'='", "'=='", "'<>'", "'!'", "':='", "':IS:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "CommentOrEncoding", "Group", "Star", "Div", "Plus", 
			"Minus", "Less", "LessEqual", "Greater", "GreaterEqual", "Equal", "TwoEqual", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\65\u01d3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\3\2\3\3\3\3\3\4\3\4\3\5"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3"+
		"\31\3\32\3\32\3\33\3\33\3\34\3\34\7\34\u0142\n\34\f\34\16\34\u0145\13"+
		"\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u0151\n\35"+
		"\f\35\16\35\u0154\13\35\3\35\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 "+
		"\3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3"+
		")\3*\3*\3*\3+\3+\3+\3+\3+\3,\3,\7,\u0180\n,\f,\16,\u0183\13,\3,\3,\3,"+
		"\7,\u0188\n,\f,\16,\u018b\13,\3,\3,\3,\5,\u0190\n,\3-\3-\3.\3.\3/\3/\3"+
		"\60\3\60\3\61\6\61\u019b\n\61\r\61\16\61\u019c\3\62\3\62\3\62\3\62\7\62"+
		"\u01a3\n\62\f\62\16\62\u01a6\13\62\3\62\3\62\3\63\3\63\3\63\3\63\7\63"+
		"\u01ae\n\63\f\63\16\63\u01b1\13\63\3\63\3\63\3\64\3\64\7\64\u01b7\n\64"+
		"\f\64\16\64\u01ba\13\64\3\64\3\64\3\65\6\65\u01bf\n\65\r\65\16\65\u01c0"+
		"\3\65\3\65\3\66\3\66\3\66\3\66\3\67\3\67\38\38\78\u01cd\n8\f8\168\u01d0"+
		"\138\38\38\5\u0143\u0152\u01ce\29\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y\2[\2]"+
		"\2_\2a.c/e\60g\61i\62k\63m\64o\65\3\2\r\t\2&)\62;C\\aac|\u00a3\u0251\u1e04"+
		"\u1ef5\5\2C\\aac|\3\2\62;\3\2\63;\3\2$$\4\2$$^^\3\2^^\3\2))\4\2))^^\5"+
		"\2\"\"C\\c|\5\2\13\f\17\17\"\"\2\u01dd\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2"+
		"\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2a\3\2\2\2\2"+
		"c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3"+
		"\2\2\2\3q\3\2\2\2\5s\3\2\2\2\7u\3\2\2\2\tw\3\2\2\2\13z\3\2\2\2\r|\3\2"+
		"\2\2\17\u0085\3\2\2\2\21\u008e\3\2\2\2\23\u0092\3\2\2\2\25\u00a2\3\2\2"+
		"\2\27\u00ac\3\2\2\2\31\u00b9\3\2\2\2\33\u00c1\3\2\2\2\35\u00d0\3\2\2\2"+
		"\37\u00d7\3\2\2\2!\u0110\3\2\2\2#\u0112\3\2\2\2%\u0118\3\2\2\2\'\u011d"+
		"\3\2\2\2)\u0125\3\2\2\2+\u0133\3\2\2\2-\u0135\3\2\2\2/\u0137\3\2\2\2\61"+
		"\u0139\3\2\2\2\63\u013b\3\2\2\2\65\u013d\3\2\2\2\67\u013f\3\2\2\29\u014a"+
		"\3\2\2\2;\u0159\3\2\2\2=\u015b\3\2\2\2?\u015d\3\2\2\2A\u015f\3\2\2\2C"+
		"\u0161\3\2\2\2E\u0163\3\2\2\2G\u0166\3\2\2\2I\u0168\3\2\2\2K\u016b\3\2"+
		"\2\2M\u016d\3\2\2\2O\u0170\3\2\2\2Q\u0173\3\2\2\2S\u0175\3\2\2\2U\u0178"+
		"\3\2\2\2W\u018f\3\2\2\2Y\u0191\3\2\2\2[\u0193\3\2\2\2]\u0195\3\2\2\2_"+
		"\u0197\3\2\2\2a\u019a\3\2\2\2c\u019e\3\2\2\2e\u01a9\3\2\2\2g\u01b4\3\2"+
		"\2\2i\u01be\3\2\2\2k\u01c4\3\2\2\2m\u01c8\3\2\2\2o\u01ca\3\2\2\2qr\7<"+
		"\2\2r\4\3\2\2\2st\7*\2\2t\6\3\2\2\2uv\7+\2\2v\b\3\2\2\2wx\7/\2\2xy\7@"+
		"\2\2y\n\3\2\2\2z{\7.\2\2{\f\3\2\2\2|}\7<\2\2}~\7K\2\2~\177\7I\2\2\177"+
		"\u0080\7P\2\2\u0080\u0081\7Q\2\2\u0081\u0082\7T\2\2\u0082\u0083\7G\2\2"+
		"\u0083\u0084\7<\2\2\u0084\16\3\2\2\2\u0085\u0086\7<\2\2\u0086\u0087\7"+
		"G\2\2\u0087\u0088\7Z\2\2\u0088\u0089\7E\2\2\u0089\u008a\7G\2\2\u008a\u008b"+
		"\7R\2\2\u008b\u008c\7V\2\2\u008c\u008d\7<\2\2\u008d\20\3\2\2\2\u008e\u008f"+
		"\7>\2\2\u008f\u0090\7/\2\2\u0090\u0091\7@\2\2\u0091\22\3\2\2\2\u0092\u0093"+
		"\7<\2\2\u0093\u0094\7V\2\2\u0094\u0095\7J\2\2\u0095\u0096\7G\2\2\u0096"+
		"\u0097\7\"\2\2\u0097\u0098\7E\2\2\u0098\u0099\7Q\2\2\u0099\u009a\7P\2"+
		"\2\u009a\u009b\7F\2\2\u009b\u009c\7K\2\2\u009c\u009d\7V\2\2\u009d\u009e"+
		"\7K\2\2\u009e\u009f\7Q\2\2\u009f\u00a0\7P\2\2\u00a0\u00a1\7<\2\2\u00a1"+
		"\24\3\2\2\2\u00a2\u00a3\7<\2\2\u00a3\u00a4\7K\2\2\u00a4\u00a5\7O\2\2\u00a5"+
		"\u00a6\7R\2\2\u00a6\u00a7\7N\2\2\u00a7\u00a8\7K\2\2\u00a8\u00a9\7G\2\2"+
		"\u00a9\u00aa\7U\2\2\u00aa\u00ab\7<\2\2\u00ab\26\3\2\2\2\u00ac\u00ad\7"+
		"<\2\2\u00ad\u00ae\7V\2\2\u00ae\u00af\7G\2\2\u00af\u00b0\7U\2\2\u00b0\u00b1"+
		"\7V\2\2\u00b1\u00b2\7\"\2\2\u00b2\u00b3\7K\2\2\u00b3\u00b4\7P\2\2\u00b4"+
		"\u00b5\7R\2\2\u00b5\u00b6\7W\2\2\u00b6\u00b7\7V\2\2\u00b7\u00b8\7<\2\2"+
		"\u00b8\30\3\2\2\2\u00b9\u00ba\7<\2\2\u00ba\u00bb\7O\2\2\u00bb\u00bc\7"+
		"C\2\2\u00bc\u00bd\7E\2\2\u00bd\u00be\7T\2\2\u00be\u00bf\7Q\2\2\u00bf\u00c0"+
		"\7<\2\2\u00c0\32\3\2\2\2\u00c1\u00c2\7<\2\2\u00c2\u00c3\7G\2\2\u00c3\u00c4"+
		"\7P\2\2\u00c4\u00c5\7F\2\2\u00c5\u00c6\7\"\2\2\u00c6\u00c7\7Q\2\2\u00c7"+
		"\u00c8\7H\2\2\u00c8\u00c9\7\"\2\2\u00c9\u00ca\7O\2\2\u00ca\u00cb\7C\2"+
		"\2\u00cb\u00cc\7E\2\2\u00cc\u00cd\7T\2\2\u00cd\u00ce\7Q\2\2\u00ce\u00cf"+
		"\7<\2\2\u00cf\34\3\2\2\2\u00d0\u00d1\7/\2\2\u00d1\u00d2\7/\2\2\u00d2\u00d3"+
		"\7/\2\2\u00d3\u00d4\7\61\2\2\u00d4\u00d5\7\61\2\2\u00d5\u00d6\7\61\2\2"+
		"\u00d6\36\3\2\2\2\u00d7\u00d8\7U\2\2\u00d8\u00d9\7m\2\2\u00d9\u00da\7"+
		"g\2\2\u00da\u00db\7v\2\2\u00db\u00dc\7e\2\2\u00dc\u00dd\7j\2\2\u00dd\u00de"+
		"\7\"\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7p\2\2\u00e0\u00e1\7h\2\2\u00e1"+
		"\u00e2\7q\2\2\u00e2\u00e3\7t\2\2\u00e3\u00e4\7o\2\2\u00e4\u00e5\7c\2\2"+
		"\u00e5\u00e6\7v\2\2\u00e6\u00e7\7k\2\2\u00e7\u00e8\7q\2\2\u00e8\u00e9"+
		"\7p\2\2\u00e9\u00ea\7\"\2\2\u00ea\u00eb\7/\2\2\u00eb\u00ec\7\"\2\2\u00ec"+
		"\u00ed\7f\2\2\u00ed\u00ee\7q\2\2\u00ee\u00ef\7\"\2\2\u00ef\u00f0\7p\2"+
		"\2\u00f0\u00f1\7q\2\2\u00f1\u00f2\7v\2\2\u00f2\u00f3\7\"\2\2\u00f3\u00f4"+
		"\7o\2\2\u00f4\u00f5\7q\2\2\u00f5\u00f6\7f\2\2\u00f6\u00f7\7k\2\2\u00f7"+
		"\u00f8\7h\2\2\u00f8\u00f9\7{\2\2\u00f9\u00fa\7\"\2\2\u00fa\u00fb\7c\2"+
		"\2\u00fb\u00fc\7p\2\2\u00fc\u00fd\7{\2\2\u00fd\u00fe\7v\2\2\u00fe\u00ff"+
		"\7j\2\2\u00ff\u0100\7k\2\2\u0100\u0101\7p\2\2\u0101\u0102\7i\2\2\u0102"+
		"\u0103\7\"\2\2\u0103\u0104\7g\2\2\u0104\u0105\7z\2\2\u0105\u0106\7e\2"+
		"\2\u0106\u0107\7g\2\2\u0107\u0108\7r\2\2\u0108\u0109\7v\2\2\u0109\u010a"+
		"\7\"\2\2\u010a\u010b\7p\2\2\u010b\u010c\7c\2\2\u010c\u010d\7o\2\2\u010d"+
		"\u010e\7g\2\2\u010e\u010f\7u\2\2\u010f \3\2\2\2\u0110\u0111\7`\2\2\u0111"+
		"\"\3\2\2\2\u0112\u0113\7<\2\2\u0113\u0114\7C\2\2\u0114\u0115\7P\2\2\u0115"+
		"\u0116\7F\2\2\u0116\u0117\7<\2\2\u0117$\3\2\2\2\u0118\u0119\7<\2\2\u0119"+
		"\u011a\7Q\2\2\u011a\u011b\7T\2\2\u011b\u011c\7<\2\2\u011c&\3\2\2\2\u011d"+
		"\u011e\7F\2\2\u011e\u011f\7G\2\2\u011f\u0120\7N\2\2\u0120\u0121\7C\2\2"+
		"\u0121\u0122\7[\2\2\u0122\u0123\7R\2\2\u0123\u0124\7*\2\2\u0124(\3\2\2"+
		"\2\u0125\u0126\7V\2\2\u0126\u0127\7C\2\2\u0127\u0128\7D\2\2\u0128\u0129"+
		"\7D\2\2\u0129\u012a\7G\2\2\u012a\u012b\7F\2\2\u012b\u012c\7\"\2\2\u012c"+
		"\u012d\7C\2\2\u012d\u012e\7T\2\2\u012e\u012f\7T\2\2\u012f\u0130\7C\2\2"+
		"\u0130\u0131\7[\2\2\u0131\u0132\7*\2\2\u0132*\3\2\2\2\u0133\u0134\7]\2"+
		"\2\u0134,\3\2\2\2\u0135\u0136\7_\2\2\u0136.\3\2\2\2\u0137\u0138\7=\2\2"+
		"\u0138\60\3\2\2\2\u0139\u013a\7\60\2\2\u013a\62\3\2\2\2\u013b\u013c\7"+
		"g\2\2\u013c\64\3\2\2\2\u013d\u013e\7G\2\2\u013e\66\3\2\2\2\u013f\u0143"+
		"\7}\2\2\u0140\u0142\13\2\2\2\u0141\u0140\3\2\2\2\u0142\u0145\3\2\2\2\u0143"+
		"\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0146\3\2\2\2\u0145\u0143\3\2"+
		"\2\2\u0146\u0147\7\177\2\2\u0147\u0148\3\2\2\2\u0148\u0149\b\34\2\2\u0149"+
		"8\3\2\2\2\u014a\u014b\7,\2\2\u014b\u014c\7,\2\2\u014c\u014d\7,\2\2\u014d"+
		"\u014e\7,\2\2\u014e\u0152\3\2\2\2\u014f\u0151\13\2\2\2\u0150\u014f\3\2"+
		"\2\2\u0151\u0154\3\2\2\2\u0152\u0153\3\2\2\2\u0152\u0150\3\2\2\2\u0153"+
		"\u0155\3\2\2\2\u0154\u0152\3\2\2\2\u0155\u0156\7~\2\2\u0156\u0157\3\2"+
		"\2\2\u0157\u0158\b\35\2\2\u0158:\3\2\2\2\u0159\u015a\7,\2\2\u015a<\3\2"+
		"\2\2\u015b\u015c\7\61\2\2\u015c>\3\2\2\2\u015d\u015e\7-\2\2\u015e@\3\2"+
		"\2\2\u015f\u0160\7/\2\2\u0160B\3\2\2\2\u0161\u0162\7>\2\2\u0162D\3\2\2"+
		"\2\u0163\u0164\7>\2\2\u0164\u0165\7?\2\2\u0165F\3\2\2\2\u0166\u0167\7"+
		"@\2\2\u0167H\3\2\2\2\u0168\u0169\7@\2\2\u0169\u016a\7?\2\2\u016aJ\3\2"+
		"\2\2\u016b\u016c\7?\2\2\u016cL\3\2\2\2\u016d\u016e\7?\2\2\u016e\u016f"+
		"\7?\2\2\u016fN\3\2\2\2\u0170\u0171\7>\2\2\u0171\u0172\7@\2\2\u0172P\3"+
		"\2\2\2\u0173\u0174\7#\2\2\u0174R\3\2\2\2\u0175\u0176\7<\2\2\u0176\u0177"+
		"\7?\2\2\u0177T\3\2\2\2\u0178\u0179\7<\2\2\u0179\u017a\7K\2\2\u017a\u017b"+
		"\7U\2\2\u017b\u017c\7<\2\2\u017cV\3\2\2\2\u017d\u0181\5[.\2\u017e\u0180"+
		"\5Y-\2\u017f\u017e\3\2\2\2\u0180\u0183\3\2\2\2\u0181\u017f\3\2\2\2\u0181"+
		"\u0182\3\2\2\2\u0182\u0190\3\2\2\2\u0183\u0181\3\2\2\2\u0184\u0189\5["+
		".\2\u0185\u0188\5Y-\2\u0186\u0188\7\"\2\2\u0187\u0185\3\2\2\2\u0187\u0186"+
		"\3\2\2\2\u0188\u018b\3\2\2\2\u0189\u0187\3\2\2\2\u0189\u018a\3\2\2\2\u018a"+
		"\u018c\3\2\2\2\u018b\u0189\3\2\2\2\u018c\u018d\5Y-\2\u018d\u0190\3\2\2"+
		"\2\u018e\u0190\5c\62\2\u018f\u017d\3\2\2\2\u018f\u0184\3\2\2\2\u018f\u018e"+
		"\3\2\2\2\u0190X\3\2\2\2\u0191\u0192\t\2\2\2\u0192Z\3\2\2\2\u0193\u0194"+
		"\t\3\2\2\u0194\\\3\2\2\2\u0195\u0196\t\4\2\2\u0196^\3\2\2\2\u0197\u0198"+
		"\t\5\2\2\u0198`\3\2\2\2\u0199\u019b\5]/\2\u019a\u0199\3\2\2\2\u019b\u019c"+
		"\3\2\2\2\u019c\u019a\3\2\2\2\u019c\u019d\3\2\2\2\u019db\3\2\2\2\u019e"+
		"\u01a4\t\6\2\2\u019f\u01a3\n\7\2\2\u01a0\u01a1\t\b\2\2\u01a1\u01a3\13"+
		"\2\2\2\u01a2\u019f\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a3\u01a6\3\2\2\2\u01a4"+
		"\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a7\3\2\2\2\u01a6\u01a4\3\2"+
		"\2\2\u01a7\u01a8\t\6\2\2\u01a8d\3\2\2\2\u01a9\u01af\t\t\2\2\u01aa\u01ae"+
		"\n\n\2\2\u01ab\u01ac\t\b\2\2\u01ac\u01ae\13\2\2\2\u01ad\u01aa\3\2\2\2"+
		"\u01ad\u01ab\3\2\2\2\u01ae\u01b1\3\2\2\2\u01af\u01ad\3\2\2\2\u01af\u01b0"+
		"\3\2\2\2\u01b0\u01b2\3\2\2\2\u01b1\u01af\3\2\2\2\u01b2\u01b3\t\t\2\2\u01b3"+
		"f\3\2\2\2\u01b4\u01b8\7<\2\2\u01b5\u01b7\t\13\2\2\u01b6\u01b5\3\2\2\2"+
		"\u01b7\u01ba\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01bb"+
		"\3\2\2\2\u01ba\u01b8\3\2\2\2\u01bb\u01bc\7<\2\2\u01bch\3\2\2\2\u01bd\u01bf"+
		"\t\f\2\2\u01be\u01bd\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0\u01be\3\2\2\2\u01c0"+
		"\u01c1\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2\u01c3\b\65\2\2\u01c3j\3\2\2\2"+
		"\u01c4\u01c5\t\b\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01c7\b\66\2\2\u01c7l\3"+
		"\2\2\2\u01c8\u01c9\13\2\2\2\u01c9n\3\2\2\2\u01ca\u01ce\7\u0080\2\2\u01cb"+
		"\u01cd\13\2\2\2\u01cc\u01cb\3\2\2\2\u01cd\u01d0\3\2\2\2\u01ce\u01cf\3"+
		"\2\2\2\u01ce\u01cc\3\2\2\2\u01cf\u01d1\3\2\2\2\u01d0\u01ce\3\2\2\2\u01d1"+
		"\u01d2\7~\2\2\u01d2p\3\2\2\2\21\2\u0143\u0152\u0181\u0187\u0189\u018f"+
		"\u019c\u01a2\u01a4\u01ad\u01af\u01b8\u01c0\u01ce\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}