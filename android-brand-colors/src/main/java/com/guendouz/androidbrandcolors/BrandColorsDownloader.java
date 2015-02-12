package com.guendouz.androidbrandcolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class will get the brand colors from the branccolor.com website using the JSoup library and save it in XML file.
 */
public class BrandColorsDownloader {

    public final static String ROOT_XML = "resources";
    public final static String COLOR_XML = "color";
    public final static String NAME_ATTRIBUTE = "name";

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {

        /**
         * Declaring xml stuff
         */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        org.w3c.dom.Document root = builder.newDocument();
        org.w3c.dom.Element resources = root.createElement(ROOT_XML);
        root.appendChild(resources);

        /**
         * Declaring jsoup stuff
         */
        Document html = Jsoup.parse(new URL("http://brandcolors.net/"), 10000);

        Elements brandColors = html.select("li.brand");
        if (brandColors != null && brandColors.size() > 0) {
            String brandName;
            for (Element brandColor : brandColors) {
                brandName = clean(brandColor.attr("data-name").toLowerCase());
                Elements colors = brandColor.select("div.color");
                for (int i = 0; i < colors.size(); i++) {
                    org.w3c.dom.Element colorNode = root.createElement(COLOR_XML);
                    Attr colorHex = root.createAttribute(NAME_ATTRIBUTE);
                    colorHex.setValue(brandName + "_" + (i + 1));

                    colorNode.appendChild(root.createTextNode("#" + colors.get(i).attr("data-hex")));
                    colorNode.setAttributeNode(colorHex);

                    resources.appendChild(colorNode);
                }
            }

            /**
             * transforming xml object (in memory) to xml file
             */
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(root);
            StreamResult result = new StreamResult(new File("colors.xml"));


            transformer.transform(source, result);
        } else {
            System.out.println("Error");
        }

    }

    /**
     * this method takes a String object as parameter and return it without special characters
     */
    public static String clean(String name) {
        return "bc_" + name.replaceAll("[^a-zA-Z0-9]+"/*"[^\\w\\s\\-_]"*/, "_");

    }
}
