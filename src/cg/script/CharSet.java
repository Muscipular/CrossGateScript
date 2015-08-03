package cg.script;

public class CharSet {
	
	public static final byte TYPE_NONE = 0;
	
	public static final byte TYPE_OPERATION = 1;
	
	public static final byte TYPE_NUMBER = 2;
	
	public static final byte TYPE_CHAR = 3;
	
	public static final byte TYPE_ANNOTATE = 4;
	
	public static final byte TYPE_PROTECT = 5;
	
	public static final byte TYPE_SPLIT = 6;
	
	public static final byte TYPE_PART = 7;
	
	private Char[] set;
	
	public void init() {
		set = new Char[127];
		for (int i = 0;i < set.length;i++) {
			if (i == 9 || (i >= 32 && i <= 38) || (i >= 40 && i <= 45) || (i >= 47 && i <= 58) || 
					(i >= 60 && i <= 62) || (i >= 65 && i <= 91) || i == 93 || i == 95 || (i >= 97 && i <= 122) 
					|| i == 124) {
				byte type;
				switch (i) {
				case 9 : case 10 : case 36 : case 44 : case 58 : case 91 : case 93 : case 95 : 
					type = TYPE_NONE;
					break;
				case 32 : 
					type = TYPE_SPLIT;
					break;
				case 34 : 
					type = TYPE_PROTECT;
					break;
				case 35 : 
					type = TYPE_ANNOTATE;
					break;
				case 40 : case 41 : 
					type = TYPE_PART;
					break;
				case 33 : case 37 : case 38 : case 42 : case 43 : case 45 : case 47 : case 60 : case 61 : case 62 : 
				case 124 : 
					type = TYPE_OPERATION;
					break;
				case 48 : case 49 : case 50 : case 51 : case 52 : case 53 : case 54 : case 55 : case 56 : case 57 : 
					type = TYPE_NUMBER;
					break;
				default : 
					type = TYPE_CHAR;
				}
				set[i] = new Char(i, true, type);
			} else {
				set[i] = new Char(i, false, TYPE_NONE);
			}
		}
	}
	
	public boolean accpet(char c) {
		return c < set.length && c >=0 && set[c].canUse();
	}
	
	public boolean isAnnotate(char c) {
		return set[c].getType() == TYPE_ANNOTATE;
	}
	
	public boolean isProtect(char c) {
		return set[c].getType() == TYPE_PROTECT;
	}
	
	public boolean isSplit(char c) {
		return set[c].getType() == TYPE_SPLIT;
	}
	
	public boolean isOperation(char c) {
		return set[c].getType() == TYPE_OPERATION;
	}
	
	public boolean isPart(char c) {
		return set[c].getType() == TYPE_PART;
	}
	
	public static boolean isNum(String word){
		return word.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

}

class Char {
	
	private final byte type;
	
	private final boolean canUse;
	
	private final int value;
	
	public Char(int value, boolean canUse, byte type) {
		this.value = value;
		this.canUse = canUse;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	public boolean canUse() {
		return canUse;
	}
	
}
