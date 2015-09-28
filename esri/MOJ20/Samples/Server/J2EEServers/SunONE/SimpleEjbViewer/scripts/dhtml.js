/**********************************************************
*	dhtml.js
*
**********************************************************/


/*
***************************************************************************************

DHTML layer functions

***************************************************************************************
*/
		
// Create a DHTML layer
function createLayer(name, inleft, intop, width, height, visible, content) {
	  var layer;
	  if (isNav4) {
	    document.writeln('<layer name="' + name + '" left=' + inleft + ' top=' + intop + ' width=' + width + ' height=' + height +  ' visibility=' + (visible ? '"show"' : '"hide"') +  '>');
	    document.writeln(content);
	    document.writeln('</layer>');
	  } else {
	    document.writeln('<div id="' + name + '" style="position:absolute; overflow:hidden; left:' + inleft + 'px; top:' + intop + 'px; width:' + width + 'px; height:' + height + 'px;' + '; z-index:1; visibility:' + (visible ? 'visible;' : 'hidden;') +  '">');
	    document.writeln(content);
	    document.writeln('</div>');
	  }
	  
	  //alert("createLayer()");
}

// get the layer object called "name"
function getLayer(name) {
	  if (isNav4)
	    return(document.layers[name]);
	  else if (isIE4) {
	    layer = eval('document.all.' + name + '.style');
	    return(layer);
	  } else if (is5up) {
		var theObj = document.getElementById(name);
		//alert("getLayer() " + name + " " + theObj);
		return theObj.style
	  }
	  else
	    return(null);
}
		
// move layer to x,y
function moveLayer(name, x, y) {		
  	var layer = getLayer(name);		
  	if (isNav4)
    	layer.moveTo(x, y);
  	//if (document.all) {
	 else {
    	layer.left = x + "px";
   		layer.top  = y + "px";
  	}
}
		
// set layer background color
function setLayerBackgroundColor(name, color) {		
  	var layer = getLayer(name);		
 	 if (isNav4)
    	layer.bgColor = color;
  	//else if (document.all)
	else
    	layer.backgroundColor = color;
}

// toggle layer to invisible
function hideLayer(name) {		
  	var layer = getLayer(name);		
  	if (isNav4)
    	layer.visibility = "hide";
  	//if (document.all)
	else
   		 layer.visibility = "hidden";
		 //layer.display="none";
}

// toggle layer to visible
function showLayer(name) {		
  	var layer = getLayer(name);		
  	if (isNav4)
    	layer.visibility = "show";
  	//if (document.all)
	else
   	 layer.visibility = "visible";
	 //layer.display="block";
}

function clipLayer(name, clipleft, cliptop, clipright, clipbottom) {		
	  var layer = getLayer(name);
	  if (isNav4) {
		    layer.clip.left   = clipleft;
		    layer.clip.top    = cliptop;
		    layer.clip.right  = clipright;
		    layer.clip.bottom = clipbottom;
	  }	  else {
		    //layer.clip = 'rect(' + cliptop + ' ' +  clipright + ' ' + clipbottom + ' ' + clipleft +')';
			//*
			var newWidth = clipright - clipleft;
			var newHeight = clipbottom - cliptop;
			layer.height = newHeight;
			layer.width	= newWidth;
			layer.top	= cliptop  + "px";
			layer.left	= clipleft + "px";
			//*/
		}

}

function panClipLayer(name, clipleft, cliptop, clipright, clipbottom) {		
	  var layer = getLayer(name);
	  if (isNav4) {
		    layer.clip.left   = clipleft;
		    layer.clip.top    = cliptop;
		    layer.clip.right  = clipright;
		    layer.clip.bottom = clipbottom;
	  }	  else {
		    layer.clip = 'rect(' + cliptop + ' ' +  clipright + ' ' + clipbottom + ' ' + clipleft +')';
			/*
			var newWidth = clipright - clipleft;
			var newHeight = clipbottom - cliptop;
			layer.height = newHeight;
			layer.width	= newWidth;
			layer.top	= cliptop  + "px";
			layer.left	= clipleft + "px";
			*/
		}

}
// replace layer's content with new content
	// not working with Nav 6
function replaceLayerContent(name, content) {
	  if (isNav4) {
		    var layer = getLayer(name);
		    layer.document.open();
		    layer.document.writeln(content);
		    layer.document.close();
	  }  else if (isIE) {
		    var str = "document.all." + name + ".innerHTML = '" + content + "'";
		    eval(str);
	  }
}

// get the Map Image width
function getWinWidth () {
	var mapFrameWidth = thePageWin.innerWidth;
	
	if (mapFrameWidth == null) {
		mapFrameWidth = document.body.clientWidth;
	}
	return mapFrameWidth;
}

 //get the Map Image height
function getWinHeight () {
	var mapFrameHeight = thePageWin.innerHeight;
	
	if (mapFrameHeight == null) {
		mapFrameHeight = document.body.clientHeight;
	}
	return mapFrameHeight;
}
	
// get the layer object called "name" if in OverviewFrame
function getOVLayer(name) {
	if (parent.OverviewFrame!=null) {
		  if (isNav4)
		    return(parent.OverviewFrame.document.layers[name]);
		  else if (isIE4) {
		    layer = eval('parent.OverviewFrame.document.all.' + name + '.style');
		    return(layer);
		  } else if (is5up) {
			var theObj = parent.OverviewFrame.document.getElementById(name);
			return theObj.style
		  }
		  else
		    return(null);
	} else
		return getLayer(name);
}

// toggle Overview layer to visible - if in OverviewFrame
function showOVLayer(name) {		
  	var layer = getOVLayer(name);		
  	if (isNav4)
    	layer.visibility = "show";
  	//if (document.all)
	else
   	 layer.visibility = "visible";
	 //layer.display="block";
}

// toggle Overview layer to invisible - if in OverviewFrame
function hideOVLayer(name) {		
  	var layer = getOVLayer(name);		
  	if (isNav4)
    	layer.visibility = "hide";
  	//if (document.all)
	else
   	 layer.visibility = "hidden";
	 //layer.display="block";
}

// clip layer in OverviewFrame
function clipOVLayer(name, clipleft, cliptop, clipright, clipbottom) {		
	  var layer = getOVLayer(name);
	  if (isNav4) {
		    layer.clip.left   = clipleft;
		    layer.clip.top    = cliptop;
		    layer.clip.right  = clipright;
		    layer.clip.bottom = clipbottom;
	  }	  else {
		    //layer.clip = 'rect(' + cliptop + ' ' +  clipright + ' ' + clipbottom + ' ' + clipleft +')';
			if ((isNaN(clipright)) || (isNaN(clipleft)) || (isNaN(cliptop)) || (isNaN(clipbottom))) {
				layer.visibility = "hidden";
			} else {
				var newWidth = clipright - clipleft;
				var newHeight = clipbottom - cliptop;
				if ((newWidth<=0) || (newHeight<=0) || (isNaN(newWidth)) || (isNaN(newHeight))) {
					layer.visibility = "hidden";
				} else {
					//if (newWidth.isNaN) newWidth = parseInt(i2Width);
					//if (newHeight.isNaN) newHeight = parseInt(i2Height);
					//alert(newWidth + "," + newHeight);
					
					layer.height = newHeight;
					layer.width	= newWidth;
					//var theTop = parseInt((parseFloat(cliptop) * 10 + 0.5)/10);
					//var theLeft = parseInt((parseFloat(clipleft) * 10 + 0.5)/10);
					//alert(cliptop + " " + clipleft);
					layer.top	= cliptop  + "px";
					layer.left	= clipleft + "px";
					//layer.display= "none";
					layer.visibility = "visible";
				}
			}
		}
}

// plot extent box on overview map - only for Overview if in OverviewFrame
function putExtentOnOVMap() {
	var ovXincre = fullOVWidth / i2Width;
	var ovYincre = fullOVHeight / i2Height;
	var vleft = (minX - fullOVLeft) / ovXincre + ovBorderWidth;
	var vright = (maxX - fullOVLeft) / ovXincre + ovBorderWidth;
	var vtop = (fullOVTop - maxY) / ovYincre + ovBorderWidth;
	var vbottom = (fullOVTop - minY) / ovYincre + ovBorderWidth;
	//if (vleft<ovHspc) vleft = ovHspc;
	//if (vtop<ovVspc) vtop = ovVspc;
	if (vright>i2Width+ovBorderWidth) vright = i2Width;
	if (vbottom>i2Height+ovBorderWidth) vbottom = i2Height;
	if (!isNav4) {
		vleft = vleft + ovHspc;
		vright = vright + ovHspc;
		vtop = vtop + ovVspc;
		vbottom = vbottom + ovVspc;
		if (vleft<ovHspc) vleft = ovHspc;
		if (vtop<ovVspc) vtop = ovVspc;
		if (vright>i2Width+ovHspc) vright = i2Width + ovHspc;
		if (vbottom>i2Height+ovVspc) vbottom = i2Height + ovVspc;
	}
	if (maxY>fullOVBottom) {
		clipOVLayer("zoomOVBoxTop",vleft,vtop-ovExtentBoxSize,vright+1,vtop);
		showOVLayer("zoomOVBoxTop");
	} else {
		hideOVLayer("zoomOVBoxTop");		
	}
	if (minX<fullOVRight) {
		clipOVLayer("zoomOVBoxLeft",vleft-ovExtentBoxSize,vtop-ovExtentBoxSize,vleft,vbottom);
		showOVLayer("zoomOVBoxLeft");
	} else {
		hideOVLayer("zoomOVBoxLeft");
	}
	if (maxX>fullOVLeft) {
		clipOVLayer("zoomOVBoxRight",vright,vtop-ovExtentBoxSize,vright+ovExtentBoxSize,vbottom);
		showOVLayer("zoomOVBoxRight");
	} else {
		hideOVLayer("zoomOVBoxRight");
	} 
	if (minY<fullOVTop) {
		clipOVLayer("zoomOVBoxBottom",vleft,vbottom-ovExtentBoxSize,vright+1,vbottom);
		showOVLayer("zoomOVBoxBottom");
	} else {
		hideOVLayer("zoomOVBoxBottom");

	}

}

	

// setup test for Nav 4.0
var isIE = false;
var isNav = (navigator.appName.indexOf("Netscape")>=0);
var isNav4 = false;
var isIE4 = false;
var is5up = false;
//alert(navigator.appVersion);
if (isNav) {
	
	if (parseFloat(navigator.appVersion)<5) {
		isNav4=true;
		//alert("Netscape 4.x or older");
	} else {
		is5up = true;
	}
} else {
	isIE4=true;
	isIE=true;
	if ((navigator.appVersion.indexOf("MSIE 5")>0) || (navigator.appVersion.indexOf("MSIE 6")>0)) {
		isIE4 = false;
		is5up = true;
		//alert("IE5");
	}
}	
		
// Set up event capture for mouse movement
if (isNav4) {
	document.captureEvents(Event.MOUSEMOVE);
	document.captureEvents(Event.MOUSEDOWN);
	document.captureEvents(Event.MOUSEUP);
}
document.onmousemove = getMouse;
document.onmousedown = mapTool;
document.onmouseup = mapTool;

//alert("dhtml.js loaded");
