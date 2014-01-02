package com.project3.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

import com.project3.util.ProjectLogger;
import com.project3.util.ProjectUtils;

public class KMeansClusterer {
	public static TreeMap<String, HashMap<String, String>> clusterInfo = new TreeMap<String, HashMap<String, String>>();
	private static HashMap<String,String> clusterVocab = new HashMap<String, String>();
	private static LinkedHashMap<String, String> docClusterMap = new LinkedHashMap<>();
	public static HashMap<String, String> docIdTokenMap = null;
	private static double avgDocLength = 0.0;
	private static double noOfDocs = 0.0;
	private static double totNoOfTerms = 0.0;
	public void doClustering() throws IOException
	{
		Set<String> clusterTerms = new HashSet<>();
		LinkedList<String> list = new LinkedList<>();
		ArrayList<String> initialDocs = new ArrayList<String>();
		int k = 0;
		int i = 0;
		HashMap<String, HashMap<String, String>> tempMap = (HashMap<String, HashMap<String, String>>)ProjectUtils.doReadFromSerailizedFile(Configuration.index);
		docIdTokenMap = tempMap.get("docID_data");
		Set<Entry<String, String>> processingParams = tempMap.get("processing_params").entrySet();
		for(Entry<String, String> e : processingParams)
		{
			noOfDocs = Double.parseDouble(e.getKey());
			totNoOfTerms = Double.parseDouble(e.getValue());
			avgDocLength = totNoOfTerms/noOfDocs;
		}
		TreeMap<Double, String> treeMap = new TreeMap<Double,String>();
		String[] keys = docIdTokenMap.keySet().toArray(new String[0]);
		StringBuilder builder = new StringBuilder("Cluster");
		builder.append(k);
		builder.append("_");
		HashMap<String, String> termMap = new HashMap<String, String>();
		HashMap<String, String> docMap = new HashMap<String,String>();
		String val = docIdTokenMap.get(keys[0]);
		String ary[] = val.split(Configuration.delimitorForToken);
		HashMap<String, String> vocabSizeMap = new HashMap<>();
		//clusterVocabSize means number of words present in each document present in the cluster
		vocabSizeMap.put("clusterVocabSize", String.valueOf(ary.length));
		Collections.addAll(clusterTerms, ary);
		termMap = (HashMap<String, String>)ProjectUtils.calculateTermCountInTheDocument(clusterTerms, val, true);
		clusterVocab = ProjectUtils.mergeMap(termMap, clusterVocab);
		docClusterMap.put(keys[0], builder.toString());
		docMap.put(keys[0], String.valueOf(ary.length));
		clusterInfo.put(builder.toString()+"termInfo", termMap);
		clusterInfo.put(builder.toString()+"docInfo", docMap);
		clusterInfo.put(builder.toString()+"clusterVocabSize", vocabSizeMap);
		initialDocs.add(keys[0]);
		TreeMap<Double, String> lengthyDoc = new TreeMap<>();
		for(i = 1; i < keys.length; i++)
//		for(i = 1; i < 100; i++)
		{
			val = docIdTokenMap.get(keys[i]);
			Integer count = (Integer)ProjectUtils.calculateTermCountInTheDocument(clusterTerms, val,false);
			double tempCnt = count;
			String docAry[] = val.split(Configuration.delimitorForToken);
			tempCnt = tempCnt/(double)docAry.length;
			if(docAry.length <= avgDocLength)
			{
				if(treeMap.size() < Configuration.topContenders)
				{
					treeMap.put(tempCnt, keys[i]);
				}
				else
				{
					treeMap.put(tempCnt, keys[i]);
					while(treeMap.size() > Configuration.topContenders)
					{
						treeMap.pollLastEntry();
					}
				}
			}
			else
			{
				lengthyDoc.put(tempCnt, keys[i]);
			}
		}
		while(treeMap.size() < Configuration.noOfClusters)
		{
			NavigableMap<Double, String> descendingMap = lengthyDoc.descendingMap();
			for(Entry<Double,String> descendingEntry : descendingMap.entrySet())
			{
				treeMap.put(descendingEntry.getKey(), descendingEntry.getValue());
				if(treeMap.size() >= Configuration.noOfClusters)
					break;
			}
		}
		k++;
		while(k < Configuration.noOfClusters)
		{
			builder = new StringBuilder("Cluster");
			builder.append(k);
			builder.append("_");
			Entry<Double,String> entry = treeMap.pollFirstEntry();
			String docId = entry.getValue();
			val = docIdTokenMap.get(docId);
			String contentAry[] = val.split(Configuration.delimitorForToken);
//			Collections.addAll(clusterVocab, contentAry);
			Set<String> set = new HashSet<String>();
			Collections.addAll(set, contentAry);
			termMap = (HashMap<String, String>)ProjectUtils.calculateTermCountInTheDocument(set, val, true);
			clusterVocab = ProjectUtils.mergeMap(termMap, clusterVocab);
			docMap = new HashMap<String,String>();
			docMap.put(docId, String.valueOf(contentAry.length));
			vocabSizeMap = new HashMap<>();
			vocabSizeMap.put("clusterVocabSize", String.valueOf(contentAry.length));
			docClusterMap.put(docId, builder.toString());
			clusterInfo.put(builder.toString()+"termInfo", termMap);
			clusterInfo.put(builder.toString()+"docInfo", docMap);
			clusterInfo.put(builder.toString()+"clusterVocabSize", vocabSizeMap);
			initialDocs.add(docId);
			treeMap = ProjectUtils.reArrangeMap(treeMap, clusterVocab.keySet(), docIdTokenMap);
			k++;
		}
		//can be commented from here..
//		ArrayList<String> initialDocs = new ArrayList<String>();
//		HashMap<String, HashMap<String, String>> tempMap = (HashMap<String, HashMap<String, String>>)ProjectUtils.doReadFromSerailizedFile(Configuration.index);
//		docIdTokenMap = tempMap.get("docID_data");
//		String[] keys = docIdTokenMap.keySet().toArray(new String[0]);
//		int i = 0;
//		ProjectUtils.doSerialization("temp.txt", clusterInfo);
//		HashMap<String, HashMap<String, String>> tempx = (HashMap<String, HashMap<String, String>>) ProjectUtils.doReadFromSerailizedFile("temp.txt");
//		clusterInfo.putAll(tempx);
//		ProjectUtils.doSerialization("temp1.txt", clusterVocab);
//		clusterVocab = (HashMap<String,String>) ProjectUtils.doReadFromSerailizedFile("temp1.txt");
//		ProjectUtils.doSerialization("temp2.txt", docClusterMap);
//		HashMap<String,String> temp2 = (HashMap<String,String>) ProjectUtils.doReadFromSerailizedFile("temp2.txt");
//		docClusterMap.putAll(temp2);
//		LinkedList<String> list = new LinkedList<>();
		//commenting ends..
		for(i = 1; i < keys.length; i++)
//		for(i = 1; i < 100; i++)
		{
			ProjectLogger.logger.log(Level.INFO,"Clustering the first 1000 docs"+String.valueOf(i));
			if(!initialDocs.contains(keys[i]))
			{
				String docContent = docIdTokenMap.get(keys[i]);
				String docContentAry[] = docContent.split(Configuration.delimitorForToken);
				if(docContentAry.length > avgDocLength)
					list.add(keys[i]);
				else
				{
					LinkedHashMap<String, BigDecimal> rankingMap = calculateClusterScoreForADoc(docContent);
					Entry<String, BigDecimal> firstVal = ProjectUtils.determineClashOccured(rankingMap);
					if(firstVal != null)
					{
						addToTheCluster(firstVal.getKey(),docContent,keys[i]);
					}
					else
					{
						list.add(keys[i]);
					}
				}
			}
		}
		i = 0;
		for(String temp : list)
		{
			ProjectLogger.logger.log(Level.INFO,"Clustering the big docs docs"+String.valueOf(i));
			i++;
			String docContent = docIdTokenMap.get(temp);
			LinkedHashMap<String, BigDecimal> map = calculateClusterScoreForADoc(docContent);
			for(String str : map.keySet())
			{
				addToTheCluster(str, docContent,temp);
				break;
			}
		}
		ProjectUtils.doSerialization(Configuration.clusterIndex, clusterInfo);
		ProjectUtils.doSerialization("temp1.txt", clusterVocab);
		ProjectUtils.doSerialization("temp2.txt", docClusterMap);
//		recluster();
//		ProjectUtils.doSerialization(Configuration.clusterIndex, clusterInfo);
	}
	public LinkedHashMap<String,BigDecimal> calculateClusterScoreForADoc(String docContent)
	{
		LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
		StringBuilder builder = null;
		for(int i = 0; i < Configuration.noOfClusters; i ++)
		{
			builder = new StringBuilder("Cluster");
			builder.append(i);
			builder.append("_");
//			int vocabSize = clusterVocab.size();
			int vocabSize = 0;
			int clusterVocabSize = Integer.parseInt(clusterInfo.get(builder.toString()+"clusterVocabSize").get("clusterVocabSize"));
			Set<String> docContentSet = new HashSet<>();
			Collections.addAll(docContentSet,docContent.split(Configuration.delimitorForToken));
			HashMap<String, String> termMap = clusterInfo.get(builder.toString()+"termInfo");
			BigDecimal soln = new BigDecimal(1.0);
			soln.setScale(2,BigDecimal.ROUND_HALF_UP);
//			System.out.println(termMap.keySet());
			for(String dummy : docContentSet)
			{
				try{
					int count = ProjectUtils.findTfForADoc(dummy,docContent);
					String str = termMap.get(dummy);
					int clusterTermCnt = 0;
					if(StringUtils.isNotBlank(str))
					{
						clusterTermCnt = Integer.parseInt(termMap.get(dummy));
					}
					BigDecimal val = ProjectUtils.calculateVal((double)(clusterTermCnt+1), (double)(vocabSize+clusterVocabSize), count);
					soln = soln.multiply(val);
				}
				catch(NumberFormatException e)
				{
					System.out.println("Exception occured");
				}
			}
			map.put(builder.toString(), soln);
		}
		map = ProjectUtils.arrangeByVal(map);
		return map;
	}
	public void addToTheCluster(String clusterKey, String docContent, String docId)
	{
		HashMap<String, String> termMap = clusterInfo.get(clusterKey+"termInfo");
		HashMap<String, String> docMap = clusterInfo.get(clusterKey+"docInfo");
		HashMap<String, String> vocabSizeMap = clusterInfo.get(clusterKey+"clusterVocabSize");
		String[] docContentAry = docContent.split(Configuration.delimitorForToken);
		termMap = ProjectUtils.updateTermMap(termMap, docContent);
//		Set<String> updatedTermSet = termMap.keySet();
		docMap.put(docId, String.valueOf(docContentAry.length));
		Integer totWordsInTheCluster = Integer.parseInt(vocabSizeMap.get("clusterVocabSize"));
		totWordsInTheCluster += docContentAry.length;
		vocabSizeMap.put("clusterVocabSize", totWordsInTheCluster.toString());
		docClusterMap.put(docId, clusterKey);
		clusterInfo.put(clusterKey+"termInfo", termMap);
		clusterInfo.put(clusterKey+"docInfo", docMap);
		clusterInfo.put(clusterKey+"clusterVocabSize", vocabSizeMap);
//		clusterVocab.addAll(updatedTermSet);
//		ProjectUtils.mergeMap(termMap, clusterVocab);
	}
	public void recluster() throws IOException
	{
		HashMap<String, HashMap<String, String>> tempMap = (HashMap<String, HashMap<String, String>>)ProjectUtils.doReadFromSerailizedFile(Configuration.index);
		docIdTokenMap = tempMap.get("docID_data");
//		ProjectUtils.doSerialization("temp.txt", clusterInfo);
//		ProjectUtils.doSerialization("temp1.txt", clusterVocab);
//		ProjectUtils.doSerialization("temp2.txt", docClusterMap);
//		ProjectUtils.doSerialization("temp.txt", clusterInfo);
		HashMap<String, HashMap<String, String>> tempx = (HashMap<String, HashMap<String, String>>) ProjectUtils.doReadFromSerailizedFile("temp.txt");
		clusterInfo.putAll(tempx);
//		ProjectUtils.doSerialization("temp1.txt", clusterVocab);
		clusterVocab = (HashMap<String,String>) ProjectUtils.doReadFromSerailizedFile("temp1.txt");
//		ProjectUtils.doSerialization("temp2.txt", docClusterMap);
		HashMap<String,String> temp2 = (HashMap<String,String>) ProjectUtils.doReadFromSerailizedFile("temp2.txt");
		docClusterMap.putAll(temp2);
		String text = "Cluster";
		float stopCondition = Configuration.maxDocsToBeCrawld*Configuration.stopPointForReClustering/100;
		while(Configuration.reClusteringEnabled)
		{
		int totNoOfDocReClustered = 0;
		LinkedHashMap<String, String> dummyDocClusterMap = new LinkedHashMap<>();
		for(Entry<String, String> entry : docClusterMap.entrySet())
		{
			HashMap<String, String> termMap = clusterInfo.get(entry.getValue()+"termInfo");
			HashMap<String, String> originalDocMap = clusterInfo.get(entry.getValue()+"docInfo");
			HashMap<String, String> originalClusterVocabSize = clusterInfo.get(entry.getValue()+"clusterVocabSize");
			
			HashMap<String, String> tempTermMap = new HashMap<>();
			HashMap<String, String> tempDocMap = new HashMap<>();
			HashMap<String, String> tempClusterMap = new HashMap<>();
			tempTermMap.putAll(termMap);
			tempDocMap.putAll(originalDocMap);
			tempClusterMap.putAll(originalClusterVocabSize);
			String docContent = docIdTokenMap.get(entry.getKey());
//			tempDocMap.remove(entry.getValue());
			removeTermsFromClusterForDoc(tempTermMap, docContent);
			tempDocMap.remove(entry.getKey());
			Integer clusterSize = Integer.parseInt(tempClusterMap.get("clusterVocabSize"));
			String[] docContentAry = docContent.split(Configuration.delimitorForToken);
			clusterSize -= docContentAry.length;
			tempClusterMap.put("clusterVocabSize", clusterSize.toString());
			HashMap<String, String> tempClusterVocab = new HashMap<>();
			tempClusterVocab.putAll(clusterVocab);
			removeTermsFromClusterForDoc(clusterVocab,docContent);
			dummyDocClusterMap.put(entry.getKey(), entry.getValue());
			if(tempDocMap.size() != 0 && tempTermMap.size() != 0)
			{
				clusterInfo.put(entry.getValue()+"termInfo", tempTermMap);
				clusterInfo.put(entry.getValue()+"docInfo", tempDocMap);
				clusterInfo.put(entry.getValue()+"clusterVocabSize",tempClusterMap);
				LinkedHashMap<String, BigDecimal> score = calculateClusterScoreForADoc(docContent);
				Entry<String, BigDecimal> firstVal = ProjectUtils.determineClashOccured(score);
				if(firstVal != null)
				{
					if(!firstVal.getKey().equalsIgnoreCase(entry.getValue()))
					{
						addToTheCluster(firstVal.getKey(),docContent,entry.getKey());
						totNoOfDocReClustered++;
						dummyDocClusterMap.put(entry.getKey(), firstVal.getKey());
					}
					else
					{
						clusterInfo.put(entry.getValue()+"termInfo", termMap);
						clusterInfo.put(entry.getValue()+"docInfo", originalDocMap);
						clusterInfo.put(entry.getValue()+"clusterVocabSize",originalClusterVocabSize);
						clusterVocab = tempClusterVocab;
					}
				}
				else
				{
					Set<String> keys = score.keySet();
					BigDecimal val = score.get(entry.getValue());
					for(String firstKey : keys)
					{
						if(score.get(firstKey).compareTo(val) == 1)
						{
							addToTheCluster(firstKey,docContent,entry.getKey());
							totNoOfDocReClustered++;
							dummyDocClusterMap.put(entry.getKey(), firstKey);
							break;
						}
						else
						{
							clusterInfo.put(entry.getValue()+"termInfo", termMap);
							clusterInfo.put(entry.getValue()+"docInfo", originalDocMap);
							clusterInfo.put(entry.getValue()+"clusterVocabSize",originalClusterVocabSize);
							clusterVocab = tempClusterVocab;
							break;
						}
					}
				}
			}
			else
			{
				String clusterKey = entry.getValue();
				clusterInfo.remove(entry.getValue()+"termInfo");
				clusterInfo.remove(entry.getValue()+"docInfo");
				clusterInfo.remove(entry.getValue()+"clusterVocabSize");
				int txtLen = text.length();
				int k = Integer.parseInt(clusterKey.substring(txtLen, clusterKey.indexOf("_")));
				k++;
				StringBuilder builder = null;
				for(;k<Configuration.noOfClusters;k++)
				{
					builder = new StringBuilder("Cluster");
					builder.append(k);
					builder.append("_");
					termMap = clusterInfo.get(builder.toString()+"termInfo");
					originalDocMap = clusterInfo.get(builder.toString()+"docInfo");
					originalClusterVocabSize = clusterInfo.get(builder.toString()+"clusterVocabSize");
					clusterInfo.remove(builder.toString()+"termInfo");
					clusterInfo.remove(builder.toString()+"docInfo");
					clusterInfo.remove(builder.toString()+"clusterVocabSize");
					builder = new StringBuilder("Cluster");
					builder.append(k-1);
					builder.append("_");
					clusterInfo.put(clusterKey+"termInfo", termMap);
					clusterInfo.put(clusterKey+"docInfo", originalDocMap);
					clusterInfo.put(clusterKey+"clusterVocabSize", originalClusterVocabSize);
				}
				Configuration.noOfClusters = clusterInfo.size()/3;
				LinkedHashMap<String, BigDecimal> rankingMap = calculateClusterScoreForADoc(docContent);
				Entry<String,BigDecimal> firstVal = ProjectUtils.determineClashOccured(rankingMap);
				if(firstVal != null)
				{
					addToTheCluster(firstVal.getKey(), docContent, entry.getKey());
					totNoOfDocReClustered++;
					dummyDocClusterMap.put(entry.getKey(), firstVal.getKey());
					
				}
				else
				{
					for(String str : rankingMap.keySet())
					{
						addToTheCluster(str, docContent,entry.getKey());
						dummyDocClusterMap.put(entry.getKey(), str);
						break;
					}
				}
			}
		}
		if(totNoOfDocReClustered < stopCondition)
		{
			Configuration.reClusteringEnabled = false;
		}
		docClusterMap = null;
		docClusterMap = dummyDocClusterMap;
		}
	}
	public void removeTermsFromClusterForDoc(HashMap<String, String> termMap, String docContent)
	{
		Set<String> docTokens = new HashSet<>();
		Collections.addAll(docTokens, docContent.split(Configuration.delimitorForToken));
		for(String token : docTokens)
		{
			int count = ProjectUtils.findTfForADoc(token,docContent);
			Integer countInTheCluster = Integer.valueOf(termMap.get(token));
			countInTheCluster -= count;
			if(countInTheCluster == 0)
				termMap.remove(token);
			else
				termMap.put(token,countInTheCluster.toString());
		}
	}
	public static LinkedHashMap<String, Double> findTargeClusters(String docContent)
	{
		LinkedHashMap<String, Double> map = new LinkedHashMap<>();
		StringBuilder builder = null;
		for(int i = 0; i < Configuration.noOfClusters; i ++)
		{
			builder = new StringBuilder("Cluster");
			builder.append(i);
			builder.append("_");
			Set<String> docContentSet = new HashSet<>();
			Collections.addAll(docContentSet,docContent.split(Configuration.delimitorForToken));
			HashMap<String, String> termMap = clusterInfo.get(builder.toString()+"termInfo");
			double clusterTermCnt = 0.0;
			for(String dummy : docContentSet)
			{
				String str = termMap.get(dummy);
				if(StringUtils.isNotBlank(str))
				{
					clusterTermCnt = clusterTermCnt + Integer.parseInt(termMap.get(dummy));
				}
			}
			map.put(builder.toString(), clusterTermCnt);
		}
		map = ProjectUtils.arrangeByValForBM25(map);
		return map;
	}
}