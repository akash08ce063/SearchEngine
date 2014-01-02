package com.project3.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.project3.core.Configuration;

public class ProjectUtils {

	public static String extractFileNameFromUrl(String url) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(4, "html,aspx");
		map.put(3, "jsp,php");
		StringBuilder fileName = null;
		StringBuilder builder = new StringBuilder(url);
		boolean found = false;
		for (int i = builder.lastIndexOf("."); i >= 0; i--) {
			char c = builder.charAt(i);
			if (c == '.' && (i + 1) != builder.length()) {
				int changedIndex = 0;
				for (Integer key : map.keySet()) {
					changedIndex = key + (i + 1);
					if (changedIndex <= builder.length()) {
						String dummy = builder.substring(i + 1, changedIndex);
						String extAry[] = map.get(key).split(",");
						for (String temp : extAry) {
							if (temp.equalsIgnoreCase(dummy)) {
								int y = i;
								String str = builder.substring(0, y);
								int lstInd = str.lastIndexOf("/");
								fileName = new StringBuilder(str.substring(
										lstInd + 1, str.length()));
								fileName.append(".");
								fileName.append(dummy);
								found = true;
								break;
							}
						}
					}
					if (found) {
						i = -1;
						break;
					}
				}
			}
		}
		if (!found) {
			fileName = new StringBuilder(String.valueOf(new Date().getTime()));
		}
		return fileName.toString();
	}
	public static void doSerialization(String fileName, Map map) throws IOException
	{
		ProjectLogger.logger.log(Level.INFO,"Started doSerialization : ");
		if(map != null && !map.isEmpty() && StringUtils.isNotBlank(fileName))
		{
			StringBuilder builder = new StringBuilder("");
			Set<Entry> set = map.entrySet();
			boolean isString = false;
			for(Map.Entry entry1 : set)
			{
				if(!(entry1.getValue() instanceof Map))
				{
					isString = true;
					break;
				}
			}
			if(!isString)
			{
				for(Map.Entry entry : set)
				{
					Map docIdTfMap = (Map)entry.getValue();
					Set<Entry> docIdTfKeys = docIdTfMap.entrySet();
					builder.append(entry.getKey().toString());
					builder.append(Configuration.delimForKeyAndValOutrMap);
					for(Map.Entry entry1 : docIdTfKeys)
					{
						builder.append(entry1.getKey());
						builder.append(Configuration.delimForKeyAndValInrMap);
						builder.append(entry1.getValue());
						builder.append(Configuration.inrMapDelimitor);
					}
					builder.append(Configuration.outrMapDelimitor);
				}
			}
			else
			{
				for(Map.Entry entry : set)
				{
					builder.append(entry.getKey().toString());
					builder.append(Configuration.delimForKeyAndValOutrMap);
					builder.append(entry.getValue().toString());
					builder.append(Configuration.outrMapDelimitor);
				}
			}
			try{
				FileUtils.writeStringToFile(new File(fileName), builder.toString());
			}
			catch(IOException e)
			{
				ProjectLogger.logger.log(Level.SEVERE,"Exception Occured : ",e);
				throw e;
			}

		}
		ProjectLogger.logger.log(Level.INFO,"Finished doSerialization : ");
	}
	public static Object doReadFromSerailizedFile(String fileName) throws IOException
	{
		ProjectLogger.logger.log(Level.INFO,"Started doReadFromSerailizedFile : ");
		Object obj = null;
		if(StringUtils.isNotBlank(fileName))
		{
			if(StringUtils.isNotBlank(fileName))
			{
				try{
					String content = FileUtils.readFileToString(new File(fileName));
					String q = Configuration.outrMapDelimitor;
					String contentAry[] = content.split(q);
					String tmpStr[] = contentAry[0].split(Configuration.delimForKeyAndValOutrMap);
					boolean isString = false;
					if(tmpStr[1].indexOf(Configuration.inrMapDelimitor) == -1)
					{
						isString = true;
					}
					if(isString)
					{
						HashMap<String, String> index = new HashMap<String, String>();
						for(int i = 0; i < contentAry.length; i ++)
						{
							String tempContent = contentAry[i];
							if(StringUtils.isNotBlank(tempContent))
							{
								String keyVal[] = tempContent.split(Configuration.delimForKeyAndValOutrMap);
								index.put(keyVal[0], keyVal[1]);
							}
						}
						obj = index;
					}
					else
					{
						HashMap<String, HashMap<String, String>> index = new HashMap<String, HashMap<String, String>>();
						for(int i = 0; i < contentAry.length; i ++)
						{
							String tempContent = contentAry[i];
							if(StringUtils.isNotBlank(tempContent))
							{
								String keyVal[] = tempContent.split(Configuration.delimForKeyAndValOutrMap);
								StringBuilder builder = new StringBuilder(keyVal[1]);
								int firstIndex = builder.indexOf(Configuration.inrMapDelimitor);
								HashMap<String, String> innerMap = new HashMap<String, String>();
								for(;firstIndex != -1;)
								{
									String subStr = builder.substring(0, firstIndex);
									String ary[] = subStr.split(Configuration.delimForKeyAndValInrMap);
									innerMap.put(ary[0], ary[1]);
									firstIndex = firstIndex+Configuration.inrMapDelimitor.length();
									builder.delete(0,firstIndex);
									firstIndex = builder.indexOf(Configuration.inrMapDelimitor);
								}
								index.put(keyVal[0], innerMap);
							}
						}
						obj = index;
					}
				}
				catch(IOException e)
				{
					ProjectLogger.logger.log(Level.SEVERE,"Exception Occured : ",e);
					throw e;
				}
			}
		}
		ProjectLogger.logger.log(Level.INFO,"Finished doReadFromSerailizedFile : ");
		return obj;
	}
	public static Object calculateTermCountInTheDocument(Set<String> set, String docContent, boolean includeCountInMap)
	{
		Object obj = null;
		if(!includeCountInMap)
		{
			int count = 0;
			Set<String> docContentTerms = new HashSet<>();
			Collections.addAll(docContentTerms, docContent.split(Configuration.delimitorForToken));
			for(String str : docContentTerms)
			{
				if(set.contains(str))
				{
					Pattern regEx = Pattern.compile("(^|\\s)"+str+"(\\s|$)");
					Matcher matcher = regEx.matcher(docContent);
					while (matcher.find()) {
						count++;
					}
				}
			}
			obj = Integer.valueOf(count);
		}
		else
		{
			HashMap<String, String> termMap = new HashMap<String, String>();
			for(String str : set)
			{
				int count = 0;
				Pattern regEx = Pattern.compile("(^|\\s)"+str+"(\\s|$)");
				Matcher matcher = regEx.matcher(docContent);
				while (matcher.find()) {
					count++;
				}
				termMap.put(str, String.valueOf(count));
			}
			obj = termMap;
		}
		return obj;
	}
	public static TreeMap<Double, String> reArrangeMap(TreeMap<Double, String> treeMap,Set<String> clusterVocab, HashMap<String, String> docIdContentMap)
	{
		TreeMap<Double, String> dummyMap = new TreeMap<>();
		for(String str : treeMap.values())
		{
			String docContent = docIdContentMap.get(str);
			double length = docContent.split(Configuration.delimitorForToken).length;
			Integer count = (Integer)calculateTermCountInTheDocument(clusterVocab, docContent, false);
			double tempCnt = count;
			tempCnt = tempCnt/length;
			dummyMap.put(tempCnt, str);
		}
		return dummyMap;
	}
	public static LinkedHashMap<String,BigDecimal> arrangeByVal(LinkedHashMap<String,BigDecimal> map)
	{
		List<Entry<String,BigDecimal>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String, BigDecimal>>() {
			@Override
			public int compare(Entry<String, BigDecimal> o1,
					Entry<String, BigDecimal> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		LinkedHashMap<String,BigDecimal> map1 = new LinkedHashMap<>();
		for(Map.Entry<String, BigDecimal> entry : (LinkedList<Map.Entry<String, BigDecimal>>)list)
		{
			map1.put(entry.getKey(), entry.getValue());
		}
		return map1;
	}
	public static LinkedHashMap<String,Double> arrangeByValForBM25(LinkedHashMap<String,Double> map)
	{
		List<Entry<String,Double>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		LinkedHashMap<String,Double> map1 = new LinkedHashMap<>();
		for(Map.Entry<String, Double> entry : (LinkedList<Map.Entry<String, Double>>)list)
		{
			map1.put(entry.getKey(), entry.getValue());
		}
		return map1;
	}
	public static HashMap<String, String> updateTermMap(HashMap<String, String> clusterMap,String docContent)
	{
		Set<String> keySet = clusterMap.keySet();
		Set<String> dummyKeySet = new HashSet<>();
		dummyKeySet.addAll(keySet);
		Collections.addAll(dummyKeySet, docContent.split(Configuration.delimitorForToken));
		for(String key : dummyKeySet)
		{
			int count = 0;
			Pattern regEx = Pattern.compile("(^|\\s)"+key+"(\\s|$)");
			Matcher matcher = regEx.matcher(docContent);
			while (matcher.find()) {
				count++;
			}
			if(count != 0)
			{
				String val = clusterMap.get(key);
				if(StringUtils.isNotBlank(val))
				{
					int value = Integer.parseInt(val);
					count+=value;
				}
				clusterMap.put(key, String.valueOf(count));
			}
		}
		return clusterMap;
	}
	public static int findTfForADoc(String wordToBeCounted,String docContent)
	{
		Pattern regEx = Pattern.compile("(^|\\s)"+wordToBeCounted+"(\\s|$)");
		Matcher matcher = regEx.matcher(docContent);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
	public static HashMap<String, String> mergeMap(HashMap<String, String>srcMap,HashMap<String, String>tgtMap)
	{
		Set<Entry<String,String>> entry = srcMap.entrySet();
		for(Entry<String, String> keyValPair : entry)
		{
			String key = keyValPair.getKey();
			String val = keyValPair.getValue();
			if(tgtMap.containsKey(key))
			{
				int count = Integer.parseInt(tgtMap.get(key));
				int srcCount = Integer.parseInt(val);
				count += srcCount;
				tgtMap.put(key, String.valueOf(count));
			}
			else
			{
				tgtMap.put(key, val);
			}
		}
		return tgtMap;
	}
	public static Entry<String, BigDecimal> determineClashOccured(LinkedHashMap<String, BigDecimal> rankingMap)
	{
		int y = 0;
		Entry<String, BigDecimal> firstVal = null;
		for(Entry<String, BigDecimal> entry: rankingMap.entrySet())
		{
			if(y==1)
			{
				if(firstVal.getValue() == entry.getValue())
				{
					return null;
				}
				break;
			}
			firstVal = entry;
			y++;
		}
		return firstVal;
	}
	public static StringBuilder readFileContent() throws IOException
	{
		StringBuilder builder = new StringBuilder("");
		String str ="";
		File folder=new File(Configuration.directoryToSaveFiles);
		File[] listofFiles=folder.listFiles();
		try{
			for (File eachFile : listofFiles) 
			{
				str = FileUtils.readFileToString(eachFile);
				builder.append(str);
				builder.append("\n");
			}
		}
		catch(IOException e)
		{
			ProjectLogger.logger.log(Level.SEVERE,"Exception Occurred : ",e);
			throw new IOException(e);
		}
		return builder;
	}
	public static String parseHtmlFiles(StringBuilder docBuilder)
	{
		Document doc;
		doc = Jsoup.parse(docBuilder.toString());
		StringBuilder titleBuilder = new StringBuilder("");
		String title = doc.title();
		if(StringUtils.isNotBlank(title))
		{
			titleBuilder.append(title);
			titleBuilder.append(" ");
			for(int i = 0; i < Configuration.weightForTitleTag - 1; i++)
			{
				titleBuilder.append(title);
				titleBuilder.append(" ");
			}
		}
		Element body = doc.body();
		Elements h1 = body.select("h1");
		StringBuilder headerContent = new StringBuilder("");
		if(h1.hasText())
		{
			String h1Text = h1.text();
			headerContent.append(h1Text);
			headerContent.append(" ");
			for(int i = 0; i < Configuration.weightForH1Tag - 1; i++)
			{
				headerContent.append(h1Text);
				headerContent.append(" ");
			}
		}
		Elements f = body.children();
		StringBuilder builder1 = new StringBuilder();
		builder1 = evaluateChildElements(f,builder1);
		builder1.append(titleBuilder.toString().trim());
		builder1.append(headerContent.toString().trim());
		String content = builder1.toString();
		return doPreprocessing(content);
	}
	public static String doPreprocessing(String content)
	{
		content=content.toLowerCase().replaceAll("[^A-Za-z ]", "");
		content = content.trim().replaceAll(" +", " ");
		if(Configuration.stopWordsRemoval)
		{
			for(String str : Configuration.stopWords)
			{
				content = content.replaceAll("(^|\\s)"+str+"(\\s|$)", " ");
			}
		}
		content = content.trim();
		return content;
	}
	public static StringBuilder evaluateChildElements(Elements elements,StringBuilder value)
	{
		for(Element x : elements)
		{
			if(!x.isBlock())
			{
				String txt = x.ownText();
				if(txt != null && txt.length() > 0){
					value.append(" ");
					value.append(txt.trim());
				}
			}
			else
			{
				Elements children = x.children();
				if(children.size() > 0)
				{
					String txt = x.ownText();
					if(txt != null && txt.length() > 0)
					{
						value.append(" ");
						value.append(txt.trim());
					}
					value = evaluateChildElements(children,value);
				}
				else
				{
					String txt = x.ownText();
					if(txt != null && txt.length() > 0)
					{
						value.append(" ");
						value.append(txt.trim());
					}
				}
			}
		}
		return value;
	}
	public static BigDecimal calculateVal(double numerator,double denominator,int pow)
	{
//		MathContext ctxt = new MathContext(2,RoundingMode.HALF_UP);
		BigDecimal num = new BigDecimal(numerator);
		BigDecimal denom = new BigDecimal(denominator);
		BigDecimal res = num.divide(denom, 30,BigDecimal.ROUND_HALF_UP);
		res.setScale(30,BigDecimal.ROUND_HALF_UP);
		BigDecimal mul = new BigDecimal(Configuration.scoreMultiplier);
		res = res.multiply(mul);
		res = res.pow(pow);
		return res;
	}
}