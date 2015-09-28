<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb"%>

<%
java.util.HashMap properties = new java.util.HashMap();
properties.put("MapPage", "/map.jsp");
properties.put("PrintPage", "/print.jsp");
properties.put("SelectPage", "/select.jsp");
properties.put("QueryPage", "/queryForm.jsp");
properties.put("ErrorPage", "/errorpage.jsp");
properties.put("PingPage", "/versionpage.jsp");
properties.put("ServiceListPage", "/serviceList.jsp");
properties.put("OutputURL", "/output");

String cmd = request.getParameter("Cmd");
if (cmd == null) cmd = request.getParameter("cmd");
System.out.println("================> JSPViewerController: cmd=" + cmd);

if (cmd == null) {
	System.out.println("cmd not found");
 	response.getWriter().println("cmd can't be null");
     	response.getWriter().flush();
      return;
}

else if (cmd.equalsIgnoreCase("Ping")) {
	System.out.println("ping " + (String)properties.get("PingPage"));
 	response.getWriter().println("JSPViewerController 1.0");
      response.getWriter().flush();
	return;
} 

com.esri.svr.map.BaseMapper baseMapper = new com.esri.svr.map.BaseMapper();
String serviceName = request.getParameter("ServiceName");
com.esri.svr.map.ServletMapper mapper = new com.esri.svr.map.ServletMapper(properties, application, baseMapper, serviceName);
mapper.doGetPost(request, response); 
%>
