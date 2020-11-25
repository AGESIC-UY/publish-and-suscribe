package uy.gub.agesic.pdi.pys.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FilterTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        MESSAGE, SUBSCRIBER
    }

    @Id
    private String id;

    @DBRef
    private Topico topic;

    @DBRef
    private Filter filter;

    @NotNull
    private Type type;

    @NotNull
    private int maximumOccurrences;
}
