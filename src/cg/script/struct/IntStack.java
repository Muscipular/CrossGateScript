package cg.script.struct;

public class IntStack {
	
	public static final int NULL = -1;
	
	protected final int[] stack;
	
	protected int top;
	
	public IntStack(int size) {
		stack = new int[size];
		top = NULL;
	}
	
	public void push(int element) {
		stack[++top] = element;
	}
	
	public int peek() {
		return stack[top];
	}
	
	public int pop() {
		return stack[top--];
	}
	
	public boolean isNull() {
		return top == NULL;
	}

}
