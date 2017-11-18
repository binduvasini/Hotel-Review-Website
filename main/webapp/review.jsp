<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>

<%
	String user = "";
	if (session.getAttribute("user") != null)
		user = (String) session.getAttribute("user");
	@SuppressWarnings("unchecked")

	ArrayList<HashMap<String, Object>> resultAL = (ArrayList<HashMap<String, Object>>) request
			.getAttribute("reviewAL");
	Iterator<HashMap<String, Object>> itr = resultAL.iterator();
	Integer currentPage = (Integer) request.getAttribute("currentPage");
	Integer noOfPages = (Integer) request.getAttribute("noOfPages");
	String sortBy = (String) request.getAttribute("sortBy");
%>

<!DOCTYPE html>
<html>
<head>
<title>Reviews</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!-- <script src="/js/review.js"></script> -->
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
	<form>
		<div class="pull-right">
			<button type="button" class="btn btn-default"
				onclick="window.location.reload()">Refresh</button>
		</div>
		<div class="container">
		
		<a class="btn btn-default" href="hotelInfo?hotelId=<%=session.getAttribute("hotelid")%>"> Go Back</a><br>
			<h3><%=request.getAttribute("hotelname")%></h3>
			<br>
			<ul class="nav nav-tabs">
				<li class="disabled disabledTab"><a href="#">Sort By</a></li>
				<li><a href="/reviews?hotelname=<%=request.getAttribute("hotelname")%>&sortby=date">Date</a></li>
				<li><a href="/reviews?hotelname=<%=request.getAttribute("hotelname")%>&sortby=rating">Rating</a></li>
			</ul>
			<br>
			<table class="table table-striped">
				<tr>
					<th>User</th>
					<th>IsRecommended</th>
					<th>Review Title</th>
					<th>Review</th>
					<th>Rating</th>
					<th>Date</th>
					<th></th><th></th>
				</tr>
				<%
					while (itr.hasNext()) {
						HashMap<String, Object> resultHM = itr.next();
				%>
				<tr>
					<td><%=resultHM.get("username")%></td>
					<td><%=resultHM.get("isrecom")%></td>
					<td><%=resultHM.get("reviewtitle")%></td>
					<td><%=resultHM.get("review")%></td>
					<td><%=resultHM.get("rating")%></td>
					<td><%=resultHM.get("date")%></td>
					<%
						if (resultHM.get("username").toString().equalsIgnoreCase(user)) {
					%>
					<td><a
						href="modifyreview.jsp?reviewId=<%=resultHM.get("reviewid")%>&hotelName=<%=request.getAttribute("hotelname")%>">Modify
							Review</a></td>
					<td><a
						href="modifyreview?deletereviewId=<%=resultHM.get("reviewid")%>&hotelName=<%=request.getAttribute("hotelname")%>">Delete Review</a></td>
					<%
						}
					%>
				</tr>
				<%
					}
				%>
			</table>
			<%
				for (int i = 1; i <= noOfPages; i++) {
			%>
			<ul class="pagination">
			<%if(sortBy!=null && !(sortBy.isEmpty())){%>
				<li><a href="/reviews?hotelId=<%=request.getAttribute("hotelId")%>&hotelname=<%=request.getAttribute("hotelname")%>&page=<%=i%>&sortby=<%=sortBy%>"><%=i%></a></li>
			<%}else{%>
				<li><a href="/reviews?hotelId=<%=request.getAttribute("hotelId")%>&hotelname=<%=request.getAttribute("hotelname")%>&page=<%=i%>"><%=i%></a></li>
			<%} %></ul>
			<%
				}
			%>
			<button type="button" class="btn btn-primary"
				onclick="diplayAddReview(<%=request.getAttribute("hotelId")%>)">Add
				Review</button>
		</div>
	</form>
	<div id='display'></div>
</body>
<script>
function diplayAddReview(hotelId) {
	$('#display').load('addreview.jsp?hotelId='+hotelId);
}

</script>
</html>