package co.uk.innovation.axonframeworktryout;

import co.uk.innovation.axonframeworktryout.agregator.AggregateTest;
import co.uk.innovation.axonframeworktryout.agregator.ReturnType;
import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.SuccessCmd;
import net.jodah.concurrentunit.Waiter;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandAndGetAgregateTests {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private EventStore store;

    @Test
    public void returnObjectComand() {

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);
        System.out.println("HERE WE ARE");

        /*
        UnitOfWork uow = DefaultUnitOfWork.startAndGet(null);
        AggregateTest test = (AggregateTest) store.load(id);
        System.out.println(test);

        */
        DomainEventStream eventStream = store.readEvents(id);
        while ( eventStream.hasNext() ){
            DomainEventMessage devm = eventStream.next();
            System.out.println("Typo " + devm.getType());
        }

    }




}
