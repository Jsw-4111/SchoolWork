import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.utulsa.unet.UDPSocket;
import edu.utulsa.unet.RSendUDPI;


public class RSendUDP implements RSendUDPI{

	private int gMode;
	private long sCons = 256;
	private long fSize = 0;
	private long time = 1000; // in milliseconds
	private String name;
	private int lPort = 0;
	private final int defPort = 12987;
	private InetSocketAddress receiverIP;
	private final InetSocketAddress def = new InetSocketAddress(1);
	private UDPSocket socket;
	private List<DatagramPacket> packs = new ArrayList<DatagramPacket>();
	private final int headerSize = 8;
	/*
	 * Grader should set all information EXCEPT for the MTU which is received from the unet.properties file.
	 * Sender and receiver should attach a header to all data files which IS INCLUDED in the maximum transmission size.
	 * All other parts are included by the datagram/UDPSocket classes and do not count towards the transmission size.
	 * Use inputstreamreader
	 */
	public static void main(String[] args)
	{
		InetSocketAddress r = new InetSocketAddress("127.0.0.1", 3345);
		RSendUDP rSend = new RSendUDP();
		rSend.setFilename("bin\\text.txt");
		rSend.setMode(0);
		rSend.setLocalPort(3344);
		rSend.setTimeout(200);
		rSend.setReceiver(r);
		rSend.sendFile();
	}

	@Override
	public boolean setMode(int mode) {
		// TODO Auto-generated method stub
		if (mode > 1 || mode < 0)
		{
			System.out.println("Error detected! Mode values range from 0 to 1, please try again.");
		}
		gMode = mode;
		return false;
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return gMode;
	}

	@Override
	public boolean setModeParameter(long n) { // This is the size of your window. Only applicable for mode == 1
		// TODO Auto-generated method stub
		sCons = n;
		return false;
	}

	@Override
	public long getModeParameter() {
		// TODO Auto-generated method stub
		return sCons;
	}

	@Override
	public void setFilename(String fname) {
		// TODO Auto-generated method stub
		name = fname;
	}

	@Override
	public String getFilename() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean setTimeout(long timeout) { // in milliseconds
		// TODO Auto-generated method stub
		time = timeout;
		return false;
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return time;
	}

	@Override
	public boolean setLocalPort(int port) {
		// TODO Auto-generated method stub
		lPort = port;
		System.out.println("Hello my port is " + lPort);
		return false;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return lPort;
	}

	@Override
	public boolean setReceiver(InetSocketAddress receiver) {
		// TODO Auto-generated method stub
		receiverIP = receiver;
		System.out.println(receiverIP);
		return false;
	}

	@Override
	public InetSocketAddress getReceiver() {
		// TODO Auto-generated method stub
		return receiverIP;
	}

	@Override
	public boolean sendFile() {
		/*
		 * If datagram packet adds a header, how do I know the size of my header?
		 */
		
		// take size of the elements in buffer and add them together, divide them by size to get the number of packets.
		if(lPort == 0)
		{
			lPort = 12987;
		}
		if(receiverIP.equals(def))
		{
			try {
				receiverIP = new InetSocketAddress(InetAddress.getLocalHost(), lPort);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				System.out.println("Issue receiving IP from socket");
			}
		}
		try {
			String mode = "";
			if(gMode == 0)
				mode = "Stop and Wait";
			else
				mode = "sliding window";
			System.out.println("Expecting connection with host from an IP of " + InetAddress.getLocalHost() + 
					" on port number " + lPort + " using the " + mode + " file transfer method.");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Issue receiving local IP, please try again.");
		}
		try
		{
			UDPSocket socket = new UDPSocket(lPort);
			System.out.println("Socket established with connection to port " + socket.getPort());
			int size = socket.getSendBufferSize();
			int maxFrames = (int) Math.ceil(sCons/size);
			byte[] rBuffer = new byte[socket.getReceiveBufferSize()];
			byte[] sBuffer = new byte[size];
			DatagramPacket send;
			DatagramPacket ack = new DatagramPacket(rBuffer, rBuffer.length);
			System.out.println("Attempting to get file with file name: " + name);
			byte[] file = Files.readAllBytes(Paths.get(name));
			System.out.println("File received");
			fSize = file.length;
			long fSize1 = fSize;
			int packNum = (int) Math.ceil(fSize/(size - headerSize));
			for (int i = 0; i <= packNum; i++)
			{
				int datalen = 0;
				//ByteBuffer bytes = new ByteBuffer(0, 0, size, (int) sCons);
				//bytes.putInt(0, i);
				sBuffer[0] = (byte) ((i >> 24) & 0xFF); // First, place header into buffer
				sBuffer[1] = (byte) (((i << 8) >> 24) & 0xFF);
				sBuffer[2] = (byte) (((i << 16) >> 24) & 0xFF);
				sBuffer[3] = (byte) (((i << 24) >> 24) & 0xFF);
				if (i != packNum)
				{
					datalen = size - 8;
				}
				else
				{
					datalen = (int) (fSize - (packNum*(size-8)));
				}
				sBuffer[4] = (byte) ((datalen >> 24) & 0xFF); // First, place header into buffer
				sBuffer[5] = (byte) (((datalen << 8) >> 24) & 0xFF);
				sBuffer[6] = (byte) (((datalen << 16) >> 24) & 0xFF);
				sBuffer[7] = (byte) ((datalen  >> 24) & 0xFF);
				int byteNum = size - 8;
				if (i == packNum)
				{
					byteNum = file.length%(size-8);
				}
				for(int j = 8, k = 0; j < byteNum; j++, k++)
				{
					sBuffer[j] = file[k];
				}
				send = new DatagramPacket(sBuffer, sBuffer.length, getReceiver());
				System.out.println("Attempting to add datagram packet");
				packs.add(send);
			}
			int packIterator = 0; // Where you are in the packet list
			int i = 0;
			if(gMode == 0) // Stop and wait
			{
				maxFrames = 1;
			}
			else if (gMode == 1) // Sliding window, might not even need this if statement
			{ // Use fileinputstream
				
			}
			else
			{
				System.out.println("Error detected! Unrecognized mode! " + gMode);
				socket.close();
				System.exit(-1);
			}
			long[][] timeOut = new long[(int) maxFrames][2]; // First value is time, 2nd is packet position
			int j = 0;
			
			System.out.println("Attempting to connect");
			socket.connect(getReceiver());
			while(i <= maxFrames) // While there can be more frames sent
			{
				if(timeOut[(int) (j%maxFrames)][0] >= System.currentTimeMillis())
				{
					try {
						System.out.println("Sender attempting to send")
						socket.setSoTimeout((int)time);
						socket.receive(ack);
						rBuffer = ack.getData();
						int rSeqNum = rBuffer[0] + rBuffer[1] + rBuffer[2] + rBuffer[3];
						rSeqNum -= 2*rSeqNum;
						i--;
						System.out.println("Receiver acknowledges package of sequence number: " + rSeqNum);
					} catch (IOException e) { // Math behind the j's might be wrong
						System.out.println("Sender attempting to send")
						socket.send(packs.get((int) timeOut[(int) (++j%maxFrames)][1]));
						System.out.println("Error with sending message, resending now.");
						System.out.println("Sender sent message with a sequence number of " + packIterator + " and a number of bytes of " + packs.get(packIterator).getLength());
						timeOut[j%maxFrames][0] = System.currentTimeMillis() + time;
						timeOut[(j+1)%maxFrames][1] = timeOut[(j-1)%maxFrames][1];
					}
				}
				else if (packIterator < packs.size()) // As long as there are more packets
				{
					if (i < maxFrames) 
					{
						System.out.println("Sender attempting to send");
						socket.send(packs.get(packIterator));
						System.out.println("Sender sent message with a sequence number of " + packIterator + " and a number of bytes of " + packs.get(packIterator).getLength());
						timeOut[(int) (++j%maxFrames)][0] = System.currentTimeMillis() + time;
						timeOut[(int) (j%maxFrames)][1] = packIterator;
						i++;
					}
					/*else if (i >= sCons)
					{
						socket.setSoTimeout((int)time);
						socket.receive(ack);
						rBuffer = ack.getData();
						System.out.println("Receiver acknowledges package of sequence number: " + (int) rBuffer[0] + (int) rBuffer[1] + (int) rBuffer[2] + (int) rBuffer[3]);
					}*/
				}
			}
		} catch(IOException e) {
			System.out.println("Error detected! Issue with connecting to socket, please try again.");
			System.exit(-1);
		}
		socket.close();
		return false;
	}

}
