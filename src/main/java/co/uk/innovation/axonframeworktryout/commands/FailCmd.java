package co.uk.innovation.axonframeworktryout.commands;

import lombok.Builder;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
@Builder
public class FailCmd {

    @TargetAggregateIdentifier
    String id;
    boolean fail;

}
