package cg.script;

public interface KeyWords {
	
	boolean check(String word);
	
	boolean isMethod(String word);
	
	boolean isExpression(String word);
	
	boolean isMath(String word);
	
	boolean expressionParam(String method);
	
	boolean startAnnotate(String word);
	
	boolean endAnnotate(String word);
	
	byte getOperationLevel(String word);

}
