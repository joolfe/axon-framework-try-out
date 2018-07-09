package co.uk.innovation.axonframeworktryout;

import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.FailCmd;
import co.uk.innovation.axonframeworktryout.commands.SuccessCmd;
import net.jodah.concurrentunit.Waiter;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvariantsAgregateTests {

    @Autowired
    private CommandGateway commandGateway;

    @Test(expected = IllegalStateException.class)
    public void invariantFailComand() {

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        FailCmd cmdF = FailCmd.builder().id(id).fail(true).build();
        commandGateway.sendAndWait(cmdF);

    }


    @Test
    public void invariantFailFutureComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        FailCmd cmdF = FailCmd.builder().id(id).fail(true).build();

        CompletableFuture<String> future = commandGateway.send(cmdF);
        future.thenAccept((s) -> {
            System.out.println( "Success in Future: ");
            waiter.fail();
        }).exceptionally( ex -> {
            System.out.println( "Error in future: "+ex.getMessage());
            waiter.assertEquals(ex.getCause().getClass(), IllegalStateException.class);
            waiter.resume();
            return null;
        });

        waiter.await(1000);

    }


    @Test
    public void aggreateNoExistCallbackComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        FailCmd cmdF = FailCmd.builder().id(id).fail(true).build();

        commandGateway.send(cmdF, new CommandCallback<FailCmd, Object>() {
            @Override
            public void onSuccess(CommandMessage<? extends FailCmd> commandMessage, Object result) {
                waiter.fail("Failure creating this");

            }
            @Override
            public void onFailure(CommandMessage<? extends FailCmd> commandMessage, Throwable cause) {
                waiter.assertEquals(cause.getClass(), IllegalStateException.class);
                waiter.resume();
            }
        });

        waiter.await(1000);

    }


}
