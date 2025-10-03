package engine.api.dto;

import java.util.Objects;

public record FunctionIdentifier(String name, String userString) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FunctionIdentifier that = (FunctionIdentifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
