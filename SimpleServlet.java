package wasdev.sample.servlet;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import java.sql.ResultSet;
//import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
//import com.mysql.jdbc.PreparedStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.*;
//import com.fasterxml.jackson.databind.*;
import org.codehaus.jackson.map.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/SimpleServlet")
public class SimpleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int clid=0;
		int flag;
		String csid="";
		String output="";
		Map<String,Object> context = new HashMap<String,Object>();

		response.setContentType("text/html");
		try{
			ConversationService service = new ConversationService("2017-05-04");
			service.setUsernameAndPassword("27b3969e-1180-48b1-a1f8-1cf3883158fb", "e4qjy0yQQKCU");
			String workspaceId = "bd85076e-93bd-4b0c-8515-9e96d50e3384";     	

			String input= request.getParameter("sstr");
			System.out.println("outside"+input);
			String flagfirst= request.getParameter("fl");

			if(input!=null){

				if ("q".equals(input)) {
					System.out.println("Exit!");
					System.exit(0);
				}

				MessageRequest newMessage;
				MessageResponse response1;
				HttpSession session=request.getSession();
				if(flagfirst.equals("1")){
					System.out.println("flag="+flagfirst);
					newMessage = new MessageRequest.Builder().inputText("hi").build();

					response1=service.message(workspaceId, newMessage).execute();
					session=request.getSession();
					context = response1.getContext();
					session.setAttribute("session_context", context);
					System.out.println("inif:"+response1);
					response.getWriter().print(response1.getText().get(0));
					
					//response.getWriter().print(response1.getText().get(0));   
				}
				else
				{
				//	System.out.println("elseflag="+flagfirst);
					ObjectMapper oMapper = new ObjectMapper();
					context = oMapper.convertValue(session.getAttribute("session_context"), Map.class);
				//	context=session.getAttribute("session_context");
					newMessage = new MessageRequest.Builder().inputText(input).context(context).build();
					System.out.println("newMessage:"+ newMessage);
					response1=service.message(workspaceId, newMessage).execute();
					context = response1.getContext();
					session.setAttribute("session_context", context);
					output=response1.getText().get(0);
					
					System.out.println("fhffjf"+output);

					// newMessage = new MessageRequest.Builder().inputText(input).context(context).build();
					//newMessage will contain user input and Context till now

					//	response1 = service.message(workspaceId, newMessage).execute();  
					System.out.println("Watson Response:"+ response1);

					System.out.println("Watson Response:"+ response1.getText().get(0));
					
					//Response List's first response
				//	context = response1.getContext();

				/*	for( Object m:context.keySet()){  
						System.out.println(m.toString());  
					}*/
					if(context.get("cutoff_button").equals("on") ){

						if(context.get("college_button").equals("on") && context.get("subject_button").equals("on") && context.get("category_button").equals("on") 
								&& context.get("year_button").equals("on") && context.get("list_button").equals("on") ){
							try{
								Class.forName("com.mysql.jdbc.Driver");  
								Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Bot","root","");

								//retrieve the collegeid
								String query= "SELECT collegeid FROM College WHERE name=?;"; //check the column name
								PreparedStatement preparedStatement = con.prepareStatement(query);
								preparedStatement.setString(1, context.get("college_name").toString());	
								//	System.out.println(context.get("college_name").toString());
								boolean n= preparedStatement.execute();
								flag=0;

								if(n){
									ResultSet rs=preparedStatement.getResultSet();	
									//System.out.println(rs.next());

									while(rs.next())  {
										flag=1;
										//System.out.println(flag);
										clid= rs.getInt(1); //int will work as no decimal no change required anywhere at all 
										//	System.out.println(clid);
									}
									if(flag==0){
										output="College name invalid";

									}					
								}
								//cutoff 2016 computer Science general first dduc
								//System.out.println("working");     
								//extract course subject and program type
								String subject="";
								String progtype="";
								if(flag==1){
									String temp=context.get("subject_name").toString();
									//	System.out.println(context.get("subject_name").toString()); 
									String[] words=temp.split(" ");
									String course=words[0];
									//	System.out.println("course is"+course);
									if(course.equals("B.com.")){
										//		System.out.println("in if");	
										progtype="H";
										subject= "NA";
										course = "B.Com.";
									}
									else{
										progtype=words[1];
										subject=words[2];
										//	System.out.println(course+progtype+subject);
										int var=3;		
										while(var<words.length)
										{subject = subject+" "+words[var];
										var++;
										}
									}
									//	System.out.println(subject);


									//retrieve the courseid
									flag=0;
									query= "SELECT courseid FROM course WHERE coursename=? AND subjectname=? AND programtype=?;";
									preparedStatement = con.prepareStatement(query);
									preparedStatement.setString(1, course);
									preparedStatement.setString(2, subject);
									preparedStatement.setString(3, progtype);
									n= preparedStatement.execute();

									//ResultSet rs=stmt.executeQuery(); 
									if(n){
										ResultSet rs=preparedStatement.getResultSet();	
										while(rs.next())  {
											flag=1;
											csid=rs.getString(1);  //check the type string
										}
										if(flag==0){
											output="course name invalid";

										}
									}
								}
								//please tell me first cutoffs computer science 2016 DDUC general 
								if(flag==1){

									String selectSQL = "SELECT "+context.get("category_type").toString()+" FROM cutoff WHERE collegeid=? AND courseid= ? AND year=? AND list_no= ? ;";
									preparedStatement = con.prepareStatement(selectSQL);

									/*if(context.get("year").equals("2016"))
    							preparedStatement.setString(2, "cutoffs_2016");
    						else
    							preparedStatement.setString(2, "cutoffs_2017");*/


									//preparedStatement.setString(1,a);
									String year=context.get("year").toString();
									/*System.out.println(year);
    						 words=year.split(".");
    						 System.out.println(words[0]);*/

									String year1=year.substring(0,4);


									//	System.out.println(year1);
									String list=context.get("list_num").toString();
									String listno=list.substring(0, 1);

									preparedStatement.setInt(1, clid); 
									preparedStatement.setString(2, csid);
									preparedStatement.setString(3, year1); 
									preparedStatement.setString(4, listno);
									//System.out.println(clid+csid+year1+listno);
									n= preparedStatement.execute();

									flag=0; 

									if(n){
										ResultSet rs=preparedStatement.getResultSet();	
										while(rs.next()){ 
											if(rs.getFloat(1)==0){
												output="cutoff closed, contact college for further assistance";
											}
											else{
												output=output + rs.getFloat(1);
											}
											flag=1;
										}
										if(flag==0){
											output= "Course doesnt exist in the college";

										}  
									}
								}
								preparedStatement.close(); 
								con.close();
							}catch(Exception e){ System.out.println(e);} }	

					}
					response.getWriter().print(output);
				}





			}
		}
		finally{}


	}


}