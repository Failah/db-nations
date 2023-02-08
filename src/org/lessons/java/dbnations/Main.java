package org.lessons.java.dbnations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws SQLException {
		String url = "jdbc:mysql://localhost:3306/nations";
		String user = "root";
		String password = "root";
		Scanner s = new Scanner(System.in);

		try (Connection con = DriverManager.getConnection(url, user, password)) {
			String sql = "select c.country_id , c.name as country_name , r.name as region_name , c2.name as continent "
					+ "from countries c " + "inner join regions r " + "on c.region_id = r.region_id "
					+ "inner join continents c2 " + "on r.continent_id = c2.continent_id " + "order by c.name";

			try (PreparedStatement ps = con.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					System.out.println("COUNTRY ID\t\tCOUNTRY NAME\t\t\t\t\tREGION NAME\t\t\t\t\tCONTINENT");
					while (rs.next()) {
						System.out.printf("%d\t\t\t%-20s\t\t%-20s\t\t%-20s%n", rs.getInt("country_id"),
								rs.getString("country_name"), rs.getString("region_name"), rs.getString("continent"));
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}
}
