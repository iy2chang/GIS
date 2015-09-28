<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb"%>

<%
com.esri.svr.cmn.Feature[] identifyResults = (com.esri.svr.cmn.Feature[])request.getAttribute("identify.results");

//default values 
boolean hasSelection = false;
String serviceName = "world";
String cmd = "Identify";
String active = "0";
String mapX = "0";
String mapY = "0";
String tolerance = "0.001";

String selectminX = "0";
String selectminY = "0";
String selectmaxX = "0";
String selectmaxY = "0";
String queryString = "";
String findString = "";
String layername = "";

if (request.getAttribute("service.name") != null) serviceName = (String)request.getAttribute("service.name"); 
if (request.getAttribute("cmd") != null)          cmd         = (String)request.getAttribute("cmd");      // action variable controls the action of the map
if (request.getAttribute("active") != null)       active      = (String)request.getAttribute("active");   // active layer index
if (request.getAttribute("map.x") != null )       mapX        = (String)request.getAttribute("map.x");
if (request.getAttribute("map.y") != null )       mapY        = (String)request.getAttribute("map.y");
if (request.getAttribute("tolerance") != null )   tolerance   = (String)request.getAttribute("tolerance");

if (request.getAttribute("find.string") != null ) findString = (String)request.getAttribute("find.string");
if (request.getAttribute("query.string") != null ) queryString = (String)request.getAttribute("query.string");
if (request.getAttribute("select.minx") != null ) selectminX   = (String)request.getAttribute("select.minx");
if (request.getAttribute("select.miny") != null ) selectminY   = (String)request.getAttribute("select.miny");
if (request.getAttribute("select.maxx") != null ) selectmaxX   = (String)request.getAttribute("select.maxx");
if (request.getAttribute("select.maxy") != null ) selectmaxY   = (String)request.getAttribute("select.maxy");

if (request.getAttribute("layer.name") != null ) layername  = (String)request.getAttribute("layer.name");

String identHTML = ""; 
if (identifyResults != null && identifyResults.length > 0) {
	hasSelection = true;
			    
	String fieldnames[] = identifyResults[0].getFieldNames();
	
	identHTML += "<FONT FACE='Arial' SIZE='-1'><b>" + layername + "</b></FONT> \n";
	identHTML += "<table border='1' cellspacing='0' cellpadding='2' bgcolor='white'> \n ";

	//retrieves the size of the fieldName array list for looping.
	identHTML += "<tr> \n";
      for(int i = 0; i < fieldnames.length; i++){  
		//if (fieldnames[i].equalsIgnoreCase("#SHAPE#")) continue; // skip #SHAPE# field
            identHTML += "<th><FONT FACE='Arial' SIZE='-2'>" + fieldnames[i] + "</a></th> \n";  
	}
	identHTML += "</tr> \n";
	identHTML += " </tr> \n ";

      for(int i = 0; i < identifyResults.length; i++){      
		String[] values = identifyResults[i].getFieldValues();
		//  retrieves all of the field values in the loop and embeds them in the html.  
		identHTML += "<tr> \n ";
		for (int j=0; j<values.length; j++) {
                	identHTML += "<td><FONT FACE='Arial' SIZE='-2'>" + values[j] + "</FONT></td>";
		}
		identHTML += "</tr> \n ";
	}

      identHTML += "</table> \n ";
} 
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<title>Select Results</title>
</head>
<body bgcolor="Silver">
<center>
<FORM name=SelectForm action="">
<%= identHTML %>
<input type='hidden' name='Cmd' value='Select'>
<input type='hidden' name='OptionCmd' value='Select'>
<input type='hidden' name='Active' value='<%= active %>'>
<input type='hidden' name='ServiceName' value=''>
<input type='hidden' name='SampleField' value=''>
<input type='hidden' name='MapX' value='<%= mapX %>'>
<input type='hidden' name='MapY' value='<%= mapY %>'>
<input type='hidden' name='SelectMinX' size='5' value=''>
<input type='hidden' name='SelectMinY' size='5' value=''>
<input type='hidden' name='SelectMaxX' size='5' value=''>
<input type='hidden' name='SelectMaxY' size='5' value=''>
<input type='hidden' name='Tolerance' value='<%= tolerance %>'>
<input type='hidden' name='QueryString' value=''>
<input type='hidden' name='FindString' value=''>
<input type='hidden' name='BufferDistance' size='5' value=''>
<input type='hidden' name='BufferUnit' size='5' value=''>
<input type='hidden' name='TargetLayer' size='5' value=''>
</form>
</center>
<script language='JavaScript' type='text/javascript'>
	// update global variables
    var map = parent.MapFrame;
	parent.MapFrame.SelectMinX = map.setDecimalString("<%= selectminX %>");
	parent.MapFrame.SelectMinY = map.setDecimalString("<%= selectminY %>");
	parent.MapFrame.SelectMaxX = map.setDecimalString("<%= selectmaxX %>");
	parent.MapFrame.SelectMaxY = map.setDecimalString("<%= selectmaxY %>");
	parent.MapFrame.HasSelection = <%= hasSelection %>;
	// parent.MapFrame.ServiceName = '<%= serviceName %>';
	// parent.MapFrame.ActiveLayerIndex = <%= active %>;
	parent.MapFrame.QueryString = "<%= queryString %>";
	parent.MapFrame.FindString = "<%= findString %>";
	parent.MapFrame.SelectionQueryFindCmd = "<%= cmd %>";

	//alert("update map!! <%= serviceName %> ");
	// update the map
	// if you just want to get select/identify results 
	// without updating the	map, you can comment out the following request!
	if (<%= hasSelection %>) {
		var frm = parent.TOCFrame.document.forms[0];
		var cmd = "<%= cmd %>";
		switch (cmd) {
		case "Query":
			frm.QueryString.value = parent.MapFrame.QueryString;
			frm.Cmd.value="Refresh_Select";
		case "Identify":
		case "Select":
			frm.Cmd.value="Refresh_Select";
			parent.MapFrame.FindString = "";
			break;
		case "Find": 
			frm.FindString.value = parent.MapFrame.FindString;
			frm.Cmd.value="Refresh_Find";
			parent.MapFrame.QueryString = "";
			break;
		case "Buffer_Find":
			frm.FindString.value = parent.MapFrame.FindString;
		case "Buffer_Select":
			frm.Cmd.value = "Refresh_Buffer_Select";
			// these parameters probably should be set by JSP page instead
			// of coming from browser in case the values in browser are
			// modified by others
			frm.BufferDistance.value = parent.MapFrame.BufferDistance;
			frm.BufferUnit.value = parent.MapFrame.BufferUnit;
			frm.TargetLayer.value = parent.MapFrame.TargetLayer;
			break;
		}
		//frm.MapX.value = <%= mapX %>;
		//frm.MapY.value = <%= mapY %>;
		//frm.Tolerance.value = "<%= tolerance %>";
		frm.SelectMinX.value = "<%= selectminX %>";
		frm.SelectMinY.value = "<%= selectminY %>";
		frm.SelectMaxX.value = "<%= selectmaxX %>";
		frm.SelectMaxY.value = "<%= selectmaxY %>";
		frm.QueryString.value = parent.MapFrame.QueryString;
		frm.action = parent.MapFrame.MOJServletController;
		frm.ServiceName.value = parent.MapFrame.ServiceName;
		//alert("Query String=" + parent.MapFrame.QueryString);
		frm.submit();
	}
</script>
</BODY>
</HTML>
