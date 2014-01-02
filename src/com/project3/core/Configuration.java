package com.project3.core;

public class Configuration {
	public static final String directoryToSaveFiles = ".//HtmlFiles//";
	public static final int noOfFilesToBeAggregated = 100;
	public static final String logDirectory = ".//Logs//IIR_Project3.log";
	public static final String url = "http://www.concordia.ca";
	public static final int maxDocsToBeCrawld = 1000;
	public static final String index = ".//Index.txt";
	public static final String outrMapDelimitor = "#ome@";
	public static final String delimForKeyAndValInrMap = "#eik@";
	public static final String delimForKeyAndValOutrMap = "#eok@";
	public static final String inrMapDelimitor = "#ime@";
	public static int noOfClusters = 30;
	public static final int topContenders = 40;
	public static int stopPointForReClustering = 5;
	public static boolean reClusteringEnabled = true;
	public static final double scoreMultiplier = 100;
	public static final String delimitorForToken = " ";
	public static final int weightForTitleTag = 5;
	public static final int weightForH1Tag = 2;
	public static final String clusterIndex = ".//clusterIndex.txt";
	public static final boolean stopWordsRemoval = true;
	public static String[] stopWords = null;
	public static final int totOutputDocs = 50;
	public static final String invertedIndex = ".//InvertedIndex.txt";
	public static final double k = 1.2;
	public static final double b = 0.75;
}
