package com.project3.core;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

import com.project3.util.ProjectLogger;
import com.project3.util.ProjectUtils;

public class HTMLParser
{
	static HashMap<String, StringBuilder> abstractMap = new HashMap<String, StringBuilder>();
	static HashMap<String, String> dataMap = new HashMap<String, String>();
	static HashMap<String,ArrayList<String>> invertedIndex = new HashMap<String, ArrayList<String>>();
	static HashMap<String,HashMap<String, Integer>> termFrequencyMap = new HashMap<String, HashMap<String, Integer>>();
	static HashMap<String,HashMap<String,String>> refMap = new HashMap<String,HashMap<String,String>>();
	static HashMap<String,String> docID_size = new HashMap<String,String>();
	static HashMap<String,String> processing_params = new HashMap<String,String>();

	static int noOfDocs=0;

	public static void parse() throws IOException {
		StringBuilder builder = ProjectUtils.readFileContent();
		HTMLParser hpObject = new HTMLParser();
		StringTokenizer builderTokens = new StringTokenizer(builder.toString());
		String tempToken="";
		while(builderTokens.hasMoreElements())
		{
			tempToken=builderTokens.nextToken();
			String[] docTitleArr=null;
			String docTitle="";
			String[] docURLArr=null;
			String docURL="";
			String docID="";
			StringBuilder docBuilder = new StringBuilder();
			if(tempToken!=null && tempToken.contains("<IIR>"))
			{
				String[] tokenArray = tempToken.split("<IIR>");

				docTitleArr=StringUtils.substringsBetween(tokenArray[1], "<IIRTitle>", "</IIRTitle>");
				docTitle=docTitleArr[0];

				docURLArr=StringUtils.substringsBetween(tokenArray[1], "<IIRUrl>", "</IIRUrl>");
				docURL=docURLArr[0];


				Date date = new Date();
				String dateStr = String.valueOf(date.getTime());

				docID=docTitle+"<>"+docURL+"<>"+dateStr;

				while(builderTokens.hasMoreElements())
				{
					tempToken=builderTokens.nextToken();
					if(tempToken!=null && tempToken.equals("</IIR>"))
					{
						break;
					}
					else
					{
						tempToken = tempToken.toString().trim();
						docBuilder.append(tempToken);
						docBuilder.append(" ");
					}
				}
				abstractMap.put(docID, docBuilder);	
				String content = ProjectUtils.parseHtmlFiles(docBuilder);
				dataMap.put(docID, content);
				ProjectLogger.logger.log(Level.INFO,"Document processed : "+docID);
			}
		}
		hpObject.createInvertedIndex(dataMap);
		hpObject.createTermFrequencyMap();
	}

	private void createTermFrequencyMap() throws IOException 
	{	
		HashMap<String,Integer> idFreqMap=null;
		ArrayList<String> docIDsList = null;
		for(Map.Entry<String, ArrayList<String>> eachTermDocIdsMap : invertedIndex.entrySet())
		{
			idFreqMap = new HashMap<String,Integer>();
			String dummyTerm = eachTermDocIdsMap.getKey();
			docIDsList = eachTermDocIdsMap.getValue();
			for(String eachDocID : docIDsList)
			{
				int tfCounter=0;
				String dataFromDocID = dataMap.get(eachDocID).toString();
				String[] dataTokensFromID = dataFromDocID.split(" ");
				for(int i=0;i<dataTokensFromID.length;i++)
				{
					if((!dataTokensFromID[i].isEmpty()) && (dummyTerm.contains(dataTokensFromID[i])))
					{
						tfCounter++;//this is no of docs containing this term only
					}
				}
				idFreqMap.put(eachDocID, tfCounter);
				tfCounter=0;
			}
			termFrequencyMap.put(dummyTerm, idFreqMap);

			docIDsList.clear();
		}
		ProjectUtils.doSerialization(Configuration.invertedIndex, termFrequencyMap);
	}

	public void createInvertedIndex(HashMap<String, String> dataMap2) throws IOException
	{
		int collectionSize=0;
		int docSize;
		for(Map.Entry<String, String> eachEntryInDataMap : dataMap2.entrySet())
		{
			if(eachEntryInDataMap.getKey()!=null)
			{
				docSize=0;
				noOfDocs++;
				String ID = eachEntryInDataMap.getKey();
				String data = eachEntryInDataMap.getValue().toString();
				data = data.replaceAll("[^A-Za-z ]", "");
				data.toLowerCase();
				StringTokenizer docTokens = new StringTokenizer(data.toString());

				String docToken="";
				while(docTokens.hasMoreElements())
				{
					docToken=docTokens.nextToken();
					if(docToken!=null)
					{
						docSize++;
						collectionSize++;
						if(invertedIndex.get(docToken)==null)
						{
							ArrayList<String> postingList =new ArrayList<String>();
							if(!invertedIndex.containsValue(ID))
							{
								postingList.add(ID);
								invertedIndex.put(docToken, postingList);
							}
						}
						else if(invertedIndex.get(docToken)!=null)
						{
							if(!invertedIndex.get(docToken).contains(ID))
							{
								invertedIndex.get(docToken).add(ID);
							}
						}
					}	
				}
				docID_size.put(ID, String.valueOf(docSize));
			}
		}
		processing_params.put(String.valueOf(noOfDocs), String.valueOf(collectionSize));

		refMap.put("docID_size", docID_size);
		refMap.put("processing_params", processing_params);
		refMap.put("docID_data", dataMap2);
		ProjectUtils.doSerialization(Configuration.index, refMap);
		//refMap.put("docID_data_orig", dataMap);
	}
}