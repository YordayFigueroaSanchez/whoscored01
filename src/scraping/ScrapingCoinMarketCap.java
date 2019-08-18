package scraping;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;

public class ScrapingCoinMarketCap {

	public static final String xmlFilePath = "xmlfile.xml";

	public static void main(String[] args) throws ParserConfigurationException, TransformerConfigurationException {
		// TODO Auto-generated method stub

		String url = "https://coinmarketcap.com/";

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// elemento raiz
		org.w3c.dom.Document doc = docBuilder.newDocument();
		org.w3c.dom.Element rootElement = doc.createElement("monedas");
		doc.appendChild(rootElement);

		if (getStatusConnectionCode(url) == 200) {
			Document documento = getHtmlDocument(url);
			Elements elementos = documento.select("#currencies tr:has(td.text-center)");
			System.out.println(elementos.size());
			for (Element elem : elementos) {
				String titulo = elem.getElementsByClass("text-center").text();
				String autor = elem.getElementsByClass("currency-name-container").text();
				//String marketCap = elem.getElementsByClass("market-cap").text();
				String price = elem.getElementsByClass("price").text();
				System.out.println(titulo + ":" + autor + ":" + price);

				// moneda
				org.w3c.dom.Element empleado = doc.createElement("moneda");
				rootElement.appendChild(empleado);
				// atributo del elemento empleado
				Attr attr = doc.createAttribute("id");
				attr.setValue(titulo);
				empleado.setAttributeNode(attr);

				// nombre
				org.w3c.dom.Element nombre = doc.createElement("nombre");
				nombre.appendChild(doc.createTextNode(autor));
				empleado.appendChild(nombre);

				// valor
				org.w3c.dom.Element valor = doc.createElement("valor");
				valor.appendChild(doc.createTextNode(price));
				empleado.appendChild(valor);
			}
		}

		
		//nombre del fichero
		Date  fecha = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss"); 
		System.out.println("Hora y fecha: "+hourdateFormat.format(fecha));
		String nombreFichero = hourdateFormat.format(fecha);
		
		// escribimos el contenido en un archivo .xml
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String ruta = "dataXML\\";
		StreamResult result = new StreamResult(new File(ruta,nombreFichero + ".xml"));

		// StreamResult result = new StreamResult(new File("archivo.xml"));
		// Si se quiere mostrar por la consola...
		// StreamResult result = new StreamResult(System.out);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File saved!");

	}

	/**
	 * Con esta método compruebo el Status code de la respuesta que recibo al hacer
	 * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
	 * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
	 * 502 Bad Gateway 503 Service Unavailable
	 * 
	 * @param url
	 * @return Status Code
	 */
	public static int getStatusConnectionCode(String url) {

		Response response = null;

		try {
			response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
		}
		return response.statusCode();
	}

	/**
	 * Con este método devuelvo un objeto de la clase Document con el contenido del
	 * HTML de la web que me permitirá parsearlo con los métodos de la librelia
	 * JSoup
	 * 
	 * @param url
	 * @return Documento con el HTML
	 */
	public static Document getHtmlDocument(String url) {

		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		}
		return doc;
	}
}
