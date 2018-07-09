package co.uk.innovation.axonframeworktryout;

import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.SuccessCmd;
import net.jodah.concurrentunit.Waiter;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.junit.Assert;
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
public class NoExistAgregateTests {

    @Autowired
    private CommandGateway commandGateway;

    @Test(expected = AggregateNotFoundException.class)
    public void aggreateNoExistComand() {

        String id = UUID.randomUUID().toString();
        SuccessCmd cmd = SuccessCmd.builder().id(id).text("MyText").build();
        System.out.println(cmd);
        commandGateway.sendAndWait(cmd);

    }


    @Test
    public void aggreateNoExistFutureComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        SuccessCmd cmd = SuccessCmd.builder().id(id).text("MyText").build();
        CompletableFuture<String> future = commandGateway.send(cmd);
        future.thenAccept((s) -> {
            System.out.println( "Success in Future: ");
            waiter.fail();
        }).exceptionally( ex -> {
            System.out.println( "Error in future: "+ex.getMessage());
            waiter.assertEquals(ex.getCause().getClass(), AggregateNotFoundException.class);
            waiter.resume();
            return null;
        });

        waiter.await(1000);

    }


    @Test
    public void aggreateNoExistCallbackComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        SuccessCmd cmd = SuccessCmd.builder().id(id).text("MyText").build();
        commandGateway.send(cmd, new CommandCallback<SuccessCmd, Object>() {
            @Override
            public void onSuccess(CommandMessage<? extends SuccessCmd> commandMessage, Object result) {
                waiter.fail("Failure creating this");

            }
            @Override
            public void onFailure(CommandMessage<? extends SuccessCmd> commandMessage, Throwable cause) {
                waiter.assertEquals(cause.getClass(), AggregateNotFoundException.class);
                waiter.resume();
            }
        });

        waiter.await(1000);

    }


}
