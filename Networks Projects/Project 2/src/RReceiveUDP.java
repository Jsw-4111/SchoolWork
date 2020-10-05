import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;

public class RReceiveUDP implements RReceiveUDPI{
	
	public int gMode; // 0 for S&W, 1 for sliding window
	public long sCons;
	public String Name;
	public int lPort = 12987;
	public List<byte[]> packs = new ArrayList<byte[]>();
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
			int packSize = bufSize - 12;
			byte[] rBuffer = new byte[socket.getSendBufferSize()];
			DatagramPacket receive = new DatagramPacket(rBuffer, rBuffer.length);
			List<Integer> collected = new ArrayList<Integer>(0);
			try {
				boolean hasNext = true;
				boolean timeStarted = false;
				int packNum = 0;
				while(hasNext) {
					if(!timeStarted)
					{
						startTime = System.currentTimeMillis();
						timeStarted = true;
					}
					System.out.println("Processed " + collected.size() + " out of " + packNum + " packets so far.");
					socket.receive(receive);
					System.out.println("Successful connection to IP address " + receive.getAddress() + " and port "
							+ receive.getPort());
					senderIP = receive.getAddress();
					sendPort = receive.getPort();
					ByteBuffer rtemp = ByteBuffer.allocate(socket.getSendBufferSize());
					rtemp.put(receive.getData());
					int seqNum = (int) ((rtemp.array()[0] & 0xFF) * Math.pow(2, 24) + (rtemp.array()[1] & 0xFF) * Math.pow(2, 16)
						+ (rtemp.array()[2] & 0xFF) * Math.pow(2, 8) + (rtemp.array()[3] & 0xFF));
					int datalen = (int) ((rtemp.array()[4] & 0xFF) * Math.pow(2, 24) + (rtemp.array()[5] & 0xFF) * Math.pow(2, 16)
						+ (rtemp.array()[6] & 0xFF) * Math.pow(2, 8) + (rtemp.array()[7] & 0xFF));
					packNum = (int) ((rtemp.array()[8] & 0xFF) * Math.pow(2, 24) + (rtemp.array()[9] & 0xFF) * Math.pow(2, 16)
						+ (rtemp.array()[10] & 0xFF) * Math.pow(2, 8) + (rtemp.array()[11] & 0xFF));
					System.out.println("Received message of sequence number " + seqNum + " with " + datalen + " bytes of data");
					if(collected.isEmpty() || !collected.contains(seqNum))
					{
						collected.add(seqNum);
						packs.add(rtemp.array());
					}
					if(packs.size() >= packNum)
						hasNext = false;
					byte[] ACK = new byte[4];
					ACK[0] = (byte) (seqNum >> 24); // First, place header into buffer
					ACK[1] = (byte) (seqNum >> 16);
					ACK[2] = (byte) ((seqNum >> 8));
					ACK[3] = (byte) (seqNum);
					DatagramPacket send;
					socket.send(send = new DatagramPacket(ACK, ACK.length, senderIP, sendPort)); // Need sender's port and IP
				}
			} catch(IOException e) {
				System.out.println("Connection to host was unsuccessful, please try again\n" + e.getMessage());
			}
			System.out.println("Packets processed, starting file creation");
			List<ByteBuffer> temp = new ArrayList<ByteBuffer>(packs.size());
			for(int i = 0; i < packs.size(); i++)
			{
				temp.add(ByteBuffer.allocate(0));
			}
			for(int i = 0; i < packs.size(); i++)
			{
				
				int sequence = (int) ((packs.get(i)[0] & 0xFF)*Math.pow(2, 24) + (packs.get(i)[1] & 0xFF)*Math.pow(2, 16) + 
					(packs.get(i)[2] & 0xFF)*Math.pow(2, 8) + (packs.get(i)[3] & 0xFF));
				int pSize = (int) ((packs.get(i)[4] & 0xFF)*Math.pow(2, 24) + (packs.get(i)[5] & 0xFF)*Math.pow(2, 16) + 
					(packs.get(i)[6] & 0xFF)*Math.pow(2, 8) + (packs.get(i)[7] & 0xFF));
				System.out.println("Packet size of " + pSize + " and sequence number of " + sequence);
				temp.set(sequence, ByteBuffer.allocate(pSize));
				temp.set(sequence, temp.get(sequence).put(packs.get(i), 12, pSize));
			}
			FileOutputStream create = new FileOutputStream(Name);
			for (ByteBuffer buf : temp)
			{
				create.write(buf.array());
			}
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
