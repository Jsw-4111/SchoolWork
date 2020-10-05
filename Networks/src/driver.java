import java.net.InetSocketAddress;

public class driver {
	/*
		Is it okay to not implement the UDPI files?
	*/
	public static void main(String[] args)
	{
		String fName = "src\\text.txt";
		int mode = 0;
		long modeParam = 150;
		long timeout = 200;
		int lPort = 3344;
		RSendUDP send = new RSendUDP();
		InetSocketAddress r = new InetSocketAddress("127.0.0.1", 3345);
		send.setFilename(fName);
		send.setMode(mode);
		send.setLocalPort(lPort);
		send.setTimeout(timeout);
		send.setReceiver(r);
		send.sendFile();
	}
}