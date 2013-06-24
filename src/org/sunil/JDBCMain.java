package org.sunil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCMain {

	public static void main(String []args){
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement psmt= null;
		CallableStatement pCall = null;
		try {
			//get connection object
			conn = JDBC.getInstance();

			String sql = "Select * from Category";

			/**
			 * NOTES:
			 * 
			 * Statement object is used to execute static sql. 
			 * execute() method returns boolean and this cannot be used with PreparedStatement or Callable statement.
			 * executeUpdate(sql) methods returns number of rows that got affected. It can used with insert, update or delete.
			 * executeQuery(sql) method returns resultset after executing sql statement.
			 * This can be used with Select query
			 **/

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			while(rs.next()){
				System.out.println(rs.getString("cat_name"));
			}

			String updateSql = "Update circle set name = ? where id = ?";
			/**
			 * PreparedStatement interface extends Statement interface.
			 * Advantage of preparedStatement over Statement is that preparedStatement 
			 * provides capability of supplying arguments dynamically.
			 * SQL statement is precompiled and stored in PreparedStatement object
			 *  
			 */
			psmt  = conn.prepareStatement(updateSql);

			psmt.setString(1, "Circle2");
			psmt.setInt(2, 2);
			psmt.addBatch();

			
			psmt.setString(1, "Circle3");
			psmt.setInt(2, 3);
			psmt.addBatch();

			psmt.setString(1, "Circle4");
			psmt.setInt(2, 4);
			psmt.addBatch();

			psmt.setString(1, "Circle5");
			psmt.setInt(2, 5);
			psmt.addBatch();


			int []numberOfRowsAffected = psmt.executeBatch();


			for(int i :numberOfRowsAffected){
				System.out.println( i);
			}

			/**
			 * 
			 */
			String prepareSql = "{call getCircle (?, ?)}";
			pCall = conn.prepareCall(prepareSql);
			pCall.setInt(1, 5);
			pCall.registerOutParameter(2, java.sql.Types.VARCHAR);
			pCall.execute();

			String name = pCall.getString(2);
			System.out.println("Circle name is "+ name);


		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}finally{

			try {
				if(stmt!=null)
					stmt.close();

				if(psmt!=null)
					psmt.close();

				if(conn!=null)
					conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}



class JDBC{
	private static Connection conn = null;

	private JDBC(){	}

	public static synchronized Connection getInstance() throws ClassNotFoundException, SQLException{
		String url = "jdbc:oracle:thin:@//localhost:1521/XE";
		if(conn!=null){
			return conn;
		}else{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "USERNAME", "PASSWORD");
		}
		return conn;
	}
}
