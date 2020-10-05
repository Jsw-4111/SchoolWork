import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;

import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;
import java.net.DatagramPacket;

public class RReceiveUDP implements RReceiveUDPI{
	
	public int gMode; // 0 for S&W, 1 for sliding window
	public long sCons;
	public String Name;
	public int lPort = 12987;
	public HashMap<Integer, byte[]> packs;
	/*
	 * How do I know what IP I want to use? ARQ? IP?
	 */
	public static void main(String[] args)
	{
		RReceiveUDP rRec = new RReceiveUDP();
		rRec.setFilename("src\\text.txt");
		rRec.setMode(0);
		rRec.setLocalPort(3344);
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
		try {
			UDPSocket socket = new UDPSocket(lPort);
			byte[] rBuffer = new byte[socket.getReceiveBufferSize()];
			byte[] ackBuffer = new byte[socket.getSendBufferSize()];
			DatagramPacket receive = new DatagramPacket(rBuffer, rBuffer.length);
			while(socket.isConnected())
			{
				try {
					socket.receive(receive);
					System.out.println("Successful connection to IP address " + socket.getInetAddress() + " and port "
							+ socket.getPort());
					byte[] temp = receive.getData();
					int seqNum = temp[0]*2^24 + temp[1]*2^16 + temp[2]*2^8 + temp[3];
					int datalen = temp[4]*2^24 + temp[5]*2^16 + temp[6]*2^8 + temp[7];
					byte[] data = Arrays.copyOfRange(temp, 9, datalen + 8);
					System.out.println("Received message of sequence number " + seqNum + " with " + temp.length + " bytes of data");
					DatagramPacket packetData = new DatagramPacket(data, data.length);
					packs.put(seqNum, data);
					seqNum -= 2*seqNum;
					temp[0] = (byte) (seqNum / 2^24); // First, place header into buffer
					temp[1] = (byte) ((seqNum % 2^24)/2^16);
					temp[2] = (byte) ((seqNum % 2^16)/256);
					temp[3] = (byte) (seqNum % 256);
					DatagramPacket send;
					socket.send(send = new DatagramPacket(temp, temp.length));
				} catch(IOException e) {
					System.out.println("Connection to host was unsuccessful, please try again");
				}
			}
			
		} catch (IOException e) {
			System.out.println("Issue connecting with socket, please try again.");
		}
		return false;
	}

}
