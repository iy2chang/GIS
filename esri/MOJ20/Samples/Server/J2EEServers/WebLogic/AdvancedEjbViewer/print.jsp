<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % --> 
<%@ page language="java" buffer="8kb"  %>

<%
String imageUrl = "";
String imageWidth = "100";
String imageHeight = "100";

if (request.getAttribute("image.width") != null)  imageWidth  = (String)request.getAttribute("image.width"); 
if (request.getAttribute("image.height") != null) imageHeight = (String)request.getAttribute("image.height");       
if (request.getAttribute("image.url") != null)    imageUrl    = (String)request.getAttribute("image.url");    
%>

<html><meta http-equiv="Content-Type" content="text/html" />
<head>
<title>Layout Map</title>
</head>
<center>
<body BGCOLOR="White" TEXT="Black" LEFTMARGIN=0 TOPMARGIN=0>
<FONT FACE="Arial"><B>
<TABLE WIDTH="650" BORDER="2" CELLSPACING="0" CELLPADDING="0" NOWRAP>
<TR>
	<TD WIDTH="<%= imageWidth %>" HEIGHT="<%= imageHeight %>" >
		<IMG SRC="<%= imageUrl %>" WIDTH='<%= imageWidth %>' HEIGHT='<%= imageHeight %>' HSPACE=0 VSPACE=0 BORDER=0 ALT="">
	</TD>
</TR>
</TABLE>
</B></FONT>
