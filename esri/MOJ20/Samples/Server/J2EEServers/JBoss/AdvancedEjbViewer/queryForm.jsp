<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb" %>

<%
com.esri.svr.cmn.LayerInfo layerInfo = (com.esri.svr.cmn.LayerInfo)request.getAttribute("layer.info");
System.out.println("layer info " + layerInfo);

String[] fieldNames = layerInfo.getFieldNames();
String[] fieldTypes = layerInfo.getFieldTypes();

System.out.println("# field names=" + fieldNames.length);
System.out.println("# field types=" + fieldTypes.length);

String selectedFieldIndex = (String)request.getAttribute("selected.fieldindex");
int index = 0;

try { index = Integer.valueOf(selectedFieldIndex).intValue(); } catch (Exception ex) {}

String setFieldNames = "";
String setFieldTypes = "";
String formFields = "";
if (fieldNames != null) {
	for (int i=0; i<fieldNames.length; i++) {
		setFieldNames += "parent.MapFrame.LayerFields[" + i + "] = '" + fieldNames[i] + "';";
		setFieldTypes += "parent.MapFrame.LayerFieldType[" + i + "] = '" + fieldTypes[i] + "';";
		
		System.out.println(" name=" + fieldNames[i] + " type=" + fieldTypes[i]);
		
		if (fieldNames[i].equals("#SHAPE#")) continue;
		formFields += "                <option value='";
		formFields += fieldNames[i] + "'";
		if (index == i) formFields += " selected";
		formFields += ">" + fieldNames[i] + "\n";
	}
	setFieldNames += "parent.MapFrame.LayerFieldCount = " + fieldNames.length + ";";
}

String sampleValues = "";
String[] samples = null;
try { samples = (String[])request.getAttribute("sample.values"); } catch (Exception ex) {}

if (samples != null && samples.length > 0) {
	System.out.println("# samples=" + samples.length);
	
	java.util.Vector toBeSorted = new java.util.Vector();
	for (int i = 0; i<samples.length; i++) toBeSorted.add(samples[i]);
	
	java.util.Collections.sort(toBeSorted);
	
	sampleValues += "        <select name='QueryValue'> \n";
	for (int i = 0; i<toBeSorted.size(); i++) {
		String str = (String)toBeSorted.elementAt(i);
		if (!fieldTypes[index].equals("8")) {
			sampleValues += "            <option value='\"" + str + "\"'>\"" + str + "\" \n";
		} else { 
			sampleValues += "            <option value='" + str + "'>" + str + " \n";
		}
	}
	sampleValues += "        </select>";
} else { 
	sampleValues += "        <input name='QueryValue' size='25'	maxlength='1000'> \n";
	sampleValues += "        <input type='button' name='makeList'	value='Get Samples' onclick='changeToSamples()'>";
}
%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<HEAD>
<script language="javascript">
	var t = parent.MapFrame
	var dQuote = '"';
	var sQuote = '\'';
	var lastExpr = "";
	var currExpr = "";
	var sampleList = new Array();
	var qField = "";
	function addString() {
		lastExpr = document.QueryStuff.QueryString.value;
		qField = document.QueryStuff.QueryField.options[document.QueryStuff.QueryField.selectedIndex].value;
		var qOperator = document.QueryStuff.QueryOperator.options[document.QueryStuff.QueryOperator.selectedIndex].value;
		var fNum = document.QueryStuff.QueryField.selectedIndex + 1;
		var qString = document.QueryStuff.QueryValue.value;
		//alert("query string=" + qString + " type=" + t.LayerFieldType[fNum]);
		if (t.LayerFieldType[fNum] != 8) {
			if (qString.indexOf(dQuote)==-1) {
				qString = dQuote + qString + dQuote;
			}
		}
		//alert(qString);
		if (t.LayerFieldType[fNum].indexOf("91",0)!=-1) {
			qString = t.formatDate(qString);
		}
		//alert(qString);
		if ((t.LayerFieldType[fNum].indexOf("4",0)!=-1) || 
			(t.LayerFieldType[fNum].indexOf("8",0)!=-1) ||
			(t.LayerFieldType[fNum].indexOf("6",0)!=-1) ||
			(t.LayerFieldType[fNum].indexOf("5",0)!=-1)) {
			qString = t.convertDecimal(qString);
		}
		var qString2 = document.QueryStuff.QueryString.value;
		var theString =  qField + " " + qOperator + " " + qString;
		document.QueryStuff.QueryString.value = qString2 + theString;
		currExpr = document.QueryStuff.QueryString.value;
		
		//alert("current where clause=>" + currExpr);
	}
 	
	function sendQuery() {
		var theString = document.QueryStuff.QueryString.value;
 		if (theString!="") {
 			var ary = new Array("<",">","="," LIKE "," like "," IS "," is ");
 			var va = 0;
 			var arylen = ary.length;
 			for (i=0;i<arylen;i++){
 				a = ary[i]
 				s = theString.search(a)
 				va = va + s 
 			}
 			if (va == -7){
 				alert("No operator in query");
 			} else {
 				sampleList=null;
				while (theString.indexOf(dQuote) != -1) {
					theString = theString.replace('"', '\'');
				}
				doQuery(theString, "");
 			}
 		} else {
 			alert("Query expression is blank.\nNo features have been selected.");
 		}
 	}
	
	function doQuery(queryString, sampleField) { 
		parent.MapFrame.QueryString = queryString;
		document.QueryForm.action = parent.MapFrame.MOJEJBController;
		document.QueryForm.QueryString.value = queryString;
		document.QueryForm.Active.value = t.ActiveLayerIndex;
		document.QueryForm.ServiceName.value = 	t.ServiceName;
		document.QueryForm.Cmd.value = "Query";
		document.QueryForm.SampleField.value = sampleField;
		document.QueryForm.submit();
	}
	
	function addAnd() {
		lastExpr = document.QueryStuff.QueryString.value;
		var qString2 = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = qString2 + " AND ";
		currExpr = document.QueryStuff.QueryString.value;
	}

	function addOr() {
		lastExpr = document.QueryStuff.QueryString.value;
		var qString2 = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = qString2 + " OR ";
		currExpr = document.QueryStuff.QueryString.value;
	}

	function addNot() {
		lastExpr = document.QueryStuff.QueryString.value;
		var qString2 = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = qString2 + "NOT ";
		currExpr = document.QueryStuff.QueryString.value;
	}

	function addLeftPara() {
		lastExpr = document.QueryStuff.QueryString.value;
		var qString2 = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = qString2 + "(";
		currExpr = document.QueryStuff.QueryString.value;
	}

	function addRightPara() {
		lastExpr = document.QueryStuff.QueryString.value;
		var qString2 = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = qString2 + ")";
		currExpr = document.QueryStuff.QueryString.value;
	}

	function undoString() {
		currExpr = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = lastExpr;
		t.setQueryString = document.QueryStuff.QueryString.value;
		lastExpr = currExpr;
	}

	function clearString() {
		lastExpr = document.QueryStuff.QueryString.value;
		document.QueryStuff.QueryString.value = "";	
		t.setQueryString = "";
	}

	function setUp() {
		if (t.showSampleValues) {
			t.fieldIndex = document.QueryStuff.QueryField.selectedIndex + 1;
			t.setQueryString = document.QueryStuff.QueryString.value;
			var theField = t.LayerFields[t.fieldIndex];
			//alert(theField + " " + t.fieldIndex);
			doQuery("", theField);
			//t.tempGetSamples(theField);
			//t.writeQueryForm();
		}
	}

	function changeToSamples() {
		t.fieldIndex = document.QueryStuff.QueryField.selectedIndex + 1;
		t.setQueryString = document.QueryStuff.QueryString.value;
		t.showSampleValues=true;
		//alert(t.LayerFields[t.fieldIndex]);
		doQuery("", t.LayerFields[t.fieldIndex]);
		//t.tempGetSamples(t.LayerFields[t.fieldIndex]);
	}

</script>
</head>
<body link="Blue" vlink="Blue" alink="Blue" leftmargin=0 topmargin=0 bgcolor="Silver" onload="window.focus(); parent.MapFrame.queryOpen=true;" onunload="parent.MapFrame.queryOpen=false;">
<center>
<FORM name="QueryForm" action="">
<input type='hidden' name='Cmd' value='Query'>
<input type='hidden' name='OptionCmd' value='Select'>
<input type='hidden' name='Active' value=''>
<input type='hidden' name='ServiceName' value=''>
<input type='hidden' name='QueryString' value=''>
<input type='hidden' name='SampleField' value=''>
<input type='hidden' name='MapX' value=''>
<input type='hidden' name='MapY' value=''>
<input type='hidden' name='SelectMinX' size='5' value=''>
<input type='hidden' name='SelectMinY' size='5' value=''>
<input type='hidden' name='SelectMaxX' size='5' value=''>
<input type='hidden' name='SelectMaxY' size='5' value=''>
<input type='hidden' name='Tolerance' value=''>
<input type='hidden' name='BufferDistance' size='5' value=''>
<input type='hidden' name='BufferUnit' size='5' value=''>
<input type='hidden' name='TargetLayer' size='5' value=''>
</FORM>
<form name="QueryStuff" onsubmit="sendQuery();return false;">
	<font face="Arial" size="-3">
	<table border="0" cellspacing="0" cellpadding="2" width=100%>
	<tr><td align="CENTER" bgcolor="Silver"><font face="Arial" size="-2">Field</font></td>
	<td align="CENTER" bgcolor="Silver"><font face="Arial" size="-2">Operator</font></td>
	<td align="CENTER" bgcolor="Silver"><font face="Arial" size="-2">Value</font></td>
	<td rowspan="2" align="CENTER" bgcolor="Silver">
	<input type="button" name="theAnd" value=" And " onclick="addAnd()">
	<input type="button" name="theOr" value=" Or " onclick="addOr()"><br>
	<input type="button" name="theNot" value=" Not " onclick="addNot()">
	<input type="button" name="theLeft" value="  (  " onclick="addLeftPara()">
	<input type="button" name="theRight" value="  )  " onclick="addRightPara()">
	</td>
	</tr>
	<tr><td align="CENTER" bgcolor="Silver">
	<select name="QueryField" onchange="setUp();">
<%=formFields%>
	</select>
	</td><td align="CENTER" bgcolor="Silver">
	<select name="QueryOperator">
		<OPTION selected value=" = ">=
		<option value=" < ">&lt;
		<option value=" > ">&gt;
		<option value=" <= ">&lt;=
		<option value=" >= ">&gt;=
		<option value=" LIKE ">LIKE
	</select>
	</td>
	<td align="CENTER" bgcolor="Silver">
<%= sampleValues%>
	</td>
	</td>
	</tr>
	<tr><td colspan="4" align="center" bgcolor="Black">
	<input type="button" name="addIt" value="Add to Query String" onclick="addString()">
	<input type="Text" name="QueryString" size="50" maxlength="1024" value=''><br>
	<input type="submit" value="Execute" name="submit">
	<input type="button" name="UnDo" value="Undo" onclick="undoString()">
	<input type="button" name="Clear" value="Clear" onclick="clearString()">
	</td></tr>
	</table>
	</font>
</center>
</FORM>
<script language="javascript">
<%=setFieldNames%>
<%=setFieldTypes%>
</script>
</body>
</html>
