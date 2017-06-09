using System;
using System.Collections.Generic;
using Apache.Ignite.Core;
using Apache.Ignite.Core.Binary;
using Apache.Ignite.Core.Cache.Event;
using Apache.Ignite.Core.Cache.Query.Continuous;

class Program
{
    static void Main(string[] args)
    {
        // Retrieve user name
        Console.Write("Hi, enter your name: ");
        var name = Console.ReadLine();

        // Register Message type
        var cfg = new IgniteConfiguration
        {
            BinaryConfiguration = new BinaryConfiguration
            {
                NameMapper = new BinaryBasicNameMapper {IsSimpleName = true}
            }
        };

        // Start Ignite and retrieve cache
        var ignite = Ignition.Start(cfg);
        var cache = ignite.GetOrCreateCache<long, Message>("chat");

        // Register Message binary type.
        ignite.GetBinary().GetBinaryType(typeof(Message));

        // Initialize unique ID sequence
        var messageId = ignite.GetAtomicSequence("chatId", 0, true);

        // Set up continuous query
        cache.QueryContinuous(new ContinuousQuery<long, Message>(new CacheListener()));

        // Run the chat loop
        while (true)
        {
            Console.Write("> ");

            var msgText = Console.ReadLine();
            var msgId = messageId.Increment();

            cache[msgId] = new Message(name, msgText);
        }
    }

    private class CacheListener : ICacheEntryEventListener<long, Message>
    {
        public void OnEvent(IEnumerable<ICacheEntryEvent<long, Message>> evts)
        {
            foreach (var evt in evts)
                Console.WriteLine($"{evt.Value.Author}: {evt.Value.Text}");
        }
    }
}