<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb" %>

<%
com.esri.svr.cmn.ServiceInfo[] services = (com.esri.svr.cmn.ServiceInfo[])request.getAttribute("service.list");
String selectOptions = "";
System.out.println("serciceList.jsp # of services=" + services.length);

int index = 0;
if (services != null) {
	for (int i=0; i<services.length; i++) {
		System.out.println(" service=" + services[i].getServiceName());
		selectOptions += "                <option value='";
		selectOptions += services[i].getServiceName() + "'";
		if (index == i) selectOptions += " selected";
		selectOptions += ">" + services[i].getServiceName() + "\n";
	}
}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<title>MOJ Server AdvancedEJB Viewer</title>
</head>

<script language='JavaScript' type='text/javascript'>
var imageWidth = 500;
var imageHeight = 350;

var minX = 0;
var minY = 0;
var maxX = 0;
var maxY = 0;

var fullMinX = 0;
var fullMinY = 0;
var fullMaxX = 0;
var fullMaxY = 0;

var scalebar1Units = "miles";
var scalebar2Units = "kilometers";
var pixelTolerance = 3;

// get the Map Image width
function getWinWidth () {
	var mapFrameWidth = parent.MapFrame.window.innerWidth;
	if (mapFrameWidth == null) {
		mapFrameWidth = parent.MapFrame.document.body.clientWidth;
	}
	return mapFrameWidth;
}

 //get the Map Image height
function getWinHeight () {
	var mapFrameHeight = parent.MapFrame.window.innerHeight;
	if (mapFrameHeight == null) {
		mapFrameHeight = parent.MapFrame.document.body.clientHeight;
	}
	return mapFrameHeight;
}

function loadNewService(){
	parent.ToolFrame.document.location = parent.MapFrame.ContextPath + "/toolbar.htm";
	parent.MapFrame.ServiceName = document.FrmAddService.ServiceName.value;
	//parent.MapFrame.clickFunction('zoomin');
	//parent.ToolFrame.setToolPic("Zoom In");
	
	var theForm = parent.TOCFrame.document.forms[0];
	theForm.Width.value = getWinWidth();
	theForm.Height.value = getWinHeight();
	theForm.Cmd.value = "InitMap"; 
	theForm.ServiceName.value = document.FrmAddService.ServiceName.value;
	theForm.action = parent.MapFrame.MOJEJBController;
	theForm.submit();
}

</script>

<body  BGCOLOR="Silver">
<form name=FrmAddService>
<input type='hidden' name='Active' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='MapX' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='MapY' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='Tolerance' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='SelectMinX' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='SelectMinY' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='SelectMaxX' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='SelectMaxY' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='Cmd' size='5' value='InitMap'>&nbsp;&nbsp;
<input type='hidden' name='OptionCmd' size='5' value=''>&nbsp;&nbsp;
<input type='hidden' name='QueryString' value=''>
<input type='hidden' name='SampleField' value=''>
<center>
<table border="0">
<tr>
<td align="center">
<font face="Arial" size="3"><b>Select a service</b></font>
</font>
</td>
</tr>
<tr>
<td align="center">
<select name='ServiceName'>
<%= selectOptions %>
</select>
</td>
</tr>
<tr>
<td align="center">
<input type='button' name='getService' value='Get Map Service' onclick='loadNewService();'>
</td>
</tr>
</form>
</table>
</center>
</body>
</html>
