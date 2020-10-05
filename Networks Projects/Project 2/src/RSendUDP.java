import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.utulsa.unet.UDPSocket;
import edu.utulsa.unet.RSendUDPI;

class RSendUDP implements RSendUDPI{

    private int gMode;
    private long sCons = 256;
    private long fSize = 0;
    private long time = 100;
    private String name;
    private int lPort = 0;
    private final int defPort = 12987;
    private InetSocketAddress def = new InetSocketAddress(1);
    private UDPSocket socket;
	private List<DatagramPacket> packs = new ArrayList<DatagramPacket>();
	private InetSocketAddress receiverIP;
    private final int headerSize = 12;

    public static void main(String[] args)
    {
		InetSocketAddress r = new InetSocketAddress("127.0.0.1", 3345);
		RSendUDP rSend = new RSendUDP();
		rSend.setFilename("text.txt");
		rSend.setMode(0);
		rSend.setLocalPort(3344);
		rSend.setTimeout(500);
		rSend.setModeParameter(1500);
		rSend.setReceiver(r);
		rSend.sendFile();
    }

	/*
		When you first send, send through any available port to get to the receiver. The receiver uses the information it gains from the src location to determine
		where to send its ack.
	*/

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
			lPort = defPort;
		}
		// if(receiverIP.equals(def))
		// {
		// 	try {
		// 		receiverIP = new InetSocketAddress(InetAddress.getLocalHost(), lPort);
		// 	} catch (UnknownHostException e) {
		// 		// TODO Auto-generated catch block
		// 		System.out.println("Issue receiving IP from socket");
		// 	}
		// }
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
			byte[] rBuffer = new byte[size];
			byte[] sBuffer;
			DatagramPacket send;
			DatagramPacket ack = new DatagramPacket(rBuffer, rBuffer.length);
			System.out.println("Attempting to get file with file name: " + name);
			byte[] file = Files.readAllBytes(Paths.get(name));
			System.out.println("File received");
			fSize = file.length;
			int packNum = (int) Math.ceil((double) fSize/((double) size - (double) headerSize));
			ByteBuffer buf = ByteBuffer.allocate(4);
			int fIter = 0;
			for (int i = 0; i < packNum; i++)
			{
				sBuffer = new byte[size];
				int datalen = 0;
				sBuffer[0] = (byte) ((i >> 24)); // First, place header into buffer
				sBuffer[1] = (byte) ((i >> 16));
				sBuffer[2] = (byte) ((i >> 8));
				sBuffer[3] = (byte) i;
				if (i+1 < packNum)
				{
					datalen = size - 12;
				}
				else
				{
					datalen = (int) (file.length - ((packNum-1)*(size-12)));
				}
				sBuffer[4] = (byte) ((datalen >> 24)); // First, place header into buffer
				sBuffer[5] = (byte) ((datalen >> 16));
				sBuffer[6] = (byte) ((datalen >> 8));
				sBuffer[7] = (byte) ((datalen));
				sBuffer[8] = (byte) (packNum >> 24);// Total packets
				sBuffer[9] = (byte) (packNum >> 16);
				sBuffer[10] = (byte) ((packNum >> 8));
				sBuffer[11] = (byte) ((packNum));
				for(int j = 12; j < datalen + 12; j++, fIter++)
				{
					sBuffer[j] = file[fIter];
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
			{
				maxFrames = (int) sCons/size;
				if(sCons%size != 0)
					maxFrames++;
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
			boolean finished = false;
			List<Integer> complete = new ArrayList<Integer>(0);
			int iterations = 0;
			while(i <= maxFrames && !finished) // While there can be more frames sent
			{
				if (complete.size() == packs.size())
				{
					System.out.println("Received confirmation that receiver has received all intended messages. Program now exiting.");
					finished = true;
					break;
				}
				else if(timeOut[(int) (j%maxFrames)][0] <= System.currentTimeMillis()
					 && timeOut[(int) (j%maxFrames)][0] != -1)
				{
					try {
						socket.setSoTimeout((int)time);
						socket.receive(ack);
						rBuffer = ack.getData();
						int rSeqNum = (int) ((rBuffer[0] & 0xFF) * Math.pow(2, 24) + (rBuffer[1] & 0xFF) * Math.pow(2, 16)
								+ (rBuffer[2] & 0xFF) * Math.pow(2, 8) + (rBuffer[3] & 0xFF));
						for(int k = 0; k < timeOut.length; k++)
						{
							if (rSeqNum == (int)timeOut[k][1])
							{
								timeOut[k][0] = -1;
							}
						}
						j++;
						if (!complete.contains(rSeqNum))
							complete.add(rSeqNum);
						i--;
						System.out.println("Receiver acknowledges package of sequence number: " + rSeqNum);
					} catch (IOException e) {
						if(!complete.contains((int) timeOut[j%maxFrames][1]))
						{
							System.out.println("Recipient took too long to respond, resending.");
							System.out.println("Sender sent message with a sequence number of " + timeOut[j%maxFrames][1] + " and a number of bytes of " + packs.get((int)timeOut[j%maxFrames][1]).getLength());
							socket.send(packs.get((int) timeOut[(int) (j%maxFrames)][1]));
							timeOut[j%maxFrames][0] = System.currentTimeMillis() + time;
						}
						else
						{
							timeOut[j%maxFrames][0] = -1;
						}
					}
					j++;
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
						packIterator++;
						i++;
					}
				}
				if(timeOut[j%maxFrames][0] == -1)
				{
					j++;
				}
			}
		} catch(IOException e) {
			System.out.println("Error detected! Issue with connecting to socket, please try again.");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		return false;
	}
}