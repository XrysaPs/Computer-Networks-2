import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.System;
import java.util.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SocketProgramming2 {
	final int serverPort = 38033;
	final int clientPort = 48033;

	public void echo() {
		try {
			File responseFile = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\ECHO\\ResponseTime_no.txt");
			FileOutputStream out = new FileOutputStream(responseFile);
			Writer w = new OutputStreamWriter(out);
			
			File times = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\ECHO\\Times_no.txt");
			FileOutputStream out3 = new FileOutputStream(times);
			Writer w3 = new OutputStreamWriter(out3);
			
			List<Integer> l = new ArrayList<Integer>();
			
			String packetInfo = "E0000\r";

			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = packetInfo.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(5000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			long sendTime = 0;
			int responseTime = 0;
			int lostPackets = 0;
			
			do{
				s.send(p);
				sendTime = System.currentTimeMillis();

				try {
					r.receive(q);
					endTime = System.currentTimeMillis();
					responseTime = (int)(endTime - sendTime);
					w.write(responseTime + "\n");
					l.add((int)endTime);
				
					System.out.println("Response time = " + responseTime);
					System.out.println("Duration = " + (endTime - startTime));
					String message = new String(rxbuffer,0,q.getLength());
					w3.write(message, 18, 8);
					w3.write("\n");
					System.out.println(message);
					System.out.println();			 
				} catch (Exception x) {
					System.out.println(x);
					lostPackets +=1;
					System.out.println("Lost packets : " + lostPackets);
				}
			}while(endTime - startTime <= 240000);
				
			System.out.println("Size of list: " + l.size());
			throughput(l, (int) startTime, (int) endTime);

			w.close();
			w3.close();
			r.close();
			s.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void throughput(List<Integer> l, int startTime, int endTime) {
		try {
			File throughput = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\ECHO\\Throughput_no.txt");
			FileOutputStream out2 = new FileOutputStream(throughput);
			Writer w2 = new OutputStreamWriter(out2);
			
			int t=0;
			int num = 0;
			int seconds = 8;
			
			while(startTime+t+(seconds*1000)<=endTime) {
				for(int k=0; k<l.size(); k++){
					if (l.get(k)>= startTime+t && l.get(k) <= startTime+t+(seconds*1000)) {
						num++;
					}
				}
				w2.write((double)num/seconds + "\n");
				num=0;
				System.out.println("t="+t+" done!");
				t+=1000;
			}
			w2.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void temperature() {
		try {
			String packetInfo = "E5267T00\r";
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = packetInfo.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(5000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			s.send(p);
			r.receive(q);
			String message = new String (rxbuffer, 0, q.getLength());
			System.out.println(message);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void image() {
		String imageRequest = "M6170CAM=FIX\r";
		
		try {
			JFrame f = new JFrame();
			f.setSize(1000, 600);
			JLabel l = null;
			
			BufferedImage bufferedImage=null;
			ByteArrayInputStream in=null;
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = imageRequest.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			s.send(p);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(10000);
			byte[] rxbuffer = new byte[128];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			try {
				for(;;) {
					r.receive(q);
					System.out.println("Started");
					byteArray.write(rxbuffer);
					System.out.println(byteArray.size());
					if(q.getData()[q.getLength()-1]==-39 && q.getData()[q.getLength()-2]==-1) {
						System.out.println("Ended");
						break;
					}
				}
				
			}catch(Exception e) {
				System.out.println(e);
			}
			
			byte[] c = byteArray.toByteArray();
			in = new ByteArrayInputStream(c);
			bufferedImage = ImageIO.read(in);
			ImageIcon icon = new ImageIcon(bufferedImage);
			l=new JLabel(icon);
			f.add(l);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
			//saving the image in folder
			ImageIO.write(bufferedImage, "jpg", new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\IMAGE\\image_FIX.jpg"));

		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void video() {
		String imageRequest = "M2973CAM=PTZUDP=1024\r";
		
		try {
			JFrame f = new JFrame("Video");
			f.setSize(1000, 600);
			JPanel panel = new JPanel(new BorderLayout());
			JLabel l = null;
			
			BufferedImage bufferedImage=null;
			ByteArrayInputStream in=null;
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ImageIcon icon = null;
			
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = imageRequest.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(10000);
			byte[] rxbuffer = new byte[1024];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			for(;;) {
				s.send(p);
			try {
				for(;;) {
					r.receive(q);
					System.out.println("Started");
					byteArray.write(rxbuffer);
					System.out.println(byteArray.size());
					if(q.getData()[q.getLength()-1]==-39 && q.getData()[q.getLength()-2]==-1) {
						System.out.println("Ended");
						break;
					}
				}
				
			}catch(Exception e) {
				System.out.println(e);
			}
			
			byte[] c = byteArray.toByteArray();
			in = new ByteArrayInputStream(c);
			bufferedImage = ImageIO.read(in);
			icon = new ImageIcon(bufferedImage);
			l=new JLabel(icon);
			panel.removeAll();
			panel.add(l, BorderLayout.CENTER);
			f.add(panel);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
			byteArray.reset();
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void audio() {
		String audioRequest = "A8259AQL13F999";
		
		try {			
			File samples = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\SAMPLES.txt");
			FileOutputStream out = new FileOutputStream(samples);
			Writer w = new OutputStreamWriter(out);
			
			File difference = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\DIFFERENCES.txt");
			FileOutputStream out2 = new FileOutputStream(difference);
			Writer w2 = new OutputStreamWriter(out2);
			
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = audioRequest.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(5000);
			byte[] rxbuffer = new byte[128];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			System.out.println("Loading...");
			
			s.send(p);
			for(int k=0; k<999; k++) {
			try {
				r.receive(q);
				
				byte[] b = new byte[rxbuffer.length * 2];
				byte[] d = new byte[b.length];
				for (int u=0; u<rxbuffer.length; u++) {
					b[2*u] = (byte)(((rxbuffer[u] >>> 4) & 0xF) - 8);
					b[2*u+1] = (byte)((rxbuffer[u] & 0xF)-8);
					w2.write(b[2*u] + "\n");
					w2.write(b[2*u+1] + "\n");
				}
				d[0] = (byte)b[0];
				w.write(d[0] + "\n");
				for(int n=1; n<b.length; n++) {
					d[n] = (byte)(d[n-1] + b[n]);
					w.write(d[n] + "\n");
				}
				baos.write(d);
			}catch(Exception e) {
				System.out.println(e);
			}
			}
			
			AudioFormat linearPCM = new AudioFormat(4000, 16, 1, true, false);
			SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
			lineOut.open(linearPCM, 32000);
			lineOut.start();
			byte[] audioBufferOut = new byte[baos.toByteArray().length];
			audioBufferOut = baos.toByteArray();
			System.out.println("Audio is here.");
			lineOut.write(audioBufferOut, 0, audioBufferOut.length);
			lineOut.stop();
			System.out.println("Audio ended.");
			File cutaudioFile = new File ("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\FREQ.wav");
			InputStream in=new ByteArrayInputStream(audioBufferOut);
            AudioInputStream ais = new AudioInputStream(in, linearPCM, audioBufferOut.length);
            AudioSystem.write(ais, Type.WAVE, cutaudioFile);
			lineOut.close();
			w.close();
			w2.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void audioAQ() {
		String audioRequest = "A8971AQL08F999";
		
		try {
			File samples = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\1.txt");
			FileOutputStream out = new FileOutputStream(samples);
			Writer w = new OutputStreamWriter(out);
			
			File difference = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\2.txt");
			FileOutputStream out2 = new FileOutputStream(difference);
			Writer w2 = new OutputStreamWriter(out2);
			
			File time = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\3.txt");
			FileOutputStream out3 = new FileOutputStream(time);
			Writer w3 = new OutputStreamWriter(out3);
			
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = audioRequest.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(5000);
			byte[] rxbuffer = new byte[132];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			byte[] audioBufferOut = new byte[999*4*128];
			System.out.println("Loading...");
			int deigma1=0, deigma2=0;
			s.send(p);
			for(int k=0; k<999; k++) {
			try {
				r.receive(q);
				w3.write(System.currentTimeMillis() + "\n");
				int m = (rxbuffer[1]<<8) | (rxbuffer[0] & 0xFF);
				int step = (rxbuffer[3]<<8) | (rxbuffer[2] & 0xFF);
				//w.write(m + "\n");
				//w2.write(step + "\n");
				for(int t=4; t<132; t++) {
					int nibble1 = (byte) ((rxbuffer[t]>>4) & 0xF);
					int nibble2 = (byte) (rxbuffer[t] & 0xF);
					
					int dif1 = (nibble1-8)*step + m;
					int dif2 = (nibble2-8)*step + m;
					w2.write(dif1 + "\n");
					w2.write(dif2 + "\n");
					deigma1 = deigma2+dif1;
					w.write(deigma1 + "\n");
					deigma2 = deigma1+dif2;
					w.write(deigma2 + "\n");
					audioBufferOut[4*(t-4)+k*4*128] = (byte) (deigma1 & 0xFF);
					audioBufferOut[4*(t-4)+1+k*4*128] = (byte) ((deigma1>>8) & 0xFF);
					audioBufferOut[4*(t-4)+2+k*4*128] = (byte) (deigma2 & 0xFF);
					audioBufferOut[4*(t-4)+3+k*4*128] = (byte) ((deigma2>>8) & 0xFF);
				}
			}catch(Exception e) {
				System.out.println(e);
			}
			}
			w.close();
			w2.close();
			AudioFormat linearPCM = new AudioFormat(8000, 16, 1, true, false);
			SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
			lineOut.open(linearPCM, 32000);
			lineOut.start();
			System.out.println("Audio is ready to play.");
			lineOut.write(audioBufferOut, 0, audioBufferOut.length);
			lineOut.stop();
			File cutaudioFile = new File ("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\SOUND_AQDPCM\\4.wav");
			InputStream in=new ByteArrayInputStream(audioBufferOut);
            AudioInputStream ais = new AudioInputStream(in, linearPCM, audioBufferOut.length);
            AudioSystem.write(ais, Type.WAVE, cutaudioFile);
			System.out.println("Audio ended.");
			lineOut.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void ithakiCopter() {		
		try {
			File lmotor = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\COPTER\\LMOTOR2.txt");
			FileOutputStream out = new FileOutputStream(lmotor);
			Writer w = new OutputStreamWriter(out);
			File rmotor = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\COPTER\\RMOTOR2.txt");
			FileOutputStream out2 = new FileOutputStream(rmotor);
			Writer w2 = new OutputStreamWriter(out2);
			File altitude = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\COPTER\\ALTITUDE2.txt");
			FileOutputStream out3 = new FileOutputStream(altitude);
			Writer w3 = new OutputStreamWriter(out3);
			File timeCopter = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\COPTER\\Time_copter2.txt");
			FileOutputStream out4 = new FileOutputStream(timeCopter);
			Writer w4 = new OutputStreamWriter(out4);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(5000);
			byte[] rxbuffer = new byte[132];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			
			long startTime = System.currentTimeMillis();
			long duration=0;
			do{
				r.receive(q);
				long currentTime = System.currentTimeMillis();
				duration = currentTime-startTime;
				String message = new String (rxbuffer, 0, rxbuffer.length);
				System.out.println(message);
				w.write(message, 40, 3);
				w.write("\n");
				w2.write(message, 51, 3);
				w2.write("\n");
				w3.write(message, 64, 3);
				w3.write("\n");
				w4.write(message, 24, 8);
				w4.write("\n");
				System.out.println(duration);
			}while(duration<=180000);
			w.close();
			w2.close();
			w3.close();
			w4.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void obd() {
		String request = "V0915OBD=01 0C";

		try {

			File obd = new File("C:\\Users\\user\\Desktop\\Εργασία Δίκτυα_2\\ΔΕΥΤΕΡΗ_ΣΥΝΟΔΟΣ\\obd\\rpm.txt");
			FileOutputStream out2 = new FileOutputStream(obd);
			Writer w2 = new OutputStreamWriter(out2);
			
			DatagramSocket s = new DatagramSocket();
			byte[] txbuffer = request.getBytes();
			byte[] hostIP = { (byte)155,(byte)207,18,(byte)208 };
			InetAddress hostAddress = InetAddress.getByAddress(hostIP);
			DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			
			DatagramSocket r = new DatagramSocket(clientPort);
			r.setSoTimeout(7000);
			byte[] rxbuffer = new byte[2048];
			DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
			long duration = 0;
			long startTime = System.currentTimeMillis();
			do{
				s.send(p);
				try {
					r.receive(q);
					long currentTime = System.currentTimeMillis();
					duration = currentTime - startTime;
					String message = new String(rxbuffer, 0, rxbuffer.length);
					System.out.println(message);
					String xx = new String (rxbuffer, 6, 2);
					System.out.println("XX = " + xx);
					w2.write(rpm(xx, rxbuffer)+"\n");
				}catch(Exception e) {
					System.out.println(e);
				}
			}while(duration <= 240000);
			w2.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public int engRunTime (String xx, byte[] rxbuffer) {		
		String yy = new String (rxbuffer, 9, 2);
		System.out.println("YY = " + yy);
		System.out.println(Integer.parseInt(xx,16));
		System.out.println(Integer.parseInt(yy,16));
		int engRunTime =  256 * Integer.parseInt(xx,16) + Integer.parseInt(yy,16);
		System.out.println(engRunTime);
		return engRunTime;
	}
	
	public int temp (String xx, byte[] rxbuffer) {		
		System.out.println(Integer.parseInt(xx,16));
		int temp =  Integer.parseInt(xx,16) - 40;
		System.out.println(temp);
		return temp;
	}
	
	public int throttlePos (String xx, byte[] rxbuffer) {
		System.out.println(Integer.parseInt(xx,16));
		int throttlePos =  Integer.parseInt(xx,16) * 100 / 255;
		System.out.println(throttlePos);
		return throttlePos;
	}
	
	public int rpm (String xx, byte[] rxbuffer) {
		String yy = new String (rxbuffer, 9, 2);
		System.out.println("YY = " + yy);
		System.out.println(Integer.parseInt(xx,16));
		System.out.println(Integer.parseInt(yy,16));
		int rpm = ((Integer.parseInt(xx,16) * 256) + Integer.parseInt(yy,16)) / 4;
		System.out.println(rpm);
		return rpm;
	}
	
	public int vehSpeed (String xx, byte[] rxbuffer) {
		System.out.println(Integer.parseInt(xx,16));
		int vehSpeed =  Integer.parseInt(xx,16);
		System.out.println(vehSpeed);
		return vehSpeed;
	}
	
	public static void main(String[] args) {
		SocketProgramming2 VirtualModem=new SocketProgramming2();
		
		//VirtualModem.echo();
		//VirtualModem.image();
		//VirtualModem.temperature();
		VirtualModem.video();
		//VirtualModem.audio();
		//VirtualModem.audioAQ();
		//VirtualModem.ithakiCopter();
		//VirtualModem.obd();
	}
}
