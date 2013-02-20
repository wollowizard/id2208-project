/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.id2208.project;

import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.PartImpl;
import com.ibm.wsdl.PortTypeImpl;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Annotated;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author alfredo
 */
public class WsdlParser {

    Types types;
    Map messages;
    Map portTypes;
    Map bindings;
    Map services;

    public WsdlParser(String filename) throws WSDLException {
        // get hold the WSDLFactory
        WSDLFactory factory = WSDLFactory.newInstance();

        // create an object to read the WSDL file
        WSDLReader reader = factory.newWSDLReader();

        // pass the URL to the reader for parsing and get back a WSDL definiton
        Definition wsdlInstance = reader.readWSDL(null, filename);

        // get a map of the five specific parts a WSDL file
        types = wsdlInstance.getTypes();
        messages = wsdlInstance.getMessages();
        portTypes = wsdlInstance.getPortTypes();
        bindings = wsdlInstance.getBindings();
        services = wsdlInstance.getServices();
    }

    public String getServiceName() {
        String s = "";
        Iterator it = services.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            s = ((QName) pairs.getKey()).getLocalPart();
        }
        return s;
    }

    public List<OperationImpl> getOperations() {
        List<OperationImpl> operations = new ArrayList<>();
        Iterator it = portTypes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            PortTypeImpl pti = (PortTypeImpl) pairs.getValue();
            operations = (List<OperationImpl>) pti.getOperations();
        }

        return operations;
    }

    public ArrayList<QName> getOutputElements(OperationImpl op) {

        ArrayList<QName> elementNames = new ArrayList<>();

        Map parts = op.getOutput().getMessage().getParts();
        Iterator it2 = parts.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs2 = (Map.Entry) it2.next();
            PartImpl part = (PartImpl) pairs2.getValue();


            QName elementName = part.getElementName();

            if (elementName != null) {
                elementNames.add(elementName);
            }
            //System.out.println("par" + part);


        }
        //System.out.println("aaaaaaaaa" + elementNames);
        return elementNames;
    }

    public ArrayList<QName> getOutputTypes(OperationImpl op) {

        ArrayList<QName> typeNames = new ArrayList<>();

        Map parts = op.getOutput().getMessage().getParts();
        Iterator it2 = parts.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs2 = (Map.Entry) it2.next();
            PartImpl part = (PartImpl) pairs2.getValue();


            QName elementName = part.getTypeName();

            if (elementName != null) {
                typeNames.add(elementName);
            }

        }
        //System.out.println("aaaaaaaaa" + elementNames);
        return typeNames;
    }

    public ArrayList<QName> getInputElements(OperationImpl op) {

        ArrayList<QName> elementNames = new ArrayList<>();
        Map parts = op.getInput().getMessage().getParts();


        Iterator it2 = parts.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs2 = (Map.Entry) it2.next();
            PartImpl part = (PartImpl) pairs2.getValue();


            QName elementName = part.getElementName();
            if (elementName != null) {
                elementNames.add(elementName);
            }

        }
        return elementNames;
    }

    public ArrayList<QName> getInputTypes(OperationImpl op) {

        ArrayList<QName> elementNames = new ArrayList<>();
        Map parts = op.getInput().getMessage().getParts();

        Iterator it2 = parts.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs2 = (Map.Entry) it2.next();
            PartImpl part = (PartImpl) pairs2.getValue();


            QName elementName = part.getTypeName();
            if (elementName != null) {
                elementNames.add(elementName);
            }
        }
        return elementNames;
    }

    public List getExtensibilityElements() {
        return types.getExtensibilityElements();

    }

    public ArrayList<String> getElementsMatchingQnames(QName qname, boolean useType) {



        ArrayList<String> annotated = new ArrayList<>();
        for (Object o : getExtensibilityElements()) {
            if (o instanceof javax.wsdl.extensions.schema.Schema) {
                org.w3c.dom.Element elt = ((javax.wsdl.extensions.schema.Schema) o).getElement();


                if (!useType) {

                    NodeList nl = elt.getChildNodes();
                    ArrayList<Node> elements = getElements(nl);
                    ArrayList<Node> typesArray = getTypes(nl);
                    //node n is an element

                    ArrayList<Node> MatchedElements = new ArrayList<>();
                    for (Node n : elements) {
                        System.out.println(n.getLocalName());
                        //System.out.println(" -----" + n.getAttributes().getNamedItem("name") + "      " + qname.getLocalPart());
                  


                        if (n.getAttributes() != null && n.getAttributes().getNamedItem("name") != null && n.getAttributes().getNamedItem("name").getTextContent().equals(qname.getLocalPart())) {

                            MatchedElements.add(n);
                        }
                    }


                    for (Node n : MatchedElements) {
                        //System.out.println("------" + n.getAttributes().getNamedItem("name"));
                        getAnnotated(n, annotated, typesArray);
                    }

                    /*System.out.println("Array:");
                     for (String s : annotated) {
                     System.out.println("string:" + s);
                     }
                     System.out.println("Array: done");*/
                } else {

                    NodeList nl = elt.getChildNodes();

                    ArrayList<Node> typesArray = getTypes(nl);
                    //node n is an element

                    ArrayList<Node> MatchedTypes = new ArrayList<>();


                    for (Node n : typesArray) {
                        //System.out.println(" -----" + n.getAttributes().getNamedItem("name") + "      " + qname.getLocalPart());
                        if (n.getAttributes().getNamedItem("name").getTextContent().equals(qname.getLocalPart())) {

                            MatchedTypes.add(n);
                        }
                    }

                    for (Node n : MatchedTypes) {
                        //System.out.println("------" + n.getAttributes().getNamedItem("name"));
                        getAnnotated(n, annotated, typesArray);
                    }

                    /*System.out.println("Array:");
                     for (String s : annotated) {
                     System.out.println("string:" + s);
                     }
                     System.out.println("Array: done");*/


                }
            }

        }


        return annotated;
    }

    private void getAnnotated(Node n, ArrayList<String> annotated, ArrayList<Node> types) {
        if (n.getAttributes() != null) {
            //System.out.println("called with node " + n.getLocalName() + n.getAttributes().getNamedItem("name"));
        }
        if (n.getAttributes() != null && n.getAttributes().getNamedItem("sawsdl:modelReference") != null) {
            String toAdd = n.getAttributes().getNamedItem("sawsdl:modelReference").getTextContent();
            String[] split = toAdd.split("#");
            toAdd = split[1];
            if (!annotated.contains(toAdd)) {

                annotated.add(toAdd);
                //System.out.println("added" + n.getAttributes().getNamedItem("sawsdl:modelReference").getTextContent());
            }


        } else {
            //check for type
            if (n.getAttributes() != null && n.getAttributes().getNamedItem("type") != null) {


                String localName = n.getAttributes().getNamedItem("type").getTextContent();
                int index = 1;
                String[] split = localName.split(":");
                if (split.length == 1) {
                    index = 0;
                }
                localName = split[index];

                for (Node n3 : types) {

                    if (n3.getAttributes() != null && n3.getAttributes().getNamedItem("name") != null && n3.getAttributes().getNamedItem("name").getTextContent().equals(localName)) {

                        getAnnotated(n3, annotated, types);

                    }
                }

            } else {


                NodeList childNodes = n.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    getAnnotated(childNodes.item(i), annotated, types);
                }
            }

        }


    }

    private static ArrayList<Node> getElements(NodeList nl) {
        ArrayList<Node> elements = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getLocalName() != null && nl.item(i).getLocalName().equals("element")) {
                //System.out.println(nl.item(i).getLocalName());
                elements.add(nl.item(i));
            }
        }
        return elements;
    }

    private ArrayList<Node> getTypes(NodeList nl) {
        ArrayList<Node> elements = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getLocalName() != null && (nl.item(i).getLocalName().equals("complexType") || nl.item(i).getLocalName().equals("simpleType"))) {
                //System.out.println(nl.item(i).getLocalName());
                elements.add(nl.item(i));
            }
        }
        return elements;
    }

    private void addAllNodes(Node node, List<Node> listOfNodes) {
        if (node != null) {
            listOfNodes.add(node);
            NodeList childNodes = node.getChildNodes();

            List<Node> children = null;

            if (childNodes != null) {
                children = new ArrayList<>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    children.add(childNodes.item(i));
                }
            }


            if (children != null) {
                for (Node child : children) {
                    addAllNodes(child, listOfNodes);
                }
            }
        }
    }

    private static boolean isValidType(String t) {
        if (t.equals("string") || t.equals("int") || t.equals("double") || t.equals("date")) {
            return true;
        }
        return false;
    }

    public List<Node> returnAllNodes(Node node) {
        List<Node> listOfNodes = new ArrayList<Node>();
        addAllNodes(node, listOfNodes);
        return listOfNodes;
    }

    public void getAnnotatedFromComplex(NodeList nl, String name, ArrayList<String> names) {
        ArrayList<Node> all = new ArrayList<>();

        //System.out.println("calling function with " + name);


        for (int i = 0; i < nl.getLength(); i++) {
            all.addAll(returnAllNodes(nl.item(i)));
        }


        for (Node ComplexElement : all) {
            if (ComplexElement.getAttributes() != null && ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference") != null) {
                //System.out.println("aaaaaaaaaaaa    " + ComplexElement.getLocalName() + "   " + ComplexElement.getAttributes().getNamedItem("name")+ "    " +ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference"));
            }
        }




        for (Node ComplexElement : all) {

            if (ComplexElement.getAttributes() != null && ComplexElement.getAttributes().getNamedItem("name") != null && ComplexElement.getAttributes().getNamedItem("name").getTextContent().equals(name)) {
                //System.out.println("aaaaaaaaaaaa" + ComplexElement.getLocalName() + "   " + ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference"));

                //System.out.println("aaaaaaaaaaaa" + ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference").getTextContent());
                if (ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference") != null) {
                    //System.out.println("----" + ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference").getTextContent());
                    names.add(ComplexElement.getAttributes().getNamedItem("sawsdl:modelReference").getTextContent());
                    break;
                }

                if (ComplexElement.getAttributes().getNamedItem("name") != null && ComplexElement.getAttributes().getNamedItem("name").getTextContent().equals(name)) {

                    NodeList childNodes = ComplexElement.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        List<Node> returnAllNodes = returnAllNodes(childNodes.item(i));
                        for (Node n : returnAllNodes) {

                            if (!n.hasChildNodes() && n.getLocalName() != null && n.getLocalName().compareTo("element") == 0) {

                                NamedNodeMap attributes = n.getAttributes();
                                String textContent = attributes.getNamedItem("name").getTextContent();
                                String type = attributes.getNamedItem("type").getNodeValue();

                                //System.out.println("aaaaaaaaaaaaa" + textContent);

                                String[] split = type.split(":");
                                int index = 0;
                                if (split.length == 2) {
                                    index = 1;
                                }
                                String unqualifiedtype = split[index];

                                getAnnotatedFromComplex(nl, unqualifiedtype, names);

                            }
                        }
                    }
                }
            }
        }
    }
}
