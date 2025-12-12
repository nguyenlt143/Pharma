package vn.edu.fpt.pharma.test.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom H2 Function to simulate MySQL DATE_FORMAT
 *
 * This allows H2 database to support MySQL's DATE_FORMAT() function
 * for integration testing purposes.
 *
 * Usage in SQL:
 * SELECT DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') FROM table
 *
 * @author Integration Test Team
 */
public class H2DateFormatFunction {

    /**
     * Simulates MySQL DATE_FORMAT function for H2 database
     *
     * @param timestamp The timestamp to format
     * @param format MySQL format string (e.g., '%Y-%m-%d %H:%i:%s')
     * @return Formatted date string
     */
    public static String dateFormat(java.sql.Timestamp timestamp, String format) {
        if (timestamp == null || format == null) {
            return null;
        }

        try {
            // Convert MySQL format to Java SimpleDateFormat
            String javaFormat = convertMySQLFormatToJava(format);
            SimpleDateFormat sdf = new SimpleDateFormat(javaFormat);
            return sdf.format(new Date(timestamp.getTime()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert MySQL date format to Java SimpleDateFormat
     *
     * MySQL Format -> Java Format:
     * %Y -> yyyy (4-digit year)
     * %m -> MM (2-digit month)
     * %d -> dd (2-digit day)
     * %H -> HH (24-hour)
     * %i -> mm (minutes)
     * %s -> ss (seconds)
     */
    private static String convertMySQLFormatToJava(String mysqlFormat) {
        return mysqlFormat
            .replace("%Y", "yyyy")
            .replace("%m", "MM")
            .replace("%d", "dd")
            .replace("%H", "HH")
            .replace("%i", "mm")
            .replace("%s", "ss")
            .replace("%M", "MMMM")  // Full month name
            .replace("%b", "MMM")   // Abbreviated month
            .replace("%W", "EEEE")  // Full weekday name
            .replace("%a", "EEE");  // Abbreviated weekday
    }

    /**
     * Register this function with H2 database
     *
     * This should be called during test setup to make DATE_FORMAT available
     */
    public static void register(Connection connection) throws SQLException {
        connection.createStatement().execute(
            "CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR \"vn.edu.fpt.pharma.test.config.H2DateFormatFunction.dateFormat\""
        );
    }
}

