package cat.iesesteveterradas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class PR210Honor {

    private static final String DB_URL = "jdbc:sqlite:for_honor.db";

    public static void main(String[] args) {
        initializeDatabase();
        menu();
    }

    // Inicilizar la base de datos
    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "Faccio", null);
                if (!tables.next()) {
                    createTables(conn);
                    insertInitialData(conn);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String createFactionTable = "CREATE TABLE Faccio (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(15) NOT NULL," +
                "resum VARCHAR(500)" +
                ");";
        String createCharacterTable = "CREATE TABLE Personatge (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(15) NOT NULL," +
                "atac REAL," +
                "defensa REAL," +
                "idFaccio INTEGER," +
                "FOREIGN KEY (idFaccio) REFERENCES Faccio(id)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createFactionTable);
            stmt.execute(createCharacterTable);
        }
    }

    private static void insertInitialData(Connection conn) throws SQLException {
        String[] factionData = {
            "INSERT INTO Faccio (nom, resum) VALUES ('Cavallers', 'Though seen as a single group, the Knights are hardly unified. There are many Legions in Ashfeld, the most prominent being The Iron Legion.');",
            "INSERT INTO Faccio (nom, resum) VALUES ('Vikings', 'The Vikings are a loose coalition of hundreds of clans and tribes, the most powerful being The Warborn.');",
            "INSERT INTO Faccio (nom, resum) VALUES ('Samurais', 'The Samurai are the most unified of the three factions, though this does not say much as the Daimyos were often battling each other for dominance.');"
        };
        String[] characterData = {
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Warden', 1, 3, 1);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Conqueror', 2, 2, 1);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Peacekeep', 2, 3, 1);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Raider', 3, 3, 2);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Warlord', 2, 2, 2);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Berserker', 1, 1, 2);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Kensei', 3, 2, 3);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Shugoki', 2, 1, 3);",
            "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Orochi', 3, 2, 3);"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String faction : factionData) {
                stmt.execute(faction);
            }
            for (String character : characterData) {
                stmt.execute(character);
            }
        }
    }

    // Menu
    private static void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- PR210Honor Menu ---");
            System.out.println("1. Mostrar una taula");
            System.out.println("2. Mostrar personatges per facció");
            System.out.println("3. Mostrar el millor atacant per facció");
            System.out.println("4. Mostrar el millor defensor per facció");
            System.out.println("5. Sortir");

            System.out.print("Selecciona una opció: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> displayTable();
                case 2 -> displayCharactersByFaction();
                case 3 -> displayTopAttackerByFaction();
                case 4 -> displayTopDefenderByFaction();
                case 5 -> {
                    System.out.println("Sortint de l'aplicació.");
                    return;
                }
                default -> System.out.println("Opció no vàlida.");
            }
        }
    }

    private static void displayTable() {
        System.out.println("Introdueix el nom de la taula ('Faccio' o 'Personatge'):");
        Scanner scanner = new Scanner(System.in);
        String tableName = scanner.nextLine();

        String query = "SELECT * FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar la taula: " + e.getMessage());
        }
    }

    private static void displayCharactersByFaction() {
        System.out.print("Introdueix el nom de la facció: ");
        Scanner scanner = new Scanner(System.in);
        String factionName = scanner.nextLine();

        String query = "SELECT Personatge.nom FROM Personatge JOIN Faccio ON Personatge.idFaccio = Faccio.id WHERE Faccio.nom = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, factionName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar personatges per facció: " + e.getMessage());
        }
    }

    private static void displayTopAttackerByFaction() {
        System.out.print("Introdueix el nom de la facció: ");
        Scanner scanner = new Scanner(System.in);
        String factionName = scanner.nextLine();

        String query = "SELECT Personatge.nom, MAX(atac) as maxAtac FROM Personatge JOIN Faccio ON Personatge.idFaccio = Faccio.id WHERE Faccio.nom = ? GROUP BY Personatge.nom ORDER BY maxAtac DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, factionName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Millor atacant: " + rs.getString("nom"));
            } else {
                System.out.println("No s'ha trobat cap personatge per a aquesta facció.");
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar el millor atacant per facció: " + e.getMessage());
        }
    }

    private static void displayTopDefenderByFaction() {
        System.out.print("Introdueix el nom de la facció: ");
        Scanner scanner = new Scanner(System.in);
        String factionName = scanner.nextLine();

        String query = "SELECT Personatge.nom, MAX(defensa) as maxDefensa FROM Personatge JOIN Faccio ON Personatge.idFaccio = Faccio.id WHERE Faccio.nom = ? GROUP BY Personatge.nom ORDER BY maxDefensa DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, factionName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Millor defensor: " + rs.getString("nom"));
            } else {
                System.out.println("No s'ha trobat cap personatge per a aquesta facció.");
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar el millor defensor per facció: " + e.getMessage());
        }
    }

    public static Path obtenirPathFitxer() {
        return Paths.get(System.getProperty("user.dir"), "data", "bones_practiques_programacio.txt");
    }

    public static List<String> readFileContent(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }
}
