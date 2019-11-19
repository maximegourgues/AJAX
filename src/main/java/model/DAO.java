package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class DAO {

	private final DataSource myDataSource;

	/**
	 *
	 * @param dataSource la source de données à utiliser
	 */
	public DAO(DataSource dataSource) {
		this.myDataSource = dataSource;
	}

	/**
	 * ventes par client
	 *
	 * @return Une Map associant le nom du client à son chiffre d'affaires
	 * @throws SQLException
	 */
	public Map<String, Double> salesByCustomer() throws SQLException {
		Map<String, Double> result = new HashMap<>();
		String sql = "SELECT NAME, SUM(PURCHASE_COST * QUANTITY) AS SALES" +
		"	      FROM CUSTOMER c" +
		"	      INNER JOIN PURCHASE_ORDER o ON (c.CUSTOMER_ID = o.CUSTOMER_ID)" +
		"	      INNER JOIN PRODUCT p ON (o.PRODUCT_ID = p.PRODUCT_ID)" +
		"	      GROUP BY NAME";
		try (Connection connection = myDataSource.getConnection(); 
		     Statement stmt = connection.createStatement(); 
		     ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				// On récupère les champs nécessaires de l'enregistrement courant
				String name = rs.getString("NAME");
				double sales = rs.getDouble("SALES");
				// On l'ajoute à la liste des résultats
				result.put(name, sales);
			}
		}
		return result;
	}
	public List<DiscountCode> allCodes() throws SQLException {

		List<DiscountCode> result = new LinkedList<>();

		String sql = "SELECT * FROM DISCOUNT_CODE ORDER BY DISCOUNT_CODE";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("DISCOUNT_CODE");
				float rate = rs.getFloat("RATE");
				DiscountCode c = new DiscountCode(id, rate);
				result.add(c);
			}
		}
		return result;
	}
        
	
}
