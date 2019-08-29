/* 
 * Copyright PT Len Industri (Persero) 

 *  
 * TO PT LEN INDUSTRI (PERSERO), AS APPLICABLE, AND SHALL NOT BE USED IN ANY WAY
 * OTHER THAN BEFOREHAND AGREED ON BY PT LEN INDUSTRI (PERSERO), NOR BE REPRODUCED
 * OR DISCLOSED TO THIRD PARTIES WITHOUT PRIOR WRITTEN AUTHORIZATION BY
 * PT LEN INDUSTRI (PERSERO), AS APPLICABLE
 */

package org.len.tdl.duy.xml_CRUD;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.transform.TransformerConfigurationException;

/**
 *
 * @author Solveware
 */

/**
 * 
 * This is xml_CRUD class
 */
public class xml_CRUD {

     /**
     * Reference Variables
     */
    public ArrayList<String> Daftar_skenario = new ArrayList<String>(5);
    public String[][] Daftar_port = new String[2][2];
    public String[] Daftar_setting = new String[3];
    private boolean existing_file;
    
    /**
     * Method to get characters
     * @param field
     * @param evt
     */
    public static void typedAngka(JTextField field, KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
            Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
    }
    String myDocumentPath = System.getProperty("user.home") + "/Documents";
    File fXmlFile = new File(myDocumentPath+ "/tools_datalink/xml/file.xml"); 
    
    /**
     * Method to read or load Xml File
     */
    public void read() {

        if (fXmlFile.exists() && !fXmlFile.isDirectory()) {
            setExisting_file(true);
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                NodeList PUList = doc.getElementsByTagName("PU");
                Daftar_skenario.clear();
                for (int temp = 0; temp < PUList.getLength(); temp++) {
                    Node nNode = PUList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String jml_paket = eElement.getElementsByTagName("jml_paket").item(0).getTextContent();
                        String delay = eElement.getElementsByTagName("delay").item(0).getTextContent();
                        String id = eElement.getAttribute("id");
                        if ("-".equals(jml_paket) & "-".equals(delay)) {

                        } else {
                            Daftar_skenario.add(eElement.getAttribute("id") + "#"
                                    + eElement.getElementsByTagName("jml_paket").item(0).getTextContent() + "#"
                                    + eElement.getElementsByTagName("delay").item(0).getTextContent() + "#"
                                    + eElement.getElementsByTagName("noname").item(0).getTextContent());
                        }
                    }
                }

                NodeList portList = doc.getElementsByTagName("Port");
                for (int temp1 = 0; temp1 < portList.getLength(); temp1++) {
                    Node mNode = portList.item(temp1);
                    if (mNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) mNode;
                        Daftar_port[temp1][0] = eElement.getElementsByTagName("name").item(0).getTextContent();
                        Daftar_port[temp1][1] = eElement.getElementsByTagName("baudrate").item(0).getTextContent();
                    }
                }

                NodeList settList = doc.getElementsByTagName("Settings");
                for (int temp2 = 0; temp2 < settList.getLength(); temp2++) {
                    Node oNode = settList.item(temp2);
                    if (oNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) oNode;
                        Daftar_setting[0] = eElement.getElementsByTagName("key").item(0).getTextContent();
                        Daftar_setting[1] = eElement.getElementsByTagName("datarate").item(0).getTextContent();
                        Daftar_setting[2] = eElement.getElementsByTagName("own_NPU").item(0).getTextContent();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setExisting_file(false);
            JOptionPane.showMessageDialog(null, "Xml file aren't exist ", "InfoBox: XML not found", JOptionPane.INFORMATION_MESSAGE);
        }

    }
    
    /**
     * Method to print out array list
     */
    public void print_arraylist() {
        int jml = Daftar_skenario.size();
        for (int i = 0; i == jml; i++) {
        }
    }
/**
 * 
 * @param key String key 16 karakter
 * @param data_rate datarate modem ex: 300, 600, 1200, 2400, 9600
 * @param own_NPU self addres / own number participant unit
 */

    public void updt_sett(String key, String data_rate, String own_NPU) {
        if (fXmlFile.exists() && !fXmlFile.isDirectory()) {

            try {

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc;
                doc = docBuilder.parse(fXmlFile);
                int port = doc.getElementsByTagName("Settings").getLength();
                for (int i = 0; i < port; i++) {
                    Node Sett = doc.getElementsByTagName("Settings").item(i);
                    NamedNodeMap attr = Sett.getAttributes();
                    Node nodeAttr = attr.getNamedItem("id");
                    if ("1".equals(nodeAttr.getTextContent())) {
                        NodeList xlist = Sett.getChildNodes();
                        for (int x = 0; x < xlist.getLength(); x++) {
                            Node node = xlist.item(x);
                            if ("key".equals(node.getNodeName())) {
                                node.setTextContent(key);
                                node.setNodeValue(key);
                            }
                            if ("datarate".equals(node.getNodeName())) {
                                node.setTextContent(data_rate);
                                node.setNodeValue(data_rate);
                            }
                            if ("own_NPU".equals(node.getNodeName())) {
                                node.setTextContent(own_NPU);
                                node.setNodeValue(own_NPU);
                            }
                        }
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(fXmlFile.toPath().toString()));

                transformer.transform(source, result);

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Method to update port GPS and Modem, Baudrate GPS and Modem
     * @param port_GPS
     * @param port_Data
     * @param BaudGPS
     * @param Bauddata
     */
    public void updt_port(String port_GPS, String port_Data, String BaudGPS, String Bauddata) {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc;
            doc = docBuilder.parse(fXmlFile);
            int port = doc.getElementsByTagName("Port").getLength();
            for (int i = 0; i < port; i++) {
                Node Port = doc.getElementsByTagName("Port").item(i);
                NamedNodeMap attr = Port.getAttributes();
                Node nodeAttr = attr.getNamedItem("id");
                if ("1".equals(nodeAttr.getTextContent())) {
                    NodeList xlist = Port.getChildNodes();
                    for (int x = 0; x < xlist.getLength(); x++) {
                        Node node = xlist.item(x);
                        if ("name".equals(node.getNodeName())) {
                            node.setTextContent(port_GPS);
                            node.setNodeValue(port_GPS);
                        }
                        if ("baudrate".equals(node.getNodeName())) {
                            node.setTextContent(BaudGPS);
                            node.setNodeValue(BaudGPS);
                        }
                    }
                }
                if ("2".equals(nodeAttr.getTextContent())) {
                    NodeList xlist = Port.getChildNodes();
                    for (int x = 0; x < xlist.getLength(); x++) {
                        Node node = xlist.item(x);
                        if ("name".equals(node.getNodeName())) {
                            node.setTextContent(port_Data);
                            node.setNodeValue(port_Data);
                        }
                        if ("baudrate".equals(node.getNodeName())) {
                            node.setTextContent(Bauddata);
                            node.setNodeValue(Bauddata);
                        }

                    }
                }

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fXmlFile.toPath().toString()));
            transformer.transform(source, result);

        } catch (SAXException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     *
     * @param NPU
     * @param item
     * @param delay
     */
    public void updt(String NPU, String item, String delay) {
        if (fXmlFile.exists() && !fXmlFile.isDirectory()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc;
                doc = docBuilder.parse(fXmlFile);
                int a = doc.getElementsByTagName("PU").getLength();
                for (int i = 0; i < a; i++) {
                    Node PU = doc.getElementsByTagName("PU").item(i);
                    NamedNodeMap attr = PU.getAttributes();
                    Node nodeAttr = attr.getNamedItem("id");
                    if (NPU.equals(nodeAttr.getTextContent())) {
                        NodeList list = PU.getChildNodes();
                        for (int b = 0; b < list.getLength(); b++) {
                            Node node = list.item(b);
                            if ("jml_paket".equals(node.getNodeName())) {
                                node.setTextContent(item);
                                node.setNodeValue(item);
                            }
                            if ("delay".equals(node.getNodeName())) {
                                node.setTextContent(delay);
                                node.setNodeValue(delay);
                                doc.createTextNode(delay);
                            }
                            if ("noname".equals(node.getNodeName())) {
                                node.setTextContent("-");
                                node.setNodeValue("-");
                                doc.createTextNode("-");
                            }
                        }
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(fXmlFile.toPath().toString()));
                transformer.transform(source, result);

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Method to update scenario table
     * @param NPU
     */
    public void update_scenario(String NPU) {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc;
            doc = docBuilder.parse(fXmlFile);
      
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fXmlFile.toPath().toString()));
            transformer.transform(source, result);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(xml_CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the existing_file
     */
    public boolean isExisting_file() {
        return existing_file;
    }

    /**
     * @param existing_file the existing_file to set
     */
    public void setExisting_file(boolean existing_file) {
        this.existing_file = existing_file;
    }
}