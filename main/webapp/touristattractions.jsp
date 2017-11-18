<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.parser.JSONParser"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.json.simple.parser.ParseException"%>

<%
	
%>

<!DOCTYPE html>
<html>
<head>
<title>Tourist attractions</title>
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
		</div>
	</nav>
	<form action="touristattraction" method="post">

		<div class="container">
		
		<a class="btn btn-default" href="hotelInfo?hotelId=<%=session.getAttribute("hotelid")%>"> Go Back</a>
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
			<br><p>Please select a radius (in miles)</p>
			<label class="radio-inline">
      <input type="radio" name="radius" value="1">radius 1
    </label>
    <label class="radio-inline">
      <input type="radio" name="radius" value="2">radius 2
    </label>
    <label class="radio-inline">
      <input type="radio" name="radius" value="4">radius 3
    </label>
    <input type="hidden" name="hotelid" value="<%=session.getAttribute("hotelid")%>">
			 <button type="submit" class="btn btn-danger">Show attractions</button>
			 <%if(request.getParameter("radius")!=null){
				
				 %>
			<table class="table">
				<thead>
					<tr>
						<th>Name</th>
						<th>Address</th>
						<th>Rating</th>
					</tr>
				</thead>
				<tbody>
					<%
					
					String jsonString = (String) request.getAttribute("jsonString");
					JSONParser parser = new JSONParser();
					JSONObject attractionJsonObj;

					attractionJsonObj = (JSONObject) parser.parse(jsonString);
					JSONArray results = (JSONArray) attractionJsonObj.get("results");
					@SuppressWarnings("unchecked")
					Iterator<JSONObject> resultsItr = results.iterator();
						while (resultsItr.hasNext()) {
							JSONObject resultDetailsJsonObj = resultsItr.next();
							double rating = 0;
							if (resultDetailsJsonObj.get("rating") != null)
								rating = Double.parseDouble(resultDetailsJsonObj.get("rating").toString());
					%>
					<tr class="danger">
						<td><%=resultDetailsJsonObj.get("name").toString()%></td>
						<td><%=resultDetailsJsonObj.get("formatted_address").toString()%></td>
						<td><%=rating%></td>
					</tr>
					<%
					}
					
					%>
				</tbody>
			</table>
			<%} %>
		</div>
	</form>
</body>
</html>