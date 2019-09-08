package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Driver{

	static int clientCount = 0;
	static boolean running = true;
	
	public static void main(String[] args) throws IOException {
		
		//DataBase clientDB = DataBase.createDB(new File("ClientData.ser"));
		
		
		System.out.println("Main Thread " + Thread.currentThread());
		
		//Create Threadpool which will handle all async messaging tasks
		int numberOfThreads = 10;
		ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(pool);
		
				
		System.out.println("This is the async server");
		AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
		server.bind(new InetSocketAddress(4040));
	

		//This next part is essentially blocking, but I need it this way to 
		//create an asycnsocketchannel
		try {
			while(true){
			Future<AsynchronousSocketChannel> client = server.accept();
			AsynchronousSocketChannel clientSocket = client.get();
			AppHandler clientHandler = new AppHandler(clientSocket);
			pool.submit(clientHandler);
			System.out.println("Client Accepted: " + ++clientCount);
			}
		} catch (InterruptedException  | ExecutionException e) {
			e.printStackTrace();
		} 
		
	}


}
