package engine.expansion.tree;

import engine.instruction.utility.InstructionReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpansionNode {
    private final InstructionReference instruction;
    private final ExpansionNode parent;
    private final List<ExpansionNode> children = new ArrayList<>();

    public ExpansionNode(InstructionReference instruction) {
        this(instruction, null); // root node
    }

    // private constructor for child nodes
    private ExpansionNode(InstructionReference instruction, ExpansionNode parent) {
        this.instruction = instruction;
        this.parent = parent;
    }

    public InstructionReference getInstructionRef() {
        return instruction;
    }

    public Optional<ExpansionNode> getParent() {
        return Optional.ofNullable(parent);
    }

    public ExpansionNode addChild(InstructionReference instruction){
        var child = new ExpansionNode(instruction, this);
        children.add(child);
        return child;
    }

    public List<ExpansionNode> getChildren() {
        return List.copyOf(children);
    }
}
