import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;

import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;

public class RReceiveUDP implements RReceiveUDPI{
	
	public int gMode; // 0 for S&W, 1 for sliding window
	public long sCons;
	public String Name;
	public int lPort = 12987;
	public HashMap<Integer, byte[]> packs = new HashMap<Integer, byte[]>();
	public InetAddress senderIP;
	public int sendPort;
	public int bufSize = 0;
	/*
	 * How do I know what IP I want to use? ARQ? IP?
	 */
	public static void main(String[] args)
	{
		RReceiveUDP rRec = new RReceiveUDP();
		rRec.setFilename("text2.txt");
		rRec.setMode(0);
		rRec.setLocalPort(3345);
		rRec.receiveFile();
	}

	@Override
	public boolean setMode(int mode) {
		// TODO Auto-generated method stub
		if (mode > 1 || mode < 0)
		{
			System.out.println("Error detected! Mode values range from 0 to 1, please try again.");
			System.exit(-1);
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
	public boolean setModeParameter(long n) {
		// TODO Auto-generated method stub
		if(gMode == 1)
		{
			sCons = n;
		}
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
		Name = fname;
	}

	@Override
	public String getFilename() {
		// TODO Auto-generated method stub
		return Name;
	}

	@Override
	public boolean setLocalPort(int port) {
		// TODO Auto-generated method stub
		lPort = port;
		return false;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return lPort;
	}

	@Override
	/*
		The instructions say to send the sender's information through the socket,
		but how do I receive it through the receiver's side? I have to be able to
		keep the socket up indefinitely and on the right port/IP before I even start
		my sender class right?
	*/
	public boolean receiveFile() {
		// This might be a try-catch clause.
		if(lPort == 0)
		{
			lPort = 12987;
		}
		try {
			System.out.println("Expecting connection with host from an IP of " + InetAddress.getLocalHost() + 
					" on port number " + lPort + " using the " + gMode + " file transfer method.");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("Issue receiving local IP, please try again.");
		}
		long startTime = System.currentTimeMillis();
		try {
			UDPSocket socket = new UDPSocket(lPort);
			bufSize = socket.getSendBufferSize();
			byte[] rBuffer = new byte[socket.getSendBufferSize()];
			byte[] ackBuffer = new byte[socket.getSendBufferSize()];
			DatagramPacket receive = new DatagramPacket(rBuffer, rBuffer.length);
			try {
				boolean hasNext = true;
				startTime = System.currentTimeMillis();
				while(hasNext) {
					socket.receive(receive);
					System.out.println("Successful connection to IP address " + receive.getAddress() + " and port "
							+ receive.getPort());
					senderIP = receive.getAddress();
					sendPort = receive.getPort();
					byte[] temp = receive.getData();
					int seqNum = temp[0]*2^24 + temp[1]*2^16 + temp[2]*2^8 + temp[3];
					int datalen = temp[4]*2^24 + temp[5]*2^16 + temp[6]*2^8 + temp[7];
					int packNum = temp[8]*2^24 + temp[9]*2^16 + temp[10]*2^8 + temp[11];
					System.out.println("Received message of sequence number " + seqNum + " with " + temp.length + " bytes of data");
					packs.put(seqNum, temp);
					if(packs.size() >= packNum)
						hasNext = false;
					temp[0] = (byte) (seqNum >> 24); // First, place header into buffer
					temp[1] = (byte) (seqNum >> 16);
					temp[2] = (byte) ((seqNum >> 8));
					temp[3] = (byte) (seqNum);
					DatagramPacket send;
					socket.send(send = new DatagramPacket(temp, temp.length, senderIP, sendPort)); // Need sender's port and IP
				}
			} catch(IOException e) {
				System.out.println("Connection to host was unsuccessful, please try again\n" + e.getMessage());
			}

			ByteBuffer result = ByteBuffer.allocate(1);
			for(int i = 0; i < packs.size(); i++)
			{
				result.put(i*bufSize, packs.get(i));
			}
			FileWriter create = new FileWriter(Name);
			create.write(result.toString());
			create.close();
			long finishTime = System.currentTimeMillis();
			long totalTime = finishTime - startTime;
			System.out.println("File transfer complete, system spent " + totalTime + " milliseconds on transfer");
			socket.close();
		} catch (IOException e) {
			System.out.println("Issue connecting with socket, please try again.\n" + e.getMessage());
		}

		return false;
	}

}
