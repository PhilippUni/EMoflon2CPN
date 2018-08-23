package translation.mapper;

import java.util.ArrayList;
import java.util.List;

public class EmoflonAddressInterpreter {
	public static List<String> addressToList(String address) {
		List<String> list = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for(char c : address.toCharArray()) {
			if(c == '/') {
				if(builder.length() > 0) 
					list.add(builder.toString());
				builder.setLength(0);
			}
			else builder.append(c);
		}
		list.add(builder.toString());
		return list;
	}
	
	public static int addressToNumber(String address) {
		List<String> list = addressToList(address);
		String last = list.get(list.size() - 1);
		StringBuilder builder = new StringBuilder();
		boolean dotFound = false;
		for(char c : last.toCharArray()) {
			if(dotFound) builder.append(c);
			else if(c == '.') dotFound = true;
		}
		return Integer.parseInt(builder.toString());
	}
}
