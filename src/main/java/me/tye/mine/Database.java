package me.tye.mine;

import me.tye.mine.clans.Clan;
import me.tye.mine.clans.Member;
import me.tye.mine.utils.TempConfigsStore;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.UUID;

public class Database {
private static boolean initiated = false;
private static Database database;

public static Database getInstance() {
  return database;
}


private Connection dbConnection;

/**
 Creates the connection to the database & creates the required tables if they don't exist.
 * @throws SQLException If there was an error interacting with the database.
 */
public Database() throws SQLException {
  if (initiated) return;

  String databasePath = FileUtils.removeExtension(TempConfigsStore.database.getAbsolutePath()) + ".db";

  String databaseUrl = "jdbc:sqlite:" + databasePath;

  dbConnection = DriverManager.getConnection(databaseUrl);

  dbConnection.setAutoCommit(false);

  //Create default tables;
  String clanTable = """
        CREATE TABLE IF NOT EXISTS clans (
        clanID TEXT NOT NULL PRIMARY KEY,
              
        name TEXT NOT NULL,
        description TEXT NOT NULL
              
        ) WITHOUT ROWID;
        """;

  String PermsTable = """
        CREATE TABLE IF NOT EXISTS perms (
        permID TEXT NOT NULL PRIMARY KEY,
              
        name TEXT NOT NULL,
        description TEXT NOT NULL,
              
              
        clanID TEXT,
        memberID TEXT,
        FOREIGN KEY (clanID) REFERENCES clans (clanID) ON DELETE CASCADE,
        FOREIGN KEY (memberID) REFERENCES membersID (memberID) ON DELETE CASCADE,
        
        CHECK (clanID != NULL AND memberID != NULL)
              
        ) WITHOUT ROWID;
        """;

  String claimsTable = """
        CREATE TABLE IF NOT EXISTS claims (
        claimID TEXT NOT NULL PRIMARY KEY,
              
              
              
        clanID TEXT NOT NULL,
        FOREIGN KEY (clanID) REFERENCES clans (clanID) ON DELETE CASCADE

        ) WITHOUT ROWID;
        """;

  String memberTable = """
        CREATE TABLE IF NOT EXISTS members (
        memberID TEXT NOT NULL PRIMARY KEY,
              

              
        clanID TEXT,
        FOREIGN KEY (clanID) REFERENCES clans (clanID) ON DELETE SET NULL
              
        ) WITHOUT ROWID;
        """;


  Statement statement = dbConnection.createStatement();
  statement.execute(clanTable);
  statement.execute(claimsTable);
  statement.execute(memberTable);
  statement.execute(PermsTable);

  dbConnection.commit();

  database = this;
  initiated = true;
}


private Connection getDbConnection() throws SQLException {
  dbConnection.setAutoCommit(false);
  return dbConnection;
}

/**
 Gets the result of the given query from the database.
 * @param query The given query.
 * @return The result set from the database.
 * @throws SQLException If there was an error querying the database.
 */
private static ResultSet getResult(String query) throws SQLException {
  Connection dbConnection = getInstance().getDbConnection();
  Statement statement = dbConnection.createStatement();
  return statement.executeQuery(query);
}

/**
 Loads the data from the database into memory.
 */
public static void loadData() throws SQLException {
  Connection dbConnection = getInstance().getDbConnection();

a

}

public static void createClan(Clan newClan) throws SQLException{
  Connection dbConnection = getInstance().getDbConnection();
  PreparedStatement statement = dbConnection.prepareStatement(
      "INSERT INTO clans (clanID, name, description) VALUES(?,?,?)");

  statement.setString(1, newClan.getClanID().toString());
  statement.setString(2, newClan.getName());
  statement.setString(3, newClan.getDescription());

}

/**
 Checks if a clan with the given UUID already exists.
 * @param clanID The UUID to check.
 * @return True if the clan exists or if there was an error interacting with the database.
 */
public static boolean clanExists(UUID clanID) {
  return exists("clanID", "clans", clanID);
}

/**
 Checks if a claim with the given UUID already exists.
 * @param claimID The UUID to check.
 * @return True if the claim exists or if there was an error interacting with the database.
 */
public static boolean claimExists(UUID claimID) {
  return exists("claimID", "claims", claimID);
}

/**
 Checks if a member with the given UUID already exists.
 * @param memberID The UUID to check.
 * @return True if the member exists or if there was an error interacting with the database.
 */
public static boolean memberExists(UUID memberID) {
  return exists("memberID", "members", memberID);
}

/**
 Checks if a perm with the given UUID already exists.
 * @param permID The UUID to check.
 * @return True if the perm exists or if there was an error interacting with the database.
 */
public static boolean permExists(UUID permID) {
  return exists("permID", "perms", permID);
}


/**
 Checks if the given uuid exists in the given column, in the given table.
 * @param column The given column.
 * @param table The given table.
 * @param uuid The given uuid.
 * @return True if the uuid is taken or if there was an error interacting with the database.
 */
private static boolean exists(String column, String table, UUID uuid) {
  try (ResultSet uuidIsTaken = getResult(
      "SELECT "+column+" FROM "+table+" WHERE "+column+" == "+uuid.toString()
  )) {

    return !uuidIsTaken.next();

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return true;
  }
}


public static @Nullable Member getMember(UUID memberID) {
  try (ResultSet memberData = getResult(
      "SELECT memberID FROM members WHERE memberID == "+memberID.toString()
  )) {

    memberData.next();

    String clanID = memberData.getString("clanID");

    return new Member(memberID, );

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }

}

}
