// buffer.js


var charSet="ISO-8859-1";
var drawTargetLayer = true;
var bufferTargetLayer = "";
var bufferTargetLayerIndex = 0;
var bufferDistance = 5;
var bufferSmoothEdges = 1/100;

var getBufferedData = false;

/*
***************************************************************************************

Buffer functions 

***************************************************************************************
*/


function writeBufferForm() {
	if (!parent.MapFrame.HasSelection) {
		alert("No feaure(s) has been selected! ");
		return;
	}
	
	if (useTextFrame) {
		//if ((is5up) && (isNav)) parent.TextFrame.document.location= appDir + "text.htm";
		var Win1 = parent.TextFrame;
		Win1.document.open();
		var t = "parent.MapFrame";
		
	} else {
		var Win1 = open(appDir + "text.htm","QueryWindow","width=575,height=150,scrollbars=yes,resizable=yes");
			var t = "opener";
			if (parent.MapFrame!=null) t = "opener.parent.MapFrame";
	}
	
	Win1.document.writeln('<html><meta http-equiv="Content-Type" content="text/html; charset=' + charSet + '"><HEAD>');
	Win1.document.writeln('			<script language="javascript">');
	Win1.document.writeln('var t = ' + t + ';');
	Win1.document.writeln('function doBuffer() {');
	Win1.document.writeln('	var f = document.forms[0];');	
	Win1.document.writeln('	var theIndex = f.theTarget.selectedIndex;');
	Win1.document.writeln('	var theGetDataChecked = f.getData.checked;');
	Win1.document.writeln('	var g = parent.TOCFrame.document.forms[0];');
	Win1.document.writeln('	var m = parent.MapFrame;');
	Win1.document.writeln('	if (theGetDataChecked && theIndex>0) {');
	Win1.document.writeln('		g = document.forms[0];');	
	Win1.document.writeln('	}');		
	Win1.document.writeln('	if (parseFloat(f.theDistance.value)>=0) {');
	Win1.document.writeln('		var mDistance = t.forceComma(f.theDistance.value);');
	Win1.document.writeln('		//mDistance = t.convertUnits(mDistance, t.MapUnits, t.ScaleBarUnits);');
	Win1.document.writeln('		t.BufferDistance = mDistance;')
	Win1.document.writeln('		t.BufferUnit = t.ScaleBarUnits;')
	Win1.document.writeln('		g.BufferDistance.value = mDistance;')
	Win1.document.writeln('		g.BufferUnit.value = t.ScaleBarUnits;')
	Win1.document.writeln('		g.SelectMinX.value = f.SelectMinX.value;')
	Win1.document.writeln('		g.SelectMinY.value = f.SelectMinY.value;')
	Win1.document.writeln('		g.SelectMaxX.value = f.SelectMaxX.value;')
	Win1.document.writeln('		g.SelectMaxY.value = f.SelectMaxY.value;')
	Win1.document.writeln('		g.QueryString.value = m.QueryString;')
	Win1.document.writeln('		g.FindString.value = m.FindString;')
	Win1.document.writeln('		g.ServiceName.value = m.ServiceName;')
	Win1.document.writeln('		if (theIndex>0) {');
	Win1.document.writeln('			t.TargetLayer = parseInt(f.theTarget.options[theIndex].value);')
	Win1.document.writeln('			g.TargetLayer.value = parseInt(f.theTarget.options[theIndex].value);');
	Win1.document.writeln('			if (theGetDataChecked) {');
	Win1.document.writeln('				g.Cmd.value = "Buffer_Select";');
	Win1.document.writeln('			} else {');
	Win1.document.writeln('				g.Cmd.value = "Refresh_Buffer_Select";');
	Win1.document.writeln('			}');
	Win1.document.writeln('		} else {');
	Win1.document.writeln('			t.TargetLayer = -1;');
	Win1.document.writeln('			g.TargetLayer.value = -1;');
	Win1.document.writeln('			g.Cmd.value = "Buffer";');
	Win1.document.writeln('		}');
	Win1.document.writeln('		parent.MapFrame.SelectionQueryFindCmd = "Buffer_Select";');
	Win1.document.writeln('		g.action = parent.MapFrame.MOJEJBController;'); 
	Win1.document.writeln('		g.submit();');
	Win1.document.writeln('	} else {');
	Win1.document.writeln('		alert("' + msgList[81] + '");');
	Win1.document.writeln('	}');
	Win1.document.writeln('}');
	/*
	Win1.document.writeln('var t = ' + t);
	Win1.document.writeln('function doBuffer() {');
	Win1.document.writeln('	var f = document.forms[0];');		
	Win1.document.writeln('	t.bufferDistance = f.theDistance.value;');
	// 
	Win1.document.writeln('	theIndex = f.theTarget.selectedIndex;');
	Win1.document.writeln('	if (theIndex>0) {');
	Win1.document.writeln('		t.bufferTargetLayer = f.theTarget.options[theIndex].text;');
	Win1.document.writeln('		t.bufferTargetLayerIndex = parseInt(f.theTarget.options[theIndex].value);');
	Win1.document.writeln('		t.getBufferedData = f.getData.checked;');
	Win1.document.writeln('		t.drawTargetLayer=true;');
	Win1.document.writeln('	} else {');
	Win1.document.writeln('		t.drawTargetLayer=false;');
	Win1.document.writeln('		t.getBufferedData = false;');
	Win1.document.writeln('	}');
	// 
	Win1.document.writeln('	if (parseFloat(f.theDistance.value)>=0) {');
	Win1.document.writeln('		t.bufferIt();');
	Win1.document.writeln('	} else {');
	Win1.document.writeln('		alert("' + msgList[81] + '");');
	Win1.document.writeln('	}');
	Win1.document.writeln('}');
	*/
	
	Win1.document.writeln('</script>');
	Win1.document.writeln('</head>');
	Win1.document.writeln('<body bgcolor="' + textFrameBackColor + '" text="white" leftmargin=0 topmargin=0 onload="window.focus()">');
	//Win1.document.writeln('<div align="center"><form onsubmit="doBuffer(); return false;">');
	Win1.document.writeln('<div align="center"><form action="" onsubmit="doBuffer(); return false;">');
	Win1.document.writeln('<table cellspacing="2" cellpadding="0" bgcolor="' + textFrameFormColor + '" width=100%>');
	Win1.document.writeln('<tr><td align="CENTER"><FONT face=Arial><STRONG>Buffer</STRONG></FONT></td></tr>');
	// /*
	Win1.document.writeln('<tr><td align="CENTER"><font face="Arial" size="-2">' + msgList[82]);
	Win1.document.writeln('<SELECT  name=theTarget>');
	Win1.document.writeln('<option value=-1>' + msgList[87]);
	
	for (var i=(layerCount-1); i>=0; i--) {
		if (LayerType[i]!="image") {
			Win1.document.writeln('<option value=' + i + '>' + LayerName[i]);
		}
	}
	Win1.document.writeln('</SELECT>');
	Win1.document.writeln(msgList[83] + '<input type="Text" name="theDistance" value="0" size="5"> ');
	
	var j = 1;
	for (var i=0;i<sUnitList.length;i++) {
		if (ScaleBarUnits==sUnitList[i]) j=i;
	}
	Win1.document.writeln(sUnitList[j]);

	Win1.document.writeln(msgList[84]);
	Win1.document.writeln(LayerName[ActiveLayerIndex]);
	Win1.document.writeln('</font></td></tr>');
	Win1.document.writeln('<tr><td align="CENTER">');
	Win1.document.writeln('<input type="Button" name="theButton" value="' + buttonList[9] + '" onclick="doBuffer()">');
	Win1.document.write('<INPUT TYPE="checkbox" NAME="getData" VALUE="' + buttonList[10] + '"');
	if (getBufferedData) {
		Win1.document.write(' CHECKED');
	}
	// Currently disabled 
	//Win1.document.write(' DISABLED');
	
	Win1.document.writeln('><font face="Arial" size="-2">' + msgList[85] + '</font>');
	Win1.document.writeln('</td></tr>');
	Win1.document.writeln('</table>');
	Win1.document.write("<input type='hidden' name='Cmd' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='OptionCmd' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='Active' size='5' value='" + parent.MapFrame.ActiveLayerIndex + "'> \n");
	Win1.document.write("<input type='hidden' name='SelectMinX' size='5' value='" + forceComma(parent.MapFrame.SelectMinX) + "'> \n");
	Win1.document.write("<input type='hidden' name='SelectMinY' size='5' value='" + forceComma(parent.MapFrame.SelectMinY) + "'> \n");
	Win1.document.write("<input type='hidden' name='SelectMaxX' size='5' value='" + forceComma(parent.MapFrame.SelectMaxX) + "'> \n");
	Win1.document.write("<input type='hidden' name='SelectMaxY' size='5' value='" + forceComma(parent.MapFrame.SelectMaxY) + "'> \n");

	Win1.document.write("<input type='hidden' name='QueryString' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='FindString' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='MapX' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='MapY' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='Tolerance' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='BufferDistance' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='BufferUnit' size='5' value=''> \n");
	Win1.document.write("<input type='hidden' name='TargetLayer' size='5' value=''> \n"); 
	Win1.document.write("<input type='hidden' name='SampleField' size='5' value=''> \n"); 
	Win1.document.write("<input type='hidden' name='ServiceName' size='10' value='" + parent.MapFrame.ServiceName + "'> \n");
	Win1.document.writeln('</form></div></body></html>');
	Win1.document.close();
}

// buffer around selected features
function bufferIt() {
	hideLayer("measureBox");
	//alert("Function not yet enabled.");
	showBuffer=true;
	//sendMapXML();
}

