SearchEngine
============

An AI based search engine that indexes concordia.ca
<br>
<b>Pre-requisite libraries:</b>
<br>
commons-io-2.4.jar
<br>
commons-lang3-3.1.jar
<br>
jsoup-1.7.3.jar
<br>
websphinx.jar
<br>
<b>Execution:</b>
<br>
The main method is found in the class Execution.java present in the package 'com.project3.execute'. All the indexes are present here with this project. So if you run the code, the code will work fine. If you feel like you would want to re-index everything from the scratch, you are free to do. Just un-comment the crawler, indexing and clustering portion present in the init() of Execution.java. But the indexing might take ~2 hrs to crawl, index and cluster 1000 web pages of concordia.ca. The entire configurations for the project rests with the Configuration.java class in the package 'com.project3.core'. The url to be crawled is also mentioned in the configuration file, which you can change if you wish to index another domain.
