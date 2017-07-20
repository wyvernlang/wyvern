package wyvern.stdlib.support;

import java.sql.*;

public class Database {

    public static Database database = new Database();
    public Database() {
    }


    public Database(String connectionStr, String user, String password) {
        this.connectionStr = connectionStr;
        this.user = user;
        this.password = password;
    }

    private String connectionStr, user, password;


    /************
     * Inner Table Class, for more secure table queries
     */
    public static class Table {
        String name, primaryKey;
        String[] colNames, formats;

        public Table(String name, String[] colNames, String[] formats) {
            this.name = name;
            this.colNames = colNames;
            this.formats = formats;
        }

        // Example table for the empty constructor
        public Table() {
            // Set the table name
            this.name = "cats";

            // Set the column names
            String[] colNames = new String[3];
            colNames[0] = "id";
            colNames[1] = "name";
            colNames[2] = "owner";
            this.colNames = colNames;

            // Set the primary key
            this.primaryKey = colNames[0];

            // Set the formats of the columns
            String[] formats = new String[3];
            formats[0] = "INT unsigned NOT NULL AUTO_INCREMENT";
            formats[1] = "VARCHAR(50) NOT NULL";
            formats[2] = "VARCHAR(50) NOT NULL";
            this.formats = formats;
        }

        // TODO: check this --> Return an object array, was considering this for insert.
        public Object[] makeEntry(Object[] entry) {
            // Check if each entry in 'entry' matches with the type of the table?
            return entry;
        }
    }

    /**
     * Connect to a database
     */
    public Connection connect(String connectionStr, String user, String password) {
        try {
            // Establish connection with DB
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(connectionStr, user, password);

            // Print success message
            System.out.println("Connected to database successfully."); // no longer has DB specific message

            // Return established connection
            return con;

        } catch (Exception e) {
            System.out.println(e);
            return null; // Should a failed connect return something else?
        }
    }

    /**
     * Disconnect from the database.
     */
    public void disconnect(Connection con) {
        try {
            // Close connection
            con.close();

            // Print success message
            System.out.println("Disconnected from database successfully.");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Create a new table.
     * @param con
     * @param tableName
     */
    public static void createTable(Connection con, String tableName, String tableContents) {
        try {
            // Our SQL (create table) query
            String query = "CREATE TABLE " + tableName + tableContents;

            // Create java statement
            PreparedStatement statement = con.prepareStatement(query);

            // Execute statement
            statement.execute();

            // Close statement
            statement.close();
        } catch (Exception e) { System.out.println(e); }
    }

    /**
     * Create a new table.
     * @param con
     * @param table
    public static void createTable(Connection con, Table table) {
        try {
            // Convert table to string
            String colsAndFormats = "";
            for (int i = 0; i < table.colNames.length; i++) {
                colsAndFormats += table.colNames[i] + " " + table.formats[i] + ", ";
            }

            // Our SQL (create table) query
            String query = "CREATE TABLE " + table.name + " (" + colsAndFormats + " PRIMARY KEY " + "(" + table.primaryKey + "));";

            // Create java statement
            PreparedStatement statement = con.prepareStatement(query);

            // Execute statement
            statement.execute();

            // Close statement
            statement.close();
        } catch (Exception e) { System.out.println(e); }
    }
     */

    /**
     public void select(Connection1 con, String[] colNames, String table) {
     try {
     // Convert colNames into usable string
     String args = "";
     for (int i = 0; i < colNames.length; i++) {
     args += colNames[i];
     if (i < colNames.length - 1) {
     args += ", ";
     }
     }

     // Our SQL query
     String query = "SELECT " + args + " FROM " + table;
     System.out.println("Select query looks like this: " + query);

     // Create java statement
     PreparedStatement statement = con.prepareStatement(query);

     // Execute the statement
     statement.execute();

     // Close the statement
     statement.close();

     } catch (Exception e) { System.out.println(e); }
     }
     */

    /**
     * Insert an entry/value into a table.
     * @param con
     * @param table
     * @param entry

    public void insert (Connection1 con, String table, String entry) { // Just going to use "VALUE" instead of a format string.
    try {
    // Our SQL insert query
    String query = "INSERT INTO " + table + " VALUE " + entry; // If entry were an array, could insert multiple values.

    // Create java statement
    PreparedStatement statement = con.prepareStatement(query);

    // Execute query
    statement.execute();

    // Close statement
    statement.close();
    } catch (Exception e) { System.out.println(e); }

    }
     */

    /**
     * Delete a specified column in a table.
     * @param arg
     * @param table

    public void deleteEntry(Connection1 con, String table, String arg) { // example arg: "name='muffin' "
    try {

    // Our SQL delete query
    String query = "DELETE from " + table + " WHERE " + arg;

    // Create java statement
    PreparedStatement preparedStatement = con.prepareStatement(query); // maybe just statement?

    // Execute statement
    preparedStatement.execute();

    // Close statement
    preparedStatement.close();

    } catch (Exception e) { System.out.println(e); }
    }
     */

    /**
     * BAD NAME: Drop a column in the table (can make drop table function).
     * TODO: Don't use strings for the arguments
     * @param col
     * @param table

    public void deleteCol(Connection1 con, String table, String col) {
    try {

    // Our SQL drop query
    String query = "ALTER TABLE " + table + " DROP " + col;

    // Create java statement
    PreparedStatement preparedStatement = con.prepareStatement(query);

    // Execute statement
    preparedStatement.execute();

    // Close statement
    preparedStatement.close();

    } catch (Exception e) { System.out.println(e); }
    }
     */

    /**
     public static void main(String[] args) {
     Database db = new Database();
     Connection1 con = db.connect("jdbc:mysql://localhost:3306/pets", "root", "");
     Table cats = new Table();
     db.createTable(con, cats);
     db.select(con, new String[]{"id", "name"}, "cats");
     db.insert(con, "cats", "(6, 'maddie', 'muffin')");
     db.deleteEntry(con, "cats", "id=24");
     db.disconnect(con);

     /*
     db.select("*", "cats");
     db.delete("name='lena'", "cats");
     db.insert("(name, owner, birth)","('lena', 'muffin iii', '2015-01-03')", "cats");
     db.createTable("dogs", "id INT unsigned NOT NULL AUTO_INCREMENT, name VARCHAR(150) NOT " +
     "NULL, PRIMARY KEY (id)");
     db.dropTable("name", "dogs");
     db.disconnect();
     db.select("*", "cats"); // Test to make sure connection is closed.
     }
     */
}