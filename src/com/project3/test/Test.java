package com.project3.test;

import java.io.IOException;
import java.net.URL;

import websphinx.Link;

import com.project3.core.IRCrawler;

public class Test {

	public static void main(String[] args) throws IOException {
//		 CRawler implementation
		IRCrawler crawler = new IRCrawler();
		URL url = new URL("http://www.concordia.ca");
		Link link = new Link(url);
		crawler.setRoot(link);
		crawler.run();
		System.out.println("a");
	}
}
