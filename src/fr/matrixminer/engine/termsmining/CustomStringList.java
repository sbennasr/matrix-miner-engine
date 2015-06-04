package fr.matrixminer.engine.termsmining;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CustomStringList extends ArrayList<String> {
    @Override
    public boolean contains(Object o) {
        String paramStr = (String)o;
        for (String s : this) {
            if (paramStr.equalsIgnoreCase(s)) return true;
        }
        return false;
    }
    
	public List<String> removeDuplicate(){
	Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	set.addAll(this);
	List<String> list = new ArrayList<String>(set);
	return list;
	}
}