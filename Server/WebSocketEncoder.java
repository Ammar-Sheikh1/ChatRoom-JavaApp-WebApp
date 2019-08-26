package project7;

public class WebSocketEncoder {

	/**
	 * Encodes a String into a byte array under the WebSocket Protocol 
	 * @param String message
	 * @return
	 */
	public static byte[] encodeString(String message) {
		byte[] encoding = new byte[message.length() + 2];
		
		//First byte is 129 to represent opcode
		encoding[0] = (byte) 129;
		
		//Next byte is length
		encoding[1] = (byte) message.length();
		
		//Data
		for(int i=0;i<message.length();i++) {
			encoding[i+2] = (byte) message.charAt(i);
		}
		
		return encoding;
	}
}
