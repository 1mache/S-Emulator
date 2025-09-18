import engine.api.dto.ProgramPeek;
import engine.execution.ProgramRunner;
import engine.loader.FromXMLProgramLoader;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();

        try {
            loader.loadXML(
                    "C:\\Users\\Dmytro\\Akademit\\Java\\tests\\minus.xml", null
            );

            loader.validateProgram();
            Program program = loader.getProgram();

            var runner = new ProgramRunner(program);
            System.out.println("Max expansion degree: " + program.getMaxExpansionDegree());
            runner.initInputVariables(List.of(5L,2L));
            ProgramPeek peek = new ProgramViewer(program).getProgramPeek(2);
            for (var instr : peek.instructions()) {
                System.out.println(instr.stringRepresentation());
            }
            runner.run();
            System.out.println("Output: " + runner.getRunOutput());
            System.out.println("Took cycles: " + runner.getCycles());

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NotXMLException | UnknownLabelException e) {
            System.out.println(e.getMessage());
        }
    }
}