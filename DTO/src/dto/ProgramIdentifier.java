package dto;

import java.util.Objects;

public record ProgramIdentifier(String name, String userString, boolean isMain) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProgramIdentifier that = (ProgramIdentifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
