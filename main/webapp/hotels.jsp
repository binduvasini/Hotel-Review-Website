<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>

<%
	//out.println(request.getAttribute("hotelAL"));
	@SuppressWarnings("unchecked")
	ArrayList<HashMap<String, Object>> resultAL = (ArrayList<HashMap<String, Object>>) request
			.getAttribute("hotelAL");
	
	//session.setAttribute("hotelAL", resultAL);
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="home.jsp">Home</a>
			</div>
			<ul class="nav navbar-nav">
				<li class="active"><a href="hotels">Hotels</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="logout"><span
						class="glyphicon glyphicon-log-in"></span> Logout</a></li>
			</ul>
		</div>
	</nav>
	<form action="hotels" method="post">

		<div class="container">
			<a class="btn btn-default" href="home.jsp"> Go Back</a>
			<%
		
			if (resultAL.size() == 0) {
				
		%><h4>No results found</h4>
			<%
			} else {
				Iterator<HashMap<String, Object>> itr = resultAL.iterator();
		%>
			<h3>Hotel</h3>
			<div class="list-group">
				<%
					while (itr.hasNext()) {
							HashMap<String, Object> resultHM = itr.next();
				%>
				<a href="hotelInfo?hotelId=<%=resultHM.get("hotelid")%>"
					class="list-group-item"><%=resultHM.get("hotelname")%></a>
				<%
					}
				%>
			</div>
			<%
			
			String viewhotel = (String) request.getAttribute("viewhotel");
			
			if(viewhotel!=null){
			if(viewhotel.equalsIgnoreCase("saved")){%>
			<br> <br>
			<button type="submit" class="btn btn-default">Clear List</button>
			<%}
			}
			%>
			<input type="hidden" name="hotelaction" value="clearhotel"> <input
				type="hidden" name="user" value="<%=session.getAttribute("user")%>">
		</div>
		<%
			}
			
		%>
	</form>
</body>
</html>