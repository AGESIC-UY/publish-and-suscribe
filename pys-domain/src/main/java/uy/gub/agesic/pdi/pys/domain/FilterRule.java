package uy.gub.agesic.pdi.pys.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRule implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Operator {
        GREATER, GREATEROREQUAL, EQUAL, MINOR, LESSOREQUAL, CONTAINS;

        @Override
        public String toString() {
            switch (this) {
                case GREATER:
                    return ">";
                case GREATEROREQUAL:
                    return ">=";
                case EQUAL:
                    return "==";
                case MINOR:
                    return "<";
                case LESSOREQUAL:
                    return "<=";
                case CONTAINS:
                    return "contains";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @NotNull
    @Size(max = 250)
    private String name;

    @NotNull
    private Operator operator;

    @NotNull
    private Factor leftFactor;

    @NotNull
    private Factor rightFactor;
}
