package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class VehicleTest {
    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    private static Connection conn;

    @BeforeClass
    public static void initJdbc() {
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zu Datenbank nicht möglich: " + e.getMessage());
            System.exit(1);
        }
        try {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE vehicle (" +
                    "id INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                    "brand VARCHAR(255) NOT NULL," +
                    "type VARCHAR(255) NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @AfterClass
    public static void teardownJdbc() {
        try {
            conn.createStatement().execute("DROP TABLE vehicle");
            System.out.println("Tabelle VEHICLE gelöscht");
        } catch (SQLException e) {
            System.out.println("Tablle VEHICLE konnte nicht gelöscht werden:\n" + e.getMessage());
        }
        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Good bye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ddl() {
        int countInserts = 0;
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO vehicle (id, brand, type) VALUES (1, 'Opel', 'Commodore')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (2, 'Opel', 'Kapitän')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (3, 'Opel', 'Kadett')";
            countInserts += stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        assertThat(countInserts, is(3));
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, brand, type FROM vehicle");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));
            assertThat(rs.getString("TYPE"), is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));
            assertThat(rs.getString("TYPE"), is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));
            assertThat(rs.getString("TYPE"), is("Kadett"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
