package sec02.ex02;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//클라이언트가 웹 브라우저를 열어 주소창에 요청하는 주소 
//1. DB에 저장되어 있는 모든 회원정보를 조회해줘 ~
// http://localhost:8090/pro16/member/listMembers.do로 요청 
// /memebr/memberForm.do
//2. 회원가입 디자인 페이지로 이동 시켜 줘 ~~ 
// <a href="/pro13/member/memberForm.do">
// http://localhost:8090/pro16/member/memberForm.do로 요청 

//3. memberForm.jsp에서 가입할 회원정보를 입력한 후 DB에 INSERT시켜줘 ~라는 요청 
// http://localhost:8090/pro16/member/addMember.do로 요청 

@WebServlet("/member/*")	//웹브라우저에게 요청시 두 단계로 요청이 이루어집니다.

public class MemberController extends HttpServlet{

	
	MemberDAO memberDAO; 
	
	//역할 : 클라이언트가 웹브라우저로 서블릿 요청할때.. MemberController서블릿 class를 
	// 톰캣 메모리에 올릴때 호출되는 메소드 
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		
		memberDAO = new MemberDAO();
	}
	
	//GET방식으로 요청이 들어오면 호출되는 콜백메소드 
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);//다시 doHandle메소드 호출시 request와 response전달 
	}
	

	//POST방식으로 요청이 들어오면 호출되는 콜백메소드 
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);//다시 doHandle메소드 호출시 request와 response전달 

	}
	

	//클라이언트가 GET방식 또는 POST방식으로 요청이 들어오면 호출되는 콜백메소드 
	protected void doHandle(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//request영역에 데이터가 존재하면 한글처리 
		request.setCharacterEncoding("UTF-8");
		//클라이언트의 웹브라우저로 내보낼(응답할, 출력할) 응답데이터 형식 MIME-TYPE을 response객체에 설정 
		response.setCharacterEncoding("text/html;charset=uft-8");
		
		//request 객체의 getPathInfo()메소드를 호출하여 클라이언트가 요청한 주소 URL정보를 얻는다.
		String action = request.getPathInfo(); 
		System.out.println(action);	//listMembers.do <---모든 회원정보 조회 요청주소 
										//memberForm.do  <-회원가입 작성페이지 이동 요청주소 
										// /addMembers.do <--DB에 입력한 회원정보 추가 요청 주소 
										// /listMembers.do <--요청 주소 
		String nextPage = null; // <---- 뷰 경로를 저장할 변수 
		
		
		//action변수의 값에 따라 if문을 분기해서 요청한 작업을 수행하는데..
		
		//만약 action변수의 값이 null 이거나 /listMembers.do인경우에 모든 회원정보 조회 요청이 들어왔을때를 말함
		if(action == null || action.equals("/listMembers.do")) {
			List<MemberVO> membersList = memberDAO.listMembers();
			
			request.setAttribute("membersList", membersList);
			
			//검색한 회원정보들(ArrayList)의 데이터들을 보여줄 VIEW페이지 주소 설정 
			nextPage = "/test02/listMembers.jsp";
			//DB에 입력한 회원정보를 추가 시켜줘 ~~라는 요청 주소를 받았을때..
		}else if(action.equals("/addMember.do")) {
		
			//요청한 값 얻기(입력한 회원정보들 얻기)
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			
			//MemberVO에 임시로 저장 
			MemberVO memberVO = new MemberVO(id,pwd,name,email);
			
			//요청한 회원정보를 DB의 테이블에 INSERT하기 위한 메소드 호출 
			memberDAO.addMember(memberVO);
			
			//DB에 회원 등록 후 다시 모든 회원정보를 검색요청(MemberController로) 위한 요청 주소를 저장 
			nextPage = "/member/listMembers.do";
	
		}else if(action.equals("/memberForm.do")) {	
			//회원가입창 화면 VIEW페이지 주소 설정 
			nextPage = "/test02/memberForm.jsp";
		}
		
		//디스패치 방식으로 포워딩(재요청해서 이동) (view로) 
		RequestDispatcher dispatche =
				 request.getRequestDispatcher(nextPage);
		
		 dispatche.forward(request, response);
		
		/*
		 * //View(listMembers.jsp)페이지로 응답할 데이터(검색한 회원정보들 ArrayList)를 전달하여
		 * //출력(응답)해야하는데... 그러기 위해서 임시로 저장할 공간은? request객체 메모리 영역이므로 //request객체에
		 * ArrayList를 저장함. request.setAttribute("membersList", membersList);
		 * 
		 * //디스패치 방식으로 포워딩(재요청해서 이동) (listMembers.jsp로.... view로.....)
		 
		 RequestDispatcher dispatche =
		 request.getRequestDispatcher("/test01/listMembers.jsp");
		 * 
		 * //실제 포워딩 dispatche.forward(request, response); //response.sendRedirect는 새로운
		 * 페이지를 요청함
		 */
	}//doHandle메소드 끝
	
}//MemberController클래스 끝 