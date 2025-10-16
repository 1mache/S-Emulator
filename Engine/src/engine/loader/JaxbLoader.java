package engine.loader;

import engine.jaxb.generated.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class JaxbLoader {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "engine.jaxb.generated";

    public static SProgram loadProgramFromXML(String xmlPath)
            throws JAXBException, FileNotFoundException {
        InputStream inputStream = new FileInputStream(xmlPath);
        return loadProgramFromXML(inputStream);
    }

    public static SProgram loadProgramFromXML(InputStream inputStream)
            throws JAXBException {
        return deserializeFrom(inputStream);
    }

    private static SProgram deserializeFrom(InputStream in)
            throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (SProgram) u.unmarshal(in);
    }
}
