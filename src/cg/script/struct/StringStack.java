package cg.script.struct;

public class StringStack {
	
	public static final int NULL = -1;
	
	protected final String[] stack;
	
	protected int top;
	
	public StringStack(int size) {
		stack = new String[size];
		top = NULL;
	}
	
	public void push(String element) {
		stack[++top] = element;
	}
	
	public String peek() {
		return stack[top];
	}
	
	public String pop() {
		return stack[top--];
	}
	
	public boolean isNull() {
		return top == NULL;
	}

}
