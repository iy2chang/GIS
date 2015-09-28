<!-- %@ page language="java" buffer="8kb" errorPage="CatalogInfoError.jsp" % -->
<%@ page language="java" buffer="8kb"  %>

<h1>Deployed Service Information</h1>
<FORM METHOD="POST" >
<% 
  String[] details = (String[])request.getAttribute("servicedetails");
  boolean canRemoveRefresh = false;

  if (details == null || details.length==0) {
    out.println ("<p>No more details available</p>");
  } else {
    out.println ("<table column='2' border='0' bordercolor='yellow' valign='top'>");
    out.println ("<tr>");
    out.println ("<td><b>Service Name:</b></td><td><input name='name' value='" + details[0] + "'></td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td><b>Service Type:</b></td><td><input name='type' value='" + details[1] + "'></td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td><b>Image Format:</b></td><td><input name='image_format' value='" + details[5] + "'></td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td><b>Output Directory:</b></td><td><input name='output_dir' value='"+ details[3] + "'> </td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td><b>Output URL:</b></td><td><input name='output_url' value='" + details[4] + "'></td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td colspan='2'><b>Service Config Doc:</b></td>");
    out.println ("</tr>");
    out.println ("<tr>");
    out.println ("<td colspan='2'><textarea name='config' rows=5 cols=60>" + details[2] +"</textarea></td>");
    out.println ("</tr>");
    out.println ("</table>");
    canRemoveRefresh=true;
  }
  out.println("<input type='hidden' name='canRemoveRefresh' value='" + canRemoveRefresh + "'>");
  out.println("<input type='hidden' name='canAdd' value='false'>");
  out.println("<input type='hidden' name='cmd' value=''>");
  out.println("<input type='hidden' name='old_name' value='" + details[0] +"'>");

  out.println("<a href='/AdvancedEjbViewer/viewer.htm?ServiceName=" + details[0] +  "' target='_blank' >Show " + details[0] + " Map  </a>");
%>
</FORM>