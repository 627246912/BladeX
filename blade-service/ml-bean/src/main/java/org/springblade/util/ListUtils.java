package org.springblade.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bond
 * @date 2020/6/20 11:00
 * @desc
 */
public class ListUtils {

	public static String[] stringListToArray(List<String> list){
		String[] array = (String[])list.toArray(new String[list.size()]);
		return array;
	}

	public static Object[] ObjectListToArray(List<Object> list){
		Object[] array = (Object[])list.toArray(new Object[list.size()]);
		return array;
	}

	public static Integer[] IntegerListToArray(List<Integer> list){
		Integer[] array = (Integer[])list.toArray(new Integer[list.size()]);
		return array;
	}


	public static List<String> getDiffrent(List<String> list1, List<String> list2){
		Map<String,Integer> map = new HashMap<String,Integer>(list1.size()+list2.size());
		List<String> diff = new ArrayList<String>();
		List<String> maxList = list1;
		List<String> minList = list2;
		if(list2.size()>list1.size()){
			maxList = list2;
			minList = list1;
		}

		for (String string : maxList){
			map.put(string, 1);
		}

		for (String string : minList){
			Integer cc = map.get(string);
			if(cc!=null){
				map.put(string, ++cc);
				continue;
			}
			map.put(string, 1);
		}

		for(Map.Entry<String, Integer> entry:map.entrySet()){
			if(entry.getValue()==1)
			{
				diff.add(entry.getKey());
			}
		}
		return diff;
	}
	public static String ListToStringCommaSymbol(List<String> list){
		StringBuffer str=new StringBuffer();
		for(String string :list){
			str.append(string).append(",");
		}

		return str.substring(0,str.length()-1).toString();
	}

}
