import com.esri.mo2.ui.bean.*;
import java.awt.*;

public class ScaleBarDemo extends javax.swing.JFrame {
  Map map1 = new Map();
  Layer layer1 = new Layer();
  FormatedScaleBar tranditionalSB = new FormatedScaleBar();

  public ScaleBarDemo() {
    setTitle("ScaleBar Demo");
    setSize(400, 400);
    try {
      init();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    setVisible(true);
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
  }

  private void init() throws Exception {
    getContentPane().add(map1, BorderLayout.CENTER);
    layer1.setDataset(
        "com.esri.mo2.src.file.FileSystemConnection!C:/ESRI/ESRIDATA/USA/states.shp!");
    map1.add(layer1, null);
    map1.setMapUnit(com.esri.mo2.util.Units.DECIMAL_DEGREES);
    getContentPane().add(tranditionalSB, java.awt.BorderLayout.SOUTH);
    tranditionalSB.setMap(map1);
  }

  public static void main(String[] args) {
    new ScaleBarDemo();
  }

  class FormatedScaleBar extends com.esri.mo2.ui.bean.ScaleBar {
    protected String formatXLabel(double x) {
      return DecimalToDMSString(x);
    }

    protected String formatYLabel(double y) {
      return DecimalToDMSString(y);
    }

    private String DecimalToDMSString(double number) {

      //Degree
      String strnum = Double.toString(number);

      String deg = strnum.substring(0, strnum.indexOf('.'));
      String temp = "0" + strnum.substring(strnum.indexOf('.'), strnum.length());

      //minutes
      double num = Double.parseDouble(temp) * 60d;
      String temp1 = Double.toString(num);
      String min = temp1.substring(0, temp1.indexOf('.'));
      String temp2 = "0" + temp1.substring(temp1.indexOf('.'), temp1.length());

      //seconds
      double num2 = Double.parseDouble(temp2) * 60d;
      String temp3 = Double.toString(num2);
      String sec = temp3.substring(0, temp3.indexOf('.'));

      char d = 186; // 0
      char m = 39; // '
      char s = 34; // "
      String result = deg + d + min + m + sec + s;
      return result;
    }

  }

}
