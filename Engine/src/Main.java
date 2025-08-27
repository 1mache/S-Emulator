import engine.execution.ProgramRunner;
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
                    "C:\\Users\\Dmytro\\Akademit\\Java\\tests\\synthetic.xml"
            );

            loader.validateProgram();
            Program program = loader.getProgram();
            var runner = new ProgramRunner(program);
            System.out.println("Max expansion degree: " + runner.getMaxExpansionDegree());
            runner.initInputVariables(1L, 2L);
            runner.run(3);
            System.out.println(runner.getResult());
            System.out.println("Took cycles: " + runner.getCycles());

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NotXMLException | UnknownLabelException e) {
            System.out.println(e.getMessage());
        }
    }
}