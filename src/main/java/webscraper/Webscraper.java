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
			e.printStackTrace();
		}
		return pageHtml;
	}
	
	// Parse html and get link to all images on the page
	public static String[] getImages(String html, String site) {
		// Formatting site so that it can be prepended to img source
		if (! site.endsWith("/")) {
			site += "/";
		}
		String[] imagesArray;
		Document document = Jsoup.parse(html);
		Elements images = document.getElementsByTag("img");
		// Create array of size equal to amount of image tags
		imagesArray = new String[images.size()];
		int counter = 0;
		for (Element image : images) {
			System.out.println(image.attr("src").substring(0,2));
			Boolean isDoubleSlash = image.attr("src").substring(0,2).equals("//");
			System.out.println(isDoubleSlash);
			if (image.attr("src").contains("http")) {
				System.out.println("Images are already fully qualified!");
				imagesArray[counter] = image.attr("src");
//				counter++;
			} else if (image.attr("src").substring(0,2).equals("//")) {
				System.out.println("Starts with '//'");
				imagesArray[counter] = "http:" + image.attr("src");
//				counter ++;
			} else {
				imagesArray[counter] = site + image.attr("src");
//				counter++;
			}
			counter++;
		}
		for (String name : imagesArray)
			System.out.println(name);
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
				
				System.out.printf("[%d / %d] -- Finished downloading: %s\n%s\n", counter, imageLinks.length, filename, image);
				counter ++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} // end try block
	} // end for loop
	
	public static void main(String[] args) {
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
		
		}

	} // end class block
