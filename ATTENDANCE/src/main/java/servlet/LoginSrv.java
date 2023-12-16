package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import process.LonginTtableOut;

/**
 * Servlet implementation class LoginSrv
 */
// 어노테이션 기능으로 이전에는 톰캣 web.xml 에 Servlet 이라고 선언해야 했는데 버전업 되면서 아래와 같이 쉬워짐
// URL http://43.201.59.250:8080/ATTENDANCE/Login 이라고 호출하면 본 클래스가 수행됨
@WebServlet("/Login")
public class LoginSrv extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginSrv() {
        super();
        // TODO Auto-generated constructor stub    
    } 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    // WEB 호출시 GET 방식 (전달하고자 하는 항목을 헤더에 넣는 방식)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	// WEB 호출시 POST 방식 (전달하고자 하는 항목을 바디에 항목으로 넣는 방식)
	// 우리는 POST 방식으로 JSON 문자열을 전송하고 받음
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		// 변수선언
		String userId=""; 		// 사용자ID
		String userType="";		// 학생구분
		String userPasswrd="";	// 비밀번호
		
		StringBuffer jb = new StringBuffer();
		String line = null;
		
		System.out.println("start");
		
		// JSON 사용선언
		JSONObject resltObj = new JSONObject();
		
		// UTF8 설정
		request.setCharacterEncoding("utf8");
		response.setContentType("application/x-json; charset=UTF-8");
		
		// 입력된 값이 끝까지 다 읽어들임
		BufferedReader reader = request.getReader();
		while (( line = reader.readLine()) != null )
		{
			// 문자열에 계속 추가
			jb.append(line);
		}
		// 문자열을 JSON 으로 변환
		JSONObject jobj = JSONObject.fromObject(jb.toString());
		
		// JSON 에서 전달된 항목을 추출
        // for(int i=0;i<jsonArr.size();i++){
		userId = (jobj.get("id") == null) ? "" : jobj.get("id").toString();
		userType = (jobj.get("userType") == null) ? "" : jobj.get("userType").toString();
		userPasswrd = (jobj.get("pass") == null) ? "" : jobj.get("pass").toString();
		
		// 화면에 보여주기
		System.out.println("userId :".concat(userId));
		System.out.println("userPasswrd :".concat(userPasswrd));
		System.out.println("userType :".concat(userType));
		
		// 항목을 인자로 해서 주처리 클래스 호출
		LonginTtableOut longinTtableOut = new LonginTtableOut(userId,userType,userPasswrd);
		
		// 호출 처리결과 JSON 을 호출한 클래스로 부터 가져오기
		resltObj = longinTtableOut.getResult();
		
		//resltObj = jobj;
		
		System.out.println("resltObj :".concat(resltObj.toString()));
		
		// 응답으로 클라이언트에 뿌리기
		response.getWriter().print(resltObj);
		
		
	}

}
