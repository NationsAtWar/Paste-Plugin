package org.nationsatwar.paste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.plugin.PluginBase;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.nationsatwar.paste.Request.RequestStatus;
import org.nationsatwar.paste.Request.RequestType;

public class RequestHandler {
	private PluginBase plugin = null;
	private static final String SQL_DRIVER = "com.mysql.jdbc.Driver";
	private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public RequestHandler(PluginBase instance) {
		this.plugin = instance;
	}
	
	public boolean createTable() {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "CREATE TABLE IF NOT EXISTS `"+db_table+"` ("+
					"`Id` int(11) NOT NULL AUTO_INCREMENT,"+
					" `Time` datetime NOT NULL," +
					" `Type` text NOT NULL," +
					" `Status` text NOT NULL," +
					" `User` text NOT NULL," +
					" `Data` text NOT NULL," +
					" PRIMARY KEY (`Id`)" +
					" ) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=19 ;";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			
			plugin.getLogger().info("SQL: "+statm.toString());
			statm.executeUpdate();
			
			conn.close();
			conn = null;
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean testConnection() {
		boolean val = false;
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");

		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			if(conn.isValid(10)) {
				val = true;
			}
			
			conn.close();
			conn = null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}
	
	public boolean testTable() {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			ResultSet tables = conn.getMetaData().getTables(null, null, db_table, null);
			if(tables.next()) {
				return true;
			}
			conn.close();
			conn = null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean sendRequest(Request request) {
		String db_name = plugin.getConfig().getString("mysql.web.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.web.url") + ":" + plugin.getConfig().getString("mysql.web.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.web.user");
		String db_pass = plugin.getConfig().getString("mysql.web.pass");
		String db_table = plugin.getConfig().getString("mysql.web.table");
		
		String sql = "INSERT INTO `"+db_table+"` (`Time`, `Type`, `Status`, `User`, `Data`) VALUES (?,?,?,?,?);";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			plugin.getLogger().info("TIME: "+new SimpleDateFormat(MYSQL_DATE_FORMAT).format(request.Time));
			statm.setString(1, new SimpleDateFormat(MYSQL_DATE_FORMAT).format(request.Time));
			statm.setString(2, request.Type.toString());
			statm.setString(3, RequestStatus.SENT.toString());
			statm.setString(4, request.User);
			statm.setString(5, request.Data.toJSONString());
			plugin.getLogger().info("SQL: "+statm.toString());
			statm.execute();
			
			conn.close();
			conn = null;
			
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLTimeoutException e) {
			if(this.cacheRequest(request)) {
				return true;
			}
			this.plugin.getLogger().warning("Request cached.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(this.cacheRequest(request)) {
				return true;
			}
			this.plugin.getLogger().warning("Got a DB exception, but it worked okay in the cache.");
		}
		return false;
	}
	
	private boolean cacheRequest(Request request) {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "INSERT INTO `"+db_table+"` (`Time`, `Type`, `Status`, `User`, `Data`) VALUES (?,?,?,?,?);";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			plugin.getLogger().info("TIME: "+new SimpleDateFormat(MYSQL_DATE_FORMAT).format(request.Time));
			statm.setString(1, new SimpleDateFormat(MYSQL_DATE_FORMAT).format(request.Time));
			statm.setString(2, request.Type.toString());
			statm.setString(3, request.Status.toString());
			statm.setString(4, request.User);
			statm.setString(5, request.Data.toJSONString());
			plugin.getLogger().info("SQL: "+statm.toString());
			statm.execute();
			
			conn.close();
			conn = null;
			
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public Request getResponse(Request request) {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "SELECT * FROM `"+db_table+"` WHERE `Type`=? AND `Status` = '"+RequestStatus.SENT+"';";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			
			statm.setString(1, request.Type.toString());
			plugin.getLogger().info("SQL: "+statm.toString());
			ResultSet rs = statm.executeQuery();
				
			if(request.Type == RequestType.CONFIRMTOKEN) {
				while(rs.next()) {
					Object obj = JSONValue.parse(rs.getString("Data"));
					JSONObject data = (JSONObject) obj;
					if(request.User.equalsIgnoreCase((String) data.get("username"))) {
						//I don't like this at all...
						request.Data = data;
						if(request.Id ==0) {
							request.Id = rs.getInt("Id");
						}
						if(request.Time == null) {
							request.Time = rs.getDate("Time", Calendar.getInstance());
						}
						if(request.Type == null) {
							request.Type = Request.RequestType.valueOf(rs.getString("Type").trim().toUpperCase());
						}
						if(request.Status == null) {
							request.Status = Request.RequestStatus.valueOf(rs.getString("Status").trim().toUpperCase());
						}
						if(request.User == null) {
							request.User = rs.getString("User");
						}
						request.Data = (JSONObject) JSONValue.parse(rs.getString("Data"));
						break;
					}
				}
			}
			
			conn.close();
			conn = null;
			
			return request;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean setRequestStatus(Request request) {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "UPDATE `"+db_table+"` SET `Status`=? WHERE `Id`=?;";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			
			statm.setString(1, request.Status.toString());
			statm.setInt(2, request.Id);
			plugin.getLogger().info("SQL: "+statm.toString());
			int rs = statm.executeUpdate();

			
			conn.close();
			conn = null;
			
			if(rs > 0) {
				return true;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteRequest(int requestId) {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "DELETE FROM `"+db_table+"` WHERE `Id`=?;";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			
			statm.setInt(1, requestId);
			plugin.getLogger().info("SQL: "+statm.toString());
			int rs = statm.executeUpdate();

			conn.close();
			conn = null;
			
			if(rs > 0) {
				return true;
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean resendCache() {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");
		
		String sql = "SELECT * FROM `"+db_table+"` WHERE `Status` = '"+RequestStatus.UNSENT+"';";
		
		ArrayList<Request> requestList = new ArrayList<Request>();
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			plugin.getLogger().info("SQL: "+statm.toString());
			ResultSet rs = statm.executeQuery();
			
			while(rs.next()) {
				Request request = new Request(0, null, null, null, null);
				request.Id = rs.getInt("Id");
				request.Time = rs.getDate("Time", Calendar.getInstance());
				request.Type = Request.RequestType.valueOf(rs.getString("Type").trim().toUpperCase());
				request.Status = Request.RequestStatus.valueOf(rs.getString("Status").trim().toUpperCase());
				request.User = rs.getString("User");
				request.Data = (JSONObject) JSONValue.parse(rs.getString("Data"));
				requestList.add(request);
			}
			
			conn.close();
			conn = null;
			
			if(requestList.size() > 0) {
				for(Request request : requestList) {
					//break the loop to hopefully prevent massive spam traffic to the db.
					if(!this.sendRequest(request)) {
						break;
					}
					this.deleteRequest(request.Id);
				}
			}
			
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean clean() {
		String db_name = plugin.getConfig().getString("mysql.plugin.database");
		String db_url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.plugin.url") + ":" + plugin.getConfig().getString("mysql.plugin.port") + "/";
		String db_user = plugin.getConfig().getString("mysql.plugin.user");
		String db_pass = plugin.getConfig().getString("mysql.plugin.pass");
		String db_table = plugin.getConfig().getString("mysql.plugin.table");

		String sql = "DELETE FROM `"+db_table+"` WHERE `Status` = '"+RequestStatus.COMPLETED+"' OR `Status` = '"+RequestStatus.REJECTED+"'";
		
		try {
			Class.forName(SQL_DRIVER);
			Connection conn = DriverManager.getConnection( db_url + db_name, db_user, db_pass);
			PreparedStatement statm = conn.prepareStatement(sql);
			plugin.getLogger().info("SQL: "+statm.toString());
			
			statm.executeUpdate();
			
			conn.close();
			conn = null;
			
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
