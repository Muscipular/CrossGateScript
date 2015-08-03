package cg.script.memory;

import java.util.List;

public interface Memory {
	
	void define(String word, List<String> params);
	
	Object get(String word);

}
