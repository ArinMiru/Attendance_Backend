package process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.json.JSONObject;

public class AttendanceIns {    
	
	//private JSONArray jary = new JSONArray();
	private JSONObject jobj1 = new JSONObject();
	
	public AttendanceIns (JSONObject jobj) {
		
		Connection conn=null;
		Statement stmt=null;
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		int r_cnt=0;
		
		String sql="";
		
		try {
			//Class.forName("com.mariadb.jdbc.Driver");

			// ��Ĺ�� context.xml �� ����� jdbc/mariadb ���� ��������
			Context initCtx= new InitialContext();
			Context envCtx= (Context)initCtx.lookup("java:comp/env");
			
			DataSource ds=(DataSource)envCtx.lookup("jdbc/mariadb");
			//DataSource ds=(DataSource)initCtx.lookup("java:comp/env/jdbc/DREAM");
			
			//JSONObject jobj = new JSONObject();
			// context.xml �ּ� ������ ������ db  ����	
			conn = ds.getConnection();
			//conn = DriverManager.getConnection("jdbc:mysql://3.38.116.207","hapje010","Kdw98739873!");  

			stmt=conn.createStatement();  
			// ������� ���� �����ͺ��̽� ����
			stmt.execute("use ATTENDANCE");
			
			////////////////////////////////////////////////////////////////////////////
			// �����
			sql = "INSERT INTO PT_ATTENDANCE ( 	"
				+ "	 USER_ID 					"
				+ "	 ,USER_TYPE 				"
				+ "	 ,CLASS_NO 					"
				+ "	 ,CHK_DT 					"
				+ "	 ,TT_ORDER 					"
				+ "	 ,TT_DAY 					"
				+ "	 ,ENTER_SBST 				"
				+ "	 ,ENTER_DT 					"
				+ "	 ,SBST 						"
				+ "  ) VALUE ( 					"
				+ "   ? 						"
				+ "  ,? 						"
				+ "  ,? 						"
				+ "  ,date_format(SYSDATE(),'%Y-%m-%d') "
				+ "  ,? 						"
				+ "  ,? 						"
				+ "  ,'Y' 						"
				+ "  ,date_format(SYSDATE(),'%Y-%m-%d %T') "
				+ "  ,? 						"
				+ "  ) 							"
				+ "  ON DUPLICATE KEY UPDATE 	"
				+ "	    OUT_SBST   = CASE 		"
				+ "					   WHEN ? = 'S' THEN  OUT_SBST "
				+ "					   ELSE 'Y' "
				+ "					 END  		"
				+ "		,OUT_DT     = CASE 		"
				+ "		               WHEN ? = 'S' THEN  OUT_DT "
				+ "		               ELSE date_format(SYSDATE(),'%Y-%m-%d %T') "
				+ "		              END  		"
				+ "		,SBST       = CASE 		"
				+ "		               WHEN ? = 'S' THEN  SBST "
				+ "		               ELSE CONCAT(SBST,'|',?) "
				+ "		              END 		";
					
			pstmt = conn.prepareStatement(sql);
			// id �� null �̸� "" �ƴϸ� �ۿ��� JSON �� ���޵� input ������ ����
			pstmt.setString(1, (jobj.get("id") == null) ? "" : jobj.get("id").toString());
			pstmt.setString(2, (jobj.get("userType") == null) ? "" : jobj.get("userType").toString());
			
			pstmt.setString(3, (jobj.get("CLASS_NO") == null) ? "" : jobj.get("CLASS_NO").toString());
			pstmt.setInt(4, Integer.parseInt(jobj.get("TT_ORDER").toString()));
			pstmt.setString(5, (jobj.get("TT_DAY") == null) ? "" : jobj.get("TT_DAY").toString());
			pstmt.setString(6, (jobj.get("SBST") == null) ? "" : jobj.get("SBST").toString());
			
			pstmt.setString(7, jobj.get("TYPE").toString());
			pstmt.setString(8, jobj.get("TYPE").toString());
			pstmt.setString(9, jobj.get("TYPE").toString());
			pstmt.setString(10, (jobj.get("SBST") == null) ? "" : jobj.get("SBST").toString());
			
			System.out.println(pstmt.toString());

			// SQL ���� INSERT,UPDATE,DELET ���� executeUpdate
			// �̰�� �ݿ��� ������ ����
			r_cnt = pstmt.executeUpdate();
			
			// ��� ó���� �Ǽ��� PROC_CNT �̸�����  JSON ����
			jobj1.put("PROC_CNT", r_cnt);

	    } catch(SQLException se) {
	    	se.printStackTrace();
	    	jobj1.put("PROC_CNT", 0);
	    } catch(Exception e) {
			e.printStackTrace();
			jobj1.put("PROC_CNT", 0);
	    } finally {
	    	if (rs != null)
	    	{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	if (stmt != null)
	    	{
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    	if (pstmt != null)
	    	{
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	if (conn != null)
	    	{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	}
    
	public JSONObject getResult() {
		return jobj1;
	}

}