package db;

// Java Imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class SpeciesChangeListDAO {

    private SpeciesChangeListDAO() {
    }

    public static void createEntry(int eco_id, int species_id, int biomass) throws SQLException {
        String query_1 = "SELECT * FROM `eco_species_change` WHERE `eco_id` = ? AND `species_id` = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = GameDB.getConnection();
            pstmt = con.prepareStatement(query_1);
            pstmt.setInt(1, eco_id);
            pstmt.setInt(2, species_id);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                pstmt.close();

                String query_2 = "INSERT INTO `eco_species_change` (`eco_id`, `species_id`, `biomass`) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(query_2);
                pstmt.setInt(1, eco_id);
                pstmt.setInt(2, species_id);
                pstmt.setInt(3, biomass);
            } else {
                pstmt.close();

                String query_3 = "UPDATE `eco_species_change` SET `biomass` = ? WHERE `eco_id` = ? AND `species_id` = ?";
                pstmt = con.prepareStatement(query_3);
                pstmt.setInt(1, biomass);
                pstmt.setInt(2, eco_id);
                pstmt.setInt(3, species_id);
            }

            pstmt.execute();
            pstmt.close();
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
    
    public static void removeEntry(int eco_id, int... species_id) throws SQLException {
        String query = "DELETE FROM `eco_species_change` WHERE `eco_id` = ? AND `species_id` IN (";

        for (int i = 0; i < species_id.length; i++) {
            query += "?";

            if (i < species_id.length - 1) {
                query += ", ";
            } else {
                query += ")";
            }
        }

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = GameDB.getConnection();
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, eco_id);

            for (int i = 0; i < species_id.length; i++) {
                pstmt.setInt(2 + i, species_id[i]);
            }

            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    public static Map<Integer, Integer> getList(int eco_id) throws SQLException {
        Map<Integer, Integer> speciesChangeList = new HashMap<Integer, Integer>();

        String query = "SELECT * FROM `eco_species_change` WHERE `eco_id` = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = GameDB.getConnection();
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, eco_id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                speciesChangeList.put(rs.getInt("species_id"), rs.getInt("biomass"));
            }

            rs.close();
            pstmt.close();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return speciesChangeList;
    }
}
