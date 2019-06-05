package it.polito.tdp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.model.Distretto;
import it.polito.tdp.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	
	public List<Year> listAllYears(){
		String sql = "SELECT DISTINCT YEAR(e.reported_date) AS yr " + 
					"FROM `events` AS e " + 
					"ORDER BY yr ";
		
		List<Year> anni = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				anni.add(Year.of(res.getInt("yr")));
			}
			
			conn.close();
			return anni ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}
	
	
	public List<Distretto> listDistretti(Year anno){
		
		String sql = "SELECT e.district_id AS did, avg(e.geo_lat) AS lat, avg(geo_lon) AS lon " + 
				"FROM `events` AS e " + 
				"WHERE YEAR(e.reported_date) = ? " + 
				"GROUP BY did ";
		
		List<Distretto> distretti = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					distretti.add(new Distretto(res.getInt("did"), new LatLng(res.getDouble("lat"), res.getDouble("lon"))));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
				
			}
			
			conn.close();
			return distretti ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}
	
	
	
	
	
	
	

}
