import engine.execution.ProgramRunner;
import engine.execution.Runner;
import engine.jaxb.loader.ProgramLoader;
import engine.jaxb.loader.XMLLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.program.Program;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        XMLLoader loader = new ProgramLoader();

        try {
            loader.loadXML(
                    "C:\\Users\\Dmytro\\Akademit\\Java\\tests\\basic.xml"
            );

            loader.validateProgram();
            Program program = loader.getProgram();
            Runner runner = new ProgramRunner(program);
            runner.run(5L);
            System.out.println(runner.getResult());

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NotXMLException | UnknownLabelException e) {
            System.out.println(e.getMessage());
        }
    }
}