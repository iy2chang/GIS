/**********************************************************
*	select.js
*
*	functions for selection
*
**********************************************************/
	//mode - 0=selection; 1=query
var queryMode=1;
	//mode - 1=query; 2=box,point; 3=line,polygon
var selectionMode=1;
var setQueryString="";

// start select box display
function startSelectBox(e) {
	moveLayer("theMap",hspc,vspc);
	getImageXY(e);
	// keep it within the MapImage
	//if ((mouseX<iWidth) && (mouseY<iHeight)) {
		if (selectBox) {
			stopSelectBox(e);
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
			selectBox=true;
			
			showLayer("zoomBoxTop");
			showLayer("zoomBoxLeft");
			showLayer("zoomBoxRight");
			showLayer("zoomBoxBottom");
		}
	//}
	return false;	
}

// stop select box display..... select
function stopSelectBox(e) {
	selectBox=false;
	
	hideLayer("zoomBoxTop");
	hideLayer("zoomBoxLeft");
	hideLayer("zoomBoxRight");
	hideLayer("zoomBoxBottom");
	 
	if ((zright <zleft+2) && (zbottom < ztop+2)) {
		/*
		getMapXY(mouseX,mouseY);
		var searchTolerance = (xDistance/imageWidth) * pixelTolerance;
		tempLeft = mapX-searchTolerance;
		tempTop = mapY-searchTolerance;
		tempRight = mapX+searchTolerance;
		tempBottom = mapY+searchTolerance;
		
		//select(e);
		 */
	} else {
		pixelX = xDistance / imageWidth;
		var theY = imageHeight - ztop;
		pixelY = yDistance / imageHeight;
		tempTop = pixelY * theY + minY;
		tempRight = pixelX * zright + minX;
		tempLeft = pixelX * zleft + minX;
		theY = imageHeight - zbottom;
		pixelY = yDistance / imageHeight;
		tempBottom = pixelY * theY + minY;
	}
		window.scrollTo(0,0);
		
		
	if (toolMode==5) {
		if (tempLeft+tempBottom+tempRight+tempTop==0) {
			alert("Set an area with the cursor");
		} else {
			parent.MapFrame.QueryString = "";
			parent.MapFrame.HasSelection = false;
			var f = parent.TextFrame.document.forms[0];
			f.SelectMinX.value = forceComma(tempLeft);
			f.SelectMinY.value = forceComma(tempBottom);
			f.SelectMaxX.value = forceComma(tempRight);
			f.SelectMaxY.value = forceComma(tempTop);
			f.action = parent.MapFrame.MOJServletController;
			f.Active.value = parent.MapFrame.ActiveLayerIndex;
			f.ServiceName.value = parent.MapFrame.ServiceName;
			f.Cmd.value = "Select";
			//alert("select minx=" + tempLeft + " miny=" + tempBottom + " maxx=" + tempRight + " maxy=" + tempTop + " service name=" + parent.MapFrame.ServiceName + " "  + parent.MapFrame.ActiveLayerIndex);
			f.submit();
		}
	} else {
		alert("Use the Set Select Area tool to set area");
	}
		
	return true;
}

function clearSelectBox() {
	/*
	hideLayer("zoomBoxTop");
	hideLayer("zoomBoxLeft");
	hideLayer("zoomBoxRight");
	hideLayer("zoomBoxBottom");
	*/
	zooming=false;
	panning=false;
	selectBox=false;
	var f = parent.PostFrame.document.forms[0];
	if (toolMode==5) {
		tempLeft = 0;
		tempBottom = 0;
		tempRight = 0;
		tempTop = 0;	
		f.SelectMinX.value = forceComma(tempLeft);
		f.SelectMinY.value = forceComma(tempBottom);
		f.SelectMaxX.value = forceComma(tempRight);
		f.SelectMaxY.value = forceComma(tempTop);
	}
	f.Query.value = "no";
	f.action = "map.jsp";
	//f.submit();
	getMapImage();
 	parent.TextFrame.document.URL = "blank.htm";
}

/*
function goSelect() {
	hideLayer("zoomBoxTop");
	hideLayer("zoomBoxLeft");
	hideLayer("zoomBoxRight");
	hideLayer("zoomBoxBottom");
	if (toolMode==5) {
		if (tempLeft+tempBottom+tempRight+tempTop==0) {
			alert("Set an area with the cursor");
		} else {
			//alert("Box: " + tempLeft + ", " + tempBottom + ", " + tempRight + ", " + tempTop + "\nSelection Code executed here");			
			var f = parent.PostFrame.document.forms[0];
			f.SelectMinX.value = tempLeft;
			f.SelectMinY.value = tempBottom;
			f.SelectMaxX.value = tempRight;
			f.SelectMaxY.value = tempTop;
			f.action = "selectAndHighlight.jsp";
			getMapImage();
		}
	} else {
		alert("Use the Set Select Area tool to set area");
	}
}
*/

