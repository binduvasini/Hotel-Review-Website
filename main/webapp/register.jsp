<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="css/mystyle.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<title>User Registration</title>
</head>
<body>
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="index.jsp">Hotels Website</a>
			</div>
		</div>
	</nav>
	<form class="form-horizontal" action="register" method="post">
	<%
				String status = "";
				if(request.getParameter("status")!=null){
					status = (String)request.getParameter("status");
						%><div class="alert alert-danger alert-dismissable">
						<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
						<%=status%>
					</div>
					<%
				}
				
				%>
		<div class="form-group">
			<label class="control-label col-sm-2" for="email">Username:</label>
			<div class="col-sm-10">
				<input type="text" class="form-control" name="user" size="30" style="width:200px;" placeholder="Enter username">
			</div>
		</div>
		<div class="form-group">
			<label class="control-label col-sm-2" for="pwd">Password:</label>
			<div class="col-sm-10">
				<input type="password" class="form-control" name="pass" size="30" style="width:200px;" placeholder="Enter password">
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="submit" class="btn btn-default">Register</button>
			</div>
		</div>
	</form>
</body>
</html>