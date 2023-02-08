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
		boolean repeat = false;
		Scanner s = new Scanner(System.in);

		while (!repeat) {
			System.out.print("Insert a search word: ");
			System.out.println();
			String search = s.nextLine();

			try (Connection con = DriverManager.getConnection(url, user, password)) {
				String sql = "select c.country_id , c.name as country_name , r.name as region_name , c2.name as continent "
						+ "from countries c " + "inner join regions r " + "on c.region_id = r.region_id "
						+ "inner join continents c2 " + "on r.continent_id = c2.continent_id " + "where c.name like ? "
						+ "order by c.name";

				try (PreparedStatement ps = con.prepareStatement(sql)) {
					ps.setString(1, "%" + search + "%");
					try (ResultSet rs = ps.executeQuery()) {
						System.out.println("COUNTRY ID\t\tCOUNTRY NAME\t\t\tREGION NAME\t\t\tCONTINENT");
						while (rs.next()) {
							System.out.printf("%d\t\t\t%-20s\t\t%-20s\t\t%-20s%n", rs.getInt("country_id"),
									rs.getString("country_name"), rs.getString("region_name"),
									rs.getString("continent"));
						}
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			System.out.println();
			System.out.print("Insert the id of a country: ");
			int id = s.nextInt();
			s.nextLine();

			try (Connection con = DriverManager.getConnection(url, user, password)) {
				// -- Query per recuperare le lingue parlate in una nazione
				// select l.language , c.name as country
				// from countries c
				// inner join country_languages cl
				// on c.country_id = cl.country_id
				// inner join languages l
				// on cl.language_id = l.language_id
				// where cl.country_id = ?;
				String sqlLanguages = "select l.language , c.name as country " + "from countries c "
						+ "inner join country_languages cl " + "on c.country_id = cl.country_id "
						+ "inner join languages l " + "on cl.language_id = l.language_id " + "where cl.country_id = ?";

				try (PreparedStatement psLanguages = con.prepareStatement(sqlLanguages)) {
					psLanguages.setInt(1, id);
					try (ResultSet rsLanguages = psLanguages.executeQuery()) {
						System.out.println();
						System.out.println("Languages spoken in the country with id " + id + ":");
						while (rsLanguages.next()) {
							System.out.println(rsLanguages.getString("language"));
						}
					}
				}

				// Query per recuperare le statistiche più recenti per una country
				// select cs.country_id , cs.year , cs.population , cs.gdp
				// from country_stats cs
				// where cs.country_id = ?
				// order by cs.year desc
				// limit 1;
				String sqlStatistics = "select cs.country_id , cs.year , cs.population , cs.gdp "
						+ "from country_stats cs " + "where cs.country_id = ? " + "order by cs.year desc " + "limit 1";

				try (PreparedStatement psStatistics = con.prepareStatement(sqlStatistics)) {
					psStatistics.setInt(1, id);
					try (ResultSet rsStatistics = psStatistics.executeQuery()) {
						if (rsStatistics.next()) {
							System.out.println();
							System.out.println("Latest statistics for the country with id " + id + ":");
							System.out.println("Year: " + rsStatistics.getInt("year"));
							System.out.println("Population: " + rsStatistics.getInt("population"));
							System.out.println("Gdp: " + rsStatistics.getBigDecimal("gdp"));
						} else {
							System.out.println("No statistics found for the country with id " + id);
						}
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			System.out.println();
			while (true) {
				System.out.print("Would you like to do another research? (y/n): ");
				try {
					String choice = s.nextLine();
					if (choice.equalsIgnoreCase("y")) {
						break;
					} else if (choice.equalsIgnoreCase("n")) {
						System.out.println();
						System.out.println("Program closed.");
						repeat = true;
						break;
					} else {
						System.out.println("Invalid input. Please enter 'y' o 'n'.");
					}
				} catch (Exception e) {
					System.out.println("Invalid input. Please enter 'y' o 'n'.");
				}
			}
		}

	}
}
