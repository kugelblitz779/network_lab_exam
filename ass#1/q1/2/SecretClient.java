import java.net.*;
import java.io.*;

public class SecretClient {
	public static void main(String[] args) throws Exception {
		DatagramSocket cs = new DatagramSocket();
		cs.setSoTimeout(3000);

		try {
			byte[] rd, sd;
			String filename = "HELLO";
			String GREETING;
			String reply;
			DatagramPacket sp, rp;
			boolean end = false;
			int seq_no = 0;

			while (!end) {
				// send Greeting
				if (seq_no == 0)
					GREETING = "REQUEST" + filename + "\r\n";
				else
					GREETING = "ACK " + seq_no + " \r\n";
				sd = GREETING.getBytes();
				sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
				cs.send(sp);
				System.out.println("Sent Greetings");

				// get next consignment
				rd = new byte[512];
				rp = new DatagramPacket(rd, rd.length);
				try {
					cs.receive(rp);
					seq_no++;
					// print SECRET
					reply = new String(rp.getData());
					System.out.println(reply);
					if (reply.trim().contains("END")) // last consignment
					{
						end = true;
						seq_no = 0;
					}
				} catch (SocketTimeoutException ex) {
					System.out.println("timeout");
				}
			}
			cs.close();

		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Please pass in the server ip address and port number as argument");
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
