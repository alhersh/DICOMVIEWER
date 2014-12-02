/*
** This is a DicomViewer class
** In response to the challenge assignment for the R&D position at Imagilys
** Started date : April, 11 2014 
** End date : April, 14 2014
** Up to date working hours : 30
** Linked classes:
    ** DicomParser
    ** ImagePanel
    ** DisplayPanel
    ** ChangeContrast
** The print statments are for code tracing and testing
 */

/**
 *
 * @author Taha Alhersh
 */

import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;


public class DicomViewer extends MouseMotionAdapter implements ActionListener, TreeSelectionListener, MouseMotionListener{
    
JScrollPane scrollPane;
protected DefaultMutableTreeNode rootNode;
protected DefaultTreeModel treeModel;
protected JTree tree;
protected String [] ImagesPath;
protected JMenuBar menuBar;
protected JMenu menu;
protected JMenuItem menuItemOpen,menuItemClose;
protected String [] inData;
private ImagePanel imagePanel;	
private boolean stop;
private int currentFrame;
private Vector<BufferedImage> images;
DefaultMutableTreeNode root;
String PatientName,PatientDOB,Modality;
ChangeContrast changeC;
BufferedImage img;
public DicomViewer(){
    ImagesPath = null;
    inData = new String[7];
    images = new Vector<BufferedImage>();
    imagePanel = new ImagePanel();
    imagePanel.setBackground(Color.black);

    root = new DefaultMutableTreeNode(".");
    for (int i=0; i < inData.length;i++){
        inData[i] = "";
        changeC= new ChangeContrast(true);
    }//for
}//Constructor DicomViewer
 
// Create JMenuBar method
public JMenuBar createMenuBar() {
    menuBar = new JMenuBar();
    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menu);
    //DICOM images open item
    menuItemOpen = new JMenuItem("Open DICOM files",KeyEvent.VK_O);
    menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(
    KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    menuItemOpen.getAccessibleContext().setAccessibleDescription("Opening DICOM file");
    menuItemOpen.addActionListener(this);
    menu.add(menuItemOpen);
    //Close menu item
    menuItemClose = new JMenuItem("Close",KeyEvent.VK_Q);
    menuItemClose.setAccelerator(KeyStroke.getKeyStroke(
    KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    menuItemClose.getAccessibleContext().setAccessibleDescription("Opening DICOM file");
    menuItemClose.addActionListener(this);
    menu.add(menuItemClose);
    return menuBar;
}
   

public void actionPerformed(ActionEvent e) {

if (e.getSource().equals(menuItemOpen)){
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(true);
    DicomParser parser;
    File[] files;
    
    int action = chooser.showOpenDialog(menu);
    switch(action) {
        case JFileChooser.APPROVE_OPTION:

            files = chooser.getSelectedFiles();
            parser = new DicomParser(files);
            parser.getFilesInfo(files);
            //parser.printFilesInfo();
//            root.removeAllChildren();
//            treeModel.reload();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            root.setUserObject(".");
            treeModel.nodeChanged(root);
            addNodes(files, root);
            treeModel.reload();
            tree.expandPath(new TreePath(root));
            break;
        case JFileChooser.CANCEL_OPTION:
            return;
    }//switch
}//if
if (e.getSource().equals(menuItemClose)){
    System.exit(0);
}//if
}//actionPerformed
     
protected void addNodes(File [] files, DefaultMutableTreeNode root) {
    DicomParser parser = new DicomParser(files);
    parser.getFilesInfo(files);
    String [] Patients = parser.getPatientID();
    //System.out.println("Patients from addNodes size : " + Patients.length);
    DefaultMutableTreeNode PatientNode = null;
    DefaultMutableTreeNode StudyNode = null;
    DefaultMutableTreeNode SeriesNode = null;
    DefaultMutableTreeNode ImageNode = null;
    //This loop will extract for every patien --> Study --> Series --> Images
    for (int i =0; i < parser.getPatientID().length; i++){
        PatientNode = new DefaultMutableTreeNode("Patient ID : "+Patients[i]);
        root.add(PatientNode);
        String [] Studys = parser.getStudy(Patients[i]);
        for (int j =0; j < parser.getStudy(Patients[i]).length; j++){
            StudyNode = new DefaultMutableTreeNode("Study ID : "+Studys[j]);
            PatientNode.add(StudyNode);
            String [] Series = parser.getSeries(Patients[i],Studys[j]);
            for (int k =0 ; k <  parser.getSeries(Patients[i],Studys[j]).length; k++){
                SeriesNode = new DefaultMutableTreeNode("Series ID : "+ Series[k]);
                StudyNode.add(SeriesNode);
                String [] Images = parser.getImages(Patients[i],Studys[j],Series[k]);
                ImagesPath = Images;
                for (int m =0 ; m < parser.getImages(Patients[i],Studys[j],Series[k]).length;m++){
                    ImageNode = new DefaultMutableTreeNode (m);
                    SeriesNode.add(ImageNode);
                }//for Images
            }//for Series
        }//for Studys
    }//for Patients
}//addNodes       


//Open method to open the choosen file from the Tree and its frames
private void openFile(File file) {
    DicomObject object = null;
    images.clear();
    try {
        DicomInputStream dis = new DicomInputStream(file);
        object = dis.readDicomObject();
        dis.close();
        ImageReader reader = new DicomImageReaderSpi().createReaderInstance();
        FileImageInputStream input = new FileImageInputStream(file);
        reader.setInput(input);	
        img = reader.read(0);
        imagePanel.setImage(img);
       
        listHeader(object);
        changeC = new ChangeContrast(img,img);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.setFont(new Font("Calibri", Font.PLAIN, 12));
        String sP = PatientName;
        String sPD =  PatientDOB;
        String MD =  Modality;
        g2d.drawRect(5, 5, 100, 45);
        g2d.drawString(sP, 10, 15);
        g2d.drawString(sPD.substring(6,8)+"-"+sPD.substring(4,6)+"-"+sPD.substring(0,4), 10, 30);
        g2d.drawString(MD, 10, 45);
        //g2d.dispose();
        
         imagePanel.repaint();
    } catch(Exception e) {
        e.printStackTrace();
        imagePanel.setImage(null);
        return;
    }
}// openFile

public void listHeader(DicomObject object) {
    Iterator iter = object.datasetIterator();
    while(iter.hasNext()) {
        DicomElement element = (DicomElement) iter.next();
        int tag = element.tag();
        try {
            String tagName = object.nameOf(tag);
            String tagAddr = TagUtils.toString(tag);
            String tagVR = object.vrOf(tag).toString();
            if (tagVR.equals("SQ")) {
                if (element.hasItems()) {
                    listHeader(element.getDicomObject());
                    continue;
                }
            }  
            String tagValue = object.getString(tag); 
            //[CS] Modality
            if (tagAddr.equals("(0008,0060)")){
                Modality = tagValue;
                //   System.out.println(tagValue);         
                continue;
            }
            //[PN] Patient’s Name
            if (tagAddr.equals("(0010,0010)")){
                PatientName = tagValue;
                //  System.out.println(tagValue);       
                continue;
            }
            if (tagAddr.equals("(0010,0030)")){
                PatientDOB = tagValue; 
                //   System.out.println(tagValue);      
                continue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//while iter
}//listHeader

// Preparing the JTree
public JTree createTree() {
    tree = new JTree();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(".");
    treeModel = new DefaultTreeModel(root);
    tree.setModel(treeModel);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree = new JTree(root);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);
    return tree;
}// createTree
    
//Creating the image Panel
public JPanel createImage() {
    //DefualtPanel has been set up to handle the failure of reading image
    ImagePanel DefualtPanel = new ImagePanel(new ImageIcon("/Defualt.jpg").getImage());
    JPanel panel = new JPanel(new BorderLayout());
    
    panel = imagePanel;
    panel.addMouseMotionListener(new MouseMotionAdapter(){
         public void mouseDragged(MouseEvent e) {
           // JOptionPane.showMessageDialog(null, "Test draging me");

            changeC.changeOffSet();
            imagePanel.setImage(changeC.rescale());
            
            changeC = new ChangeContrast(img, img);

            
         }                
      });
    if (panel == null){
        return DefualtPanel;
    }
    else
    return panel;
}// createImage
    
private static void createAndShowGUI() {
//Create and set up the window.
    DicomViewer viewer = new DicomViewer();
    JFrame frame = new JFrame("DICOM Viewer");
    JScrollPane treeScroll = new JScrollPane(viewer.createTree());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setTopComponent(treeScroll);
    Dimension minimumSize = new Dimension(100, 50);
    treeScroll.setMinimumSize(minimumSize);
    splitPane.setDividerLocation(100); 
    splitPane.setPreferredSize(new Dimension(300, 100));
    frame.add(splitPane,BorderLayout.WEST);
    frame.add(viewer.createImage(), BorderLayout.CENTER);
    frame.setJMenuBar(viewer.createMenuBar());
    
    //Display the window.
    frame.setSize(555, 380);
    frame.setVisible(true);
    
    
}// createAndShowGUI

// main method
public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
    public void run() {
        createAndShowGUI();
    }
    });
}
// Handle the file selection from the tree
public void valueChanged(TreeSelectionEvent e) {
    Object NodeValue = tree.getLastSelectedPathComponent();  
    String name;  
    name = (NodeValue == null) ? "NONE" : NodeValue.toString();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
    tree.getLastSelectedPathComponent();
    if (node.isLeaf()) {
        
        File file = new File(ImagesPath[Integer.parseInt(name)]);
        this.openFile(file);
        imagePanel.setSize(200, 200);
        imagePanel.repaint();
        imagePanel.updateUI();
    }
}//valueChnaged


}//class
