package main.java.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import main.java.gui.ImageType;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utils class.
 * 
 * @author Teodora C.
 */
public class Utils {

	private static final String USER_HOME_DIR = System.getProperty("user.home");
	private static final String PERSISTENCE_DIR = "StegoTool";
	private static final String PERSISTENCE_FILE = "persistenceFile.xml";
	private static final String ROOT_TAG = "stegotool";
	private static final String COVER_IMAGE_TAG = ImageType.COVER.getType();
	private static final String SECRET_IMAGE_TAG = ImageType.SECRET.getType();
	private static final String STEGO_IMAGE_TAG = ImageType.STEGO.getType();
	private static final String SOURCE_IMAGE_TAG = ImageType.SOURCE.getType();
	private static final String HIDDEN_IMAGE_TAG = ImageType.HIDDEN.getType();
	private static final String IMAGE_PATH_TAG = "imagepath";

	/**
	 * Creates the xml elements for the xml file.
	 * 
	 * @param data
	 */
	public static void createXmlFileElements(HashMap<String, String> data) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			// root element
			Element rootElement = addRootElement(document, ROOT_TAG);

			// sub root elements
			Element coverImageElement = addSubRootElement(document,
					rootElement, COVER_IMAGE_TAG, "id", "1");
			Element secretImageElement = addSubRootElement(document,
					rootElement, SECRET_IMAGE_TAG, "id", "2");
			Element stegoImageElement = addSubRootElement(document,
					rootElement, STEGO_IMAGE_TAG, "id", "3");
			Element sourceImageElement = addSubRootElement(document,
					rootElement, SOURCE_IMAGE_TAG, "id", "4");
			Element hiddenImageElement = addSubRootElement(document,
					rootElement, HIDDEN_IMAGE_TAG, "id", "5");

			if (data == null) {
				data = defineData();
			}

			// child elements
			addChildrenElements(document, coverImageElement,
					data.get(COVER_IMAGE_TAG));
			addChildrenElements(document, secretImageElement,
					data.get(SECRET_IMAGE_TAG));
			addChildrenElements(document, stegoImageElement,
					data.get(STEGO_IMAGE_TAG));
			addChildrenElements(document, sourceImageElement,
					data.get(SOURCE_IMAGE_TAG));
			addChildrenElements(document, hiddenImageElement,
					data.get(HIDDEN_IMAGE_TAG));

			writeXmlFile(document);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	/**
	 * Defines the persistence data.
	 * 
	 * @return
	 */
	private static HashMap<String, String> defineData() {
		HashMap<String, String> data = new HashMap<String, String>();
		String noPath = "";
		data.put(ImageType.COVER.getType(), noPath);
		data.put(ImageType.SECRET.getType(), noPath);
		data.put(ImageType.STEGO.getType(), noPath);
		data.put(ImageType.SOURCE.getType(), noPath);
		data.put(ImageType.HIDDEN.getType(), noPath);
		return data;
	}

	/**
	 * Adds the root element to the xml document.
	 * 
	 * @param document
	 * @param elementName
	 * @return
	 */
	private static Element addRootElement(Document document, String elementName) {
		Element rootElement = document.createElement(elementName);
		document.appendChild(rootElement);
		return rootElement;
	}

	/**
	 * Adds the sub root element to the root element.
	 * 
	 * @param document
	 * @param rootElement
	 * @param elementName
	 * @param elementAttributeName
	 * @param elementAttributeValue
	 * @return
	 */
	private static Element addSubRootElement(Document document,
			Element rootElement, String elementName,
			String elementAttributeName, String elementAttributeValue) {
		Element subRootElement = document.createElement(elementName);
		rootElement.appendChild(subRootElement);
		Attr attribute = document.createAttribute(elementAttributeName);
		attribute.setValue(elementAttributeValue);
		subRootElement.setAttributeNode(attribute);
		return subRootElement;
	}

	/**
	 * Adds the children to the sub root elements.
	 * 
	 * @param document
	 * @param subRootElement
	 * @param imagePath
	 */
	private static void addChildrenElements(Document document,
			Element subRootElement, String imagePath) {
		Element imagePathElement = document.createElement(IMAGE_PATH_TAG);
		imagePathElement.appendChild(document.createTextNode(imagePath));
		subRootElement.appendChild(imagePathElement);
	}

	/**
	 * Reads data from the xml file.
	 * 
	 * @return
	 */
	public static HashMap<String, String> readXmlFileData() {

		HashMap<String, String> data = null;

		try {

			File persistenceFile = getPersistenceFile();
			if (persistenceFile.exists()) {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory
						.newDocumentBuilder();
				Document document = documentBuilder.parse(persistenceFile);

				// optional, but recommended
				// read this -
				// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				document.getDocumentElement().normalize();

				data = new HashMap<String, String>();
				NodeList nodeList = document.getDocumentElement()
						.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String dataKey = node.getNodeName();
					String dataValue = node.getChildNodes().item(0)
							.getTextContent();
					data.put(dataKey, dataValue);
				}
			} else {
				createXmlFileElements(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Writes data to the xml file.
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, String> writeXmlFileData(
			HashMap<String, String> data) {

		try {

			File persistenceFile = getPersistenceFile();
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(persistenceFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();

			Set<String> dataKeys = data.keySet();
			for (String dataKey : dataKeys) {
				String dataValue = data.get(dataKey);
				Node node = document.getElementsByTagName(dataKey).item(0);
				NodeList nodeList = node.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node childNode = nodeList.item(i);
					if (!childNode.getTextContent().equals(dataValue)) {
						childNode.setTextContent(dataValue);
					}
				}
			}
			writeXmlFile(document);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Creates the instance of the persistence file.
	 * 
	 * @return
	 */
	private static File getPersistenceFile() {
		File persistenceFileDir = new java.io.File(USER_HOME_DIR,
				PERSISTENCE_DIR);
		File persistenceFile = new File(persistenceFileDir, PERSISTENCE_FILE);
		return persistenceFile;
	}

	/**
	 * Writes the xml file.
	 * 
	 * @param document
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private static void writeXmlFile(Document document)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);

		File persistenceFileDir = new java.io.File(USER_HOME_DIR,
				PERSISTENCE_DIR);
		if (!persistenceFileDir.exists()) {
			System.out
					.println("Creating directory to persist the data for the application: "
							+ persistenceFileDir.getAbsolutePath());
			boolean success = new java.io.File(USER_HOME_DIR, PERSISTENCE_DIR)
					.mkdirs();
			if (success) {
				System.out.println("Directory created!");
			}
		}

		File persistenceFile = new File(persistenceFileDir, PERSISTENCE_FILE);
		if (!persistenceFile.exists()) {
			System.out
					.println("Creating file to persist the data for the application: "
							+ persistenceFile.getAbsolutePath());
			try {
				persistenceFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File created!");
		}

		System.out
				.println("Updating file to persist the data for the application: "
						+ persistenceFile.getAbsolutePath());
		StreamResult result = new StreamResult(persistenceFile);
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
		transformer.transform(source, result);

		System.out.println("File updated!");
	}

}
