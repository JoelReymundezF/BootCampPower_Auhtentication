package co.com.crediya.r2dbc.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostgreSQLConnectionPoolTest {

    @InjectMocks
    private PostgreSQLConnectionPool connectionPool;

    @Mock
    private PostgresqlConnectionProperties properties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.host()).thenReturn("localhost");
        when(properties.port()).thenReturn(5432);
        when(properties.database()).thenReturn("dbName");
        when(properties.schema()).thenReturn("schema");
        when(properties.username()).thenReturn("username");
        when(properties.password()).thenReturn("password");
    }

    @Test
    void getConnectionConfigSuccess() {
        assertNotNull(connectionPool.getConnectionConfig(properties));
    }

    @Test
    void transactionManagerBeanCreated() {
        ConnectionFactory mockFactory = mock(ConnectionFactory.class);
        ReactiveTransactionManager txManager = connectionPool.transactionManager(mockFactory);
        assertNotNull(txManager);
    }

    @Test
    void transactionalOperatorBeanCreated() {
        ConnectionFactory mockFactory = mock(ConnectionFactory.class);
        ReactiveTransactionManager txManager = connectionPool.transactionManager(mockFactory);
        TransactionalOperator operator = connectionPool.transactionalOperator(txManager);
        assertNotNull(operator);
    }
}
