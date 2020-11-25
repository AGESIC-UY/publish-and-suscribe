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
public class Factor implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        XPATH, SUBSCRIBER, FIXEDTEXT, LIST
    }

    public enum ValueType {
        TEXT, NUMBER, DATETIME
    }

    @NotNull
    private Type type;

    @NotNull
    @Size(max = 250)
    private String value;

    @NotNull
    @Size(max = 100)
    private ValueType valueType;

    @Size(max = 100)
    private String valueFormat;
}
