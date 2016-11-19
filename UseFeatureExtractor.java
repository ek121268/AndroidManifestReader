package com.perfecto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UseFeatureExtractor {

	public List<String> extract(String filePath) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		byte[] buf = readFile(filePath);

		String xml = AndroidXMLDecompress.decompressXML(buf);

		Document document = getDocument(xml);

		List<String> usesFeature = getUsesFeature(document);

		return usesFeature;
	}

	private byte[] readFile(String filePath) throws IOException, FileNotFoundException {
		InputStream is = null;
		ZipFile zip = null;

		if (filePath.endsWith(".apk") || filePath.endsWith(".zip")) {

			zip = new ZipFile(filePath);
			ZipEntry mft = zip.getEntry("AndroidManifest.xml");
			is = zip.getInputStream(mft);

		} else {
			is = new FileInputStream(filePath);
		}

		byte[] buf = new byte[10240];
		is.read(buf);

		is.close();
		if (zip != null) {
			zip.close();
		}
		return buf;
	}

	private Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		return doc;
	}

	private List<String> getUsesFeature(Document document) {
		NodeList nodes = document.getElementsByTagName("uses-feature");
		List<String> usesFeatures = new LinkedList<>();
		if (nodes != null) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				String name = element.getAttribute("name");
				if (name == null) {
					name = element.getAttribute("android:name");
				}
				if (name != null) {
					System.out.println("name: " + name);
					usesFeatures.add(name);
				}
			}
		}
		return usesFeatures;
	}
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		String filePath = "C:\\aaa\\apps\\testMoveLongPath.apk";
		UseFeatureExtractor extractor = new UseFeatureExtractor();
		List<String> usesFeature = extractor.extract(filePath);
		System.out.println(usesFeature);
	}

}
