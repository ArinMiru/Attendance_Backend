package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import process.AttendanceIns;

/**
 * Servlet implementation class LoginSrv
 */
//어노테이션 기능으로 이전에는 톰캣 web.xml 에 Servlet 이라고 선언해야 했는데 버전업 되면서 아래와 같이 쉬워짐
//URL http://43.201.59.250:8080/ATTENDANCE/Attendance 이라고 호출하면 본 클래스가 수행됨
@WebServlet("/Attendance")
public class AttendanceSrv extends HttpServlet {
	private static final long serialVersionUID = 1L; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AttendanceSrv() {
        super();
        // TODO Auto-generated constructor stub    
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		StringBuffer jb = new StringBuffer(); // 문자열 버퍼를 담을 변수
		String line = null; // string 문자열만 저장하는 용도
		
		JSONObject resltObj = new JSONObject(); // json 선언하는 것
		
		request.setCharacterEncoding("utf8"); //받을 때로 utf-8(한글코드)로 받겠다
		response.setContentType("application/x-json; charset=UTF-8"); // 응답을 json의 문자열로 주겠다 / 캐릭터는 utf-8(한글 코드) 한글 깨짐 방지
		
		BufferedReader reader = request.getReader();
		while (( line = reader.readLine()) != null )
		{
			jb.append(line);
		}
		
		JSONObject jobj = JSONObject.fromObject(jb.toString());
			
		System.out.println("jobj :".concat(jobj.toString()));
		
		// 항목을 인자로 해서 주처리 클래스 호출
		AttendanceIns attendanceIns = new AttendanceIns(jobj);
		
		// 호출 처리결과 JSON 을 호출한 클래스로 부터 가져오기
		resltObj = attendanceIns.getResult();
		
		System.out.println("resltObj :".concat(resltObj.toString()));
		// 응답으로 클라이언트에 뿌리기
		response.getWriter().print(resltObj);
		
		
	}

}
