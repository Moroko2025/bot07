package suitebot;

import suitebot.ai.BotAi;
import suitebot.ai.SampleBotAi;
import suitebot.server.SimpleServer;

public class BotServer
{
	public static final int DEFAULT_PORT = 9501;

	public static void main(String[] args)
	{
		BotAi botAi = new SampleBotAi(); // replace with your own AI

		int port = determinePort(args);

		System.out.println("listening on port " + port);
		new SimpleServer(port, new BotRequestHandler(botAi)).run();
	}

	private static int determinePort(String[] args)
	{
		if (args.length == 1)
			return Integer.valueOf(args[0]);
		else
			return DEFAULT_PORT;
	}
}
