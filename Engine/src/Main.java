import engine.execution.ProgramRunner;
import engine.execution.Runner;
import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.instruction.concrete.IncreaseInstruction;
import engine.jaxb.loader.JaxbLoader;
import engine.jaxb.loader.JaxbTranslator;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.variable.Variable;
import engine.variable.VariableType;
import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Program program = JaxbTranslator.getProgram(
                    JaxbLoader.loadProgramFromXML("C:\\Users\\Dmytro\\Akademit\\Java\\tests\\basic.xml")
            );
            Runner runner = new ProgramRunner(program);
            runner.run(5L);
            System.out.println(runner.getResult());

        } catch (JAXBException e) {
            System.out.println("JAXB Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());;
        }
    }
}