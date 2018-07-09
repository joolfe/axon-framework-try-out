package co.uk.innovation.axonframeworktryout;

import co.uk.innovation.axonframeworktryout.agregator.ReturnType;
import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.FailCmd;
import co.uk.innovation.axonframeworktryout.commands.SuccessCmd;
import com.sun.corba.se.spi.protocol.RetryType;
import net.jodah.concurrentunit.Waiter;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
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
public class ReturnCommandAgregateTests {

    @Autowired
    private CommandGateway commandGateway;

    @Test
    public void returnObjectComand() {

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        SuccessCmd cmdS = SuccessCmd.builder().id(id).text("HI").build();
        ReturnType ret = commandGateway.sendAndWait(cmdS);

        Assert.assertEquals(ret.getTest(), "Hi");
        Assert.assertEquals(ret.getTest2(), "Hello");

    }


    @Test
    public void returnObjectFutureComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        SuccessCmd cmdS = SuccessCmd.builder().id(id).text("HI").build();

        CompletableFuture<ReturnType> future = commandGateway.send(cmdS);
        future.thenAccept((ReturnType r) -> {
            System.out.println( "Success in Future: ");
            waiter.assertEquals(r.getTest() , "Hi");
            waiter.assertEquals(r.getTest2() , "Hello");
            waiter.resume();
        }).exceptionally( ex -> {
            System.out.println( "Error in future: "+ex.getMessage());
            waiter.fail();
            return null;
        });

        waiter.await(1000);

    }


    @Test
    public void returnObjectCallbackComand() throws TimeoutException {

        final Waiter waiter = new Waiter();

        String id = UUID.randomUUID().toString();
        CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
        commandGateway.sendAndWait(cmd);

        SuccessCmd cmdS = SuccessCmd.builder().id(id).text("HI").build();

        commandGateway.send(cmdS, new CommandCallback<SuccessCmd, ReturnType>() {
            @Override
            public void onSuccess(CommandMessage<? extends SuccessCmd> commandMessage, ReturnType r) {
                waiter.assertEquals(r.getTest() , "Hi");
                waiter.assertEquals(r.getTest2() , "Hello");
                waiter.resume();
            }
            @Override
            public void onFailure(CommandMessage<? extends SuccessCmd> commandMessage, Throwable cause) {
                waiter.fail();
            }
        });

        waiter.await(1000);

    }


}
