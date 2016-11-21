package com.perfecto;

import java.io.ByteArrayOutputStream;
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

public class AndroidManifestReader {
	
	private Document _document;
	
	public AndroidManifestReader(String filePath) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		byte[] buf = readFile(filePath);
		String xml = AndroidXMLDecompress.decompressXML(buf);
		_document = getDocument(xml);
	}

	public List<String> getUsesFeatures() {
		List<String> usesFeatures = getElementNames("uses-feature");
		return usesFeatures;
	}
	
	public List<String> getUsesPermissions() {
		List<String> usesPermissions = getElementNames("uses-permission");
		return usesPermissions;
	}
	
	public List<String> getActivities() {
		List<String> activities = getElementNames("activity");
		return activities;
	}
	
	public Document getDocument() {
		return _document;
	}
	
	private List<String> getElementNames(String path) {
		NodeList nodes = _document.getElementsByTagName(path);
		List<String> elementNames = new LinkedList<>();
		if (nodes != null) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				String name = element.getAttribute("name");
				if (name == null) {
					name = element.getAttribute("android:name");
				}
				if ((name != null) && (!name.equals(""))) {
		//			System.out.println("name: " + name);
					elementNames.add(name);
				}
			}
		}
		return elementNames;
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

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		int nRead;
		byte[] data = new byte[10240];
		
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		
		buffer.flush();
		
		is.close();
		if (zip != null) {
			zip.close();
		}
		return buffer.toByteArray();
	}
	

	private Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		return doc;
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		String filePath = "C:\\aaa\\apps\\testMoveLongPath.apk";
//		String filePath = "C:\\aaa\\apps\\1939680295-angry-birds-2.apk";		
		AndroidManifestReader reader = new AndroidManifestReader(filePath);
		
		List<String> usesFeatures = reader.getUsesFeatures();
		System.out.println("Features:" + usesFeatures);
		
		List<String> usesPermissions = reader.getUsesPermissions();
		System.out.println("Permissions:" + usesPermissions);
		
		List<String> activities = reader.getActivities();
		System.out.println("Activities:" + activities);


	}

}
