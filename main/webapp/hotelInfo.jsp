<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cs601.project.*"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>

<%
	@SuppressWarnings("unchecked")
	ArrayList<HashMap<String, Object>> resultAL = (ArrayList<HashMap<String, Object>>) request
			.getAttribute("hotelInfoAL");
	Iterator<HashMap<String, Object>> itr = resultAL.iterator();
	Double latitude = 0.0;
	Double longitude = 0.0;
%>

<!DOCTYPE html>
<html>
<head>
<title>Hotel Information</title>
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
	<form action="hotels" method="post">
		<p>
			<%
				String status = "";
				if (request.getAttribute("status") != null) {
					status = (String) request.getAttribute("status");
			%>
		
		<div class="container">
			<div class="alert alert-info alert-dismissable">
				<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
				<%=status%>
			</div>
			<%
				}
			%>
		</div>
		<%
			if (itr.hasNext()) {
				HashMap<String, Object> resultHM = itr.next();
				Object rating = resultHM.get("rating");
				latitude = (Double) resultHM.get("latitude");
				longitude = (Double) resultHM.get("longitude");
				if (rating == null)
					rating = 0.0;
		%>
		<div class="panel-group">
			<div class="panel panel-info">
				<div class="panel-heading"><%=resultHM.get("hotelname")%>
				</div>
				<div class="panel-body pull-right">
					<button type="submit" class="btn btn-success">Save Hotel</button>
				</div>
				<div class="panel-body">
					<div class="row">
						<div class="col-md-5"><p class="text-primary">
							<%=resultHM.get("address")%></p><br><br><br><p class="bg-info" style="width:200px;">Average Rating:<%=rating%></p></div>
						<div class="col-md-10" id="map"
							style="width: 350px; height: 200px; background: white"></div>
					</div>
				</div>

				<div class="panel-body pull-right">
					<a
						href="/reviews?hotelId=<%=session.getAttribute("hotelid")%>&hotelname=<%=resultHM.get("hotelname")%>">View
						Reviews</a>
				</div>
				<div class="panel-body">
					<a
						href="https://www.expedia.com/h<%=session.getAttribute("hotelid")%>.Hotel-Information">Expedia
						link</a>
				</div>
				<div class="panel-body">
					<a
						href="/touristattraction">View
						Tourist Attractions</a>
				</div>
			</div>
		</div>
		<input type="hidden" name="hotelaction" value="savehotel"> <input
			type="hidden" name="hotelid"
			value="<%=session.getAttribute("hotelid")%>"> <input
			type="hidden" name="hotelname" value="<%=resultHM.get("hotelname")%>">
		<%
			}
		%>
	</form>
<script>
function myMap() {
  var myLatLng = {lat: <%=latitude%>, lng: <%=longitude%>};
	var mapCanvas = document.getElementById("map");
  var mapOptions = {
    center: myLatLng, zoom: 10
  };
  var map = new google.maps.Map(mapCanvas, mapOptions);
  var marker = new google.maps.Marker({
      position: myLatLng,
      map: map,
    });
}
</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDYi4IPdYiIhcaD5FXwwxHFAtlJwE4yecA&callback=myMap"></script>
</body>
</html>