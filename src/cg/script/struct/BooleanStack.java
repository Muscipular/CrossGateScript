package cg.script.struct;

public class BooleanStack {
	
	public static final int NULL = -1;
	
	protected final boolean[] stack;
	
	protected int top;
	
	public BooleanStack(int size) {
		stack = new boolean[size];
		top = NULL;
	}
	
	public void push(boolean element) {
		stack[++top] = element;
	}
	
	public boolean peek() {
		return stack[top];
	}
	
	public boolean pop() {
		return stack[top--];
	}
	
	public boolean isNull() {
		return top == NULL;
	}

}
