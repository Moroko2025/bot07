package suitebot.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer implements Runnable
{
	public static final String SHUTDOWN_REQUEST = "EXIT";
	public static final String UPTIME_REQUEST = "UPTIME";

	private final int port;
	private final SimpleRequestHandler requestHandler;

	private boolean shouldShutDown = false;
	private long startTimestamp;

	public SimpleServer(int port, SimpleRequestHandler requestHandler)
	{
		this.port = port;
		this.requestHandler = requestHandler;
	}

	public void run()
	{
		try
		{
			runInternal();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void runInternal() throws IOException
	{
		ServerSocket listener = null;
		Socket socket = null;
		startTimestamp = System.currentTimeMillis();

		try
		{
			listener = new ServerSocket(port);

			while (!shouldShutDown)
			{
				try
				{
					socket = listener.accept();
					handleRequest(socket);
				}
				finally
				{
					if (socket != null)
						socket.close();
				}
			}
		}
		finally
		{
			if (listener != null)
				listener.close();
		}
	}

	private void handleRequest(Socket socket) throws IOException
	{
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);

		String request = inputReader.readLine();
		if (request == null)
			return;

		if (SHUTDOWN_REQUEST.equals(request))
		{
			shouldShutDown = true;
			return;
		}

		if (UPTIME_REQUEST.equals(request))
			outputWriter.println((System.currentTimeMillis() - startTimestamp) / 1000);
		else
			outputWriter.println(requestHandler.processRequest(request));
	}
}
