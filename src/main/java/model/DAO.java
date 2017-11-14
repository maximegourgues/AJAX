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
	 * Liste des clients localisés dans un état des USA
	 *
	 * @param state l'état à rechercher (2 caractères)
	 * @return la liste des clients habitant dans cet état
	 * @throws SQLException
	 */
	public List<CustomerEntity> customersInState(String state) throws SQLException {
		List<CustomerEntity> result = new LinkedList<>();
		// Une requête SQL paramétrée
		String sql = "SELECT * FROM CUSTOMER WHERE STATE = ?";
		try (Connection connection  = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, state);			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					// On récupère les champs nécessaires de l'enregistrement courant
					int id = rs.getInt("CUSTOMER_ID");
					String name = rs.getString("NAME");
					String address = rs.getString("ADDRESSLINE1");
					// On crée l'objet entité
					CustomerEntity c = new CustomerEntity(id, name, address);
					// On l'ajoute à la liste des résultats
					result.add(c);
				}
			}
		}		
		return result;
	}

	/**
	 * Liste des états des USA présents dans la table CUSTOMER
	 *
	 * @return la liste des états
	 * @throws SQLException
	 */
	public List<String> existingStates() throws SQLException {
		List<String> result = new LinkedList<>();
		String sql = "SELECT DISTINCT STATE FROM CUSTOMER ORDER BY STATE";
		try ( Connection connection = myDataSource.getConnection(); 
		      Statement stmt = connection.createStatement(); 
		      ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				// On récupère les champs nécessaires de l'enregistrement courant
				String state = rs.getString("STATE");
				// On l'ajoute à la liste des résultats
				result.add(state);
			}
		}
		return result;
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
	
}
