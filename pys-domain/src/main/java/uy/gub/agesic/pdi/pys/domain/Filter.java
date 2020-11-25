package uy.gub.agesic.pdi.pys.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Filter implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Operator {
        AND, OR
    }

    public enum DocumentType {
        XML, JSON
    }

    @Id
    private String id;

    @NotNull
    @Size(max = 250)
    @Indexed
    private String name;

    @NotNull
    private Operator operator;

    @NotNull
    private DocumentType documentType;

    @Builder.Default
    List<FilterRule> rules = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
