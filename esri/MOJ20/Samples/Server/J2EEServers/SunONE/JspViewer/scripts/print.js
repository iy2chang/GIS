// print.js


printPresent=true;

var printTitle = "Map Created by MOJ Server JSP Viewer"; //titleList[4];
var printMapURL="";
var printOVURL="";
var printLegURL="";

var legVis2=false;

/*
***************************************************************************************

Print functions

***************************************************************************************
*/


// display print form
function printIt() {
	//hideLayer("measureBox");
	if (useTextFrame) {
		parent.TextFrame.document.location = "printform.htm";
	} else {
		var Win1 = open("printlayoutform.htm","PrintFormWindow","width=575,height=150,scrollbars=yes,resizable=yes,toolbar=yes");
	}
}

// display print layout form
function printLayout() {
	//hideLayer("measureBox");
	if (useTextFrame) {
		parent.TextFrame.document.location = "print_layout_form.htm";
	} else {
		var Win1 = open("print_layout_form.htm","PrintLayoutFormWindow","width=625,height=450,scrollbars=yes,resizable=yes,toolbar=yes");
	}
}

// create web page for printing layout
	// first get Map
function getPrintLayout(title) {
	hasOVMap = false;
	writePrintLayoutPage(title);
}

// create web page for printing
	// first get Map
function getPrintMap(title) {
	hasOVMap = false;
	writePrintPage(title);
}
// second, get OVMap
function getPrintOV() {
	var tempWidth = i2Width;
	var tempHeight = i2Height;
	i2Width=190;
	i2Height=150;
	var tempDraw=drawOVExtentBox;
	drawOVExtentBox=true;
	var theString = writeOVXML();
	drawOVExtentBox=tempDraw;
	i2Width=tempWidth;
	i2Height = tempHeight;
	sendToServer(imsOVURL,theString,102);
	tempWidth=null;
	tempHeight=null;
	theString=null;
}
// third, get Legend
function getPrintLegend() {
	//  waiting for Legend tags
	if (printLegURL=="") printLegURL = "images/nolegend.gif";
	writePrintPage();
}
// fourth, write the web page
function writePrintPage(title) {
	var Win1 = open("","PrintPage");
	//Win1.document.open();
	Win1.document.writeln('<html><meta http-equiv="Content-Type" content="text/html; charset=' + charSet + '"><head>');
	Win1.document.writeln('	<title>' + titleList[5] + '</title>');
	Win1.document.writeln('</head><center>');
	Win1.document.writeln('<body BGCOLOR="White" TEXT="Black" LEFTMARGIN=0 TOPMARGIN=0>');
	Win1.document.writeln('<FONT FACE="Arial"><B>');
	Win1.document.writeln('<TABLE WIDTH="650" BORDER="2" CELLSPACING="0" CELLPADDING="0" NOWRAP>');
	Win1.document.writeln('	<TR>');
	//Win1.document.writeln('		<TH COLSPAN="2">' + printTitle + '</TH>');
	Win1.document.writeln('		<TH COLSPAN="2">' + title + '</TH>');
	Win1.document.writeln('	</TR>');
	Win1.document.writeln('	<TR>');
	Win1.document.write('		<TD WIDTH="' + parent.MapFrame.getWinWidth() + '" HEIGHT="' + parent.MapFrame.getWinHeight() + '"');
	if (hasOVMap) Win1.document.write(' ROWSPAN="2"');
	Win1.document.writeln('>');
	Win1.document.writeln('			<IMG SRC="' + currentMapImageURL + '" WIDTH=' + parent.MapFrame.getWinWidth() + ' HEIGHT=' + parent.MapFrame.getWinHeight() + ' HSPACE=0 VSPACE=0 BORDER=0 ALT="">');
	Win1.document.writeln('		</TD>');
	if (hasOVMap) {
		Win1.document.writeln('		<TD HEIGHT="150" ALIGN="CENTER">');
		Win1.document.writeln('			<IMG SRC="' + currentMapImageURL + '" WIDTH=190 HEIGHT=150 HSPACE=0 VSPACE=0 BORDER=0 ALT="">');
		Win1.document.writeln('		</TD>');
	}
	Win1.document.writeln('	</TR>');
	Win1.document.writeln('</TABLE>');
	Win1.document.writeln('</B></FONT>');
	Win1.document.writeln('</body></center></html>');
	Win1.document.close();

	//legendVisible=legVis2;
	Win1=null;
	//hideRetrieveMap();
}

// fourth, write the layout web page
function writePrintLayoutPage() {
	var Win1 = open("","PrintLayoutPage");
	//Win1.document.open();
	Win1.document.writeln('<html><meta http-equiv="Content-Type" content="text/html; charset=' + charSet + '"><head>');
	Win1.document.writeln('	<title>' + titleList[5] + '</title>');
	Win1.document.writeln('</head><center>');
	Win1.document.writeln('<body BGCOLOR="White" TEXT="Black" LEFTMARGIN=0 TOPMARGIN=0>');
	Win1.document.writeln('<FONT FACE="Arial"><B>');
	Win1.document.writeln('<TABLE WIDTH="650" BORDER="2" CELLSPACING="0" CELLPADDING="0" NOWRAP>');
	Win1.document.writeln('	<TR>');
	Win1.document.write('		<TD WIDTH="' + parent.MapFrame.getWinWidth() + '" HEIGHT="' + parent.MapFrame.getWinHeight() + '"');
	Win1.document.writeln('>');
	Win1.document.writeln('			<IMG SRC="' + currentLayoutImageURL + '" WIDTH=' + parent.MapFrame.getWinWidth() + ' HEIGHT=' + parent.MapFrame.getWinHeight() + ' HSPACE=0 VSPACE=0 BORDER=0 ALT="">');
	Win1.document.writeln('		</TD>');
	Win1.document.writeln('	</TR>');
	Win1.document.writeln('</TABLE>');
	Win1.document.writeln('</B></FONT>');
	Win1.document.writeln('</body></center></html>');
	Win1.document.close();

	//legendVisible=legVis2;
	Win1=null;
	//hideRetrieveMap();
}

// fourth, write the layout web page
function writePrintLayoutPage(title) {
	//alert(" writePrintLayoutPage() " + title + " " + currentLayoutImageURL);
	var Win1 = open(currentLayoutImageURL,"PrintLayoutPage");
}

