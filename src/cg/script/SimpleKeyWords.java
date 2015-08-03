package cg.script;

public class SimpleKeyWords implements KeyWords {
	
	private final String[][] OPL = new String[][]{
		{"||"}, 
		{"&&"}, 
		{"=="}, 
		{"<", "<=", ">", ">="}, 
		{"+", "-"}, 
		{"*", "/", "%"}, 
		{"(", ")"}, 
	};
	
	@Override
	public boolean isMethod(String word) {
		return word.equals("if") || word.equals("max");
	}
	
	@Override
	public boolean isMath(String word) {
		return word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/") || word.equals("%") 
				|| word.equals("&") || word.equals("|");
	}
	
	@Override
	public boolean isExpression(String word) {
		return word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/") || word.equals("%") 
				|| word.equals("&") || word.equals("|") || word.equals("&&") || word.equals("||") 
				|| word.equals(">") || word.equals(">=") || word.equals("<") || word.equals("<=") 
				|| word.equals("==") || word.equals("(") || word.equals(")") || word.equals("=") 
				|| word.equals("!=") ;
	}
	
	@Override
	public byte getOperationLevel(String word) {
		for (byte i = 0;i < OPL.length;i++) {
			for (String op : OPL[i]) {
				if (word.equals(op)) {
					return i;
				}
			}
		}
		throw new ScriptException("Unsupport operation : " + word + " .");
	}
	
	@Override
	public boolean expressionParam(String method) {
		return method.equals("if");
	}
	
	@Override
	public boolean check(String word) {
		return true;
	}

	@Override
	public boolean startAnnotate(String word) {
		return word.equals("/*");
	}

	@Override
	public boolean endAnnotate(String word) {
		return word.equals("*/");
	}

}
