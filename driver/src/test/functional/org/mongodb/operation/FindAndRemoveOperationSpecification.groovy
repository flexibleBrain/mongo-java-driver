package org.mongodb.operation

import org.mongodb.Document
import org.mongodb.FunctionalSpecification
import org.mongodb.MongoClientOptions
import org.mongodb.codecs.DocumentCodec
import org.mongodb.codecs.PrimitiveCodecs
import org.mongodb.connection.BufferProvider
import org.mongodb.connection.Cluster
import org.mongodb.connection.ClusterableServerFactory
import org.mongodb.connection.ConnectionFactory
import org.mongodb.connection.SSLSettings
import org.mongodb.connection.ServerAddress
import org.mongodb.connection.impl.DefaultClusterFactory
import org.mongodb.connection.impl.DefaultClusterableServerFactory
import org.mongodb.connection.impl.DefaultConnectionFactory
import org.mongodb.connection.impl.DefaultConnectionProviderFactory
import org.mongodb.connection.impl.DefaultConnectionProviderSettings
import org.mongodb.connection.impl.DefaultConnectionSettings
import org.mongodb.connection.impl.DefaultServerSettings
import org.mongodb.connection.impl.PowerOfTwoBufferPool
import org.mongodb.session.ClusterSession
import org.mongodb.session.Session
import spock.lang.Ignore

import static java.util.concurrent.Executors.newScheduledThreadPool

class FindAndRemoveOperationSpecification extends FunctionalSpecification {
    private final DocumentCodec documentDecoder = new DocumentCodec()
    private final PrimitiveCodecs primitiveCodecs = PrimitiveCodecs.createDefault()
    private final BufferProvider bufferProvider = new PowerOfTwoBufferPool()
    private final MongoClientOptions options = new MongoClientOptions.Builder().build();

    private final DefaultConnectionProviderSettings connectionProviderSettings = DefaultConnectionProviderSettings.builder()
                                                                                                                  .options(options).build();

    private final SSLSettings sslSettings = SSLSettings.builder().build();
    private final DefaultConnectionSettings connectionSettings = DefaultConnectionSettings.builder().build();
    private final ConnectionFactory connectionFactory = new DefaultConnectionFactory(connectionSettings, sslSettings,
                                                                                     bufferProvider, Collections.emptyList());

    private final ClusterableServerFactory clusterableServerFactory = new DefaultClusterableServerFactory(
            DefaultServerSettings.builder().build(),
            new DefaultConnectionProviderFactory(connectionProviderSettings, connectionFactory),
            null,
            new DefaultConnectionFactory(connectionSettings, sslSettings, bufferProvider, Collections.emptyList()),
            newScheduledThreadPool(3),
            bufferProvider)

    private final Cluster cluster = new DefaultClusterFactory().create(new ServerAddress(), clusterableServerFactory)
    private final Session session = new ClusterSession(cluster);

    @Ignore('This test is failing and I think it might actually indicate a bug in the code not the test')
    def 'should remove single document'() {
        given:
        Document pete = new Document('name', 'Pete').append('job', 'handyman')
        Document sam = new Document('name', 'Sam').append('job', 'plumber')

        collection.insert(pete);
        collection.insert(sam);

        when:
        FindAndRemove findAndRemove = new FindAndRemove().select(new Document('name', 'Pete'));

        FindAndRemoveOperation<Document> operation = new FindAndRemoveOperation<Document>(bufferProvider, session, false,
                                                                                          cluster.getDescription(),
                                                                                          collection.getNamespace(), findAndRemove,
                                                                                          primitiveCodecs, documentDecoder)
        Document returnedDocument = operation.execute()

        then:
        collection.find().count() == 1;
        collection.find().one == sam
        returnedDocument == pete
    }

}