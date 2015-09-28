<%@ page language="java" buffer="8kb"%>
<HTML>
<HEAD>
<TITLE>
MapObjects Java Enterprise Edition
</TITLE>
</HEAD>
<BODY>
<p>
<%
String releaseVersion = com.esri.mo2.sys.ver.Version.RELEASE_VERSION;
String buildVersion = com.esri.mo2.sys.ver.Version.BUILD;
%>
MapObjects Java Edition <br>
Version: <%= releaseVersion %> 
<br>
Build: <%= buildVersion %>
</p>
</BODY>
</HTML>
