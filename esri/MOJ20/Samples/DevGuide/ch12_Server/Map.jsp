<%@ page language="java" buffer="8kb"%>
<%
    // get parameters
    String minx = (String)request.getAttribute("MinX");
    String miny = (String)request.getAttribute("MinY");
    String maxx = (String)request.getAttribute("MaxX");
    String maxy = (String)request.getAttribute("MaxY");
    String width = (String)request.getAttribute("Width");
    String height = (String)request.getAttribute("Height");
    String cmd = (String)request.getAttribute("Cmd");
    // servlet URL
    String servletURL = "/Demo/servlet/DemoServlet";
    // build the image URL
    StringBuffer imageURLBuf = new StringBuffer(servletURL+"?Cmd=Get_Image");
    imageURLBuf.append("&MinX=");   imageURLBuf.append(minx);
    imageURLBuf.append("&MinY=");   imageURLBuf.append(miny);
    imageURLBuf.append("&MaxX=");   imageURLBuf.append(maxx);
    imageURLBuf.append("&MaxY=");   imageURLBuf.append(maxy);
    imageURLBuf.append("&Width=");  imageURLBuf.append(width);
    imageURLBuf.append("&Height="); imageURLBuf.append(height);
    String imageURL = imageURLBuf.toString();
    // set last command as checked in radio buttons
    String zoomInChecked = "";
    String zoomOutChecked = "";
    if (cmd!=null && cmd.equalsIgnoreCase("ZoomOut")) {
        zoomOutChecked = "Checked";
        cmd="ZoomOut";
    } else {
        zoomInChecked = "Checked";
        cmd="ZoomIn";
    }
%>
<HTML>
<head>
<title>MOJava Server Components Demo</title>
</head>
<body>
<form action='<%= servletURL%>'>
<input type='hidden' name='Cmd' value='<%= cmd%>'>
<input type='hidden' name='MinX' value='<%= minx%>'>
<input type='hidden' name='MinY' value='<%= miny%>'>
<input type='hidden' name='MaxX' value='<%= maxx%>'>
<input type='hidden' name='MaxY' value='<%= maxy%>'>
<input type='hidden' name='Width' value='<%= width%>'>
<input type='hidden' name='Height' value='<%= height%>'>
<center>
<h2>World Map</h2>
</center>
<table width=500 cellspacing=0 cellpadding=0 border=1 align='center'>
        <tr>
            <td><img border=0 name=map style='cursor:hand;' galleryimg='no' src=<%= imageURL%> onClick='document.forms[0].submit();return false;'> </td>
        </tr>
        <tr>
            <table width=502 cellspacing=0 cellpadding=0 border=0 align='center' bgcolor=darkblue>
            <tr>
                <td align='center'><input type=radio name=ZoomIn align=middle value='Zoom In' <%= zoomInChecked%>    onClick="document.forms[0].Cmd.value='ZoomIn'; document.forms[0].ZoomIn.checked=true; document.forms[0].ZoomOut.checked=false; return false;" ><font color="#ffffff">Zoom In</font></td>
                <td align='center'><input type=radio name=ZoomOut align=middle value='Zoom Out' <%= zoomOutChecked%> onClick="document.forms[0].Cmd.value='ZoomOut'; document.forms[0].ZoomOut.checked=true; document.forms[0].ZoomIn.checked=false; return false;" ><font color="#ffffff">Zoom Out<font></td>
                <td align='center'><input type=radio name=zoom align=middle value='FullExtent' onClick="document.forms[0].Cmd.value='FullExtent'; document.forms[0].submit(); return false;"><font color="#ffffff">Full Extent<font></td>
            </tr>
            </table>
	    </tr>
</table>
</body>
</HTML>