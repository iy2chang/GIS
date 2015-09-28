/**********************************************************
*	functions.js
*
**********************************************************/

var tolerance = 0.001;

var imageWidth = 500;
var imageHeight = 350;

// image offsets
var hspc =0;
var vspc = 0;
// current coords
var mouseX = 0;
var mouseY = 0;
// map coords
var mapX = 0;
var mapY = 0;
// extent coords
var xDistance = 0;
var yDistance = 0;
var pixelX = 0;
var pixelY = 0;
// saved coords
var x1 = 0;
var y1 = 0;
var x2 = 0;
var y2 = 0;
// zoom box variables
var zleft = 0;
var ztop = 0;
var zright = 0;
var zbottom = 0;
// temporary holding of select coords
var tempLeft = 0;
var tempBottom = 0;
var tempRight = 0;
var tempTop = 0;

// variables for ovmap offset
var ovHspc = 0;
var ovVspc = 10;
var fullOVLeft = 0;
var fullOVRight = 0;
var fullOVTop = 0;
var fullOVBottom = 0;
var fullOVWidth = Math.abs(fullOVRight - fullOVLeft);
var fullOVHeight = Math.abs(fullOVTop - fullOVBottom);
var i2Width = 150;
var i2Height = 110;
var ovBorderWidth = 0;
var ovExtentBoxSize = 2;

var panning = false;
var zooming = false;
var zoomFactor = 2;
var selectBox = false;

var legendURL = "";
var legendVisible = true;

var showXYs = true; // show xy's in status bar
var roundNum = 2;	// round xy's to # of decimals - dynamic

var toolMode = 1; // 1=zoomin; 2=zoomout; 3=pan; 4=identify; 5=select;
var modeText = "Zoom In";

var webParams = "";
if (parent.MapFrame!=null) {
	webParams = parent.document.location.search;
} else {
	webParams = document.location.search;
}

function getMapImage(cmd) {
	var f = parent.TOCFrame.document.forms[0];
	f.ServiceName.value = parent.MapFrame.ServiceName;
	f.Cmd.value = cmd;
	f.Width.value = imageWidth;
	f.Height.value = imageHeight;
	if ((minX!=maxX) && (minY!=maxY)) {
		f.MinX.value = forceComma(minX);
		f.MinY.value = forceComma(minY);
		f.MaxX.value = forceComma(maxX);
		f.MaxY.value = forceComma(maxY);
	} else {
		f.MinX.value = "";
		f.MinY.value = "";
		f.MaxX.value = "";
		f.MaxY.value = "";
	}
	//alert("minx=" + minX + " miny=" + minY + " maxx=" + maxX + " maxy=" + maxY);
	if (HasSelection) { 
		refreshSelectionQueryFind();
	} else { 
		f.submit();
	}
}

// get the Map Image width
function getWinWidth () {
	var mapFrameWidth = window.innerWidth;
	if (mapFrameWidth == null) {
		mapFrameWidth = document.body.clientWidth;
	}
	return mapFrameWidth;
}

 //get the Map Image height
function getWinHeight () {
	var mapFrameHeight = window.innerHeight;
	if (mapFrameHeight == null) {
		mapFrameHeight = document.body.clientHeight;
	}
	return mapFrameHeight;
}

//keep track of currently selected tool, and display it to user
function clickFunction (toolName) {
	switch(toolName) {
		case "fullextent":
			fullExtent();
			break;
		case "zoomin":
			toolMode = 1;
			panning=false;
			selectBox=false;
			modeText="Zoom In";	
			if (isIE)	{
				document.all.theMap.style.cursor = "crosshair";
			}					
			break;
		case "zoomout":
			toolMode = 2;
			panning=false;
			selectBox=false;
			modeText="Zoom Out";
			if (isIE)	{
				document.all.theMap.style.cursor = "crosshair";
			}			
			break;
		case "pan":
			toolMode = 3;
			zooming=false;
			selectBox=false;
			modeText="Pan";
			if (isIE)	{
				document.all.theMap.style.cursor = "move";
			}
			break;
		case "identify":
			toolMode = 4;
			panning=false;
			zooming=false;
			selectBox=false;
			modeText="Identify";
			if (isIE)	{
				document.all.theMap.style.cursor = "hand";
			}
			break;		
		case "selectbox":
			toolMode = 5;
			panning=false;
			zooming=false;
			modeText="Select";
			if (isIE)	{
				document.all.theMap.style.cursor = "crosshair";
			}
			break;
		case "clearsel":
		 	clearSelectBox();
		 	break;	
		case "service":
			loadServiceChooser();
			break;
		case "buffer":
			writeBufferForm();
			break;
		case "legend":
			showLegendWin();
			break;
		case "query":
			queryForm();
			break;
		case "find":
			findForm();
			break;
		case "zoomactive":
			zoomActive();
			break;
		case "zoomlast":
			zoomLast();
			break;
		case "setunits":
			setUnits();
			break;
		case "print":
			printIt();
			break;
		case "printLayout":
			printLayout();
			break;
	}
	
	if (toolName == "identify") {
	    document.onmouseup = ignoreMouseup;
	} else {
	    document.onmouseup = mapTool;
	}

	//alert("tool name=" + toolName);
}

function ignoreMouseup(e) {
}

function setUnits() {
	parent.TextFrame.document.location = "setMapUnits.htm";	
}

function zoomLast() {
	var theForm = parent.TOCFrame.document.forms[0];
	theForm.Cmd.value = "Refresh";
	theForm.MinX.value = forceComma(parent.MapFrame.lastMinX);
	theForm.MinY.value = forceComma(parent.MapFrame.lastMinY);
	theForm.MaxX.value = forceComma(parent.MapFrame.lastMaxX);
	theForm.MaxY.value = forceComma(parent.MapFrame.lastMaxY);
	theForm.Active.value = parent.MapFrame.ActiveLayerIndex;
	theForm.ServiceName.value = parent.MapFrame.ServiceName;
	parent.MapFrame.updateLastExtent = false;

	if (HasSelection) { 
		refreshSelectionQueryFind();
	} else { 
		theForm.submit();
	}
	parent.MapFrame.lastMinX = parent.MapFrame.curMinX;
	parent.MapFrame.lastMinY = parent.MapFrame.curMinY;
	parent.MapFrame.lastMaxX = parent.MapFrame.curMaxX;
	parent.MapFrame.lastMaxY = parent.MapFrame.curMaxY;
}

function zoomActive() {
	var theForm = parent.TOCFrame.document.forms[0];
	theForm.Cmd.value = "ZoomActive";
	theForm.OptionCmd.value = "";
	theForm.Active.value = parent.MapFrame.ActiveLayerIndex;
	theForm.ServiceName.value = parent.MapFrame.ServiceName;
	
	if (HasSelection) {
		var cmd = parent.MapFrame.SelectionQueryFindCmd;
		switch (cmd) {
		case "Query":
			theForm.QueryString.value = parent.MapFrame.QueryString;
		case "Identify":
		case "Select":
			theForm.OptionCmd.value="Refresh_Select";
			break;
		case "Find": 
			theForm.FindString.value = parent.MapFrame.FindString;
			theForm.OptionCmd.value="Refresh_Find";
			break;
		case "Buffer_Find":
		case "Buffer_Select":
			theForm.OptionCmd.value = "Refresh_Buffer_Select";
			theForm.BufferDistance.value = parent.MapFrame.BufferDistance;
			theForm.BufferUnit.value = parent.MapFrame.BufferUnit;
			theForm.TargetLayer.value = parent.MapFrame.TargetLayer;
			break;
		}
		theForm.SelectMinX.value = forceComma(parent.MapFrame.SelectMinX);
		theForm.SelectMinY.value = forceComma(parent.MapFrame.SelectMinY);
		theForm.SelectMaxX.value = forceComma(parent.MapFrame.SelectMaxX);
		theForm.SelectMaxY.value = forceComma(parent.MapFrame.SelectMaxY);
		theForm.QueryString.value = parent.MapFrame.QueryString;
		theForm.FindString.value = parent.MapFrame.FindString;
	}
	//alert("find string=>" + theForm.FindString.value);
	theForm.submit();	
}

function mapTool(e) {
	switch (toolMode) {
		case 1:
			// zoom in
			startZoomBox(e);
			return false;
			break;
		case 2:
			// zoom out
			startZoomOutBox(e);
			return false;
			break;
		case 3:
			// pan
			startPan(e);
			return false;
			break;
		case 4:
			// identify
			identify(e);
			return false;
			break;			
		case 5:
			// select box
			startSelectBox(e);
			return false;
			break;
	}
}

// zoom in around mouse click
function zoomin(e) {
	getImageXY(e);
	getMapXY(mouseX,mouseY);
	var xHalf = xDistance/2;
	var yHalf = yDistance/2;
	minX = mapX - (xHalf/zoomFactor);
	maxX = mapX + (xHalf/zoomFactor);
	maxY = mapY + (yHalf/zoomFactor);
	minY = mapY - (yHalf/zoomFactor);
	
	getMapImage("Refresh");
}

// zoom out from mouse click
function zoomout(e) {
	getImageXY(e);
	getMapXY(mouseX,mouseY);

	minX = mapX - (xDistance*zoomFactor/2);
	maxX = mapX + (xDistance*zoomFactor/2);
	maxY = mapY + (yDistance*zoomFactor/2);
	minY = mapY - (yDistance*zoomFactor/2);
	
	getMapImage("Refresh");
}	


function clearSelection() {
	HasSelection = false;
	FindString = "";
	QueryString = "";
	tempLeft = 0;
    tempBottom = 0;
    tempRight = 0;
    tempTop = 0;
	getMapImage("Refresh");	
}

// identify
function identify(e) {
	getImageXY(e);
	getMapXY(mouseX,mouseY);

	var f = parent.TextFrame.document.forms[0];
	f.MapX.value = forceComma(mapX);
	f.MapY.value = forceComma(mapY);
	f.Tolerance.value = forceComma(tolerance);
	f.Cmd.value = "Identify";
	f.action = parent.MapFrame.MOJEJBController;
	f.ServiceName.value = parent.MapFrame.ServiceName;
	f.Active.value = parent.MapFrame.ActiveLayerIndex;
	//alert("click x=" + mapX + " y=" + mapY + " tolerance=" + tolerance + " service name=" + f.ServiceName.value + " " + parent.MapFrame.ServiceName);
	//alert("click x=" + mapX + " y=" + maxY + " " + tolerance + " " + parent.MapFrame.ActiveLayerIndex);
	f.submit();
	//getMapImage("Select");
}

// convert mouse click xy's into map coordinates
function getMapXY(xIn,yIn) {
		xDistance = maxX - minX;
		yDistance = maxY - minY;
		//mouseX = xIn;
		pixelX = xDistance / imageWidth;
		mapX = pixelX * xIn + minX;
		yIn = imageHeight - yIn;
		pixelY = yDistance / imageHeight;
		mapY = pixelY * yIn + minY;
		tolerance = pixelX * pixelTolerance;
}

function getImageXY(e) {
	if (isNav) {
		mouseX=e.pageX;
		mouseY=e.pageY;
	} else {
		mouseX=event.clientX + document.body.scrollLeft;
		mouseY=event.clientY + document.body.scrollTop;
	}
	// subtract offsets from page left and top
	mouseX = mouseX-hspc;
	mouseY = mouseY-vspc;
}	
		
// start zoom in.... box displayed
function startZoomBox(e) {
	//alert("startZoomBox() ");
		
	moveLayer("theMap",hspc,vspc);
	getImageXY(e);	
	if (zooming) {
		stopZoomBox(e);
	} else {
		x1=mouseX;
		y1=mouseY
		x2=x1+1;
		y2=y1+1;
		clipLayer("zoomBoxTop",x1,y1,x2,y2);
		clipLayer("zoomBoxLeft",x1,y1,x2,y2);
		clipLayer("zoomBoxRight",x1,y1,x2,y2);
		clipLayer("zoomBoxBottom",x1,y1,x2,y2);
		zooming=true;
		showLayer("zoomBoxTop");
		showLayer("zoomBoxLeft");
		showLayer("zoomBoxRight");
		showLayer("zoomBoxBottom");		
	}
	return false;	
}

// stop zoom box display... zoom in
function stopZoomBox(e) {
	zooming=false;
	window.scrollTo(0,0);
	hideLayer("zoomBoxTop");
	hideLayer("zoomBoxLeft");
	hideLayer("zoomBoxRight");
	hideLayer("zoomBoxBottom");
	
	if ((zright <zleft+2) && (zbottom < ztop+2)) {
		zoomin(e);
	} else {
		var theY = imageHeight - ztop;
		minY = maxY - (pixelY * zbottom);
		maxY = maxY - (pixelY * ztop);
		maxX = pixelX * zright + minX;
		minX = pixelX * zleft + minX;
		theY = imageHeight - zbottom;
		pixelY = yDistance / imageHeight;
		window.scrollTo(0,0);
		
		getMapImage("Refresh");		
	}
	return true;
}

// start zoom out... box displayed
function startZoomOutBox(e) {
	moveLayer("theMap",hspc,vspc);
	getImageXY(e);
	// keep it within the MapImage
	//if ((mouseX<imageWidth) && (mouseY<imageHeight)) {		
		if (zooming) {
			stopZoomOutBox(e);
		} else {
			x1=mouseX;
			y1=mouseY
			x2=x1+1;
			y2=y1+1;
			zleft=x1;
			ztop=y1;
			zbottom=y1;
			zright=x1
			clipLayer("zoomBoxTop",x1,y1,x2,y2);
			clipLayer("zoomBoxLeft",x1,y1,x2,y2);
			clipLayer("zoomBoxRight",x1,y1,x2,y2);
			clipLayer("zoomBoxBottom",x1,y1,x2,y2);
			zooming=true;
			showLayer("zoomBoxTop");
			showLayer("zoomBoxLeft");
			showLayer("zoomBoxRight");
			showLayer("zoomBoxBottom");
		}
	//}
	return false;	
}

// stop zoom out box. . . zoom out
function stopZoomOutBox(e) {
	zooming=false;
	hideLayer("zoomBoxTop");
	hideLayer("zoomBoxLeft");
	hideLayer("zoomBoxRight");
	hideLayer("zoomBoxBottom");
	if ((zright <zleft+2) && (zbottom < ztop+2)) {
		zoomout(e);
	} else {	
		var zWidth = Math.abs(zright-zleft);
		var zHeight = Math.abs(ztop-zbottom);
		var xRatio = imageWidth / zWidth;
		var yRatio = imageHeight / zHeight;
		var xAdd = xRatio * xDistance / 2;
		var yAdd = yRatio * yDistance / 2;
		minX = minX - xAdd;
		maxX = maxX + xAdd;
		maxY = maxY + yAdd;
		minY = minY - yAdd;
		window.scrollTo(0,0);
		getMapImage("Refresh");		
	}
	return true;
}

// start pan.... image will move
function startPan(e) {
	moveLayer("theMap",hspc,vspc);

	getImageXY(e);
	// keep it within the MapImage
	
	if (panning) {
		stopPan(e);
	} else {
		x1=mouseX;
		y1=mouseY
		x2=x1+1;
		y2=y1+1;
		//clipLayer("zoomBox",x1,y1,x2,y2);
		panning=true;
	}
	return false;
}

// stop moving image.... pan 
function stopPan(e) {
	window.scrollTo(0,0);
	panning=false;
	hideLayer("theMap");
	var ixOffset = x2-x1;
	var iyOffset = y1-y2;
	xDistance = maxX-minX;
	yDistance = maxY-minY;
	pixelX = xDistance / imageWidth;
	var theY = imageHeight - ztop;
	pixelY = yDistance / imageHeight;
	var xOffset = pixelX * ixOffset;
	var yOffset = pixelY * iyOffset;
	maxY = maxY - yOffset;
	maxX = maxX - xOffset;
	minX = minX - xOffset;
	minY = minY - yOffset;
	
	getMapImage("Refresh");
	document.mapImage.src = "images/map.gif";
	moveLayer("theMap",hspc,vspc);
	panClipLayer("theMap",0,0,imageWidth,imageHeight);
	showLayer("theMap");
	return true;			
}

// move map image with mouse
function panMouse() {
	var xMove = x2-x1;
	var yMove = y2-y1;
	var cLeft = -xMove;
	var cTop = -yMove;
	var cRight = imageWidth;
	var cBottom = imageHeight;
	if (xMove>0) {
		cLeft = 0;
		cRight = imageWidth - xMove;
	}
	if (yMove>0) {
		cTop = 0;
		cBottom = imageHeight - yMove;
	}
	moveLayer("theMap",xMove,yMove);
	panClipLayer("theMap",cLeft,cTop,cRight,cBottom);
}

function setupOVCoords(xmin,ymin,xmax,ymax) {
	fullOVLeft = xmin;
	fullOVRight = xmax;
	fullOVTop = ymax;
	fullOVBottom = ymin;
	fullOVWidth = Math.abs(fullOVRight - fullOVLeft);
	fullOVHeight = Math.abs(fullOVTop - fullOVBottom);
	//alert(fullOVLeft + ", " + fullOVBottom + ", " + fullOVRight + ", " + fullOVTop);
}

// get coordinates on ov map and reset display
function ovMapClick(x,y) {
	var ovWidth = i2Width;
	var ovHeight = i2Height;
	var ovXincre = fullOVWidth / ovWidth;
	var ovYincre = fullOVHeight / ovHeight;
	var ovX = x;
	var ovY = ovHeight - y;
	var ovmapX = ovX * ovXincre + fullOVLeft;
	var ovmapY = ovY * ovYincre + fullOVBottom;
	//saveLastExtent();
	minX = ovmapX - (xDistance/2);
	maxX = ovmapX + (xDistance/2);
	maxY = ovmapY + (yDistance/2);
	minY = ovmapY - (yDistance/2);
	//alert(ovmapX + "," +  ovmapY + "\nLeft:" + fullLeft + "\nTop:" + fullTop + "\nRight:" + fullRight + "\nBottom:" + fullBottom);
	//var theString = writeXML();
	getMapImage("Refresh");	
}

function fullExtent() {
	minX=fullMinX;
	minY=fullMinY;
	maxX=fullMaxX;
	maxY=fullMaxY;
	
	getMapImage("Refresh");
}

function loadBufferForm(){
	parent.TextFrame.document.location="buffer.htm";
}

function loadServiceChooser() {
	parent.TextFrame.document.location="loadService.htm";
}

function showLegendWin() {
	parent.LegendFrame.document.location="viewLegend.jsp";
}

// get directory path of URL
function getPath(theFullPath) {
	var theSlash = theFullPath.lastIndexOf("/");
	var theDir = theFullPath.substring(0,theSlash);
	if (theDir==null) theDir="";
	theDir = theDir + "/";
	return theDir;
}

var appDir = getPath(document.location.pathname);

function updateCoords(xmin,ymin,xmax,ymax,sb1,sb2) {
	//alert("updateCoords");
	minX=setDecimalString(xmin);
	minY=setDecimalString(ymin);
	maxX=setDecimalString(xmax);
	maxY=setDecimalString(ymax);

	//alert("Returned: " + minX + ", " + minY + ", " + maxX + ", " + maxY);
	xDistance = maxX-minX;
	yDistance = maxY-minY;
	if (fullMinX==fullMaxX) {
		fullMinX=minX;
		fullMaxX=maxX;
		fullMinY=minY;
		fullMaxY=maxY;
	}
	scalebar1Units = sb1;
	scalebar2Units = sb2;
	//putExtentOnOVMap();
}

// get the current coords
function getMouse(e) {
	window.status="";
	getImageXY(e);
	if ((zooming) || (selectBox))  {
		x2=mouseX;
		y2=mouseY;
		setClip();				
	} else if (panning) {
		x2=mouseX;
		y2=mouseY;
		panMouse();			
	}
	if (showXYs) {
		getMapXY(mouseX,mouseY)
		var u = Math.pow(10,roundNum);
		var mouseString = "Map: " + (Math.round(mapX * u)/ u) + " ; " + (Math.round(mapY * u)/ u) + "     Window: " + mouseX + " ; " + mouseY;
		window.status = mouseString;
	}
	if ((zooming) || (panning) || (selectBox))
		return false;
	else
		return true;
}

// clip zoom box layer to mouse coords
function setClip() {	
	var tempX=x1;
	var tempY=y1;
	if (x1>x2) {
		zright=x1;
		zleft=x2;
	} else {
		zleft=x1;
		zright=x2;
	}
	if (y1>y2) {
		zbottom=y1;
		ztop=y2;
	} else {
		ztop=y1;
		zbottom=y2;
	}
	
	if ((x1 != x2) && (y1 != y2)) {
		//clipLayer("zoomBox",zleft,ztop,zright,zbottom);
		clipLayer("zoomBoxTop",zleft,ztop,zright,ztop+2);
		clipLayer("zoomBoxLeft",zleft,ztop,zleft+2,zbottom);
		clipLayer("zoomBoxRight",zright-2,ztop,zright,zbottom);
		clipLayer("zoomBoxBottom",zleft,zbottom-2,zright,zbottom);
	}
}

function displayCoords(i) {
	var theType = "Zoom Rectangle = ";
	if (i==3) theType = "PanOffsets = ";
	if (i==5) theType = "Select Rectangle = ";
	var msg = theType + x1 + ", " + y1 + "  -  " + x2 + ", " + y2;
	//alert(msg);
}

// pan using arrow buttons
function panButton(panType) {
	//alert("Left:" + left + "\nTop:" + top + "\nRight:" + right + "\nBottom:" + bottom + "\nWidth:" + xDistance + "\nHeight:" + yDistance + "\nPanX:" + panX + "\nPanY:" + panY);
	//saveLastExtent();
	xDistance = Math.abs(maxX-minX);
	yDistance = Math.abs(maxY-minY);
	panX = xDistance * panFactor;
	panY = yDistance * panFactor;
	//alert("YY minx=" + minX + " miny=" + minY + " maxx=" + maxX + " maxy=" + maxY + " panX=" + panX + " panY=" + panY);
	switch(panType) {
	case 1:
		//west
		minX = minX - panX;
		maxX = maxX - panX;
		break
	case 2:
		// north
		maxY = maxY + panY;
		minY = minY + panY;
		break
	case 3:
		// east
		maxX = maxX + panX;
		minX = minX + panX;
		break
	case 4:
		// south
		minY = minY - panY;
		maxY = maxY - panY;
		break
	case 5:
		// southwest
		maxY = maxY - panY;
		minX = minX - panX;
		minY = minY - panY;
		maxX = maxX - panX;
		break
	case 6:
		// northwest
		maxY = maxY + panY;
		minX = minX - panX;
		minY = maxY - yDistance;
		maxX = minX + xDistance;
		break
	case 7:
		// northeast
		maxY = maxY + panY;
		minX = minX + panX;
		minY = maxY - yDistance;
		maxX = minX + xDistance;
		break
	case 8:
		// southeast
		maxY = maxY - panY;
		minX = minX + panX;
		minY = maxY - yDistance;
		maxX = minX + xDistance;
	}
	//checkFullExtent();
	//alert("Left:" + left + "\nTop:" + top + "\nRight:" + right + "\nBottom:" + bottom + "\nWidth:" + xDistance + "\nHeight:" + yDistance + "\nPanX:" + panX + "\nPanY:" + panY);
	//var theString = writeXML();
	//sendMapXML();
	//alert("ZZ minx=" + minX + " miny=" + minY + " maxx=" + maxX + " maxy=" + maxY);
	getMapImage("Refresh");
}

//alert("functions.js loaded");
// format decimal numerics from comma to point
// 	SQL format requires English notation
function convertDecimal(theNumString) {
	var replacer = "."
	var re = /,/g;
	var newString = theNumString.replace(re,replacer);
	return newString;
}

// check to see if active layer is in scale threshold and visible 
	// so it can be used for querying
function checkIfActiveLayerAvailable() {
	return true;
}

function loadServiceForm() { 
	//alert("List service");
	var theForm = parent.TextFrame.document.forms[0];
	theForm.action = parent.MapFrame.MOJEJBController;
	theForm.Cmd.value = "ListServices";
	//alert("List service cmd=" + theForm.Cmd.value);
	theForm.submit();
}

// the starting point. . . it all starts here on loading
function checkParams() {
	appDir = getPath(document.location.pathname);
	// global for overview map. . . change if not on same frame as Map
	ovImageVar = document.ovImage;
	debugOn = 0;
	if (parent.TextFrame==null) {
		useTextFrame = false;
		useExternalWindow=true;
	}
	//if (!hasLayer("measureBox")) useMeasure=false;
	//if ((!useMeasure) && (!drawScaleBar)) useSetUnits=false;
	//if (ovImageVar==null) hasOVMap = false;
	if (parent.TOCFrame==null) hasTOC = false;
	if (parent.ModeFrame==null) useModeFrame = false;
	
	
	if (isIE)	{
		if (hasLayer("theTop")) document.all.theTop.style.cursor = "crosshair";
		if (hasOVMap) ovImageVar.style.cursor = "hand";
	}
		
	if (hasOVMap) {
		// size of ov map image
		i2Width = parseInt(ovImageVar.width);
		i2Height = parseInt(ovImageVar.height);
		forceNewOVMap = false;
		// position of ov map
		//ovMapLeft = iWidth - (i2Width + 6);
		//ovMapTop = 2;
	}
	if (webParams!="") {
		//alert(webParams);
		getCommandLineParams(webParams);
	} else { 
		loadServiceForm();
	}
}

// get URLs and extents from URL
function getCommandLineParams(cmdString) {
	// Parse out from URL querystring parameters
	//
	//setLayerVisible.length=0;
	var cmdString2 = cmdString.toUpperCase();
	var startpos = 0;
	var endpos = 0;
	var pos = cmdString2.indexOf("HOST=");
	if (pos!=-1) {
		startpos = pos + 5;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		hostName = cmdString.substring(startpos,endpos);
		serverURL  = MOJEJBController + "?ServiceName=";
	}
	pos = cmdString2.indexOf("SERVICE="); // formally was MAPSERVICE=
	if (pos!=-1) {
		startpos = pos + 8;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		imsURL = serverURL + cmdString.substring(startpos,endpos);
	}
	pos = cmdString2.indexOf("OVMAP="); // formally was OVMAPSERVICE=
	if (pos!=-1) {
		startpos = pos + 6;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		imsOVURL = serverURL + cmdString.substring(startpos,endpos);
	}
	pos = cmdString2.indexOf("BOX=");
	if (pos!=-1) {
		startpos = pos + 4;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		var boxString = cmdString.substring(startpos,endpos);
		//alert(boxString);
		var xyBox = boxString.split(":");
		if (xyBox.length==4) {
			startLeft = parseFloat(xyBox[0]);
			startBottom = parseFloat(xyBox[1]);
			startRight = parseFloat(xyBox[2]);
			startTop = parseFloat(xyBox[3]);
			eLeft=startLeft;
			eBottom=startBottom;
			eRight=startRight;
			eTop = startTop;
		}
		//xyBox=null;
	}
	pos = cmdString2.indexOf("MAXRECT=");
	if (pos!=-1) {
		startpos = pos + 8;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		var boxString = cmdString.substring(startpos,endpos);
		//alert(boxString);
		var xyBox = boxString.split(":");
		if (xyBox.length==4) {
			limitLeft = xyBox[0];
			limitBottom = xyBox[1];
			limitRight = xyBox[2];
			limitTop = xyBox[3];
		}
		//xyBox=null;
	}
	
	pos = cmdString2.indexOf("SERVICENAME=");
	if (pos > -1) { 
		startpos = pos + 12;
		endpos = cmdString.indexOf("&",startpos);
		if (endpos==-1) endpos = cmdString.length;
		var serviceName = cmdString.substring(startpos, endpos);
		
		//alert("service name=" + serviceName);
		if (serviceName != "") {		
			parent.ToolFrame.document.location = parent.MapFrame.ContextPath + "/toolbar.htm";
			parent.MapFrame.ServiceName = serviceName;
			var theForm = parent.TOCFrame.document.forms[0];
			theForm.Width.value = getWinWidth();
			theForm.Height.value = getWinHeight();
			theForm.Cmd.value = "InitMap"; 
			theForm.ServiceName.value = serviceName;
			theForm.action = parent.MapFrame.MOJEJBController;
			theForm.submit();
		}
	}
}


// get directory path of URL
function getPath(theFullPath) {
	var theSlash = theFullPath.lastIndexOf("/");
	var theDir = theFullPath.substring(0,theSlash);
	if (theDir==null) theDir="";
	theDir = theDir + "/";
	return theDir;

}

// check for existance of layer
function hasLayer(name) {
	var result = false;
	if (isNav4) {
		if (document.layers[name]!=null) result=true;
	}  else if (isIE) {
		if (eval('document.all.' + name)!=null) result=true;
	} else if (isNav) {
		var theElements = document.getElementsByTagName("DIV");
		var theObj;
		var j = -1;
		for (i=0;i<theElements.length;i++) {
			if (theElements[i].id==name) result=true;
		}
    }
	return result;
}

function refreshSelectionQueryFind() {
	var frm = parent.TOCFrame.document.forms[0];
	var cmd = parent.MapFrame.SelectionQueryFindCmd;
		switch (cmd) {
		case "Query":
			frm.QueryString.value = parent.MapFrame.QueryString;
		case "Identify":
		case "Select":
			frm.Cmd.value="Refresh_Select";
			break;
		case "Find": 
			frm.FindString.value = parent.MapFrame.FindString;
			frm.Cmd.value="Refresh_Find";
			break;
		case "Buffer_Find":
			frm.FindString.value = parent.MapFrame.FindString;
		case "Buffer_Select":
			frm.Cmd.value = "Refresh_Buffer_Select";
			frm.BufferDistance.value = parent.MapFrame.BufferDistance;
			frm.BufferUnit.value = parent.MapFrame.BufferUnit;
			frm.TargetLayer.value = parent.MapFrame.TargetLayer;
			break;
		}
		frm.SelectMinX.value = parent.MapFrame.SelectMinX;
		frm.SelectMinY.value = parent.MapFrame.SelectMinY;
		frm.SelectMaxX.value = parent.MapFrame.SelectMaxX;
		frm.SelectMaxY.value = parent.MapFrame.SelectMaxY;
		frm.QueryString.value = parent.MapFrame.QueryString;
		frm.FindString.value = parent.MapFrame.FindString;
		frm.action = parent.MapFrame.MOJEJBController;
		frm.ServiceName.value = parent.MapFrame.ServiceName;
		frm.submit();
}

// character used by browser in decimals - either point or comma
var decimalChar = ((("theChar is" + (10/100)).indexOf("."))==-1) ? "," : ".";
//alert("Decimal character: " + decimalChar);
var forceCommaInRequest = false;

// set number string to have decimal character to match browser language type - point or comma
function setDecimalString(numberString) {
	if (numberString.indexOf(",")!=-1) forceCommaInRequest = true;
	if (decimalChar==".") {
		numberString = numberString.replace(/,/g, ".");
	} else {
		numberString = numberString.replace(/./g, ",");
	}
	return Number(numberString);
}

function forceComma(theNumber) {
	var comma = ",";
	var dot = ".";
	var charOut = comma;
	var charIn = dot;
	var numberString = new String(theNumber);
	if (forceCommaInRequest) {
		charOut = dot;
		charIn = comma;
	}
	var pos = numberString.indexOf(charOut);
	if (pos!=-1) {
		var begin = numberString.substring(0,pos);
		var ending = numberString.substring(pos+1, numberString.length);
		numberString = begin + charIn + ending;
	}
	return numberString;
}