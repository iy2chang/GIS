package directtranslation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Prompts the user to enter the connection string required to create
 * a valid TranslationConnection instance.  
 * Calling code is expected to determine modality.
 */
public class TranslationConnectionDialog extends JDialog {
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JButton okButton = new JButton();
  JButton cancelButton = new JButton();
  public boolean okPressed = false;
  JDialog thisFrame;

  public TranslationConnectionDialog() throws HeadlessException {
    try {
      this.setSize(400,300);
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void init() throws Exception {
    setTitle("Connect to PTXT Server");
    setSize(400,140);
    thisFrame = this;
    jLabel1.setText("Connection Details:");
    okButton.setText("OK");
    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        okPressed = true;
        thisFrame.setVisible(false);
      }
    });

    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        okPressed = false;
        thisFrame.setVisible(false);
      }
    });

    this.getContentPane().setLayout(new BorderLayout(0,40));
    JPanel buttonPanel = new JPanel(new GridLayout(1,2, 20, 20));
    JPanel textPanel = new JPanel(new GridLayout(2,1));
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    this.getContentPane().add(textPanel, BorderLayout.NORTH);
    textPanel.add(jLabel1);
    textPanel.add(jTextField1);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
  }

}
