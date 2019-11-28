package org.zalando.opentracing.jdbc.span;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatternPeerParserTest {

    private final PeerParser unit = new PatternPeerParser();

    @ParameterizedTest
    @CsvSource({
            "jdbc:oracle:thin:@db-users-production.team.svc.cluster.local:1521/database,db-users-production.team.svc.cluster.local",
            "jdbc:mysql://db-users-production.team.svc.cluster.local/database,db-users-production.team.svc.cluster.local",
            "jdbc:postgresql://db-users-production.team.svc.cluster.local/database,db-users-production.team.svc.cluster.local",
            "jdbc:sqlserver://db-users-production.team.svc.cluster.local,db-users-production.team.svc.cluster.local",
            "jdbc:mariadb://127.0.0.1/database,",
            "jdbc:db2://127.0.0.1:50000/database,",
            "jdbc:sap://db-users-production.team.svc.cluster.local:30015/,db-users-production.team.svc.cluster.local",
            "jdbc:informix-sqli://127.0.0.1:9088/sysuser:INFORMIXSERVER=database,",
            "jdbc:hsqldb:mem:database,database",
            "jdbc:h2:mem:database,database",
            "jdbc:derby:target/tmp/derby/database;databaseName=database,target",
            "jdbc:postgresql://localhost/database,localhost",
            "jdbc:unsupported,",
    })
    void shouldParseHostname(final String url, final String expected) {
        final Peer peer = unit.parse(url);
        assertEquals(expected, peer.getHostname());
    }

    @ParameterizedTest
    @CsvSource({
            "jdbc:oracle:thin:@db-users-production.team.svc.cluster.local:1521/database,",
            "jdbc:mysql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:postgresql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:sqlserver://db-users-production.team.svc.cluster.local,",
            "jdbc:mariadb://127.0.0.1/database,127.0.0.1",
            "jdbc:db2://127.0.0.1:50000/database,127.0.0.1",
            "jdbc:sap://db-users-production.team.svc.cluster.local:30015/,",
            "jdbc:informix-sqli://127.0.0.1:9088/sysuser:INFORMIXSERVER=database,127.0.0.1",
            "jdbc:hsqldb:mem:database,",
            "jdbc:h2:mem:database,",
            "jdbc:derby:target/tmp/derby/database;databaseName=database,",
            "jdbc:postgresql://localhost/database,",
            "jdbc:unsupported,",
    })
    void shouldParseIpv4(final String url, final String expected) {
        final Peer peer = unit.parse(url);
        assertEquals(expected, peer.getIpv4());
        assertNull(peer.getIpv6());
    }

    @ParameterizedTest
    @CsvSource({
            "jdbc:oracle:thin:@db-users-production.team.svc.cluster.local:1521/database,",
            "jdbc:mysql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:postgresql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:sqlserver://db-users-production.team.svc.cluster.local,",
            "jdbc:mariadb://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]/database,2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "jdbc:db2://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:50000/database,2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "jdbc:sap://db-users-production.team.svc.cluster.local:30015/,",
            "jdbc:informix-sqli://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:9088/sysuser:INFORMIXSERVER=database,2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "jdbc:hsqldb:mem:database,",
            "jdbc:h2:mem:database,",
            "jdbc:derby:target/tmp/derby/database;databaseName=database,",
            "jdbc:postgresql://localhost/database,",
            "jdbc:unsupported,",
    })
    void shouldParseIpv6(final String url, final String expected) {
        final Peer peer = unit.parse(url);
        assertEquals(expected, peer.getIpv6());
        assertNull(peer.getIpv4());
    }

    @ParameterizedTest
    @CsvSource({
            "jdbc:oracle:thin:@db-users-production.team.svc.cluster.local:1521/database,1521",
            "jdbc:mysql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:postgresql://db-users-production.team.svc.cluster.local/database,",
            "jdbc:sqlserver://db-users-production.team.svc.cluster.local,",
            "jdbc:mariadb://127.0.0.1/database,",
            "jdbc:db2://127.0.0.1:50000/database,50000",
            "jdbc:sap://db-users-production.team.svc.cluster.local:30015/,30015",
            "jdbc:informix-sqli://127.0.0.1:9088/sysuser:INFORMIXSERVER=database,9088",
            "jdbc:hsqldb:mem:database,",
            "jdbc:h2:mem:database,",
            "jdbc:derby:target/tmp/derby/database;databaseName=database,",
            "jdbc:unsupported,",
    })
    void shouldParsePort(final String url, final Integer expected) {
        final Peer peer = unit.parse(url);
        assertEquals(expected, peer.getPort());
    }

}
