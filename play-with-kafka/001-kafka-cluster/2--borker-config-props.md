
## kafka broker configuration properties

## https://docs.confluent.io/current/installation/configuration/broker-configs.html

broker.id: 

    - A unique ID for the broker in the Kafka cluster. 
    - This ID must be set to a unique integer for each broker. 
    - This is used for replication and topic partitioning.
    - If not set, a unique integer is generated.
    - We recommend explicitly setting this to avoid confusion in the future.


############################# Socket Server Settings #############################

listeners:

    - The listeners used by the Kafka broker for handling client requests.
    - The default value is PLAINTEXT://:9092.
    - If you have a multi-node Kafka cluster, you must set this to the IP address of the node.

advertised.listeners:

    - The listeners to publish to ZooKeeper for clients to use.
    - In IaaS environments, this may need to be different from the interface to which the broker binds.
    - If this is not set, the value for listeners will be used.
    - If listeners is not set, it will use the value returned from `java.net.InetAddress.getCanonicalHostName()`.
    - If you want to set Kafka to allow external connections, you must set this to the external IP address.


num.network.threads: 

    - The number of threads to use for handling network requests. 
    - The setting affects the number of concurrent reads/writes to the network.    
    - We recommend setting this to at least 2x the number of cores.
    - The default value is 3.

num.io.threads: 

    - The number of I/O threads that the server uses for processing requests, which may include disk I/O.
    - The setting affects the number of concurrent reads and writes to storage.
    - We recommend setting this to at least 2x the number of cores.
    - For example, if the server has 8 cores, a reasonable value for this parameter is 16.
    - The default value is 8.
    - If you have more disks, we recommend increasing the number of I/O threads appropriately.

socket.send.buffer.bytes:

    - The SO_SNDBUFF buffer of the socket connections.
    - The SO_SNDBUFF buffer is the buffer size used by the kernel for sending data on the socket.
    - The default value is 100KB.
    - If you have a high-bandwidth network, you can increase this value to improve throughput.
    - For example, if you have a 10G network, you can set this to 10MB.


socket.receive.buffer.bytes:
    
    - The SO_RCVBUFF buffer of the socket connections.
    - The SO_RCVBUFF buffer is the buffer size used by the kernel for receiving data on the socket.
    - The default value is 100KB.
    - If you have a high-bandwidth network, you can increase this value to improve throughput.
    - For example, if you have a 10G network, you can set this to 10MB.


socket.request.max.bytes:

    - The maximum size of a request that the socket server will accept 
      (protection against out of memory errors in the case of large requests).
    - The default value is 100MB.
    - If you have a high-bandwidth network, you can increase this value to improve throughput.


############################# Log Basics #############################

log.dirs: 
    
    - The directories where the Kafka logs are stored. 
    - It can be a single directory or a comma-separated list of directories.
    - The default value is /tmp/kafka-logs.
    - If you have multiple disks, you can increase throughput by specifying a comma-separated list of directories.
    - For example, if you have a topic with 12 partitions, you can spread the partitions across the disks by setting the following properties:
        - log.dirs=/disk1/kafka,/disk2/kafka,/disk3/kafka
        - log.dir.disk1=/disk1/kafka
        - log.dir.disk2=/disk2/kafka
        - log.dir.disk3=/disk3/kafka


num.partitions:

    - The default number of log partitions per topic.
    - The default value is 1.
    - We recommend setting this to a value greater than 1 for production.


num.recovery.threads.per.data.dir:

    What is log recovery?
    - Log recovery is the process of bringing the log files on disk up to date with the current state of the log.
    - This is done when a broker starts up or when a log segment is rolled out.
    - Log recovery is done in parallel for all the log partitions on the broker.
    - The number of threads used for log recovery is controlled by the num.recovery.threads.per.data.dir property.
    - The default value is 1.


############################# Internal Topic Settings  #############################

offsets.topic.replication.factor:

    - The replication factor for the offsets topic.
    - This must be set to a value greater than or equal to the number of brokers.

transaction.state.log.replication.factor:

    - The replication factor for the transaction topic.
    - This must be set to a value greater than or equal to the number of brokers.

transaction.state.log.min.isr:

    - The minimum number of in-sync replicas (ISRs) that must be available to write to for the transaction topic.
    - This must be set to a value greater than or equal to the number of brokers.


############################# Log Retention Policy #############################


log.retention.hours:

    - The number of hours to keep a log file before deleting it.
    - The default value is 168 hours (one week).

log.retention.minutes:

    - The number of minutes to keep a log file before deleting it.
    - The default value is -1, which means that the retention time is not based on time.

log.retention.ms:

    - The number of milliseconds to keep a log file before deleting it.
    - The default value is -1, which means that the retention time is not based on time.

log.retention.bytes:

    - The maximum size of the log before deleting it.
    - The default value is -1, which means that the size is not limited by this setting.

log.segment.bytes:

    - The maximum size of a log segment file.
    - When this size is reached, a new log segment file is created.
    - The default value is 1GB.


log.retention.check.interval.ms:

    - The interval at which log segments are checked to see if they can be deleted according to the retention policies.
    - The default value is 5 minutes.


log.cleanup.policy:

    - The default cleanup policy for segments beyond the retention window.
    - The policy for deleting log segments. 
    - The default is "delete". Another option is "compact".


############################# Zookeeper #############################

zookeeper.connect:

    - The ZooKeeper connection string for the ZooKeeper nodes in your Kafka cluster.
    - You can specify multiple nodes in the connection string for failover.
    - This setting is used when the Kafka server is started.
    - The default value is localhost:2181.
    - If you have a ZooKeeper cluster, you can specify the ZooKeeper nodes in the following format:
        - zookeeper.connect=zk-node1:2181,zk-node2:2181,zk-node3:2181
    - If you have a ZooKeeper cluster, we recommend specifying at least two nodes in the connection string for failover.


zookeeper.connection.timeout.ms:

    - The maximum amount of time to wait for a connection to ZooKeeper.
    - The default value is 6000 milliseconds.
    - If you have a ZooKeeper cluster, we recommend setting this to a value greater than 6000 milliseconds for production.

############################# Group Coordinator Settings #############################

group.initial.rebalance.delay.ms:

    - The amount of time the group coordinator will wait for more consumers to join a new group before performing the first rebalance.
    - The default value is 0.
    - We recommend setting this to a higher value for production.


#############################  #############################

auto.create.topics.enable:

    - Enable auto creation of topic on the server.
    - The default value is true.


delete.topic.enable:

    - Enable topic deletion on the server.
    - The default value is true.

#############################  #############################

inter.broker.protocol.version:

    - The protocol version used between Kafka brokers for inter-broker communication.
    - The default value is 0.10.2-IV0.
    - This must be set to a version that is supported by all the brokers in the cluster.

compression.type:

    - Default compression type for newly created topics.


message.max.bytes:

    - The maximum size of a message that the server can receive.
    - The default value is 1000000 bytes.
    

-------------------------------------------------------------------------------------------------
more properties
-------------------------------------------------------------------------------------------------

min.insync.replicas: 

    - This setting specifies the minimum number of replicas that must acknowledge a write for it to be considered successful. 
    - This is critical for ensuring data durability.

default.replication.factor: 

    - Sets the default replication factor for automatically created topics.

offsets.topic.replication.factor: 

    - Defines the replication factor for the offsets topic (used by Kafka to store consumer offsets).

transaction.state.log.replication.factor: 

    - Sets the replication factor for the transaction state log, which is used for exactly-once processing in Kafka Streams.

transaction.state.log.min.isr: 

    - The minimum number of in-sync replicas for the transaction state log.

log.flush.interval.messages: 

    - The number of messages to accept before forcing a flush of data to disk. 
    - This can be used to control disk I/O.

log.flush.interval.ms: 

    - The maximum time in milliseconds that a message can sit in a log before we force a flush.

log.roll.hours: 

    - The maximum time before a new log segment is rolled out.

log.cleanup.interval.mins: 

    - The frequency in minutes to check the log for segments that can be deleted.

queued.max.request.bytes: 

    - The maximum number of unacknowledged requests the server will take before blocking new requests.

num.recovery.threads.per.data.dir: 

    - The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.

auto.leader.rebalance.enable: 

    - Determines whether the broker will attempt to rebalance the leadership of partitions among brokers.

background.threads: 

    - Number of threads to use for various background processing tasks.

log.index.size.max.bytes: 

    - The maximum size in bytes of the index that maps offsets to file positions.

log.index.interval.bytes: 

    - This setting controls how frequently Kafka adds an entry to the offset index.

replica.fetch.max.bytes: 

    - The maximum amount of data the server will return per fetch request.

replica.fetch.wait.max.ms: 

    - The maximum time the server will wait for the request if there's not enough data to immediately satisfy the fetch request.

replica.lag.time.max.ms: 

    - If a follower hasn't sent any fetch requests or hasn't consumed up to the leader's log end offset for this time, the leader will remove the follower from ISR.

replica.socket.receive.buffer.bytes: 

    - The size of the TCP receive buffer to use when reading data.

replica.socket.timeout.ms: 

    - The socket timeout for network requests.



-------------------------------------------------------------------------------------------------


controlled.shutdown.enable: 

    - This setting allows the broker to move all its partitions to other brokers before shutting down, which can be useful for maintenance and upgrades.

controlled.shutdown.max.retries: 

    - Specifies the maximum number of retries when attempting controlled shutdown.

controlled.shutdown.retry.backoff.ms: 

    - The time to wait between retries during controlled shutdown.

fetch.purgatory.purge.interval.requests: 

    - The interval at which the fetch request purgatory is purged of expired requests.

group.max.session.timeout.ms: 

    - The maximum allowed session timeout for registered consumers. Longer timeouts give consumers more time to process messages, but mean longer delays for rebalancing.

group.min.session.timeout.ms: 

    - The minimum allowed session timeout for registered consumers. Shorter timeouts result in quicker consumer rebalancing.

log.cleaner.backoff.ms: 

    - The amount of time to wait before attempting to clean the log again after a cleaning job is completed.

log.cleaner.dedupe.buffer.size: 

    - The total memory used for log deduplication across all cleaner threads.

log.cleaner.enable: 

    - Determines whether log compaction is enabled, which is important for topics where retaining all data is unnecessary.

log.cleaner.io.buffer.load.factor: 

    - The buffer load factor for log cleaner I/O operations.

log.cleaner.io.buffer.size: 

    - The size of the I/O buffer for log cleaner threads.

log.cleaner.io.max.bytes.per.second: 

    - The maximum I/O bytes per second that the cleaner is allowed to use.

log.cleaner.min.cleanable.ratio: 

    - The minimum "dirty" ratio of a log to be eligible for cleaning.

log.cleaner.threads: 

    - The number of background threads used for log cleaning.

log.message.timestamp.difference.max.ms: 

    - The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message.

log.message.timestamp.type: 

    - Specifies whether to use the message timestamp provided by the producer or the broker timestamp when the message is appended.

log.preallocate: 

    - Enables log segment file preallocation.

num.replica.fetchers: 

    - The number of fetcher threads used to replicate messages from a source broker. Increasing this can increase replication throughput.

offset.metadata.max.bytes: 

    - The maximum size for a metadata entry associated with an offset commit.

offsets.commit.required.acks: 

    - The required acks for offset commit.

offsets.commit.timeout.ms: 

    - Offset commit will be considered failed if not completed within this time.

offsets.load.buffer.size: 

    - Batch size for reading offsets from the offsets log.

offsets.retention.check.interval.ms: 

    - Frequency at which to check for stale offsets.

offsets.topic.num.partitions: 

    - The number of partitions for the offset commit topic.

queued.max.requests: 

    - The maximum number of unanswered requests a broker will tolerate before blocking incoming request threads.

replica.selector.class: 

    - The class used to select which replicas to assign the leader and followers to.

unclean.leader.election.enable: 

    - Determines whether an out-of-sync replica can be elected as leader as a last resort, even though doing so may result in data loss.


-------------------------------------------------------------------------------------------------
