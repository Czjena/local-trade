
package resources;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgresContainerInitializer {

    // Definiujemy stały, współdzielony kontener.
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    // Ten blok statyczny uruchomi kontener raz, przed uruchomieniem pierwszego testu.
    static {
        POSTGRES_CONTAINER.start();
    }
}