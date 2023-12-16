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
//������̼� ������� �������� ��Ĺ web.xml �� Servlet �̶�� �����ؾ� �ߴµ� ������ �Ǹ鼭 �Ʒ��� ���� ������
//URL http://43.201.59.250:8080/ATTENDANCE/Attendance �̶�� ȣ���ϸ� �� Ŭ������ �����
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
		
		StringBuffer jb = new StringBuffer(); // ���ڿ� ���۸� ���� ����
		String line = null; // string ���ڿ��� �����ϴ� �뵵
		
		JSONObject resltObj = new JSONObject(); // json �����ϴ� ��
		
		request.setCharacterEncoding("utf8"); //���� ���� utf-8(�ѱ��ڵ�)�� �ްڴ�
		response.setContentType("application/x-json; charset=UTF-8"); // ������ json�� ���ڿ��� �ְڴ� / ĳ���ʹ� utf-8(�ѱ� �ڵ�) �ѱ� ���� ����
		
		BufferedReader reader = request.getReader();
		while (( line = reader.readLine()) != null )
		{
			jb.append(line);
		}
		
		JSONObject jobj = JSONObject.fromObject(jb.toString());
			
		System.out.println("jobj :".concat(jobj.toString()));
		
		// �׸��� ���ڷ� �ؼ� ��ó�� Ŭ���� ȣ��
		AttendanceIns attendanceIns = new AttendanceIns(jobj);
		
		// ȣ�� ó����� JSON �� ȣ���� Ŭ������ ���� ��������
		resltObj = attendanceIns.getResult();
		
		System.out.println("resltObj :".concat(resltObj.toString()));
		// �������� Ŭ���̾�Ʈ�� �Ѹ���
		response.getWriter().print(resltObj);
		
		
	}

}
