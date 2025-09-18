package engine.instruction.utility;

import engine.instruction.Instruction;

// record that represents instruction and its ordinal id in the program
public record InstructionReference(Instruction instruction, int lineId) {}
