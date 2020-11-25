package uy.gub.agesic.pdi.pys.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterSubscriber {

    private String reason;

    private String subscriberId;
}
