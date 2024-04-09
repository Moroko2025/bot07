package suitebot.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleServerTest
{
	private static final int PORT = 4444;
	private Thread serverThread;

	@BeforeEach
	void setUp() throws Exception
	{
		startServer();
	}

	@AfterEach
	void tearDown() throws Exception
	{
		if (serverThread.isAlive())
			shutdownServer();

		waitForServerToShutDown(1000);
	}

	@Test
	void testShuttingDown() throws Exception
	{
		assertTrue(serverThread.isAlive());
		shutdownServer();
		waitForServerToShutDown(1000);
		assertFalse(serverThread.isAlive());
	}

	@Test
	void testUptimeRequest() throws Exception
	{
		Thread.sleep(1500);
		int upTime = Integer.valueOf(requestServerResponse(SimpleServer.UPTIME_REQUEST));
		assertThat(upTime).isGreaterThan(0);
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	void testRequestResponse() throws Exception
	{
		assertThat(requestServerResponse("FooBar")).isEqualTo("foobar");
		assertThat(requestServerResponse("NextREQUEST")).isEqualTo("nextrequest");
	}

	private void startServer() throws InterruptedException
	{
		serverThread = new Thread(new SimpleServer(PORT, new ToLowerCaseConverter()));
		serverThread.start();
		Thread.sleep(100); // give the server thread some time to initialize
	}

	private void shutdownServer() throws IOException
	{
		requestServerResponse(SimpleServer.SHUTDOWN_REQUEST);
	}

	private void waitForServerToShutDown(int maxWaitTimeMilliseconds) throws InterruptedException, TimeoutException
	{
		int elapsed = 0;
		while (serverThread.isAlive())
		{
			if (elapsed > maxWaitTimeMilliseconds)
				throw new TimeoutException("timed out while waiting for the server to shut down");

			Thread.sleep(10);
			elapsed += 10;
		}
	}

	private String requestServerResponse(String request) throws IOException
	{
		try (
				Socket socket = new Socket("localhost", PORT);
				PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
		)
		{
			outputWriter.println(request);
			return inputReader.readLine();
		}
	}

	private static class ToLowerCaseConverter implements SimpleRequestHandler
	{
		@Override
		public String processRequest(String request)
		{
			return request.toLowerCase();
		}
	}
}
