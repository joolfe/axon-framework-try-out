package co.uk.innovation.axonframeworktryout.agregator;

import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.CreateCmdTimeout;
import co.uk.innovation.axonframeworktryout.commands.FailCmd;
import co.uk.innovation.axonframeworktryout.commands.SuccessCmd;
import co.uk.innovation.axonframeworktryout.events.AggregatorCreatedEvt;
import co.uk.innovation.axonframeworktryout.events.AggregatorSuccessEvt;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class AggregateTest {

    @AggregateIdentifier
    String id;
    String text;

    public AggregateTest() {
    }

    @CommandHandler
    public AggregateTest(CreateCmd cmd) {
        System.out.println("CreateCmd arrive "+cmd);
        apply(AggregatorCreatedEvt.builder().id(cmd.getId()).text(cmd.getText()).build());
    }

    @CommandHandler
    public AggregateTest(CreateCmdTimeout cmd) throws InterruptedException {
        System.out.println("Start sleep");
        Thread.sleep(cmd.getTime());
        System.out.println("End sleep");
        apply(AggregatorCreatedEvt.builder().id(cmd.getId()).text("").build());
    }

    @CommandHandler
    public ReturnType sucess(SuccessCmd cmd){
        apply(AggregatorSuccessEvt.builder().text("success").build());
        return new ReturnType("Hi", "Hello");
    }

    @CommandHandler
    public void shouldFail(FailCmd cmd){
        if( cmd.isFail() ) throw new IllegalStateException("Incorrect state!!");
        apply(AggregatorSuccessEvt.builder().text("success fail").build());
    }

    @EventSourcingHandler
    public void on(AggregatorCreatedEvt evt){
        System.out.println("AggregatorCreatedEvt arrive");
        this.id = evt.getId();
        this.text = evt.getText();
    }

    @EventSourcingHandler
    public void on(AggregatorSuccessEvt evt){
        System.out.println("AggregatorSuccessEvt arrive");
        this.text = evt.getText();
    }


}
