package sec02.ex01;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//MVC중에 M의 역할을 함 

// -> DB연결 등 비즈니스로직 처리 
public class MemberDAO {

	private DataSource dataFactory;	//커넥션풀을 저장할 변수 
	private Connection conn; //커넥션을 저장할 변수 
	private PreparedStatement pstmt; // SQL문을 DB에 전송해서 실행할 객체를 저장할 변수 
	private ResultSet rs;
	
	//MemberDAO객체 생성시 생성자를 호출하게 되는데...
	//생성자를 호출하면 DataSource커넥션풀을 얻는다.
	public MemberDAO() {
		
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			//커넥션풀 얻기 
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			System.out.println("커넥션풀 얻기 실패 : " + e.getMessage());
		}
	}//생성자 끝
	
	//SQL문을 이용해 모든 회원정보를 조회한 후 그 결과를 ArrayList로 반환하는 메소드 
	public List<MemberVO> listMembers(){
		
		List<MemberVO> membersList = new ArrayList<MemberVO>(); //객체를 생성할때 제네릭 설정하면 그대로 나옴 
		//제네릭 형식을 지정안하면 Object타입을 리턴함 
		
		try {
			//커넥션풀에 저장되어 있는 커넥션객체(미리 DB와 연결을 맺고 있는 객체)를 빌려온다.
			//DB연결 dataFacotry가 커넥션 풀임 
			conn = dataFactory.getConnection();
			//SQL문 : 가입 날짜를 기준으로 내림차순 정렬 해서 검색 
			String query = "select * from t_member order by joinDate desc";
			pstmt = conn.prepareStatement(query);
			//SQL문 select구문 실행 후 검색한 결과데이터들을 테이블 구조로 ResultSet에 저장 후 얻기 
			rs= pstmt.executeQuery();
			
			while(rs.next()) {
				
				//DB로부터 검색한 회원 한명(한줄 정보)를 ResultSet객체에서 꺼내어서 
				//MemberVO객체의 각 변수에 저장 
				MemberVO memberVO = new MemberVO(rs.getString("id"), rs.getString("pwd"), rs.getString("name"), rs.getString("email"),rs.getDate("joinDate"));
				
				//ArrayList에 위의 MemberVO객체 추가 
				membersList.add(memberVO);
			}
			
			
		} catch (Exception e) {
			System.out.println("listMembers메소드 내부에서 SQL실행 예외 발생 : " + e);
		}finally {
			//자원해제 
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close(); 
				if(conn != null) conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return membersList;	//사장님에게(MemberController.java) 컨트롤러에게 반환 
	}

	//새로운 회원정보를 DB에 추가시키는 메소드 
	public void addMember(MemberVO memberVO) {
		try {
			//커넥션풀로부터 커넥션 빌려오기 
			conn = dataFactory.getConnection(); //DB연결 
			
			String query = "INSERT INTO t_member(id,pwd,name,email) VALUES(?,?,?,?)";
			
			pstmt = conn.prepareStatement(query);
			
			//?값 설정 
			pstmt.setString(1, memberVO.getId());
			pstmt.setString(2, memberVO.getPwd());
			pstmt.setString(3, memberVO.getName());
			pstmt.setString(4, memberVO.getEmail());
			//INSERT실행 
			pstmt.executeUpdate(); 
			
		}catch(Exception e) {
			System.out.println("addMember메소드 내부에서 SQL실행 오류 :" + e);
		}finally {
			try {
				if(pstmt != null) pstmt.close(); 
				if(conn != null) conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	
}//클래스 끝 
