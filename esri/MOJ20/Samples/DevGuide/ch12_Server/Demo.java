import com.esri.mo2.svr.map.Map;
import com.esri.mo2.cs.geom.Envelope;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

public class Demo extends HttpServlet {
    private Map map;

    public void init() throws ServletException {
        String country = "com.esri.mo2.src.file.FileSystemConnection!D:/ESRI/ESRIDATA/WORLD/cntry92.shp!";
        map = new Map();
        try {
            com.esri.mo2.src.sys.Content content =
                com.esri.mo2.src.sys.ContentUtility.accessContent(country);
            String contentType = com.esri.mo2.map.dpy.Layer.class.getName();
            if (content.isContentTypeAvailable(contentType)) {
                com.esri.mo2.map.dpy.Layer layer = (com.esri.mo2.map.dpy.Layer)content.getData(contentType);
                map.addLayer(layer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws javax.servlet.ServletException, java.io.IOException  {
        doGetPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws javax.servlet.ServletException, java.io.IOException  {
        doGetPost(request, response);
    }

    private void doGetPost(HttpServletRequest request, HttpServletResponse response)
         throws javax.servlet.ServletException, java.io.IOException {
        String cmd = request.getParameter("Cmd");
        if (cmd==null) cmd = "InitMap";

        double minx=0d, miny=0d, maxx=0d, maxy=0d;
        int width=500, height=500;
        try {
            minx = Double.valueOf(request.getParameter("MinX")).doubleValue();
            miny = Double.valueOf(request.getParameter("MinY")).doubleValue();
            maxx = Double.valueOf(request.getParameter("MaxX")).doubleValue();
            maxy = Double.valueOf(request.getParameter("MaxY")).doubleValue();
            width = Integer.valueOf(request.getParameter("Width")).intValue();
            height = Integer.valueOf(request.getParameter("Height")).intValue();
        } catch (Exception ex) {}  // ignore any conversion error

        if (cmd.equalsIgnoreCase("Get_Image")) {
            Envelope extent = new Envelope(minx, miny, maxx-minx, maxy-miny);
            map.setExtent(extent);
            java.awt.image.BufferedImage image = map.getImage(new java.awt.Dimension(width, height));
            com.esri.mo2.map.img.ImageWriter iw = com.esri.mo2.map.img.ImageSupport.createWriterByType("png");
            iw.exportImage(response.getOutputStream(), image);
            return;
        }

        if (cmd.equalsIgnoreCase("InitMap")) {
            Envelope fullextent = map.getFullExtent();
            minx = fullextent.getXMin();     miny = fullextent.getYMin();
            maxx = fullextent.getXMax();     maxy = fullextent.getYMax();
        }

        double dx = maxx-minx, dy = maxy-miny;
        if (cmd.equalsIgnoreCase("ZoomIn")) {
            minx += dx/4;       miny += dy/4;
            maxx -= dx/4;       maxy -= dy/4;
        }
        if (cmd.equalsIgnoreCase("ZoomOut")) {
            minx -= dx/4;       miny -= dy/4;
            maxx += dx/4;       maxy += dy/4;
        }
        if (cmd.equalsIgnoreCase("FullExtent")) {
            Envelope fullextent = map.getFullExtent();
            minx = fullextent.getXMin();       miny = fullextent.getYMin();
            maxx = fullextent.getXMax();       maxy = fullextent.getYMax();
        }

        redirectToMapPage(request, response, minx, miny, maxx, maxy, width, height, cmd);
    }

    private void redirectToMapPage(
                HttpServletRequest request, HttpServletResponse response,
                double minx, double miny, double maxx, double maxy,
                int width, int height, String cmd)
        throws javax.servlet.ServletException, java.io.IOException {
        request.setAttribute("MinX", String.valueOf(minx));
        request.setAttribute("MinY", String.valueOf(miny));
        request.setAttribute("MaxX", String.valueOf(maxx));
        request.setAttribute("MaxY", String.valueOf(maxy));
        request.setAttribute("Width", String.valueOf(width));
        request.setAttribute("Height", String.valueOf(height));
        request.setAttribute("Cmd", cmd);
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/Map.jsp");
        rd.forward(request, response);
    }
}