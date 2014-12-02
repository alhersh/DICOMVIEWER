
/*
** This class to be used to generate the ImagePanel to paint the images on
** which is located at the EAST part of the JFrame
 */

/**
 *
 * @author Taha Alhersh
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


class ImagePanel extends JPanel {
    
private static final long serialVersionUID = 1L;
private BufferedImage image;
private Image img;  

// Get an image with a specified path
public ImagePanel(String img) {  
    this(new ImageIcon(img).getImage());  
} 

//Get an image that already read as Image
public ImagePanel(Image img) {  
    this.img = img;  
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));  
    setPreferredSize(size);  
    setMinimumSize(size);  
    setMaximumSize(size);  
    setSize(size);  
    setLayout(null);  
} 

//The defualt constructor to give the panel some settings
public ImagePanel() {
    super();
    this.setBackground(Color.black);			
}

// Fetching the BufferedImage image to the local image this.image
public void setImage(BufferedImage image) {
    this.image = image;
    this.updateUI();
    //addMouseMotionListener(new MouseMotionHandler());
}

// painting the normal images which is img (Image)
public void paintComponent(Graphics g) {  
    g.drawImage(img, 0, 0, null);  
    
}  

// painting the BufferedImage image
@Override
public void paint(Graphics g) {
    if (this.image != null) {
        g.drawImage(this.image, 0, 0, image.getWidth(), image.getHeight(), null);
        
    }
}

}// Class