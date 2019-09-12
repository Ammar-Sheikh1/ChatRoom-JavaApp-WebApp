package server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Driver{

	private static int clientCount = 0;

	public static void main(String[] args) throws IOException {
		
		//TODO: Reintegrate WebApp/WebSockets into Async design

		//Create and init "DataBase", which reads deserializes/serilizes info from given file
		DataBase.createDB(new File("ClientData.ser"));

		//Create Threadpool which will handle all async messaging tasks
		int numberOfThreads = 10;
		ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(pool);

		AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
		server.bind(new InetSocketAddress(4040));
		System.out.println("Java App server running...");

		//This next part is essentially blocking, but its a simple way to ensure a
		//I get an AsyncSocketChannel asap.
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
