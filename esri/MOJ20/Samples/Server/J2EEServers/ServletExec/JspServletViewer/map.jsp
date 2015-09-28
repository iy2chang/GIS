<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb"%>

<%
String[] layerNameList = (String[])request.getAttribute("layer.name.list");
boolean[] layerVisibleList = (boolean[])request.getAttribute("layer.visible.list");

//default values 
String serviceName = "world";
String MINX = "99.023";
String MINY = "10.66";
String MAXX = "126.72";
String MAXY = "35.95";

String imageWidth = "450";
String imageHeight = "350";
String imageURL = "";
String sb1Units = "Miles";
String sb2Units = "Miles";
String imageURLPrefix = "";   //"http://huafeng:8080";
String action = "Refresh";   //action variable controls the action of the map
String active = "0";         // holds value of active layer

// get values passed from control servlet
if (request.getAttribute("cmd") != null) { action = (String)request.getAttribute("cmd"); }
if (request.getAttribute("active") != null) { active = (String)request.getAttribute("active"); } 

if (request.getAttribute("image.width") != null ) { imageWidth = (String)request.getAttribute("image.width"); }
if (request.getAttribute("image.height") != null ) { imageHeight = (String)request.getAttribute("image.height"); }
if (request.getAttribute("service.name") != null ) { serviceName = (String)request.getAttribute("service.name"); }
if (request.getAttribute("image.url") != null ) imageURL = (String)request.getAttribute("image.url");

if (request.getAttribute("min.x") != null ) { MINX = (String)request.getAttribute("min.x"); }
if (request.getAttribute("min.y") != null ) { MINY = (String)request.getAttribute("min.y"); }
if (request.getAttribute("max.x") != null ) { MAXX = (String)request.getAttribute("max.x"); }
if (request.getAttribute("max.y") != null ) { MAXY = (String)request.getAttribute("max.y"); }

String refreshMapJS = "";
String formFields = "";
String layerNames = "";
String layerVisibility = "";
String toc = " ";
int iActive = 0;
try { iActive = Integer.valueOf(active).intValue();} catch (Exception ex) {}

if (layerVisibleList != null && layerNameList != null) {
	//
	// layerlist template contains a list of two string array,
	// 1) layer name <=> String
	// 2) layer visibility <=> String of "true" or "false"
	//

      refreshMapJS += "function refreshMap(){ \n ";
	refreshMapJS += "	document.mapform.ServiceName.value = parent.MapFrame.ServiceName; \n ";
	refreshMapJS += "	document.mapform.action = parent.MapFrame.MOJServletController; \n ";
      for(int i = 0; i < layerVisibleList.length; i++){
            refreshMapJS += "\n	if(document.mapform.visible" + i + ".checked){ \n ";
            refreshMapJS += "		document.mapform.layervis" + i + ".value = 'true'; \n ";
            refreshMapJS += "	} else { \n";
            refreshMapJS += "		document.mapform.layervis" + i + ".value = 'false'; \n ";
            refreshMapJS += "	} \n";
      }

      refreshMapJS += "	document.mapform.Cmd.value = 'Refresh'; \n ";
	refreshMapJS += "	if (parent.MapFrame.HasSelection) { \n";
	refreshMapJS += "		parent.MapFrame.refreshSelectionQueryFind(); \n";
	refreshMapJS += "	} else { \n"; 
	refreshMapJS += "		document.mapform.submit(); \n";
	refreshMapJS += "	} \n";
      refreshMapJS += "} \n ";

      if (active != null) {
      	iActive = Integer.valueOf(active).intValue();
      } else {
      	iActive = layerVisibleList.length - 1;
      }
	
	toc += "<FONT FACE='Arial'><B>Layers</B> \n";
	toc += "<TABLE CELLSPACING='0' CELLPADDING='1' NOWRAP> \n";
	toc += "	<TR> \n";
	toc += "		<TD ALIGN='CENTER'><FONT FACE='Arial Narrow' SIZE='-3'>Visible</FONT></TD> \n";
	toc += "		<TD ALIGN='CENTER'><FONT FACE='Arial Narrow' SIZE='-3'>Active</FONT></TD> \n";
	toc += "		<TD><FONT FACE='Arial Narrow' SIZE='-3'> </FONT></TD> \n";		
	toc += "	</TR> \n";

	for(int i = layerVisibleList.length - 1; i >= 0 ; i--) {
		toc += "<tr> \n<td>";
		toc += "<input type='Checkbox' name='visible" + i + "' value='" + i + "'";
		if (layerVisibleList[i] == true) toc += "checked";
		toc += " ></td> \n";
		toc += "<td>";
								
		toc += "<input type='Radio' name='Active' value='" + i + "'";
		if(i == iActive) {
			toc += " checked";
		}
		toc += " onClick=setActiveLayerIndex(" + i + ");> ";
		  
		toc += "</td> \n";
								
		toc += "<td><font face='Arial' size='-1'>" + layerNameList[i] + "</font></td> \n";
		toc += "</tr> \n";
		
		formFields += "<input type='hidden' name='layervis" + i +"' value='" + layerVisibleList[i] + "'> \n";
	} 
	
	for(int i=0; i<layerVisibleList.length; i++) {
		layerNames += "        map.LayerName[" + i + "] = '" + layerNameList[i] + "'; \n";
		if (layerVisibleList[i]) 
			layerVisibility += "        map.LayerVisible[" + i + "] = 1; \n";
		else
			layerVisibility += "        map.LayerVisible[" + i + "] = 0; \n";
	}
	layerNames += "        map.layerCount = " + layerVisibleList.length + ";";

	toc += "<TR> \n";
	toc += "	<TD ALIGN='center' COLSPAN='3'> \n";
	toc += "		<HR WIDTH=90> \n";
	toc += "		<INPUT TYPE='button' NAME='refreshButton' VALUE='Refresh Map' onClick='refreshMap()'> \n";
	toc += "	</TD> \n";
	toc += "</TR> \n";
	toc += "</table> \n ";	
}
%>
<HTML>
<HEAD>
<TITLE>MOJ Server JSP Servlet Viewer</TITLE>
<script language='javascript'>
<%= refreshMapJS %>
function setActiveLayerIndex(index) {
	parent.MapFrame.ActiveLayerIndex = index;
}
</script>
</HEAD>
<BODY BGCOLOR="Silver" alink="white" vlink="white" link="white">
<center>
<HR WIDTH="75%">
<form action="" name="mapform">
<%= toc %>
<input type='hidden' name='ServiceName' value=''>
<input type='hidden' name='MinX' value="<%= MINX %>" >
<input type='hidden' name='MinY' value="<%= MINY %>" >
<input type='hidden' name='MaxX' value="<%= MAXX %>" >
<input type='hidden' name='MaxY' value="<%= MAXY %>" >
<input type='hidden' name='MapX' value=''>
<input type='hidden' name='MapY' value=''>
<input type='hidden' name='Tolerance' value=''>
<input type='hidden' name='SelectMinX' size='5' value=''>
<input type='hidden' name='SelectMinY' size='5' value=''>
<input type='hidden' name='SelectMaxX' size='5' value=''>
<input type='hidden' name='SelectMaxY' size='5' value=''>
<input type='hidden' name='QueryString' size='5' value=''>
<input type='hidden' name='FindString' value=''>
<input type='hidden' name='Width' value="<%= imageWidth %>">
<input type='hidden' name='Height' value="<%= imageHeight %>">
<input type='hidden' name='BufferDistance' size='5' value=''> 
<input type='hidden' name='BufferUnit' size='5' value=''> 
<input type='hidden' name='TargetLayer' size='5' value=''>
<input type='hidden' name='Cmd' value=''>
<input type='hidden' name='OptionCmd' value='Select'>
<%= formFields%>
</form>
</center>

<script language='JavaScript' type='text/javascript'>
	//alert("update map!!");
	var map = parent.MapFrame;
<%= layerVisibility %>
<%= layerNames %>
	if (map.updateLastExtent) { 
		map.lastMinX = map.curMinX;
		map.lastMinY = map.curMinY;
		map.lastMaxX = map.curMaxX;
		map.lastMaxY = map.curMaxY;
		//map.updateLastExtent = false;
	}
	map.updateLastExtent = true;
	map.minX = map.setDecimalString("<%= MINX %>");
	map.minY = map.setDecimalString("<%= MINY %>");
	map.maxX = map.setDecimalString("<%= MAXX %>");
	map.maxY = map.setDecimalString("<%= MAXY %>");

	map.curMinX = map.setDecimalString("<%= MINX %>");
	map.curMinY = map.setDecimalString("<%= MINY %>");
	map.curMaxX = map.setDecimalString("<%= MAXX %>");
	map.curMaxY = map.setDecimalString("<%= MAXY %>");

	map.currentMapImageURL =  "<%=imageURLPrefix%><%=imageURL%>";
	map.document.mapImage.src = "<%=imageURLPrefix%><%=imageURL%>";
	//map.updateCoords(<%=MINX%>,<%=MINY%>,<%=MAXX%>,<%=MAXY%>,"<%=sb1Units%>","<%=sb2Units%>");
    map.updateCoords("<%=MINX%>","<%=MINY%>","<%=MAXX%>","<%=MAXY%>","<%=sb1Units%>","<%=sb2Units%>");
	
	if (!map.fullExtentSet) { // they are currently set to initial extent 
		map.minX = map.setDecimalString("<%= MINX %>");
		map.minY = map.setDecimalString("<%= MINY %>");
		map.maxX = map.setDecimalString("<%= MAXX %>");
		map.maxY = map.setDecimalString("<%= MAXY %>");
		map.fullExtentSet = true;
	}
</script>
</BODY>
</HTML>
