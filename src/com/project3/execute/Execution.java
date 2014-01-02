package com.project3.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.project3.core.Bm25F;
import com.project3.core.Configuration;
import com.project3.core.HTMLParser;
import com.project3.core.KMeansClusterer;
import com.project3.util.ProjectUtils;

public class Execution {
	public void init() throws IOException
	{
		//Stop words initialization
		String stopWords = FileUtils.readFileToString(new File("stopwords.txt"));
		stopWords = stopWords.replaceAll("(\r\n)+", ",");
		Configuration.stopWords = stopWords.split(",");
		//Stop words initialization ends..

		//Crawler run
		/*		IRCrawler crawler = new IRCrawler();
		URL url = new URL("http://www.concordia.ca");
		Link link = new Link(url);
		crawler.setRoot(link);
		crawler.run();*/
		//Crawler run ends here..

		//Parser and indexer portion
		//		HTMLParser.parse();
		//Parser and indexer portion ends

		//K-means clustering start
		//		KMeansClusterer clusterer = new KMeansClusterer();
		//		clusterer.doClustering();
		//K-means clustering ends

		//Reading from the serialized data
		HashMap<String, HashMap<String, String>> tempMap = (HashMap<String, HashMap<String, String>>)ProjectUtils.doReadFromSerailizedFile(Configuration.index);
		KMeansClusterer.docIdTokenMap = tempMap.get("docID_data");
		HashMap<String, HashMap<String, String>> tempx = (HashMap<String, HashMap<String, String>>) ProjectUtils.doReadFromSerailizedFile(Configuration.clusterIndex);
		KMeansClusterer.clusterInfo.putAll(tempx);
		Bm25F.invertedIndex  = (HashMap<String, HashMap<String, String>>) ProjectUtils.doReadFromSerailizedFile(Configuration.invertedIndex);
		//Reading from the serialized data ends..
	}
	public static void main(String args[]) throws Exception
	{
		Execution execution = new Execution();
		execution.init();
		Bm25F obj = new Bm25F();
		String qry = null;
		boolean rqt = true;
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		while(rqt)
		{
			System.out.println("Pls enter the query..");
			qry = rdr.readLine();
//			qry = "Network optimization";
			System.out.println(qry);
			if(StringUtils.isNotBlank(qry))
			{
				LinkedHashMap<String, Double> outputMap = obj.doQuerying(qry);
				int i = 0;
				if(outputMap != null && outputMap.size() > 0)
				{
					for(Entry<String, Double> entry : outputMap.entrySet())
					{
						if(i < outputMap.size() && i < 5){
							String docId = entry.getKey();
							String docIdAry [] = docId.split("<>");
							System.out.println("Document url :"+ docIdAry[1]);
							System.out.println("Document score :"+entry.getValue());
							System.out.println("Document contetn : ");
							System.out.println(KMeansClusterer.docIdTokenMap.get(docId).length());
							System.out.println(KMeansClusterer.docIdTokenMap.get(docId));
							System.out.println("*************************************************************************");
							i++;
						}
					}
				}	
			}
			System.out.println("Do you need to try for other queries : Press Y or N");
			qry = rdr.readLine();
			if(qry.equalsIgnoreCase("y"))
				rqt = true;
			else
				rqt = false;
		}
	}
}
