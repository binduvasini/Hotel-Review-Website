<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>


<%
	final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
	ArrayList<HashMap<String, Object>> resultAL = dbhandler.getCityState();
	Iterator<HashMap<String, Object>> itr = resultAL.iterator();
%>

<!DOCTYPE html>
<html>
<head>
<title>Home</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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
			<form class="navbar-form navbar-left" action="hotelInfo"
				method="post">

				<div class="input-group">
					<input type="text" class="form-control" placeholder="Search Hotel"
						name="hotelname">
					<div class="input-group-btn">
						<button class="btn btn-default" type="submit">
							<i class="glyphicon glyphicon-search"></i>
						</button>
					</div>
				</div>
				<input type="hidden" name="searchoption" value="searchbyname">
			</form>
			<form class="navbar-form navbar-left" action="hotelInfo"
				method="post">
				<select class="selectpicker" style="width: 400px; margin: 10px auto"
					name="selectoption">
					<option selected disabled="disabled">Search Hotel By City,
						State</option>
					<%
						while (itr.hasNext()) {
							HashMap<String, Object> resultHM = itr.next();
					%>
					<option value="<%=resultHM.get("citystate")%>"><%=resultHM.get("citystate")%></option>
					<%
						}
					%>
				</select>
				<button type="submit" class="btn btn-default btn-sm">
					<span class="glyphicon glyphicon-search"></span>
				</button>
				<input type="hidden" name="searchoption" value="searchbycity">
			</form>
		</div>
	</nav>
	<div class="container">
		<form action="hotels">
			<p>
				Welcome
				<%=session.getAttribute("user")%></p>
				<br>
				<%
				String status = "";
				if(request.getParameter("status")!=null){
					status = (String)request.getParameter("status");
						%><div class="alert alert-info alert-dismissable">
						<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
						<%=status%>
					</div>
					<%
				}
				
				%>
				<br>
			<p>
				<button type="submit" class="btn btn-info">View All Hotels</button>
			</p>
			<input type="hidden" name="viewhotel" value="all"> <br>
		</form>
		<form action="hotels">
			<p>
				<button type="submit" class="btn btn-warning">View Saved
					Hotels</button>
			</p>
			<input type="hidden" name="viewhotel" value="saved"> <input
				type="hidden" name="user" value=<%=session.getAttribute("user")%>>
			<br> <br> <br>
			<p>
				Last Login:
				<%=session.getAttribute("logintime")%></p>
		</form>
	</div>
</body>

</html>