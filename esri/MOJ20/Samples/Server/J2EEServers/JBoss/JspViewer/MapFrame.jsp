<!-- %@ page language="java" buffer="8kb" errorPage="errorpage.jsp" % -->
<%@ page language="java" buffer="8kb"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html>
<head>
<title>MOJ Server JSP Viewer</title>
<script language="JavaScript" src="scripts/resource.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/parameters.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/print.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/functions.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/dhtml.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/select.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/buffer.js" type="text/javascript"></script>
<script language="JavaScript" src="scripts/query.js" type="text/javascript"></script>
</head>

<body bgcolor="White" leftmargin=0 topmargin=0>
<script type="text/javascript" language="JavaScript1.2">

    <%
    //get the context path for this web-app
    String cp = request.getContextPath();
    if(cp.equals("/")) cp = "";
    System.out.println("Context path=>" + cp);
    %>

    ContextPath = "<%=cp%>";
    MOJServerController = "<%=cp%>/JSPViewerController.jsp";

	var thePageWin = window;
	var thePageDoc = document;
	//alert("MapFrame.htm " + thePageDoc.body.clientWidth);
	var mWidth = getWinWidth();
	var mHeight = getWinHeight();
	var sWidth = screen.width;
	var sHeight = screen.height;
	//alert("mWidth=" + mWidth + " mHeight=" + mHeight + " sWidth=" + sWidth + " sHeight=" + sHeight);

	// ov map variables
	var ovRatio = mWidth / mHeight;
	var locHeight = parseInt(mHeight/5);
	var locWidth = parseInt(locHeight*ovRatio);
	var loadBannerLeft = parseInt((mWidth - 273)/2);
	var loadBannerTop = parseInt((mHeight - 30)/2);

	var content = '<img name="mapImage" id="mapImage" src="images/map.gif" width=' + mWidth + ' height=' + mHeight + ' hspace=0 vspace=0 border=0  alt=""> ';
	createLayer("theMap",0,0,sWidth,sHeight,true,content);

	// overview map and shadow
	content = '<img name="ovShadowImage" src="images/gray_screen2.gif" border=0 ';
	//if (isNav5up) content += 'width=' + (locWidth+4) + ' height=' + (locHeight+4);
	content += '>';
	createLayer("ovShadow",4,4,(locWidth+4),(locHeight+4),false,content);
	if ((isNav4) || (isIE)) clipLayer("ovShadow",0,0,locWidth+4,locHeight+4);
	//content = '<img name="ovImage" src="images/locmap.gif" border=2 width=' + locWidth + ' height=' + locHeight +' onmousedown="ovMap2Click(event)">';
	content = '<img name="ovImage" src="images/locMap.gif" border=2 width=' + locWidth + ' height=' + locHeight +'>';
	createLayer("ovLayer",0,0,(locWidth+4),(locHeight+4),false,content);
	setLayerBackgroundColor("ovLayer", "white");
	// overview extent box
	content = '<img name="zoomOVImageTop" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomOVBoxTop",0,0,locWidth+4,locHeight+4,false,content);
	content = '<img name="zoomOVImageLeft" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomOVBoxLeft",0,0,locWidth+4,locHeight+4,false,content);
	content = '<img name="zoomOVImageRight" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomOVBoxRight",0,0,locWidth+4,locHeight+4,false,content);
	content = '<img name="zoomOVImageBottom" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomOVBoxBottom",0,0,locWidth+4,locHeight+4,false,content);

	// set Overview map extent box color
	setLayerBackgroundColor("zoomOVBoxTop", zoomBoxColor);
	setLayerBackgroundColor("zoomOVBoxLeft", zoomBoxColor);
	setLayerBackgroundColor("zoomOVBoxRight", zoomBoxColor);
	setLayerBackgroundColor("zoomOVBoxBottom", zoomBoxColor);

	// zoom/selection box
	content = '<img name="zoomImagmaxY" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomBoxTop",0,0,mWidth,mHeight,false,content);
	content = '<img name="zoomImagminX" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomBoxLeft",0,0,mWidth,mHeight,false,content);
	content = '<img name="zoomImagmaxX" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomBoxRight",0,0,mWidth,mHeight,false,content);
	content = '<img name="zoomImagminY" src="images/pixel.gif" width=1 height=1>';
	createLayer("zoomBoxBottom",0,0,mWidth,mHeight,false,content);
	content = '<img name="pixel" src="images/pixel.gif" width=1 height=1>';
	createLayer("theTop",-1,-1,(mWidth+1),(mHeight+1),true,content);

	// set zoom/selection box color
	setLayerBackgroundColor("zoomBoxTop", "red");
	setLayerBackgroundColor("zoomBoxLeft", "red");
	setLayerBackgroundColor("zoomBoxRight", "red");
	setLayerBackgroundColor("zoomBoxBottom", "red");

	imageWidth = mWidth;
	imageHeight = mHeight;

</script>

</body>
</html>
