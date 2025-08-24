package engine.program;

import engine.instruction.Instruction;

// record that represents instruction and its ordinal id in the program
public record InstructionLocator(Instruction instruction, int lineId) {}
