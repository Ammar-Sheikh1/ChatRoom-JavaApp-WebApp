package project7;

import org.json.JSONObject;

/*
 * TODO: Full websocket decoding unfinished for >64byte payloads
 * WebSocketDecoder class simply used to decode byte buffers from client
 * */
public class WebSocketDecoder {

	//Decode a client to server message
	//From byte buffer to to JSON Object
	
	
	//Reference: https://tools.ietf.org/html/rfc6455#section-5.2
	
	public static String decodeClient(byte[] encoded,int length) {
		//First byte should always be 129
		//Second byte refers to payload length
			//1. 126 ->
			//2. 127 ->
			//3. >128 -> byte - 128 = payload length
		
		//Masking key: 4 bytes (always present from client->server)
		//Payload data
		
		//Assuming not 126/127
	
		
		byte payloadLength = (byte) (encoded[1] - (byte)128);
		System.out.println("Payload length: " + payloadLength);
		//System.out.println(payloadLength);
		
		for(byte b : encoded) {
			System.out.println(b);
		}
		
		System.out.println();
		
		//Assuming Masking is set
		byte[] key = {(byte)encoded[2],(byte)encoded[3],(byte)encoded[4],(byte)encoded[5]};
		
		byte[] buffer = new byte[payloadLength];
		int index = 0;
		
		//copy data over to buffer
		for(int i=6;i<length;i++) {
			buffer[i-6] = encoded[i];
		}
		System.out.println("Data length:" + buffer.length);
		byte[] decoded = new byte[payloadLength];
		for(int i=0;i<buffer.length;i++) {
			decoded[i] = (byte) (buffer[i] ^ key[i & 0x3]);
		}
		
		StringBuilder s = new StringBuilder();
		System.out.println("string:");
		for(byte b : decoded) {
			char c = (char)b;
			System.out.println(b);
			s.append(c);
		}
		System.out.println();
		System.out.println(s);
		
		
		String m = s.toString();
		return m;
	}
	
	
}
