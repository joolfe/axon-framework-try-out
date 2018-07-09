package co.uk.innovation.axonframeworktryout.commands;

import lombok.Builder;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
@Builder
public class CreateCmd {

    @TargetAggregateIdentifier
    String id;
    String text;

}
