package cg.script;

import java.util.List;

public interface Syscall {
	
	int call(String method, List<String> params);

}
