package me.tye.mine;

import me.tye.mine.clans.Clan;
import me.tye.mine.clans.Member;
import me.tye.mine.utils.TempConfigsStore;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Collection;
import java.util.UUID;

public class Database {
private static boolean initiated = false;


private static Connection dbConnection;

/**
 Creates the connection to the database & creates the required tables if they don't exist.
 * @throws SQLException If there was an error interacting with the database.
 */
public static void init() throws SQLException {
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
              
        permLevel TEXT,
        defaultPerms TEXT NOT NULL,
              
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

  initiated = true;
}


private static @NotNull Connection getDbConnection() throws SQLException {
  dbConnection.setAutoCommit(false);
  return dbConnection;
}

/**
 Gets the result of the given query from the database.
 * @param query The given query.
 * @return The result set from the database.
 * @throws SQLException If there was an error querying the database.
 */
private static @NotNull ResultSet getResult(@NotNull String query) throws SQLException {
  Connection dbConnection = getDbConnection();
  Statement statement = dbConnection.createStatement();
  return statement.executeQuery(query);
}

/**
 Creates a where statement that matches all the given uuids in the given column.
 * @param column The given column.
 * @param uuids The given uuids.
 * @return The where statement that will match all the uuids.
 */
private static @NotNull String createWhere(String column, Collection<UUID> uuids) {
  StringBuilder where = new StringBuilder("WHERE ");

  uuids.forEach(uuid -> {
    String stringUUID = uuid.toString();
    where.append(column)
        .append(" == ")
        .append(stringUUID)
        .append(" OR ");
  });

  return where.substring(where.length()-4);
}

/**
 Loads the data from the database into memory.
 */
public static void loadData() throws SQLException {
  Connection dbConnection = getDbConnection();

a

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

    UUID clanID = UUID.fromString(memberData.getString("clanID"));
    UUID clanPermID = UUID.fromString(memberData.getString("clanPermID"));

    return new Member(memberID, clanID, clanPermID);

  } catch (SQLException |IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

public static @Nullable Clan getClan(UUID clanID) {
  try (ResultSet clanData = getResult(
      "SELECT clanID FROM clan WHERE clanID == "+clanID.toString()
  )) {

    clanData.next();

    String clanName = clanData.getString("name");
    String clanDescription = clanData.getString("description");

    return new Clan();

  } catch (SQLException |IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

public static void registerMember(UUID memberID) {
  try (Connection connection = getDbConnection()) {

    PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO members (memberID, clanID, clanPermID) VALUES(?,?,?)"
    );

    statement.setString(1, memberID.toString());
    statement.setNull(2, Types.VARCHAR);
    statement.setNull(3, Types.VARCHAR);

    statement.execute();
    connection.commit();

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove when confirmed working.
  }
}

public static void createClan(Clan newClan) {
  try (Connection dbConnection = getDbConnection()) {

    //create the clan
    PreparedStatement clanCreate = dbConnection.prepareStatement(
        "INSERT INTO clans (clanID, name, description) VALUES(?,?,?,?,?,?)");

    clanCreate.setString(1, newClan.getClanID().toString());
    clanCreate.setString(2, newClan.getName());
    clanCreate.setString(3, newClan.getDescription());

    clanCreate.execute();

    PreparedStatement memberAssign = dbConnection.prepareStatement("""
    UPDATE members
    SET clanID = ?
    ?
    """
    );

    memberAssign.setString(1, newClan.getClanID().toString());
    //TODO: check that this works.
    memberAssign.setString(2, createWhere("memberID", newClan.getClanMembers()));

    memberAssign.execute();



    dbConnection.commit();

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove when confirmed working.
  }
}
}
