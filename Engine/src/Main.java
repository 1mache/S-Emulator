import engine.execution.ProgramRunner;
import engine.jaxb.loader.FromXMLProgramLoader;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();

        try {
            loader.loadXML(
                    "C:\\Users\\Dmytro\\Akademit\\Java\\tests\\minus.xml"
            );

            loader.validateProgram();
            Program program = loader.getProgram();

            var runner = new ProgramRunner(program);
            System.out.println("Max expansion degree: " + runner.getMaxExpansionDegree());
            runner.initInputVariables(List.of(5L,2L));
            runner.run(2);
            System.out.println(runner.getVariableValues());
            System.out.println("Took cycles: " + runner.getCycles());

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NotXMLException | UnknownLabelException e) {
            System.out.println(e.getMessage());
        }
    }
}