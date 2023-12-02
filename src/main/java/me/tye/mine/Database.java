package me.tye.mine;

import me.tye.mine.clans.Claim;
import me.tye.mine.clans.Clan;
import me.tye.mine.clans.Member;
import me.tye.mine.clans.Perm;
import me.tye.mine.utils.TempConfigsStore;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
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
        
        CHECK (clanID != NULL OR memberID != NULL)
              
        ) WITHOUT ROWID;
        """;

  String claimsTable = """
        CREATE TABLE IF NOT EXISTS claims (
        claimID TEXT NOT NULL PRIMARY KEY,
              
        worldName TEXT NOT NULL,
              
        X1 REAL NOT NULL,
        X2 REAL NOT NULL,
        Y1 REAL NOT NULL,
        Y2 REAL NOT NULL,
        Z1 REAL NOT NULL,
        Z2 REAL NOT NULL,
        
        clanID TEXT NOT NULL,
        FOREIGN KEY (clanID) REFERENCES clans (clanID) ON DELETE CASCADE

        ) WITHOUT ROWID;
        """;

  String memberTable = """
        CREATE TABLE IF NOT EXISTS members (
        memberID TEXT NOT NULL PRIMARY KEY,
              
        clanPermID TEXT,
              
        clanID TEXT,
        FOREIGN KEY (clanID) REFERENCES clans (clanID) ON DELETE SET NULL
              
        ) WITHOUT ROWID;
        """;


  Statement statement = dbConnection.createStatement();
  dbConnection.setAutoCommit(false);
  statement.execute(clanTable);
  statement.execute(claimsTable);
  statement.execute(memberTable);
  statement.execute(PermsTable);
  dbConnection.commit();
  dbConnection.setAutoCommit(true);

  initiated = true;
}

/**
 Gets the connection to the database.<br>
 <b>Don't use this method with auto closable. The connection to the database should stay open.</b>
 * @return The connection to the database.
 * @throws SQLException If a connection to the database couldn't be established.
 */
private static @NotNull Connection getDbConnection() throws SQLException {
  //Attempts to reconnect to the database if it has lost connection
  if (dbConnection.isClosed()) {
    String databasePath = FileUtils.removeExtension(TempConfigsStore.database.getAbsolutePath()) + ".db";

    String databaseUrl = "jdbc:sqlite:" + databasePath;

    dbConnection = DriverManager.getConnection(databaseUrl);
  }

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
 Checks if the database has a response for a query.
 * @param query The given query.
 * @return True if the database responded with a populated result set. False otherwise.
 * @throws SQLException If there was an error querying the database.
 */
private static boolean hasResult(@NotNull String query) throws SQLException {
  ResultSet result = getResult(query);

  return result.next();
}

/**
 Creates a where statement that matches all the given uuids in the given column.
 * @param column The given column.
 * @param uuids The given uuids. This should <b>not</b> be empty.
 * @return The where statement that will match all the uuids.
 */
private static @NotNull String createWhere(@NotNull String column, @NotNull Collection<UUID> uuids) {
  StringBuilder where = new StringBuilder("WHERE ");

  for (UUID uuid : uuids) {
    String stringUUID = uuid.toString();
    where.append("\"")
         .append(column)
         .append("\"")
         .append(" == ")
         .append("\"")
         .append(stringUUID)
         .append("\"")
         .append(" OR ");
  }

  return where.substring(0, where.length()-4);
}

/**
 Checks if a clan with the given UUID already exists.
 * @param clanID The UUID to check.
 * @return True if the clan exists or if there was an error interacting with the database.
 */
public static boolean clanExists(@NotNull UUID clanID) {
  return exists("clanID", "clans", clanID);
}

/**
 Checks if a claim with the given UUID already exists.
 * @param claimID The UUID to check.
 * @return True if the claim exists or if there was an error interacting with the database.
 */
public static boolean claimExists(@NotNull UUID claimID) {
  return exists("claimID", "claims", claimID);
}

/**
 Checks if a member with the given UUID already exists.
 * @param memberID The UUID to check.
 * @return True if the member exists or if there was an error interacting with the database.
 */
public static boolean memberExists(@NotNull UUID memberID) {
  return exists("memberID", "members", memberID);
}

/**
 Checks if a perm with the given UUID already exists.
 * @param permID The UUID to check.
 * @return True if the perm exists or if there was an error interacting with the database.
 */
public static boolean permExists(@NotNull UUID permID) {
  return exists("permID", "perms", permID);
}


/**
 Checks if the given uuid exists in the given column, in the given table.
 * @param column The given column.
 * @param table The given table.
 * @param uuid The given uuid.
 * @return True if the uuid is taken or if there was an error interacting with the database.
 */
private static boolean exists(@NotNull String column, @NotNull String table, @NotNull UUID uuid) {
  try {

    return hasResult("SELECT "+column+" FROM "+table+" WHERE \""+column+"\" == \""+uuid+"\";");

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return true;
  }
}


/**
 Gets a member from the database.
 * @param memberID The uuid of the member.
 * @return The member, if present. If the member doesn't exist or there was an error, null will be returned.
 */
public static @Nullable Member getMember(@NotNull UUID memberID) {
  if (!memberExists(memberID)) return null;

  try (ResultSet memberData = getResult(
      "SELECT * FROM members WHERE \"memberID\" == \""+memberID+"\";"
  )) {

    memberData.next();

    String rawClanID = memberData.getString("clanID");
    String rawClanPermID = memberData.getString("clanPermID");

    UUID clanID = null;
    UUID clanPermID = null;


    if (rawClanID != null) {
      clanID = UUID.fromString(rawClanID);
    }

    if (rawClanPermID != null) {
      clanPermID = UUID.fromString(rawClanPermID);
    }

    return new Member(memberID, clanID, clanPermID);

  } catch (SQLException | IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

/**
 Gets a clan from the database.
 * @param clanID The uuid of the clan.
 * @return The clan, if present. If the clan doesn't exist or there was an error, null will be returned.
 */
public static @Nullable Clan getClan(@NotNull UUID clanID) {
  if (!clanExists(clanID)) return null;

  try (ResultSet clanData = getResult(
      "SELECT * FROM clans WHERE \"clanID\" == \""+clanID+"\";"
  )) {

    clanData.next();

    String clanName = clanData.getString("name");
    String clanDescription = clanData.getString("description");


    //Gets the UUIDS of all the claims
    Collection<UUID> claimIDs = new ArrayList<>();
    ResultSet claims = getResult("SELECT claimID FROM claims WHERE \"clanID\" == \""+clanID+"\";");
    while (claims.next()) {
      claimIDs.add(UUID.fromString(claims.getString("claimID")));
    }

    //Gets the UUIDS of all the members
    Collection<UUID> memberIDs = new ArrayList<>();
    ResultSet members = getResult("SELECT memberID FROM members WHERE \"clanID\" == \""+clanID+"\";");
    while (members.next()) {
      memberIDs.add(UUID.fromString(members.getString("memberID")));
    }

    //Gets the UUIDS of all the members
    Collection<UUID> permIDs = new ArrayList<>();
    ResultSet perms = getResult("SELECT permID FROM perms WHERE \"clanID\" == \""+clanID+"\";");
    while (perms.next()) {
      permIDs.add(UUID.fromString(perms.getString("permID")));
    }

    return new Clan(clanID, clanName, clanDescription, claimIDs ,memberIDs, permIDs);

  } catch (SQLException | IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

/**
 Gets a claim from the database.
 * @param claimID The uuid of the claim
 * @return The claim, if present. If the claim doesn't exist or there was an error, null will be returned.
 */
public static @Nullable Claim getClaim(@NotNull UUID claimID) {
  if (!claimExists(claimID)) return null;

  try (ResultSet claimData = getResult(
      "SELECT * FROM claim WHERE \"claimID\" == \""+claimID+"\";"
  )) {

    claimData.next();

    String worldName = claimData.getString("worldName");
    double X1 = claimData.getDouble("X1");
    double X2 = claimData.getDouble("X2");
    double Y1 = claimData.getDouble("Y1");
    double Y2 = claimData.getDouble("Y2");
    double Z1 = claimData.getDouble("Z1");
    double Z2 = claimData.getDouble("Z2");
    UUID clanID = UUID.fromString(claimData.getString("clanID"));

    return new Claim(clanID, claimID, worldName, X1, X2, Y1, Y2, Z1, Z2);

  } catch (SQLException | IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

/**
 Gets a perm from the database.
 * @param permId The uuid of the perm to get
 * @return The perm, if present. If the perm doesn't exist or there was an error, null will be returned.
 */
public static @Nullable Perm getPerm(@NotNull UUID permId) {
  if (permExists(permId)) return null;

  try (ResultSet permData = getResult(
      "SELECT * FROM perms WHERE \"permID\" == \""+permId+"\";"
  )) {

    permData.next();

    String permName = permData.getString("name");
    String permDescription = permData.getString("description");

    return new Perm(permId, permName, permDescription);

  } catch (SQLException | IllegalArgumentException e) {
    e.printStackTrace();
    //TODO: remove this before release.
    return null;
  }
}

/**
 Writes a member to the database.<br>
 <b>This method will overwrite the current entry for a member with this uuid, if it exists.</b> Use {@link #memberExists(UUID)} to check if the member exists before creating a new one.
 * @param memberID The uuid of the member to create.
 */
public static void createMember(@NotNull UUID memberID) {
  try {
    Connection dbConnection = getDbConnection();
    dbConnection.setAutoCommit(true);

    PreparedStatement statement = dbConnection.prepareStatement(
        "INSERT INTO members (memberID, clanID, clanPermID) VALUES(?,?,?)"
    );

    statement.setString(1, memberID.toString());
    statement.setNull(2, Types.VARCHAR);
    statement.setNull(3, Types.VARCHAR);

    statement.execute();

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove when confirmed working.
  }
}

/**
 Writes a clan to the database.<br>
 <b>This method will overwrite the current entry for a clan with this uuid, if it exists.</b> Use {@link #clanExists(UUID)} to check if the clan exists before creating a new one.
 * @param newClan The new clan to create.
 */
public static void writeClan(@NotNull Clan newClan) {
  try {
    Connection dbConnection = getDbConnection();
    dbConnection.setAutoCommit(false);

    //create the clan
    PreparedStatement clanCreate = dbConnection.prepareStatement(
        "INSERT INTO clans (clanID, name, description) VALUES(?,?,?)");

    clanCreate.setString(1, newClan.getClanID().toString());
    clanCreate.setString(2, newClan.getName());
    clanCreate.setString(3, newClan.getDescription());

    clanCreate.executeUpdate();


    //adds the members to the clan.
    Statement statement = dbConnection.createStatement();
    statement.execute("UPDATE members SET clanID = \""+newClan.getClanID()+"\" "+createWhere("memberID", newClan.getMemberUUIDs()));


    //adds all the claims to the clan.
    for (Claim claim : newClan.getClanClaims()) {
      PreparedStatement claimCreate = dbConnection.prepareStatement("""
        INSERT INTO claims (claimID, worldName, X1, X2, Y1, Y2, Z1, Z2, clanID) VALUES(?,?,?,?,?,?,?,?,?)
        """);

      claimCreate.setString(1, claim.getClaimID().toString());
      claimCreate.setString(2, claim.getWorldName());
      claimCreate.setDouble(3, claim.getX1());
      claimCreate.setDouble(4, claim.getX2());
      claimCreate.setDouble(5, claim.getY1());
      claimCreate.setDouble(6, claim.getY2());
      claimCreate.setDouble(7, claim.getZ1());
      claimCreate.setDouble(8, claim.getZ2());
      claimCreate.setString(9, newClan.getClanID().toString());

      claimCreate.executeUpdate();
    }

    dbConnection.setAutoCommit(true);

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO: remove when confirmed working.
  }
}

public static void createClaim(@NotNull Claim newClaim) {
  try {
    Connection dbConnection = getDbConnection();
    dbConnection.setAutoCommit(true);

    PreparedStatement statement = dbConnection.prepareStatement("""
        INSERT INTO claims (claimID, worldName, X1, X2, Y1, Y2, Z1, Z2, clanID) VALUES(?,?,?,?,?,?,?,?,?)
        """);

    statement.setString(1, newClaim.getClaimID().toString());
    statement.setString(2, newClaim.getWorldName());
    statement.setDouble(3, newClaim.getX1());
    statement.setDouble(4, newClaim.getX2());
    statement.setDouble(5, newClaim.getY1());
    statement.setDouble(6, newClaim.getY2());
    statement.setDouble(7, newClaim.getZ1());
    statement.setDouble(8, newClaim.getZ2());
    statement.setString(9, newClaim.getClaimID().toString());

    statement.executeUpdate();

  } catch (SQLException e) {
    e.printStackTrace();
    //TODO:remove when confirmed working.
  }
}
}
