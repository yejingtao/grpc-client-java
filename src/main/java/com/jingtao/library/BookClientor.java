package com.jingtao.library;

import io.grpc.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookClientor {

    private static final Logger logger = Logger.getLogger(BookClientor.class.getName());

    private final BookServiceGrpc.BookServiceBlockingStub blockingStub;

    public BookClientor(Channel channel) {
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    public void say(String name) {
        logger.info("Will try to greet " + name + " ...");
        RequestData request = RequestData.newBuilder().setName(name).build();
        Book response;
        try{
            response = blockingStub.check(request);
        }catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Invoke say result is : " + response.getName()
                + " " + response.getAuther()
                + " " + response.getPrice());
    }

    public static void main(String[] args) throws Exception{
        String user = "les miserables";
        // Access a service running on the local machine on port 50051
        String target = "localhost:8088";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        try {
            BookClientor client = new BookClientor(channel);
            client.say(user);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
