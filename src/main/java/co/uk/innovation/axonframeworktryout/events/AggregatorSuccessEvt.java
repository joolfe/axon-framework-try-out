package co.uk.innovation.axonframeworktryout.events;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AggregatorSuccessEvt {

    String text;

}
