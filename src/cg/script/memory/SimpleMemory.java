package cg.script.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cg.script.CharSet;

public class SimpleMemory implements Memory {
	
	protected Map<String, Object> variables;
	
	public SimpleMemory() {
		variables = createVariables();
	}
	
	protected Map<String, Object> createVariables() {
		return new HashMap<String, Object>();
	}

	@Override
	public void define(String word, List<String> params) {
		if (params.size() == 0) {
			cache(word);
		} else if (params.size() == 1) {
			cache(word, params.remove(0));
		} else {
			cache(word, params);
		}
	}
	
	protected void cache(String word) {
		variables.put(word, null);
	}
	
	protected void cache(String word, String param) {
		variables.put(word, CharSet.isNum(param) ? Integer.parseInt(param) : param);
	}
	
	protected void cache(String word, List<String> params) {
		variables.put(word, params.toArray(new String[params.size()]));
	}

	@Override
	public Object get(String word) {
		return variables.get(word);
	}

}
