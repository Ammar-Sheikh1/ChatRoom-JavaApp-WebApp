package project7;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: UNFINISHED, need to add support for multiple client protocol upgrading
 * (HTTP -> WS ).Also need to remove tester code
 * 		
 * @author Ammar
 *
 */

public class WebSocketServerMain implements Runnable{

	@Override
	public void run(){
		ServerSocket server = null;
		try {
			server = new ServerSocket(8080);

			System.out.println("Web App Server has started on 127.0.0.1:8080.\r\nWaiting for a connection...");
			Socket client = server.accept();
			System.out.println("A client connected.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			Scanner s = new Scanner(in, "UTF-8");
			String data = s.useDelimiter("\\r\\n\\r\\n").next();
			Matcher get = Pattern.compile("^GET").matcher(data);
			if (get.find()) {
				Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
				match.find();
				byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
						+ "Connection: Upgrade\r\n"
						+ "Upgrade: websocket\r\n"
						+ "Sec-WebSocket-Accept: "
						+ Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
						+ "\r\n\r\n").getBytes("UTF-8");
				out.write(response, 0, response.length);
				byte[] decoded = new byte[6];
				byte[] encoded = new byte[] { (byte) 235};

				//byte[] encoded = new byte[] { (byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135 };
				//byte[] key = new byte[] { (byte) 167, (byte) 225, (byte) 225, (byte) 210 };
				byte[] key = new byte[] { (byte) 163, (byte) 214, (byte) 60, (byte) 53 };

				for (int i = 0; i < encoded.length; i++) {
					decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
				}
				String message = new String(decoded);
				System.out.println(message);
			}
			byte[] buffer = new byte[] {(byte) 129,(byte)5, 'A' , 'm' , 'm', 'a' , 'r'};

			byte[] hold = new byte[64];
			//out.write(buffer);
			
			//
			JSONObject obj= new JSONObject();
		
			try {
				obj.put("Type","verify");
				obj.put("Verify", "false");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			
			while(true) {
				int incoming = in.read(hold);
				String message = WebSocketDecoder.decodeClient(hold,incoming);
				//byte[] buf = WebSocketEncoder.encodeString(message);
				byte[] buf = WebSocketEncoder.encodeString(obj.toString());
				out.write(buf);
				System.out.println();
				System.out.println(incoming);
			}
			
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		//server.close
	}
}
