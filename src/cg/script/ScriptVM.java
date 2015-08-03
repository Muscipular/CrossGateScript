package cg.script;

import java.util.LinkedList;
import java.util.List;

import cg.script.memory.Memory;
import cg.script.memory.SimpleMemory;
import cg.script.struct.BooleanStack;
import cg.script.struct.IntStack;
import cg.script.struct.StringStack;

public class ScriptVM {
	
	protected CharSet charSet;
	
	protected KeyWords keyWords;
	
	protected Syscall syscall;
	
	protected Memory memory;
	
	public void init() {
		charSet = createCharSet();
		charSet.init();
	}
	
	protected CharSet createCharSet() {
		return new CharSet();
	}
	
	protected String formatLine(String line) {
		return line.replaceAll("\t", " ").replaceAll(",", " ").replaceAll("\\.", " ").replace("¡¡", "");
	}
	
	public boolean read(String line, List<String> words, boolean inProtect) {
		line = formatLine(line);
		StringBuilder builder = new StringBuilder();
		if (line.length() > 0) {
			char[] array = line.toCharArray();
			boolean isProtect = false, isOperation = false, isMessage = false;
			for (int i = 0;i < array.length;i++) {
				char c = array[i];
				if (inProtect) {
					builder.append(c);
					int length = builder.length();
					if (length > 1 && keyWords.endAnnotate(builder.substring(length - 2))) {
						inProtect = false;
						builder.setLength(0);
					}
					continue;
				}
				
				if (!isMessage) {
					if (isOperation && !charSet.isOperation(c)) {
						String word = builder.toString();
						builder.setLength(0);
						if (!keyWords.isExpression(word)) {
							throw new ScriptException("Unsupport operation : '" + word + "'.");
						} else {
							addWord(words, word);
						}
						isOperation = false;
					}
					
					if (isProtect) {
						if (charSet.accpet(c) && charSet.isProtect(c)) {
							isProtect = false;
						}
					} else if (!charSet.accpet(c)) {
						isMessage = true;
					} else if (charSet.isAnnotate(c)) {
						addWord(words, builder.toString());
						break;
					} else if (charSet.isProtect(c)) {
						isProtect = true;
					} else if (charSet.isOperation(c)) {
						if (!isOperation){
							int length = builder.length();
							if (length > 0 && keyWords.startAnnotate(builder.substring(length - 1) + c)) {
								inProtect = true;
								addWord(words, builder.substring(0, length - 1).toString());
								break;
							} else {
								addWord(words, builder.toString());
								builder.setLength(0);
								isOperation = true;
							}
						}
					} else if (charSet.isSplit(c)) {
						if (builder.length() > 0) {
							addWord(words, builder.toString());
							builder.setLength(0);
						}
						continue;
					} else if (charSet.isPart(c)) {
						addWord(words, builder.toString());
						builder.setLength(0);
						addWord(words, c + "");
						continue;
					}
				}
				builder.append(c);
			}
			addWord(words, builder.toString());
		}
		return inProtect;
	}
	
	protected void addWord(List<String> words, String word) {
		word = word.trim();
		if (word.length() > 0) {
			words.add(word);
		}
	}
	
	/**
	 * between '(' and ')' must are expression
	 * @param words
	 */
	public void execute(List<String> words) {
		String method = words.remove(0);
		if (keyWords.isMethod(method)) {
			if (keyWords.expressionParam(method)) {
				StringStack stack = new StringStack(words.size());
				IntStack rpn_int = new IntStack(words.size()); // Reverse Polish notation
				BooleanStack rpn_boolean = new BooleanStack(words.size());
				for (String word : words) {
					if (word.equals(")")) {
						if (stack.isNull()) {
							throw new ScriptException("Stack is null but word is ')' .");
						} else {
							while (!stack.peek().equals("(")) {
								calc(stack, rpn_int, rpn_boolean);
							}
							stack.pop(); // delete '('
						}
					} else if (keyWords.isExpression(word)) {
						if (stack.isNull() || stack.peek().equals("(") || keyWords.getOperationLevel(word) > keyWords.getOperationLevel(stack.peek())) {
							stack.push(word);
						} else {
							calc(stack, rpn_int, rpn_boolean);
							stack.push(word);
						}
					} else if (CharSet.isNum(word)) { // number
						int value = Integer.parseInt(word);
						rpn_int.push(value);
					} else if (keyWords.isMethod(word)) { // method
						rpn_int.push(syscall.call(word, words));
					} else { // variable
						int point = word.indexOf('['), index = 0;
						String variable;
						if (point > -1) {
							word = word.replace("]", "");
							index = Integer.parseInt(word.substring(point + 1));
							variable = word.substring(0, point);
						} else {
							variable = word;
						}
						Object object = memory.get(variable);
						if (object instanceof Integer) {
							rpn_int.push((Integer) object);
						} else {
							int value = Integer.parseInt(point > -1 ? ((String[]) object)[index] : (String) object);
							rpn_int.push(value);
						}
					}
				}
				
				while (!stack.isNull()) {
					calc(stack, rpn_int, rpn_boolean);
				}

				if (rpn_boolean.isNull()) {
					System.out.println(rpn_int.peek());
				} else {
					System.out.println(rpn_boolean.peek());
				}
			} else { // non expression
				syscall.call(method, words);
			}
		} else { // variable
			String op = words.remove(0);
			if (op.equals("=")) {
				memory.define(method, words);
			} else {
				throw new ScriptException("Unsupport operation : " + op + " .");
			}
		}
	}
	
	public void calc(StringStack stack, IntStack rpn_int, BooleanStack rpn_boolean) {
		String op = stack.pop();
		if (keyWords.isMath(op)) {
			int v1 = rpn_int.pop();
			int v2 = rpn_int.pop();
			int result;
			if (op.equals("+")) {
				result = v2 + v1;
			} else if (op.equals("-")) {
				result = v2 - v1;
			} else if (op.equals("*")) {
				result = v2 * v1;
			} else if (op.equals("/")) {
				result = v2 / v1;
			} else if (op.equals("&")) {
				result = v2 & v1;
			} else if (op.equals("|")) {
				result = v2 | v1;
			} else if (op.equals("%")) {
				result = v2 % v1;
			} else {
				throw new ScriptException("Unsupport operation : " + op + " .");
			}
			rpn_int.push(result);
		} else {
			boolean result;
			if (op.equals("&&")) {
				boolean v1 = rpn_boolean.pop();
				boolean v2 = rpn_boolean.pop();
				result = v2 && v1;
			} else if (op.equals("||")) {
				boolean v1 = rpn_boolean.pop();
				boolean v2 = rpn_boolean.pop();
				result = v2 || v1;
			} else if (op.equals(">")) {
				int v1 = rpn_int.pop();
				int v2 = rpn_int.pop();
				result = v2 > v1;
			} else if (op.equals(">=")) {
				int v1 = rpn_int.pop();
				int v2 = rpn_int.pop();
				result = v2 >= v1;
			} else if (op.equals("<")) {
				int v1 = rpn_int.pop();
				int v2 = rpn_int.pop();
				result = v2 < v1;
			} else if (op.equals("<=")) {
				int v1 = rpn_int.pop();
				int v2 = rpn_int.pop();
				result = v2 <= v1;
			} else if (op.equals("==")) {
				int v1 = rpn_int.pop();
				int v2 = rpn_int.pop();
				result = v2 == v1;
			} else {
				throw new ScriptException("Unsupport operation : " + op + " .");
			}
			rpn_boolean.push(result);
		}
	}
	
	public void executeSimple(String[] words, int begin, int finish) {
		String[] stack = new String[finish - begin + 1];
		int point = 0;
		for (int i = begin;i < words.length;i++) {
			stack[point] = words[i];
		}
	}
	
	public static void main(String[] args) {
		ScriptVM vm = new ScriptVM();
		vm.init();
		vm.memory = new SimpleMemory();
		vm.keyWords = new SimpleKeyWords();
		vm.syscall = new Syscall() {
			
			@Override
			public int call(String method, List<String> params) {
				if (method.equals("max")) {
					return 20;
				}
				return 0;
			}
			
		};
		List<String> words = new LinkedList<String>();
		vm.read("a=1 2 3", words, false);
		vm.execute(words);
		vm.read("if a[1]+a[2]+max", words, false);
		vm.execute(words);
	}

}
