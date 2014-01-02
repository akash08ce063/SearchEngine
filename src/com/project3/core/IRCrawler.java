package com.project3.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import websphinx.Crawler;
import websphinx.DownloadParameters;
import websphinx.Page;

import com.project3.util.ProjectLogger;
import com.project3.util.ProjectUtils;

public class IRCrawler extends Crawler {
	private static final long serialVersionUID = 2383514014091378008L;
	private HashMap<String, Boolean> linksRead = new HashMap<String, Boolean>();
	private StringBuilder fileContent = new StringBuilder("");
	private int count = 0;
	private int docsCrawled = 0;
	public IRCrawler() {
		DownloadParameters dp = new DownloadParameters();
		dp.changeAcceptedMIMETypes("text/html");
		dp.changeObeyRobotExclusion(true);
		dp.changeMaxPageSize(100);
		dp.changeUserAgent("IRCrawler Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.4) "
				+ "WebSPHINX 0.5 k_sivapr@live.concordia.ca");
		setMaxDepth(4);
		setDownloadParameters(dp);
		setDomain(Crawler.SERVER);
		setLinkType(Crawler.HYPERLINKS);
		File rootFolder = new File(Configuration.directoryToSaveFiles);
		if(!rootFolder.isDirectory())
		{
			try{
			FileUtils.forceMkdir(rootFolder);
			}
			catch(IOException e)
			{
				ProjectLogger.logger.log(Level.SEVERE," Exception Occured : ",e);
			}
		}
	}
	public void visit(Page page) {
		doVisit(page);
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
		}
	}
	public synchronized void doVisit(Page page)
	{
		URL url = page.getURL();
		if(url != null){
			StringBuilder urlRead = new StringBuilder("");
			urlRead.append(url.getProtocol());
			urlRead.append("://");
			urlRead.append(url.getAuthority());
			urlRead.append(url.getFile());
			String dummy = urlRead.toString();
			if(page.getContentType().equalsIgnoreCase("text/html") && linksRead.get(dummy) == null)
			{
				linksRead.put(dummy, true);
				ProjectLogger.logger.log(Level.INFO, dummy);
				String content = page.getContent();
				if(StringUtils.isNotBlank(content))
				{
					String fileName = ProjectUtils.extractFileNameFromUrl(dummy);
					fileContent.append("<IIR><IIRTitle>");
					fileContent.append(fileName);
					fileContent.append("</IIRTitle>");
					fileContent.append("<IIRUrl>");
					fileContent.append(dummy);
					fileContent.append("</IIRUrl>");
					fileContent.append(content);
					fileContent.append("\n</IIR>\n");
					count++;
					if(count == Configuration.noOfFilesToBeAggregated)
					{
						Date d = new Date();
						String dateStr = String.valueOf(d.getTime());
						File file = new File(Configuration.directoryToSaveFiles+dateStr+".txt");
						try{
						FileUtils.writeStringToFile(file, fileContent.toString(), Charset.defaultCharset());
						}
						catch(IOException e)
						{
							ProjectLogger.logger.log(Level.SEVERE,"Exception Occured : ",e);
							docsCrawled = docsCrawled - count;
						}
						docsCrawled = docsCrawled + count;
						ProjectLogger.logger.log(Level.INFO,"Docs crawled till now : "+docsCrawled);
						count = 0;
						fileContent = new StringBuilder();
						if(docsCrawled == Configuration.maxDocsToBeCrawld)
						{
							stop();
						}
					}
				}
			}
		}
	}
/*	public boolean shouldVisit(Link l) {
		URL url = l.getURL();
		if(url != null){
			StringBuilder urlRead = new StringBuilder("");
			urlRead.append(url.getProtocol());
			urlRead.append(url.getAuthority());
			urlRead.append(url.getFile());
			Boolean linkPresent = linksRead.get(urlRead);
			int index = urlRead.indexOf(".");
			if(index != -1)
			{
			}
//			if(urlRead.in)
//			System.out.println("Link Present : "+linkPresent);
			if(linkPresent == null)
			{
				return true;
			}
			if(linkPresent)
			{
				System.out.println("Link is present..");
			}
		}
		return false;
	}*/
}
