package co.uk.innovation.axonframeworktryout;

import co.uk.innovation.axonframeworktryout.commands.CreateCmd;
import co.uk.innovation.axonframeworktryout.commands.CreateCmdTimeout;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateComandsTests {

	@Autowired
	private CommandGateway commandGateway;

	@Test
	public void sendAndWaitCreateComand() {

		String id = UUID.randomUUID().toString();
		CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
		String returnedId = commandGateway.sendAndWait(cmd);

		Assert.assertEquals(id, returnedId);
	}

	@Test
	public void futureSendCreateComand() throws ExecutionException, InterruptedException {

		String id = UUID.randomUUID().toString();
		CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
		CompletableFuture<String> future = commandGateway.send(cmd);
		String returnedId = future.get();

		Assert.assertEquals(id, returnedId);

	}

	@Test
	public void callbackCreateComand() throws TimeoutException {

		final Waiter waiter = new Waiter();

		String id = UUID.randomUUID().toString();
		CreateCmd cmd = CreateCmd.builder().id(id).text("MyText").build();
		commandGateway.send(cmd, new CommandCallback<CreateCmd, Object>() {
			@Override
			public void onSuccess(CommandMessage<? extends CreateCmd> commandMessage, Object result) {
				waiter.assertEquals(id, result.toString());
				waiter.resume();
			}
			@Override
			public void onFailure(CommandMessage<? extends CreateCmd> commandMessage, Throwable cause) {
				waiter.fail("Failure creating this");
			}
		});

		waiter.await(1000);

	}



	@Test
	public void sendAndWaitTimeOKCreateComand() {

		String id = UUID.randomUUID().toString();
		CreateCmdTimeout cmd = CreateCmdTimeout.builder().id(id).time(2000).build();
		String returnedId = commandGateway.sendAndWait(cmd, 3000, TimeUnit.MILLISECONDS);

		Assert.assertEquals(id, returnedId);
	}


	@Test
	public void sendAndWaitTimeoutCreateComand() {

		String id = UUID.randomUUID().toString();
		CreateCmdTimeout cmd = CreateCmdTimeout.builder().id(id).time(2000).build();
		String returnedId = commandGateway.sendAndWait(cmd, 100, TimeUnit.NANOSECONDS);
		System.out.println("RETURNED");
		Assert.assertEquals(id, returnedId);

	}

}
