package webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.Scanner;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Webscraper {
	
	// Get HTML for specified site
	public static String getPageHtml(String targetSite) {
		URL url;
		String pageHtml = "";
		
		try {
			url = new URL(targetSite);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			
			String content;
			while ((content = br.readLine()) != null) {
				pageHtml += content + "\r\n";
			}
			br.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("*****PROGRAM TERMINATED*****\nUnable to reach the requested site");
			System.exit(0);
		}
		return pageHtml;
	}
	
	// Parse html and get link to all images on the page
	public static String[] getImages(String html, String site) {
		// Add protocol if user forgot, ensure that a / exists at end of domain
		if (! site.endsWith("/")) {
			site += "/";
		}
		String[] imagesArray;
		Document document = Jsoup.parse(html);
		Elements images = document.getElementsByTag("img");
		if (images.size() == 0) {
			System.out.printf("\nNo images found for %s", site);
			System.exit(0);
		}
		
		// Create array of size equal to amount of image tags
		imagesArray = new String[images.size()];
		int counter = 0;
		for (Element image : images) {
			if (image.attr("src") == "") {
				continue;
			}
			if (image.attr("src").contains("http")) {
				imagesArray[counter] = image.attr("src");
			} else if (image.attr("src").substring(0,2).equals("//")) {
				imagesArray[counter] = "http:" + image.attr("src");
			} else {
				imagesArray[counter] = site + image.attr("src");
			}
			counter++;
		}
		return imagesArray;
		
	}
	public static void saveImages(String[] imageLinks) {
		int counter = 1;
		for (String image : imageLinks) {
			// if null, skip
			if (image == null) {
				continue;
			}
			
			String[] tokens=image.split("/");
			String filename = tokens[tokens.length - 1].toLowerCase();
			if (filename.contains("?")) {
				filename = filename.split("[?]")[0];
			}
			
			try {
				URL url = new URL(image);
				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while ((n=in.read(buf)) != -1) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();
				
				// Save this byte array to file locally
				FileOutputStream fos = new FileOutputStream("images\\" + filename);
				fos.write(response);
				fos.close();
				
				System.out.printf("[%d / %d] -- Finished downloading: %s\n**Source -- %s\n", counter, imageLinks.length, filename, image);
				counter ++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} // end try block
	} // end for loop
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		// Get website to scrape
		System.out.print("What website would you like to scrape images from?:\t");
		Scanner scanner = new Scanner(System.in);
		String site = scanner.nextLine();
		scanner.close();
		
		// Get html for the given website
		String html = getPageHtml(site);
		// Get an array containing link to all images
		String imageLinks[] = getImages(html, site);
		saveImages(imageLinks);
		long endTime = System.nanoTime();
		System.out.printf("This program took %d seconds to execute", (endTime - startTime)/1000000000);
		}

	} // end class block
