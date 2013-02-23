package com.id2208.project;

import com.ibm.wsdl.OperationImpl;
import com.id2208.project.generated.MatchedElementType;
import com.id2208.project.generated.MatchedOperationType;
import com.id2208.project.generated.MatchedWebServiceType;
import com.id2208.project.generated.WSMatchingType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

public class InspectWsdlFile {

    private static double THREASHOLD = 0.85;
    private static WSMatchingType matching = new WSMatchingType();
    static WsdlParser p1;
    static WsdlParser p2;
    static MatchedWebServiceType t = new MatchedWebServiceType();
    static Project matcher;

    public static void main(String[] args) throws Exception {



        p1 = new WsdlParser("./out2.wsdl");//output

        p2 = new WsdlParser("./in2.wsdl");//input


        matcher = new Project();

        List<OperationImpl> arropout = p1.getOperations();

        for (OperationImpl op : arropout) {
            //System.out.println("out---------" + op.getName());
        }
        List<OperationImpl> arropin = p2.getOperations();
        for (OperationImpl op : arropin) {
            //System.out.println("---------" + op.getName());
        }





        QName qsave = null;
        for (OperationImpl opout : arropout) {

            System.out.println("    OPERATION:  " + opout.getName());

            ArrayList<QName> outElements = p1.getOutputElements(opout);
            ArrayList<QName> outTypes = p1.getOutputTypes(opout);
            ArrayList<QName> outToUse = outElements;
            boolean outUsingTypes = false;

            if (outToUse.isEmpty()) {
                outToUse = outTypes;
                outUsingTypes = true;
            }

            //outElements.addAll(outToUse);

            for (QName q : outToUse) {
                System.out.println("out: " + q.getLocalPart());
            }

            for (OperationImpl opin : arropin) {


                ArrayList<QName> inputElements = p2.getInputElements(opin);
                ArrayList<QName> inputTypes = p2.getInputTypes(opin);

                ArrayList<QName> inToUse = inputElements;
                boolean inUsingTypes = false;

                if (inToUse.isEmpty()) {
                    inToUse = inputTypes;
                    inUsingTypes = true;
                }


                for (QName q : inToUse) {
                    System.out.println("in: " + q.getLocalPart());
                }



                for (QName q1 : outToUse) {
                    //System.out.println(" ---" + q1.getLocalPart());
                    ArrayList<String> elementsMatchingQnamesOUT = p1.getElementsMatchingQnames(q1, outUsingTypes);
                    for (String s : elementsMatchingQnamesOUT) {
                        System.out.println("------" + s);
                    }



                    for (QName q2 : inToUse) {

                        //System.out.println("    OPOUT: " + opout.getName() + " OPin " + opin.getName());

                        //System.out.println("comparing " + q1.getLocalPart() + " and " + q2.getLocalPart() + " \n");

                        ArrayList<String> elementsMatchingQnamesIN = p2.getElementsMatchingQnames(q2, inUsingTypes);


                        System.out.println("\n\nnew in " + elementsMatchingQnamesIN.size());
                        for (int i = 0; i < elementsMatchingQnamesIN.size(); i++) {
                            System.out.println(elementsMatchingQnamesIN.get(i));

                        }
                        System.out.println("end new in \n\n");




                        cmpArrays(elementsMatchingQnamesIN, opin.getName(), elementsMatchingQnamesOUT, opout.getName());
                        qsave = q2;
                    }
                }
            }


            int count = 0;
            double sum = 0.0;
            for (MatchedOperationType mo : t.getMacthedOperation()) {
                sum += mo.getOpScore();
                count++;
            }
            t.setWsScore(sum / count);

            matching.addMatchedWebServiceType(t);

            File file = new File("output.xml");
            JAXBContext jc = JAXBContext.newInstance("com.id2208.project.generated");
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(new JAXBElement<WSMatchingType>(new QName("ns2:WSMatching"), WSMatchingType.class, matching), file);


            /*System.out.println("qsave: " + qsave.getLocalPart()+"\n");
             ArrayList<String> elementsMatchingQnames = p1.getElementsMatchingQnames(qsave);
             for(String s : elementsMatchingQnames){
             System.out.println(s);
             }*/



        }


    }

    private static void cmpArrays(ArrayList<String> s1, String opin, ArrayList<String> s2, String opout) {
        for (String a : s1) {
            //System.out.println("AAAAAAAAAAAAA" +a);
        }

        //System.out.println("ENDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");



        MatchedOperationType mo = new MatchedOperationType();

        mo.setInputOperationName(opin);
        mo.setOutputOperationName(opout);
        double sum = 0.0;
        int count = 0;

        boolean entered = false;
        for (String word1 : s1) {
            for (String word2 : s2) {
                System.out.println("comparing " + word1 + " and  " +word2);
                double m = matcher.findMatching(word1, word2);
                if (m >= matcher.THRESHOLD) {
                    //add to the output
                    entered = true;
                    MatchedElementType me = new MatchedElementType();
                    me.setInputElement(word1);
                    me.setOutputElement(word2);
                    me.setScore(m);
                    sum += m;
                    count++;
                    mo.addMacthedElement(me);
                }



                /*try {
                 double EditDistanceSimilarity = EditDistance.getSimilarity(word1, word2);

                 SimilarityAssessor sim = new SimilarityAssessor();
                 String metric = SimilarityAssessor.PIRRO_SECO_METRIC;
                 // you can choose the proper metric among the implemented one by specifying its name.

                 double dictionarySimilarity;
                 dictionarySimilarity = sim.getSimilarity(word1, word2, metric);
                 double max = EditDistanceSimilarity;
                 if (dictionarySimilarity > EditDistanceSimilarity) {
                 max = dictionarySimilarity;
                 }
                 if (max > THREASHOLD) {
                 entered = true;
                 MatchedElementType me = new MatchedElementType();
                 me.setInputElement(word1);
                 me.setOutputElement(word2);
                 me.setScore(max);
                 sum += max;
                 count++;
                 mo.addMacthedElement(me);
                 }

                 } catch (WordNotFoundException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
                 }*/
            }
        }


        if (entered) {

            mo.setOpScore(sum / count);
            t.addMacthedOperation(mo);
            t.setInputServiceName(p1.getServiceName());
            t.setOutputServiceName(p2.getServiceName());
        }
    }
}
