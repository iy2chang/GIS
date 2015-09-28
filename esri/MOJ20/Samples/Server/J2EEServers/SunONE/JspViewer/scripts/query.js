// query.js

queryPresent=true;

var showSampleValues=false;



/*
***************************************************************************************

Querying functions 

***************************************************************************************
*/


// query form
function queryForm() {
	if (checkIfActiveLayerAvailable()) {
		fieldIndex=0;
		showSampleValues=false;
		//alert(showSampleValues);
		//setLayerFields(ActiveLayerIndex);
		if (showSampleValues) {
			var theText = writeFieldSample(LayerFields[fieldIndex]);
			//sendToServer(imsQueryURL,theText,40);
		} else {
			writeQueryForm();
		}
	}
		
}

// find Form
function findForm() {
	if (checkIfActiveLayerAvailable()) {
		//setLayerFields(ActiveLayerIndex);
		//alert("in findForm()");
		if (useTextFrame) {
			parent.TextFrame.document.location= "findForm.htm";
		} else {
			Win1 = open(appDir + "findForm.htm","QueryWindow","width=575,height=150,scrollbars=yes,resizable=yes");
		}
	}
}


// write out a query form
function writeQueryForm() {
	SelectMinX = 0;
	SelectMinY = 0;
	SelectMaxX = 0;
	SelectMaxY = 0;
	QueryString = "";
	SampleField = "";
	parent.TextFrame.document.location= "queryForm.jsp";	
	
	var theForm = parent.TextFrame.document.forms[0];
	theForm.Cmd.value = "Query";
	theForm.Active.value = parent.MapFrame.ActiveLayerIndex;
	theForm.ServiceName.value = parent.MapFrame.ServiceName;
	theForm.action = parent.MapFrame.MOJServerController;
	theForm.QueryString.value = "";
	theForm.SampleField.value = "";
	theForm.submit();	
	
}

/*
function writeQueryForm() {
	var startpos = 0;
	var endpos = 0;
	var SampleCount = selectData.length;
	var theIndex = fieldIndex;
	var sampleList = new Array();
	var qField = LayerFields[fieldIndex] + '="';
	var valueTitle = buttonList[2];
	var tempString = "";
	if (showSampleValues) {
		for (var i=0;i<SampleCount;i++) {
			startpos = selectData[i].indexOf(qField,0);
			startpos = startpos + qField.length;
			endpos = selectData[i].indexOf('"',startpos);
			if (LayerFieldType[theIndex].indexOf("12",0)!=-1) {
				// a Character field
				//var vData = escape(selectData[i].substring(startpos,endpos));
				var vData = makeXMLsafe(selectData[i].substring(startpos,endpos));
				//vData = fixSingleQuotes(vData);
				sampleList[i] = dQuote + vData + dQuote;
				
			} else if (LayerFieldType[theIndex]=="91") {
				// a Date field
				var theDateObj = new Date(parseFloat(selectData[i].substring(startpos,endpos)));
				
				var d = theDateObj.toUTCString();
				sampleList[i] = d.replace(/GMT|UTC/,"");
				theDateObj = null;
				
			} else {
				sampleList[i] = selectData[i].substring(startpos,endpos);
			}
		}
		valueTitle = buttonList[3];
	}
	if (useTextFrame) {
		var Win1 = parent.TextFrame;
		Win1.document.open();
		var t = "parent.MapFrame";
		
	} else {
		var Win1 = open("","QueryWindow","width=575,height=150,scrollbars=yes,resizable=yes");
		var t = "opener";
		if (parent.MapFrame!=null) t = "opener.parent.MapFrame";
	}
	
	Win1.document.writeln('<html><meta http-equiv="Content-Type" content="text/html; charset=' + charSet + '"><HEAD>');
	Win1.document.writeln('			<script language="javascript">');
		Win1.document.writeln('			var t = ' + t);
		Win1.document.writeln('			var dQuote = \'"\';');
		Win1.document.writeln('			var lastExpr = "";');
		Win1.document.writeln('			var currExpr = "";');
		Win1.document.writeln('			var sampleList = new Array();');
		Win1.document.writeln('			var qField = "";');
		Win1.document.writeln('			function addString() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				qField = document.QueryStuff.QueryField.options[document.QueryStuff.QueryField.selectedIndex].value;');
		Win1.document.writeln('				var qOperator = document.QueryStuff.QueryOperator.options[document.QueryStuff.QueryOperator.selectedIndex].value;');
		Win1.document.writeln('					var fNum = document.QueryStuff.QueryField.selectedIndex;');
		if (showSampleValues) {
			Win1.document.writeln('				qString = t.parseEntity(document.QueryStuff.QueryValue.options[document.QueryStuff.QueryValue.selectedIndex].value);');
		} else{
			Win1.document.writeln('					var qString = document.QueryStuff.QueryValue.value;');
			Win1.document.writeln('					if (t.LayerFieldType[fNum].indexOf("12",0)!=-1) {');
			Win1.document.writeln('						if (qString.indexOf(dQuote)==-1) {');
			Win1.document.writeln('							qString = dQuote + qString + dQuote;');
			Win1.document.writeln('						}');
			Win1.document.writeln('					}');
		}
		Win1.document.writeln('					if (t.LayerFieldType[fNum].indexOf("91",0)!=-1) {');
		Win1.document.writeln('						qString = t.formatDate(qString);');
		Win1.document.writeln('					}');
		Win1.document.write('					if ((t.LayerFieldType[fNum].indexOf("4",0)!=-1) || ');
		Win1.document.write('					(t.LayerFieldType[fNum].indexOf("8",0)!=-1) || ');
		Win1.document.write('					(t.LayerFieldType[fNum].indexOf("6",0)!=-1) || ');
		Win1.document.write('					(t.LayerFieldType[fNum].indexOf("5",0)!=-1)) {');
		Win1.document.writeln('						qString = t.convertDecimal(qString);');
		Win1.document.writeln('					}');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var theString =  qField + " " + qOperator + " " + qString;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + theString;');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
			
		Win1.document.writeln(' 		function sendQuery() {'); 
		Win1.document.writeln(' 			var theString = document.QueryStuff.QueryString.value;'); 
		Win1.document.writeln(' 			if (theString!="") {'); 
		Win1.document.writeln(' 				var ary = new Array("<",">","="," LIKE "," like "," IS "," is ");'); 
		Win1.document.writeln(' 				var va = 0;'); 
		Win1.document.writeln(' 				var arylen = ary.length;'); 
		Win1.document.writeln(' 				for (i=0;i<arylen;i++){'); 
		Win1.document.writeln(' 					a = ary[i]'); 
		Win1.document.writeln(' 					s = theString.search(a)'); 
		Win1.document.writeln(' 					va = va + s '); 
		Win1.document.writeln(' 				}'); 
		Win1.document.writeln(' 				if (va == -7){'); 
		Win1.document.writeln(' 					alert("' + msgList[109] + '");'); 
		Win1.document.writeln(' 				} else {'); 
		Win1.document.writeln(' 					sampleList=null;'); 
		Win1.document.writeln(' 					t.sendQueryString(theString);'); 
		Win1.document.writeln(' 					t=null;'); 
		Win1.document.writeln(' 				}'); 
		Win1.document.writeln(' 			} else {'); 
		Win1.document.writeln(' 				alert("' + msgList[110] + '");'); 
		Win1.document.writeln(' 			}'); 
		Win1.document.writeln(' 		}'); 
		
		Win1.document.writeln('			function addAnd() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + " AND ";');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
			
		Win1.document.writeln('			function addOr() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + " OR ";');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
	
		Win1.document.writeln('			function addNot() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + "NOT ";');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
			
		Win1.document.writeln('			function addLeftPara() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + "(";');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
	
		Win1.document.writeln('			function addRightPara() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				var qString2 = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = qString2 + ")";');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('			}');
	
		Win1.document.writeln('			function undoString() {');
		Win1.document.writeln('				currExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = lastExpr;');
		Win1.document.writeln('				t.setQueryString = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				lastExpr = currExpr;');
		Win1.document.writeln('			}');
			
		Win1.document.writeln('			function clearString() {');
		Win1.document.writeln('				lastExpr = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				document.QueryStuff.QueryString.value = "";	');
		Win1.document.writeln('				t.setQueryString = "";');
		Win1.document.writeln('			}');
			
		Win1.document.writeln('			function setUp() {');
		Win1.document.writeln('				if (t.showSampleValues) {');
		Win1.document.writeln('					t.fieldIndex = document.QueryStuff.QueryField.selectedIndex;');
		Win1.document.writeln('					t.setQueryString = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('					var theField = t.LayerFields[t.fieldIndex];');
		Win1.document.writeln('					//alert(theField);');
		Win1.document.writeln('					t.tempGetSamples(theField);');
		Win1.document.writeln('					t.writeQueryForm();');
		Win1.document.writeln('				}');
		Win1.document.writeln('			}');
		
		Win1.document.writeln('			function changeToSamples() {');	
		Win1.document.writeln('				t.fieldIndex = document.QueryStuff.QueryField.selectedIndex;');
		Win1.document.writeln('				t.setQueryString = document.QueryStuff.QueryString.value;');
		Win1.document.writeln('				t.tempGetSamples(t.LayerFields[t.fieldIndex]);');
		Win1.document.writeln('			}');
					
		Win1.document.writeln('		</script>');
		Win1.document.writeln('		</head>');
	
		Win1.document.writeln('		<body link="Blue" vlink="Blue" alink="Blue" leftmargin=0 topmargin=0 bgcolor="' + textFrameBackColor + '" onload="window.focus(); ' + t + '.queryOpen=true;" onunload="' + t + '.queryOpen=false;">');
		Win1.document.writeln('			<center>');
		Win1.document.writeln('			<form name="QueryStuff" onsubmit="sendQuery();return false;">');
		Win1.document.writeln('			<font face="Arial" size="-3">');
		Win1.document.writeln('			<table border="0" cellspacing="0" cellpadding="2" width=100%>');
		Win1.document.writeln('			<tr><td align="CENTER" bgcolor="' + textFrameBackColor + '"><font face="Arial" size="-2">' + msgList[72] + '</font></td>');
		Win1.document.writeln('			<td align="CENTER" bgcolor="' + textFrameBackColor + '"><font face="Arial" size="-2">' + msgList[73] + '</font></td>');
		Win1.document.writeln('			<td align="CENTER" bgcolor="' + textFrameBackColor + '"><font face="Arial" size="-2">' + valueTitle + '</font></td>');
		Win1.document.writeln('			<td rowspan="2" align="CENTER" bgcolor="Silver">');
		Win1.document.writeln('			<input type="button" name="theAnd" value=" And " onclick="addAnd()">');
		Win1.document.writeln('			<input type="button" name="theOr" value=" Or " onclick="addOr()"><br>');
		Win1.document.writeln('			<input type="button" name="theNot" value=" Not " onclick="addNot()">');
		Win1.document.writeln('			<input type="button" name="theLeft" value="  (  " onclick="addLeftPara()">');
		Win1.document.writeln('			<input type="button" name="theRight" value="  )  " onclick="addRightPara()">');
		Win1.document.writeln('			</td>');
			
		Win1.document.writeln('			</tr>');
		Win1.document.writeln('			<tr><td align="CENTER" bgcolor="' + textFrameBackColor + '">');
			Win1.document.writeln('			<select name="QueryField" onchange="setUp();">');
			for (var i=0;i<LayerFieldCount;i++) {
				Win1.document.write('<option value="' + LayerFields[i] + '"');
				if (i==fieldIndex) Win1.document.write(' selected');
				
				//Win1.document.writeln('>' + LayerFields[i]);
				
				// Put Field Alias in form if present
                if (useFieldAlias) {
					if (fieldAliasList[ActiveLayerIndex].length>0) {
	                     var start=fieldAliasList[ActiveLayerIndex].indexOf(LayerFields[i]+':');
	                     if (start == -1) {
			 				alias=LayerFields[i];
	                     } else {
	                          start=start+LayerFields[i].length+1;
	                          var end=fieldAliasList[ActiveLayerIndex].indexOf("|",start);
	                          if (end == -1) end = fieldAliasList[ActiveLayerIndex].length;
	                          else end=end-start;
	                          alias=fieldAliasList[ActiveLayerIndex].substr(start,end);
	                     }
	                     Win1.document.writeln('>'+alias);
					}
                } else {
					Win1.document.writeln('>' + LayerFields[i]);
				}
				// end of Field Alias stuff
				
			}
			Win1.document.writeln('			</select>');
		Win1.document.writeln('			</td><td align="CENTER" bgcolor="' + textFrameBackColor + '">');
		Win1.document.writeln('			<select name="QueryOperator">');
		Win1.document.writeln('			<OPTION selected value=" = ">=');
		Win1.document.writeln('			<option value=" < ">&lt;');
		Win1.document.writeln('			<option value=" > ">&gt;');
		Win1.document.writeln('			<option value=" <= ">&lt;=');
		Win1.document.writeln('			<option value=" >= ">&gt;=');
		Win1.document.writeln('			<option value=" LIKE ">LIKE');
		//Win1.document.writeln('			<option value=" CN ">CN	');
		Win1.document.writeln('			</select>');
	             
		Win1.document.writeln('			</td><td align="CENTER" bgcolor="' + textFrameBackColor + '">');
			
		if (showSampleValues) {
			Win1.document.writeln('			<select name="QueryValue">');
				for (var i=0;i<SampleCount;i++) {
					//Win1.document.writeln('<option value=\'' + sampleList[i] + '\'>' + unescape(sampleList[i]));
					tempString = parseEntity(sampleList[i]);
					tempString = tempString.replace(/ /g, "&nbsp;");
					Win1.document.writeln('<option value=\'' + sampleList[i] + '\'>' + tempString);
				}
			Win1.document.writeln('			</select>');
	    } else {
			Win1.document.writeln('<input name="QueryValue" size="25" maxlength="1000" >');
			Win1.document.writeln('			&nbsp;&nbsp;<input type="button" name="makeList" value="' + buttonList[4] + '" onclick="changeToSamples()">');
			Win1.document.writeln('			</td>');
		}         
		Win1.document.writeln('			</td>');
		Win1.document.writeln('			</tr>');
		Win1.document.writeln('			<tr><td colspan="4" align="center" bgcolor="Black">');
		Win1.document.writeln('			<input type="button" name="addIt" value="' + buttonList[5] + '" onclick="addString()">');
		Win1.document.writeln('			<input type="Text" name="QueryString" size="50" maxlength="1024" value=\'' + setQueryString + '\'><br>');
		Win1.document.writeln('			<input type="submit" value="' + buttonList[6] + '" name="submit">'); 
		Win1.document.writeln('			<input type="button" name="UnDo" value="' + buttonList[7] + '" onclick="undoString()">');
		Win1.document.writeln('			<input type="button" name="Clear" value="' + buttonList[8] + '" onclick="clearString()">');
		Win1.document.writeln('			</td></tr>');
		Win1.document.writeln('			</table>');
		Win1.document.writeln('			</font></center></FORM></body></html>');
	
		Win1.document.close();
		Win1=null;
		sampleList=null;
}*/

/*
// temporarily change getSampleValues to true and load queryform
function tempGetSamples(theField) {
	showSampleValues=true;
	if (useTextFrame) {
		var Win1 = parent.TextFrame;
		
	} else {
		var Win1 = open("","QueryWindow","width=575,height=150,scrollbars=yes,resizable=yes");
	}
	Win1.document.open();
	Win1.document.writeln('<html><meta http-equiv="Content-Type" content="text/html; charset=' + charSet + '"><HEAD>');
	Win1.document.writeln('<body BGCOLOR="' + textFrameBackColor + '" TEXT="Black" size="-1">');
	Win1.document.writeln('<div align="center"><font face="Arial"><b>');
	Win1.document.writeln(msgList[74] + LayerName[ActiveLayerIndex] + '. . .</b></font><br>');
	Win1.document.writeln('<font face="Arial" size="-1">' + msgList[75] + numberDataSamples + msgList[76] + '</font></div></body></html>');
	Win1.document.close();
	Win1=null;
	
	var theText = writeFieldSample(theField);
	//alert(theText);

}*/


