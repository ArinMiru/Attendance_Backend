package process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LonginTtableOut {    
	
	private JSONArray jary = new JSONArray();
	private JSONObject jobj = new JSONObject();
	
	public LonginTtableOut (String userId,String userType,String userPasswrd) {
		
		// SQL 연결을 위한 기본 구성 선언
		// 연결관리
		Connection conn=null;
		// sql 믄 수행관리
		Statement stmt=null;
		// 다이나믹 sql  관리
		PreparedStatement pstmt=null;
		// sql 결과 set 
		ResultSet rs = null;
		
		String sql="";
		
		try {
			//Class.forName("com.mariadb.jdbc.Driver");

			// 톰캣의 context.xml 에 선언된 jdbc/mariadb 정보 가져오기
			Context initCtx= new InitialContext();
			Context envCtx= (Context)initCtx.lookup("java:comp/env");
			
			DataSource ds=(DataSource)envCtx.lookup("jdbc/mariadb");
			//DataSource ds=(DataSource)initCtx.lookup("java:comp/env/jdbc/DREAM");
			
			//JSONObject jobj = new JSONObject();
				
			// context.xml 애서 가져온 정보로 db  연결
			conn = ds.getConnection();
			//conn = DriverManager.getConnection("jdbc:mysql://3.35.48.70","root","Kdw98739873!");  

			// SQL 전송을 위한 창 열기
			stmt=conn.createStatement();
			// 전자출결 관리 데이터베이스 선택
			stmt.execute("use ATTENDANCE");
			
			////////////////////////////////////////////////////////////////////////////
			// 아이디,비밀번호 조회 결과 추출
			// sql 조합
			// ? 는 인자값으로 변환됨
			sql = "SELECT IF(USER_PASSWRD = ?,'Y','N') AS RSLT_CD, USER_DEP_CD, USER_DEP_NM, USER_NM "
				+ "FROM PT_USERS "
				+ "WHERE USER_ID = ? AND USER_TYPE = ? ";
			
			pstmt = conn.prepareStatement(sql);
			// 순서대로 인자값 대입
			pstmt.setString(1, userPasswrd);
			pstmt.setString(2, userId);
			pstmt.setString(3, userType);
			
			System.out.println(pstmt.toString());

			// sql 수행 SELECT 경우는 executeQuery
			rs = pstmt.executeQuery();
			// 결과 fetch
			rs.next();
			// 메타선언 (컬럼명이나 자리수 등)
			ResultSetMetaData rmd = rs.getMetaData();
			// rsltCd 항목 추가
			// 컬럼명으로 JSON 항목 및 값을 구성
			jobj.put(rmd.getColumnName(1), rs.getString(rmd.getColumnName(1)));
			jobj.put(rmd.getColumnName(2), rs.getString(rmd.getColumnName(2)));
			jobj.put(rmd.getColumnName(3), rs.getString(rmd.getColumnName(3)));
			jobj.put(rmd.getColumnName(4), rs.getString(rmd.getColumnName(4)));
			// userId 항목 추가
			jobj.put("userId", userId);
			// userType 항목 추가
			jobj.put("userType", userType);
			// userPasswrd 항목 추가 ( 보안을 위해 수록하지 않음 )
			jobj.put("userPasswrd", userPasswrd);

			// 연결자 선언한거 닫아주시
			if(rs!=null) rs.close();
			if(pstmt!=null) pstmt.close();
			
			// id/비번이 맞으면
			if (rs.getString(rmd.getColumnName(1)).equals("Y"))
			{
			// 아래 SQL 추가 수행 ( 시간표)
			////////////////////////////////////////////////////////////////////////////
			// 시간표 정보 추출
			sql = "SELECT "
			+ "X.TT_ORDER " 
			+ ",X.TT_TIME_START "
			+ ",X.TT_TIME_END "
			+ ",X.TT_COURSE_NM "
			+ ",X.TT_TEACH_NM "
			+ ",X.TT_CLASS_ROOM "
			+ ",X.TT_STAFF_NUM "
			+ ",X.TT_CLASS_NO "
			+ ",X.TT_UUID1 "
			+ ",X.TT_UUID2 "
			+ ",X.TT_UUID3 "
			+ ",X.TT_UUID4 "
			+ ",X.TT_UUID5 "
			+ ",X.TT_UUID6 "
			+ ",CASE WHEN Y.ENTER_DT IS NULL AND DATE_FORMAT(CURTIME(),'%H:%i') < DATE_FORMAT(X.TT_TIME_START,'%H:%i') THEN '준비' "
			+ "   WHEN Y.ENTER_DT IS NULL AND DATE_FORMAT(CURTIME(),'%H:%i') > DATE_FORMAT(X.TT_TIME_END,'%H:%i') THEN '결석' "
			+ "	  WHEN Y.ENTER_DT IS NULL AND DATE_FORMAT(CURTIME(),'%H:%i') BETWEEN DATE_FORMAT(X.TT_TIME_START,'%H:%i') AND DATE_FORMAT(X.TT_TIME_END,'%H:%i') THEN '지각' " 
		    + "   WHEN DATE_FORMAT(Y.ENTER_DT,'%H:%i') > DATE_FORMAT(X.TT_TIME_END,'%H:%i') THEN '결석'  "
		    + "   WHEN DATE_FORMAT(Y.ENTER_DT,'%H:%i') BETWEEN DATE_FORMAT(X.TT_TIME_START,'%H:%i') AND DATE_FORMAT(X.TT_TIME_END,'%H:%i') THEN '지각'  "
		    + "   WHEN DATE_FORMAT(Y.ENTER_DT,'%H:%i') < DATE_FORMAT(X.TT_TIME_START,'%H:%i') THEN '출석' "
			+ "END GUBUN "
			+ ",X.TT_DAY "
			+ "FROM PT_TTABLE X LEFT OUTER JOIN PT_ATTENDANCE Y "
			+ "ON  Y.USER_ID = X.USER_ID AND Y.USER_TYPE = X.USER_TYPE AND Y.CLASS_NO = X.TT_CLASS_NO " 
			+ "AND Y.TT_ORDER = X.TT_ORDER  "
			+ "AND Y.TT_DAY = X.TT_DAY "
			+ "AND DATE_FORMAT(Y.CHK_DT,'%Y-%m-%d') = DATE_FORMAT(CURDATE(),'%Y-%m-%d')   "
			+ "WHERE X.USER_ID = ? AND X.USER_TYPE = ? "
			+ "AND X.TT_DAY = SUBSTR(_UTF8'일월화수목금토',DAYOFWEEK(CURDATE()),1) "    
			+ "ORDER BY X.TT_ORDER ";
				
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, userType);
			
			System.out.println(pstmt.toString());

			rs = pstmt.executeQuery();
			
			// 메타선언
			rmd = rs.getMetaData();
			
            while(rs.next()) 
            {
            	// // 컬럼명으로 JSON 항목 및 값을 구성
            	JSONObject jo=new JSONObject();
				jo.put(rmd.getColumnName(1), rs.getInt(rmd.getColumnName(1)));
				jo.put(rmd.getColumnName(2), rs.getString(rmd.getColumnName(2)));
				jo.put(rmd.getColumnName(3), rs.getString(rmd.getColumnName(3)));
				jo.put(rmd.getColumnName(4), rs.getString(rmd.getColumnName(4)));
				jo.put(rmd.getColumnName(5), rs.getString(rmd.getColumnName(5)));
				jo.put(rmd.getColumnName(6), rs.getString(rmd.getColumnName(6)));
				jo.put(rmd.getColumnName(7), rs.getInt(rmd.getColumnName(7)));
				jo.put(rmd.getColumnName(8), rs.getString(rmd.getColumnName(8)));
				jo.put(rmd.getColumnName(9), rs.getString(rmd.getColumnName(9)));
				jo.put(rmd.getColumnName(10), rs.getString(rmd.getColumnName(10)));
				jo.put(rmd.getColumnName(11), rs.getString(rmd.getColumnName(11)));
				jo.put(rmd.getColumnName(12), rs.getString(rmd.getColumnName(12)));
				jo.put(rmd.getColumnName(13), rs.getString(rmd.getColumnName(13)));
				jo.put(rmd.getColumnName(14), rs.getString(rmd.getColumnName(14)));
				jo.put(rmd.getColumnName(15), rs.getString(rmd.getColumnName(15)));
				jo.put(rmd.getColumnName(16), rs.getString(rmd.getColumnName(16)));
			
				jary.add(jo);
		    }
            
			} // if
			
			// 시간표는 TT_LIST 라는 대표명으로 배열 형태로 제공
			jobj.put("TT_LIST", jary);

	    } catch(SQLException se) {
	    	se.printStackTrace();
	    } catch(Exception e) {
			e.printStackTrace();
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
		// 결과가뵤 리턴
		return jobj;
	}

}