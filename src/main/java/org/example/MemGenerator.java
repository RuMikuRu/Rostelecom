package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemGenerator {
    private static final Logger logger = Logger.getLogger(MemGenerator.class.getName());
    private static final String DEFAULT_POSITION = "center";
    private static final String DEFAULT_FONT_NAME = "Arial";
    private static final int DEFAULT_FONT_SIZE = 32;

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.log(Level.SEVERE, "Недостаточные аргументы. Используйте \"help\" для получения рекомендаций.");
            return;
        }

        String command = args[0];

        switch (command) {
            case "help" -> printHelp();
            case "mem" -> {
                if (args.length < 3) {
                    logger.log(Level.SEVERE, "Недостаточно аргументов для команды 'mem'.");
                    return;
                }
                String imagePath = args[1];
                String text = args[2];
                String position = args.length > 3 ? args[3] : DEFAULT_POSITION;
                String fontName = args.length > 4 ? args[4] : DEFAULT_FONT_NAME;
                int fontSize = DEFAULT_FONT_SIZE;
                if (args.length > 5) {
                    try {
                        fontSize = Integer.parseInt(args[5]);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING,
                                "Неправильный размер шрифта. Будет использовано значение по умолчанию.", e);
                    }
                }
                if (addTextToImage(imagePath, text, position, fontName, fontSize))
                    logger.log(Level.INFO, "Текст успешно добавлен к изображению.");
                else
                    logger.log(Level.SEVERE, "Не удалось добавить текст к изображению.");
            }
            default -> logger.log(Level.SEVERE, "Неверная команда. Используйте \"help\" для получения рекомендаций.");
        }
    }

    private static void printHelp() {
        logger.log(Level.INFO, """
                Руководство по использованию программы:
                 command:
                 help - показывает это руководство
                 mem <путь к изображению> '<текст>' [позиция] [шрифт] [размер шрифта]
                 - добавляет указанный текст к указанному изображению
                 положение (необязательно): center (по умолчанию), top, bottom
                 шрифт (необязательно): название шрифта (например, Arial)
                 размер шрифта (необязательно): целое число""");
    }

    private static boolean addTextToImage(String imagePath, String text,
                                          String position, String fontName, int fontSize) {
        try {
            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);

            Graphics2D g2d = image.createGraphics();
            g2d.setFont(new Font(fontName, Font.PLAIN, fontSize));
            g2d.setColor(Color.WHITE);

            int textX, textY;
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            textY = switch (position) {
                case "top" -> g2d.getFontMetrics().getHeight();
                case "bottom" -> imageHeight - g2d.getFontMetrics().getHeight() / 2;
                default -> imageHeight / 2 + g2d.getFontMetrics().getAscent() / 2;
            };
            // Center the text
            textX = imageWidth / 2 - g2d.getFontMetrics().stringWidth(text) / 2;

            g2d.drawString(text, textX, textY);
            g2d.dispose();

            ImageIO.write(image, "png", imageFile);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка обработки изображения.", e);
            return false;
        }
    }
}
