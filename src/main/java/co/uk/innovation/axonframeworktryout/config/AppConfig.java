package co.uk.innovation.axonframeworktryout.config;

import co.uk.innovation.axonframeworktryout.agregator.AggregateTest;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EventStorageEngine inMemoryEventStorageEngine(){
        return new InMemoryEventStorageEngine();
    }

    @Bean
    public EventSourcingRepository eventSourcingRepository(EmbeddedEventStore store){
        return new EventSourcingRepository(AggregateTest.class, store);
    }


}
