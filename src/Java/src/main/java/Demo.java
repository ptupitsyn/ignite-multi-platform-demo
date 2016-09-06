import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryBasicIdMapper;
import org.apache.ignite.binary.BinaryBasicNameMapper;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        // Retrieve user name
        System.out.print("Hi, enter your name: ");
        Scanner consoleScanner = new Scanner(System.in);
        String name = consoleScanner.nextLine();

        // Configure Ignite to connect with .NET nodes
        BinaryConfiguration binCfg = new BinaryConfiguration();

        binCfg.setIdMapper(new BinaryBasicIdMapper());
        binCfg.setNameMapper(new BinaryBasicNameMapper());

        IgniteConfiguration cfg = new IgniteConfiguration().setBinaryConfiguration(binCfg);

        // Start Ignite and retrieve cache
        Ignite ignite = Ignition.start(cfg);
        IgniteCache<Long, Message> cache = ignite.getOrCreateCache("chat");

        // Initialize unique ID sequence
        IgniteAtomicSequence messageId = ignite.atomicSequence("chatId", 0, true);

        // Set up continuous query
        ContinuousQuery<Long, Message> qry = new ContinuousQuery<Long, Message>();

        qry.setLocalListener(new CacheEntryUpdatedListener<Long, Message>() {
            public void onUpdated(
                Iterable<CacheEntryEvent<? extends Long, ? extends Message>> iterable) throws CacheEntryListenerException {
                // This will be invoked immediately on each cache update
                for (CacheEntryEvent<? extends Long, ? extends Message> evt : iterable)
                    System.out.println(evt.getValue().author + ": " + evt.getValue().text);
            }
        });

        cache.query(qry);

        // Run the chat loop
        while (true) {
            System.out.print("> ");

            String msgText = consoleScanner.nextLine();
            Long msgId = messageId.incrementAndGet();

            cache.put(msgId, new Message(name, msgText));
        }
    }
}