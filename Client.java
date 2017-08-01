import javax.xml.soap.*;
import java.util.Iterator;
import java.net.URL;
import java.io.*;

public class Client {

    public static void main(String[] args) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            SOAPFactory soapFactory = SOAPFactory.newInstance();

            SOAPMessage message = prepareMessage();

            URL endpoint = new URL("https://opendap.co-ops.nos.noaa.gov/axis/services/highlowtidepred");
            SOAPMessage response = connection.call(message, endpoint);
            connection.close();

            System.out.print("\nPrinting the message that is being sent: \n\n");
            message.writeTo(System.out);
            System.out.println("\n\n");

            /*
            System.out.println("\nPrinting the respone that was recieved: \n\n" );
            response.writeTo(System.out);
             */

            //Uncoment this part if you want the response to be saved locally in an XML file
            /*
            FileOutputStream fout = new FileOutputStream ("SoapResponse.xml");
            response.writeTo(fout);
            fout.close();
             */

            //You can also store the response as a String
            /*
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo( out );
            String str = out.toString();  
             */

            System.out.println("\n\nIterating through the response object to get the values:\n\n");
            SOAPBody responseBody = response.getSOAPBody();

            //Checking for errors
            if (responseBody.hasFault()) {
                SOAPFault fault = responseBody.getFault();
                String actor = fault.getFaultActor();
                System.out.println("Fault contains: ");
                System.out.println("Fault code: " + fault.getFaultCodeAsName().getQualifiedName());
                System.out.println("Fault string: " + fault.getFaultString());
                if (actor != null) {
                    System.out.println("Actor: " + actor);
                }
            } else {
                Iterator iterator = responseBody.getChildElements();
                Iterator iterator2 = null;
                Iterator iterator3 = null;
                Iterator iterator4 = null;

                String tagName = null;
                Name attributeName = null;
                SOAPElement se = null;

                if (iterator.hasNext()) {
                    se = (SOAPElement) iterator.next();
                    iterator = se.getChildElements();
                    while (iterator.hasNext()) {
                        se = (SOAPElement) iterator.next();
                        printMetadata(se);
                        tagName = se.getElementName().getLocalName();
                        if ("HighLowValues".equals(tagName)) {
                            iterator2 = se.getChildElements();
                            while (iterator2.hasNext()) {
                                se = (SOAPElement) iterator2.next();
                                attributeName = soapFactory.createName("date");
                                System.out.println("=======================");
                                System.out.println(se.getAttributeValue(attributeName));
                                System.out.println("=======================");
                                iterator3 = se.getChildElements();
                                while (iterator3.hasNext()) {
                                    se = (SOAPElement) iterator3.next();
                                    iterator4 = se.getChildElements();
                                    while (iterator4.hasNext()) {
                                        se = (SOAPElement) iterator4.next();
                                        printData(se);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (SOAPException e) {
            System.err.println("ERROR: ******* " + e.toString());
        } catch (IOException io) {
            System.err.println("ERROR: ******* " + io.toString());
        }
    }

    public static SOAPMessage prepareMessage() {
        SOAPMessage message = null;
        try {
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            MessageFactory factory = MessageFactory.newInstance();
            message = factory.createMessage();

            SOAPHeader header = message.getSOAPHeader();
            SOAPBody body = message.getSOAPBody();
            header.detachNode();
            message.getMimeHeaders().addHeader("SOAPAction", "");

            Name bodyName = soapFactory.createName("getHLPredAndMetadata", "hilo",
                    "https://opendap.co-ops.nos.noaa.gov/axis/webservices/highlowtidepred/wsdl");
            SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

            //Constructing the body for the request
            Name name = soapFactory.createName("stationId");
            SOAPElement symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("9446484");
            name = soapFactory.createName("beginDate");
            symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("20170801 00:00");
            name = soapFactory.createName("endDate");
            symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("20170830 23:59");
            name = soapFactory.createName("datum");
            symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("MLLW");
            name = soapFactory.createName("unit");
            symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("0");
            name = soapFactory.createName("timeZone");
            symbol = bodyElement.addChildElement(name);
            symbol.addTextNode("0");
        } catch (SOAPException e) {
            System.err.println("ERROR: ******* " + e.toString());
        }
        return message;
    }

    public static void printMetadata(SOAPElement se) {
        String tagName = se.getElementName().getLocalName();
        if (tagName != null) {
            if ("stationId".equals(tagName)) {
                System.out.println("Printing Metadata \n");
                System.out.println("Station ID       : " + se.getValue());
            } else if ("stationName".equals(tagName)) {
                System.out.println("station Name     : " + se.getValue());
            } else if ("latitude".equals(tagName)) {
                System.out.println("Latitude         : " + se.getValue());
            } else if ("longitude".equals(tagName)) {
                System.out.println("Longitude        : " + se.getValue());
            } else if ("state".equals(tagName)) {
                System.out.println("State            : " + se.getValue());
            } else if ("dataSource".equals(tagName)) {
                System.out.println("Data Source      : " + se.getValue());
            } else if ("COOPSDisclaimer".equals(tagName)) {
                System.out.println("COOPS Disclaimer : " + se.getValue());
            } else if ("beginDate".equals(tagName)) {
                System.out.println("Begin Date       : " + se.getValue());
            } else if ("endDate".equals(tagName)) {
                System.out.println("End Date         : " + se.getValue());
            } else if ("datum".equals(tagName)) {
                System.out.println("Datum            : " + se.getValue());
            } else if ("unit".equals(tagName)) {
                System.out.println("Unit             : " + se.getValue());
            } else if ("timeZone".equals(tagName)) {
                System.out.println("Time Zone        : " + se.getValue() + "\n");
                System.out.println("Printing the data \n");
            }
        }
    }

    public static void printData(SOAPElement se) {
        String tagName = se.getElementName().getLocalName();
        if (tagName != null) {
            if (tagName.equals("time")) {
                System.out.println("Time        : " + se.getValue());
            } else if (tagName.equals("pred")) {
                System.out.println("Pred        : " + se.getValue());
            } else if (tagName.equals("type")) {
                System.out.println("Type        : " + se.getValue() + "\n");
            }
        }
    }
}
/****************************************************
SAMPLE RUN
 
>java Client
 
Printing the message that is being sent:

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Body>
        <hilo:getHLPredAndMetadata xmlns:hilo="https://opendap.co-ops.nos.noaa.gov/axis/webservices/highlowtidepred/wsdl">
            <stationId>8454000</stationId>
            <beginDate>20060921 00:00</beginDate>
            <endDate>20060922 23:59</endDate>
            <datum>0</datum>
            <unit>0</unit>
            <timeZone>0</timeZone>
        </hilo:getHLPredAndMetadata>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
 
Iterating through the response object to get the values:


Printing Metadata 

Station ID       : 8454000
station Name     : Providence
Latitude         : 41.8067
Longitude        : -71.4017
State            : RI
Data Source      : USDOC/NOAA/NOS/COOPS(Center for Operational Oceanographic Products and Services)
COOPS Disclaimer : The official Tide and Tidal Current prediction tables are published annually 
                   on October 1, for the following calendar year. 
                   Tide and Tidal Current predictions generated prior to the publishing date of 
                   the official tables are subject to change. The enclosed data are based upon 
                   the latest information available as of the date of your request. 
                   Tide and Tidal Current predictions generated may differ from the official 
                   predictions if information for the station requested has been updated since 
                   the publishing date of the official tables.
Begin Date       : 20060921 00:00
End Date         : 20060922 23:59
Datum            : MLLW
Unit             : Feet
Time Zone        : LST

Printing the data 

=======================
09/21/2006
=======================
Time        : 07:08
Pred        : 4.9
Type        : H

Time        : 12:13
Pred        : 0.3
Type        : L

Time        : 19:25
Pred        : 4.6
Type        : H

=======================
09/22/2006
=======================
Time        : 00:36
Pred        : 0.2
Type        : L

Time        : 07:43
Pred        : 5.0
Type        : H

Time        : 12:58
Pred        : 0.2
Type        : L

Time        : 19:57
Pred        : 4.6
Type        : H

**************************************************************/ 
