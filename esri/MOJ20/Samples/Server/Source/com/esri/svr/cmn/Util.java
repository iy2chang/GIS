package com.esri.svr.cmn;


import com.esri.mo2.cs.geom.Polygon;
import com.esri.mo2.cs.geom.MultiPoint;
import com.esri.mo2.cs.geom.Polyline;
import com.esri.mo2.data.feat.BaseFeature;
import com.esri.mo2.data.feat.BaseFields;
import com.esri.mo2.map.dpy.Element;
import com.esri.mo2.cs.geom.FeatureGeometry;
import com.esri.mo2.map.draw.Renderer;

/**
 * This class provides some static methods for creating Element based on given
 * gemetry.
 */
public class Util {
  /**
    * Constructs a graphic Element from the given geometry object.
    * @param featureGeometry  a FeatureGeometry object
    * @return a BaseFeatureElement object
    */
    public static Element createFeatureElement(FeatureGeometry featureGeometry) {
        if (featureGeometry instanceof MultiPoint)
            return createFeatureElement((MultiPoint)featureGeometry);
        else if (featureGeometry instanceof Polyline)
            return createFeatureElement((Polyline)featureGeometry);
        else if (featureGeometry instanceof com.esri.mo2.cs.geom.Polygon)
            return createFeatureElement((Polygon)featureGeometry);

        return null;
    }

    /**
     * Create a feature element based on the given polygon
     * @param polygon the polygon
     * @param defaultRenderer
     * @param selectionRenderer
     * @return a BaseFeature object
     */
    public static Element createFeatureElement(Polygon polygon, Renderer defaultRenderer, Renderer selectionRenderer)
    {
        BaseFeature newFeature = new BaseFeature();
        BaseFields fields = new BaseFields(1);
        fields.addField(com.esri.mo2.data.feat.Field.ShapeField);
        newFeature.setFields(fields);
        Object[] values = new Object[1];
        values[0] = polygon;
        newFeature.setValues(values);

        // construct a FeatureElement and show it in the ActetateLayer
        com.esri.mo2.map.dpy.BaseFeatureElement featureElement =
            new com.esri.mo2.map.dpy.BaseFeatureElement(
                newFeature,
                defaultRenderer,
                selectionRenderer
            );

        return featureElement;
    }

    /**
     * Create a feature element based on the given polygon
     * @param polygon the polygon
     * @return a BaseFeature object
     */
    public static Element createFeatureElement(Polygon polygon) {
        return createFeatureElement(polygon,
            com.esri.mo2.map.draw.Util.POLYGONDEFAULTRENDERER,
            com.esri.mo2.map.draw.Util.POLYGONSELECTIONRENDERER);
    }

    /**
     * Create a feature element based on the given multipoint
     * @param point the multipoint
     * @return a BaseFeature object
     */
    public static Element createFeatureElement(MultiPoint point)
    {
        BaseFeature newFeature = new BaseFeature();
        BaseFields fields = new BaseFields(1);
        fields.addField(com.esri.mo2.data.feat.Field.ShapeField);
        newFeature.setFields(fields);
        Object[] values = new Object[1];
        values[0] = point;
        newFeature.setValues(values);

        // construct a FeatureElement and show it in the ActetateLayer
        com.esri.mo2.map.dpy.BaseFeatureElement featureElement =
            new com.esri.mo2.map.dpy.BaseFeatureElement(
                newFeature,
                com.esri.mo2.map.draw.Util.MARKERDEFAULTRENDERER,
                com.esri.mo2.map.draw.Util.MARKERSELECTIONRENDERER
            );

        return featureElement;
    }

    /**
     * Create a feature element based on the given polyline
     * @param polyline the polyline
     * @return a BaseFeature object
     */
    public static Element createFeatureElement(Polyline polyline)
    {
        BaseFeature newFeature = new BaseFeature();
        BaseFields fields = new BaseFields(1);
        fields.addField(com.esri.mo2.data.feat.Field.ShapeField);
        newFeature.setFields(fields);
        Object[] values = new Object[1];
        values[0] = polyline;
        newFeature.setValues(values);

        // construct a FeatureElement and show it in the ActetateLayer
        com.esri.mo2.map.dpy.BaseFeatureElement featureElement =
            new com.esri.mo2.map.dpy.BaseFeatureElement(
                newFeature,
                com.esri.mo2.map.draw.Util.MARKERDEFAULTRENDERER,
                com.esri.mo2.map.draw.Util.MARKERSELECTIONRENDERER
            );

        return featureElement;
    }


    /**
     * Create a feature element based on the given polygon
     * @param polygon the polygon
     * @param defaultRenderer
     * @param selectionRenderer
     * @return a BaseFeature object
     */
    public static com.esri.mo2.map.core.Element createFeatureElement2(Polygon polygon, Renderer defaultRenderer, Renderer selectionRenderer)
    {
        BaseFeature newFeature = new BaseFeature();
        BaseFields fields = new BaseFields(1);
        fields.addField(com.esri.mo2.data.feat.Field.ShapeField);
        newFeature.setFields(fields);
        Object[] values = new Object[1];
        values[0] = polygon;
        newFeature.setValues(values);

        // construct a FeatureElement and show it in the ActetateLayer
        com.esri.mo2.map.elt.FeatureElement featureElement =
            new com.esri.mo2.map.elt.FeatureElement(
                newFeature,
                defaultRenderer,
                selectionRenderer
            );

        return featureElement;
    }
}