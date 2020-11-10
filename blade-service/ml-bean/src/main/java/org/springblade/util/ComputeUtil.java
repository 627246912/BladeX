package org.springblade.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputeUtil {
	private static final Pattern p = Pattern.compile("v_[A-Za-z0-9_]+");

	//脚本验证
	public static Double compute(Map<String, Double> id2RealTimeData
			,String content)throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("function compute() ");
		sb.append("{" + "\r\n");
		sb.append("" + content + "\r\n");
		sb.append("}");

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("javascript");
		Invocable invocableEngine = (Invocable) se;
		se.eval(sb.toString());

		for(Entry<String,Double> entry : id2RealTimeData.entrySet()) {
			se.put(entry.getKey(), entry.getValue());
		}

		Matcher m = p.matcher(content);
		Set<String> gvs = new HashSet<>();
		while (m.find()) {
			gvs.add(m.group());
		}

		for(String gv:gvs) {
			se.put(gv, 0);
		}

		double callbackvalue = (Double) invocableEngine.invokeFunction("compute");
		return callbackvalue;
	}

}
