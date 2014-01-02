package com.project3.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.project3.core.Configuration;
import com.project3.util.ProjectUtils;

public class Test2 {
public static void main(String[] args) throws IOException {
	HashMap<String,String> temp2 = (HashMap<String,String>) ProjectUtils.doReadFromSerailizedFile("temp2.txt");
	FileUtils.writeStringToFile(new File("try1.txt"), temp2.toString());
//	String str = "Kaushik";
//	String docContent = "Kaushik jiKaushikji Kaushikji xxxx Kaushikji";
//	Pattern regEx = Pattern.compile("(^|\\s)"+"shit"+"(\\s|$)");
//	Matcher matcher = regEx.matcher(docContent);
//	int count = 0;
//	while (matcher.find()) {
//		count++;
//	}
//	System.out.println(count);
//	System.out.println(count);
//	
//	TreeMap<String, String> map = new TreeMap<>();
//	map.put("Cluster0_", "X");
//	map.put("Cluster1_", "y");
//	map.put("Cluster2_", "z");
//	map.put("Cluster3_", "u");
//	map.put("Cluster4_", "v");
//	System.out.println(map);
//	System.out.println(map.pollLastEntry());
//	double q = 2.0;
//	for(;true;)
//	{
//		double u = 14145252.685;
//		q = ProjectUtils.calculateVal1(q,u,10);
//		System.out.println(q);
//	}
}
}
//	int i = 2;
//	StringBuilder builder = new StringBuilder("Cluster");
//	builder.append(i);
//	builder.append("_");
//	map.remove(builder.toString());
//	i++;
//	for(;i<5;i++)
//	{
//		builder = new StringBuilder("Cluster");
//		builder.append(i);
//		builder.append("_");
//		String temp = map.get(builder.toString());
//		map.remove(builder.toString());
//		builder = new StringBuilder("Cluster");
//		builder.append(i-1);
//		builder.append("_");
//		map.put(builder.toString(), temp);
//	}
////	System.out.println(map);
//}
//}
//	public static void main(String[] args) throws Exception {
//		// StringBuilder test = new StringBuilder("ab*,cd");
//		// HashMap<String, HashMap<String, Integer>> map = new HashMap<String,
//		// HashMap<String, Integer>>();
//		// // HashMap<String, Integer> innerMap = new HashMap<>();
//		// HashMap<String, Integer> innerMap = new HashMap<>();
//		// innerMap.put("xx", 23);
//		// innerMap.put("yy", 88);
//		// innerMap.put("3", 88);
//		// innerMap.put("4", 88);
//		// innerMap.put("5", 88);
//		// System.out.println(innerMap);
//		// // map.put("1", innerMap);
//		// // map.put("2", innerMap);
//		// // map.put("3", innerMap);
//		// // ProjectUtils.doSerialization(Configuration.index, map);
//		// // ProjectUtils.doSerialization(Configuration.index, innerMap);
//		// System.out.println(ProjectUtils.doReadFromSerailizedFile(Configuration.index));
//
////		String test = "kaushikji,jikaushiksd,useless,shit,fuck,you,kaushik,kaushikas";
////		// System.out.println(StringUtils.countMatches(test,"(*,)(kaushik)(*,)"));
////		// String s = "true truex";
////		// System.out.println(test.(",*kaushik(,|$)"));
////		Pattern regEx = Pattern.compile(",*kaushik(,|$)");
////		Matcher matcher = regEx.matcher(test);
////		int count = 0;
////		while (matcher.find()) {
////			count++;
////		}
////		System.out.println(count);
////		System.out.println(test.split(",*kaushik(,|$)"));
////		String ary[] = test.split(",*kaushik(,|$)");
////		for (String x : ary)
////			System.out.println(x);
//		// System.out.println(StringUtils.countMatches(test, ",*kaushik(,|$)"));
//		
//		LinkedHashMap<String, Double> x = new LinkedHashMap<>();
//		x.put("se", 8.8);
//		x.put("sex1sd", 8.8);
//		x.put("fuck", 0.0);
//		x.put("aa", 4.5);
//		x.put("bh", 89.6);
//		x.put("sex", 1.05);
//		String cluster = "Cluster";
//		String text = "Cluster1_";
//		int i = text.charAt(cluster.length());
//		i -= 48;
//		System.out.println(i);
//		
//		System.out.println(x);
//		Set<Entry<String,Double>>set = x.entrySet();
//		for(Entry<String,Double> temp : set)
//		{
//			x.put(temp.getKey(), temp.getValue()+1);
//		}
//		System.out.println(x);
////		LinkedHashMap<String, Double> y = new LinkedHashMap<>();
//////		y.putAll(x);
//////		test(x);
////		y.put("kaushik", 96.5);
////		x.put("akash", 78.0);
////		y = x;
////		System.out.println(y);
////		System.out.println(x);
////		x= null;
////		System.out.println(y);
////		System.out.println(x);
//////		String[] tmp = x.keySet().toArray(new String[0]);
////		Set<String> xq = new HashSet<>();
////		Collections.addAll(xq, tmp);
////		System.out.println(xq);
////		x = (LinkedHashMap<String, Double>) arrangeByVal(x);
////		System.out.println(x);
////		System.out.println(x.values());
//	}
//	public static void test(LinkedHashMap<String, Double> z)
//	{
//		z.put("useless", 100.4);
//	}
////	public static Map<String,Double> arrangeByVal(Map<String,Double> map)
////	{
////		List list = new LinkedList(map.entrySet());
////		Collections.sort(list,new Comparator<Map.Entry<String, Double>>() {
////
////			@Override
////			public int compare(Entry<String, Double> o1,
////					Entry<String, Double> o2) {
////				// TODO Auto-generated method stub
////				if(o2.getValue() > o1.getValue())
////					return 1;
////				else
////					return -1;
////			}
////		});
////		LinkedHashMap<String,Double> map1 = new LinkedHashMap<>();
////		for(Map.Entry<String, Double> entry : (LinkedList<Map.Entry<String, Double>>)list)
////		{
////			System.out.println(entry.getKey()+" "+entry.getValue());
////			map1.put(entry.getKey(), entry.getValue());
////		}
////		return map1;
////	}
//}
