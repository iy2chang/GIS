<!-- %@ page language="java" buffer="8kb" errorPage="CatalogInfoError.jsp" % -->
<%@ page language="java" buffer="8kb"  %>

<head>
<script language="JavaScript" type="text/javascript">

var url = "/AdvancedEjbAdmin/servlet/CatalogServlet";

function showdetails(id) {
   theForm = document.forms[0];
   theForm.action = url;
   theForm.cmd.value="ShowDetails";
   theForm.name.value = id;

   //alert("show details=>" + theForm.action);

   theForm.submit();      
}

</script>
</head>
<h1>Service List</h1>

<% 
  com.esri.svr.cmn.ServiceInfo[]  servicelist = (com.esri.svr.cmn.ServiceInfo[])request.getAttribute("servicelist");

  if (servicelist ==null || servicelist.length == 0) {
    out.println ("<p>There are no services currently deployed.</p>");
  } else {
    out.println ("<p>Here are the deployed services (select one to see");
    out.println ("details)</p>");
    %>
    <ul>
    <%
    for (int i = 0; i < servicelist.length; i++) {
    %>
      <li><a href="JavaScript:showdetails('<%=servicelist[i].getServiceName()%>')"><%= servicelist[i].getServiceName()%></li>
    <%
    }
    %>
    </ul>
    <%
  }
%>
<form>
<input type="hidden" name="cmd" value="">
<input type="hidden" name="name" value="">
<input type="hidden" name="canRemoveRefresh" value="false">
<input type='hidden' name='canAdd' value='false'>
</form>