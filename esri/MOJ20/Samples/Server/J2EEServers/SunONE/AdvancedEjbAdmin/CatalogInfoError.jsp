<%@ page language="java" buffer="8kb" isErrorPage="true" %>
<%
String message = (String)request.getAttribute("message");
//System.out.println("CatalogInfoError.jsp " + message);
if (message == null) message = "Unknown error";
%>
<HTML>
<HEAD>
<TITLE>
MOJ Server Error Page
</TITLE>
</HEAD>
<BODY>
<p>
<b><%= message %>    </b>
</p>
<FORM>
<input type='hidden' name='canRemoveRefresh' value='false'>
<input type='hidden' name='canAdd' value='false'>
<input type='hidden' name='cmd' value=''>
</FORM>
</BODY>
</HTML>
