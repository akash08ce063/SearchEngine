package com.project3.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.project3.util.ProjectUtils;


public class Bm25F{
	public static HashMap<String, HashMap<String, String>> invertedIndex = null;
	private String[] qryAry = null;
	public LinkedHashMap<String, Double> doQuerying(String query)
	{
		query = ProjectUtils.doPreprocessing(query);
		LinkedHashMap<String, Double> rankedHashMap = KMeansClusterer.findTargeClusters(query);
		qryAry = query.split(Configuration.delimitorForToken);
		LinkedHashMap<String, Double> scoreMap = new LinkedHashMap<>();
		for(Entry<String, Double> entry : rankedHashMap.entrySet())
		{
			String key = entry.getKey();
			HashMap<String, String> docMap = KMeansClusterer.clusterInfo.get(key+"docInfo");
			System.out.println(KMeansClusterer.clusterInfo.get(key+"termInfo"));
			int clusterDocSize = docMap.size();
			System.out.println(docMap);
			int clusterVocabSize = Integer.parseInt(KMeansClusterer.clusterInfo.get(key+"clusterVocabSize").get("clusterVocabSize"));
			double avgDocLength = (double)clusterVocabSize/(double)clusterDocSize;
			for(String qryTerm : qryAry)
			{
				double score = 1.0;
				HashMap<String, String>docTfMap = invertedIndex.get(qryTerm);
				int docCount = 0;
				HashMap<String, Integer> matchingDocs = new HashMap<>();
				for(Entry<String, String> keyVal :  docTfMap.entrySet())
				{
					String docKey = keyVal.getKey();
					String val = docMap.get(docKey); 
					if(StringUtils.isNotBlank(val))
					{
						docCount = docCount + 1;
						matchingDocs.put(docKey,Integer.parseInt(val));
					}
				}
				double num = clusterDocSize - docCount + 0.5;
				double denom = docCount + 0.5;
				double IDF = Math.log(num/denom);
				score = score*IDF;
				for(Entry<String, Integer> docDetails : matchingDocs.entrySet())
				{
					String tempKey = docDetails.getKey();
					int tf = Integer.parseInt(docTfMap.get(tempKey));
					double secNum = (Configuration.k + 1)*tf;
					double lenOfDoc = docDetails.getValue();
					Double scoreForDoc = scoreMap.get(tempKey);
					if(scoreForDoc == null)
						scoreForDoc = 0.0;
					double secDenom = tf + (Configuration.k*(1-Configuration.b+(Configuration.b*(lenOfDoc/avgDocLength))));
					scoreForDoc = scoreForDoc + score*secNum/secDenom;
					scoreMap.put(tempKey, scoreForDoc);
				}
			}
			ProjectUtils.arrangeByValForBM25(scoreMap);
			break;
		}
		return scoreMap;
	}

}