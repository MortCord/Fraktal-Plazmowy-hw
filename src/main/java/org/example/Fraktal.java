package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Fraktal extends JPanel {

    private final int size = 256;
    private final BufferedImage image;
    private final Random random = new Random();

    public Fraktal() {
        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        generatePlasma();
    }

    private void generatePlasma() {

        float[][] data = new float[size][size];

        data[0][0] = random.nextFloat();
        data[0][size - 1] = random.nextFloat();
        data[size - 1][0] = random.nextFloat();
        data[size - 1][size - 1] = random.nextFloat();

        divide(data, 0, 0, size - 1, size - 1, 1.0f);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                float value = Math.max(0, Math.min(1, data[y][x]));

                Color color = Color.getHSBColor(
                        0.7f - value * 0.7f,
                        1.0f,
                        0.3f + value * 0.7f
                );

                image.setRGB(x, y, color.getRGB());
            }
        }
    }

    private void divide(float[][] data,
                        int x1, int y1,
                        int x2, int y2,
                        float roughness) {

        if (x2 - x1 < 2 && y2 - y1 < 2) {
            return;
        }

        int xm = (x1 + x2) / 2;
        int ym = (y1 + y2) / 2;

        if (data[ym][xm] == 0) {
            data[ym][xm] = average(
                    data[y1][x1],
                    data[y1][x2],
                    data[y2][x1],
                    data[y2][x2]
            ) + randomOffset(roughness);
        }

        setAverage(data, xm, y1,
                data[y1][x1], data[y1][x2], roughness);

        setAverage(data, xm, y2,
                data[y2][x1], data[y2][x2], roughness);

        setAverage(data, x1, ym,
                data[y1][x1], data[y2][x1], roughness);

        setAverage(data, x2, ym,
                data[y1][x2], data[y2][x2], roughness);

        float next = roughness * 0.5f;

        divide(data, x1, y1, xm, ym, next);
        divide(data, xm, y1, x2, ym, next);
        divide(data, x1, ym, xm, y2, next);
        divide(data, xm, ym, x2, y2, next);
    }

    private void setAverage(float[][] data,
                            int x, int y,
                            float a, float b,
                            float roughness) {

        if (data[y][x] == 0) {
            data[y][x] = (a + b) / 2 + randomOffset(roughness);
        }
    }

    private float average(float a, float b, float c, float d) {
        return (a + b + c + d) / 4;
    }

    private float randomOffset(float roughness) {
        return (random.nextFloat() * 2 - 1) * roughness;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 20, 20, 512, 512, null);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(570, 590);

        frame.add(new Fraktal());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
