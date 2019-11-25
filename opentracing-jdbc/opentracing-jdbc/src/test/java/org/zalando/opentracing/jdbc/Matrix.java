package org.zalando.opentracing.jdbc;

import org.h2.tools.SimpleResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@SuppressWarnings("unused")
// visible for testing
public final class Matrix {

    private Matrix() {

    }

    /**
     * @see <a href="http://www.h2database.com/html/features.html#user_defined_functions">H2 Features: User-Defined Functions and Stored Procedures</a>
     * @param connection database connection
     * @param size matrix size
     * @return matrix
     * @throws SQLException if connection is not accessible
     */
    public static ResultSet create(final Connection connection, final Integer size) throws SQLException {
        final SimpleResultSet resultSet = new SimpleResultSet();
        resultSet.addColumn("X", Types.INTEGER, 10, 0);
        resultSet.addColumn("Y", Types.INTEGER, 10, 0);

        /*
         * A function that returns a result set can be used like a table. However, in this case the function is called
         * at least twice: first while parsing the statement to collect the column names (with parameters set to null
         * where not known at compile time). And then, while executing the statement to get the data (maybe multiple
         * times if this is a join). If the function is called just to get the column list, the URL of the connection
         * passed to the function is jdbc:columnlist:connection. Otherwise, the URL of the connection is jdbc:default:connection.
         */
        if (connection.getMetaData().getURL().equals("jdbc:columnlist:connection")) {
            return resultSet;
        }

        for (int s = size, x = 0; x < s; x++) {
            for (int y = 0; y < s; y++) {
                resultSet.addRow(x, y);
            }
        }

        return resultSet;
    }

}
