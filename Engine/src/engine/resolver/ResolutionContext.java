package engine.resolver;

import engine.expansion.SymbolRegistry;
import engine.program.generator.LabelVariableGenerator;

public record ResolutionContext(
        SymbolRegistry usedSymbols,
        SymbolRegistry ignoredSymbols,
        LabelVariableGenerator symbolGenerator
){}
