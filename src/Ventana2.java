import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

public class Ventana2 extends JFrame {

    private BufferedImage background;
    private BufferedImage buffer;

    public Ventana2() {
        super("Reloj Analogico");
        setSize(500, 500);
        reproducirMusica();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // Configura un temporizador para actualizar la ventana cada segundo
        Thread clockThread = new Thread(() -> {
            while (true) {
                try {
                    SwingUtilities.invokeAndWait(this::repaint);
                    Thread.sleep(1000); // Esperar 1 segundo
                } catch (InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();

        // Cargar la imagen de fondo desde un archivo
        try {
            background = ImageIO.read(new File("C:\\Users\\mfer-\\ProyectosGraficas\\ProyectoTercerParcial\\ProyectoPrimerParcialGraficas\\Imagen\\imgLuis.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        if (buffer == null) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        updateBuffer(buffer.getGraphics());
        g.drawImage(buffer, 0, 0, this);
    }

    private void updateBuffer(Graphics gBuffer) {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        gBuffer.drawImage(background, 0, 0, null); // Dibujar la imagen de fondo primero
        Graphics2D g2d = (Graphics2D) gBuffer;

        // Dibujar los círculos del reloj
        drawClockCircles(g2d);

        // Dibujar las manecillas del reloj
        drawClockHands(g2d, hora, min, sec);
    }

    private void drawClockCircles(Graphics2D g2d) {
        GradientPaint gradient1 = new GradientPaint(0, 0, Color.decode("#ffe642"), 0, getHeight(), Color.BLACK);
        GradientPaint gradient2 = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), Color.GRAY);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int radius1 = 170;
        int ovalX1 = centerX - radius1;
        int ovalY1 = centerY - radius1;
        int ovalWidth1 = radius1 * 2;
        int ovalHeight1 = radius1 * 2;

        g2d.setPaint(gradient1);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval(ovalX1, ovalY1, ovalWidth1, ovalHeight1);

        int radius2 = 150;
        int ovalX2 = centerX - radius2;
        int ovalY2 = centerY - radius2;
        int ovalWidth2 = radius2 * 2;
        int ovalHeight2 = radius2 * 2;

        g2d.setPaint(gradient2);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(ovalX2, ovalY2, ovalWidth2, ovalHeight2);
    }

    private void drawClockHands(Graphics2D g2d, int hora, int min, int sec) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Manecilla de las horas
        g2d.setPaint(Color.decode("#ffe642"));
        g2d.setStroke(new BasicStroke(6));
        drawHand(g2d, centerX, centerY, angulo12(hora, min), 100);

        // Manecilla de los minutos
        g2d.setPaint(Color.GREEN);
        g2d.setStroke(new BasicStroke(4));
        drawHand(g2d, centerX, centerY, angulo60(min), 120);

        // Manecilla de los segundos
        g2d.setPaint(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        drawHand(g2d, centerX, centerY, angulo60(sec), 140);

        // Dibujar los números del reloj
        drawClockNumbers(g2d);
    }

    private void drawHand(Graphics2D g2d, int x, int y, double angle, int length) {
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(angle), x, y);
        g2d.drawLine(x, y, x, y - length);
        g2d.setTransform(old);
    }

    private void drawClockNumbers(Graphics2D g2d) {
        Font font = new Font("Times New Roman", Font.BOLD, 30);
        g2d.setFont(font);
        g2d.setColor(Color.BLUE);

        String[] numbers = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 140;

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians((i - 3) * 30); // Mover el ángulo 90 grados para que 12 esté en la parte superior
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            g2d.drawString(numbers[i], x - 10, y + 10);
        }
    }

    private int angulo12(int hora, int minutos) {
        return (hora % 12) * 30 + (minutos / 2) - 90;
    }

    private int angulo60(int unidad) {
        return unidad * 6 - 90;
    }

    private void reproducirMusica() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/Musica/reloj.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

}
