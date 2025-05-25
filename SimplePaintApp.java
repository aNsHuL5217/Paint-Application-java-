import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.imageio.ImageIO;

public class SimplePaintApp extends Frame {

    private Image canvasImage;
    private Graphics2D canvasGraphics;
    private int lastX, lastY;
    private Color currentColor = Color.BLACK;
    private int brushSize = 3;

    public SimplePaintApp() {
        setTitle("Advanced Paint App");
        setSize(900, 650);
        setLayout(new BorderLayout());
        setResizable(false);

        // Exit on close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        // --- Button Panel ---
        Panel controlPanel = new Panel(new FlowLayout(FlowLayout.LEFT));

        // Color Picker
        Button colorBtn = new Button("Choose Color");
        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Pick a Color", currentColor);
            if (newColor != null) {
                currentColor = newColor;
            }
        });

        // Brush Size Slider
        Label brushLabel = new Label("Brush Size:");
        Scrollbar brushSlider = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 1, 20);
        brushSlider.addAdjustmentListener(e -> brushSize = brushSlider.getValue());

        // Clear Button
        Button clearBtn = new Button("Clear Canvas");
        clearBtn.addActionListener(e -> {
            clearCanvas();
            repaint();
        });

        // New Sketch
        Button newSketchBtn = new Button("New Sketch");
        newSketchBtn.addActionListener(e -> new SimplePaintApp());

        // Save Image
        Button saveBtn = new Button("Save Image");
        saveBtn.addActionListener(e -> saveCanvasToFile());

        controlPanel.add(colorBtn);
        controlPanel.add(brushLabel);
        controlPanel.add(brushSlider);
        controlPanel.add(clearBtn);
        controlPanel.add(newSketchBtn);
        controlPanel.add(saveBtn);

        add(controlPanel, BorderLayout.SOUTH);

        // --- Drawing Events ---
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (canvasGraphics != null) {
                    canvasGraphics.setColor(currentColor);
                    canvasGraphics.setStroke(new BasicStroke(brushSize));
                    canvasGraphics.drawLine(lastX, lastY, x, y);
                    repaint();
                    lastX = x;
                    lastY = y;
                }
            }
        });

        setVisible(true);
    }

    public void paint(Graphics g) {
        if (canvasImage == null) {
            canvasImage = createImage(getWidth(), getHeight());
            canvasGraphics = (Graphics2D) canvasImage.getGraphics();
            clearCanvas();
        }
        g.drawImage(canvasImage, 0, 0, this);
    }

    private void clearCanvas() {
        if (canvasGraphics != null) {
            canvasGraphics.setColor(Color.WHITE);
            canvasGraphics.fillRect(0, 0, getWidth(), getHeight());
            canvasGraphics.setColor(currentColor);
        }
    }

    private void saveCanvasToFile() {
        try {
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            paint(g2);
            File output = new File("sketch_" + System.currentTimeMillis() + ".png");
            ImageIO.write(img, "png", output);
            JOptionPane.showMessageDialog(this, "Image saved as: " + output.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save image.");
        }
    }

    public static void main(String[] args) {
        new SimplePaintApp();
    }
}
