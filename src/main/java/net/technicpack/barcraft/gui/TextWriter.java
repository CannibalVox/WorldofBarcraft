package net.technicpack.barcraft.gui;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;

public class TextWriter {
    private FontRenderer fontRenderer;

    public static final int LEADING = 0;
    public static final int CENTER = 1;
    public static final int TAILING = 2;

    public TextWriter(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public int getStringWidth(String text) {
        return this.fontRenderer.getStringWidth(text);
    }

    public int getStringHeight() {
        return this.fontRenderer.FONT_HEIGHT;
    }

    public void drawScaledText(double x, double y, double width, double height, String text) {
        drawScaledText(x, y, width, height, text, CENTER, CENTER);
    }

    public void drawStaticText(double x, double y, String text, int hAlignment, int vAlignment) {
        double width = fontRenderer.getStringWidth(text);
        double height = fontRenderer.FONT_HEIGHT;

        GL11.glPushMatrix();
        double xTranslate = x;
        double yTranslate = y;
        if (hAlignment == CENTER)
            xTranslate -= width / 2;
        else if (hAlignment == TAILING)
            xTranslate -= width;
        if (vAlignment == CENTER)
            yTranslate -= height / 2;
        else if (vAlignment == TAILING)
            yTranslate -= height;
        GL11.glTranslated(xTranslate, yTranslate, 0);

        this.fontRenderer.drawString(text, 0, 0, 4210752);
        GL11.glPopMatrix();
    }

    public class TextFitData {
        private double scale;
        private int lineCount;
        private String[] lines;

        public TextFitData(double scale, int lineCount, Object[] lines) {
            this.scale = scale;
            this.lineCount = lineCount;

            this.lines = new String[lineCount];

            for (int i = 0; i < lineCount; i++) {
                this.lines[i] = lines[i].toString();
            }
        }

        public double getScale() { return scale; }
        public int getLineCount() { return lineCount; }
        public String getLine(int index) { return lines[index]; }
    }

    public void drawScrollText(double x, double y, double width, double height, double scrollPos, double scale, double guiScale, String text, int hAlignment) {
        TextFitData fitData = getScrollFit(width, scale, text);
        double lineHeight = fontRenderer.FONT_HEIGHT;
        double textHeight = fitData.getLineCount() * lineHeight;
        scrollPos = Math.min(Math.max(scrollPos, 0), 1);
        double marginHeight = Math.max(textHeight - height, 0);
        double offset = scrollPos * marginHeight * -1;

        GL11.glPushMatrix();
        double xTranslate = x;
        double yTranslate = y + offset;
        if (hAlignment == CENTER)
            xTranslate += width / 2;
        else if (hAlignment == TAILING)
            xTranslate += width;
        GL11.glTranslated(xTranslate, yTranslate, 0);
        GL11.glScaled(fitData.getScale(), fitData.getScale(), fitData.getScale());
        for (int i = 0; i < fitData.getLineCount(); i++) {
            double lineY = y + offset + (i*lineHeight);
            if (lineY < y || (lineY+lineHeight) > (y+height)) {
                GL11.glTranslated(0, lineHeight, 0);
                continue;
            }

            int lineX = 0;
            if (hAlignment == CENTER)
                lineX = (int) -(double) this.fontRenderer.getStringWidth(fitData.getLine(i)) / 2;
            else if (hAlignment == TAILING)
                lineX = (int) - (double) this.fontRenderer.getStringWidth(fitData.getLine(i));

            this.fontRenderer.drawString(fitData.getLine(i), lineX, 0, 4210752);
            GL11.glTranslated(0, lineHeight, 0);
        }
        GL11.glPopMatrix();
    }

    public TextFitData getScrollFit(double width, double scale, String text) {
        String[] tokens = text.split("((?<=\\s)|(?=\\s))");

        List<String> lines = new ArrayList<String>();
        String currentLine = "";
        double currentWidth = 0;
        for (int i = 0; i < tokens.length; i++) {
            double tokenWidth = scale * fontRenderer.getStringWidth(tokens[i]);
            if (currentWidth + tokenWidth < width) {
                currentLine = currentLine.concat(tokens[i]);
                currentWidth += tokenWidth;
            } else {
                lines.add(currentLine.trim());
                currentLine = tokens[i].trim();
                currentWidth = fontRenderer.getStringWidth(currentLine);
            }
        }

        if (!currentLine.trim().equals(""))
            lines.add(currentLine.trim());

        return new TextFitData(scale, lines.size(), lines.toArray());
    }

    public TextFitData getFitForText(double width, double height, String text) {
        String[] tokens = text.split("((?<=\\s)|(?=\\s))");

        int largestToken = 0;
        for (String token : tokens) {
            int tokenSize = this.fontRenderer.getStringWidth(token);
            if (tokenSize > largestToken)
                largestToken = tokenSize;
        }

        double textWidth = this.fontRenderer.getStringWidth(text);
        double minScale = Math.min(width / textWidth, height / (double)this.fontRenderer.FONT_HEIGHT);

        int maxLines = (int) (height / ((double) this.fontRenderer.FONT_HEIGHT * minScale));

        Object[] bestText = new String[]{text};
        double bestScale = minScale;
        int bestLines = 1;

        if (maxLines > 1) {
            for (int lineCount = 2; lineCount <= maxLines; lineCount++) {
                List<String> textLines = new ArrayList<String>(lineCount);
                double maxSingleWidth = 0;
                double targetWidth = Math.max(largestToken, textWidth / lineCount);
                int tokenIndex = 0;
                for (int i = 0; i < lineCount; i++) {
                    double currentWidth = 0;
                    String lineText = "";
                    boolean beginningOfLine = true;

                    while (tokenIndex < tokens.length && currentWidth < targetWidth) {
                        String thisToken = tokens[tokenIndex++];
                        if (beginningOfLine && thisToken.trim().equals(""))
                            continue;
                        beginningOfLine = false;

                        int tokenWidth = this.fontRenderer.getStringWidth(thisToken);
                        if (thisToken.trim().equals("") && currentWidth + tokenWidth >= targetWidth) {
                            currentWidth += tokenWidth;
                            continue;
                        }

                        lineText = lineText.concat(thisToken);
                        currentWidth += tokenWidth;
                    }

                    if (maxSingleWidth < currentWidth)
                        maxSingleWidth = currentWidth;
                    if (lineText != null && !lineText.trim().equals(""))
                        textLines.add(lineText);
                }
                double horizontalScale = width / maxSingleWidth;
                double verticalScale = height / (textLines.size() * this.fontRenderer.FONT_HEIGHT);
                double currentScale = Math.min(horizontalScale, verticalScale);
                if (currentScale > bestScale) {
                    bestScale = currentScale;
                    bestLines = textLines.size();
                    bestText = textLines.toArray();
                }
            }
        }

        return new TextFitData(bestScale, bestLines, bestText);
    }

    public void drawScaledText(double x, double y, double width, double height, String text, int hAlignment, int vAlignment) {
        TextFitData fitData = getFitForText(width, height, text);

        double totalTextHeight = fitData.getLineCount() * this.fontRenderer.FONT_HEIGHT * fitData.getScale();

        GL11.glPushMatrix();
        double xTranslate = x;
        double yTranslate = y;
        if (hAlignment == CENTER)
            xTranslate += width / 2;
        else if (hAlignment == TAILING)
            xTranslate += width;
        if (vAlignment == CENTER)
            yTranslate += (height - totalTextHeight) / 2;
        else if (vAlignment == TAILING)
            yTranslate += (height - totalTextHeight);
        GL11.glTranslated(xTranslate, yTranslate, 0);
        GL11.glScaled(fitData.getScale(), fitData.getScale(), fitData.getScale());
        for (int i = 0; i < fitData.getLineCount(); i++) {
            int lineX = 0;
            if (hAlignment == CENTER)
                lineX = (int) -(double) this.fontRenderer.getStringWidth(fitData.getLine(i).toString()) / 2;
            else if (hAlignment == TAILING)
                lineX = (int) - (double) this.fontRenderer.getStringWidth(fitData.getLine(i).toString());

            this.fontRenderer.drawString(fitData.getLine(i), lineX, i * this.fontRenderer.FONT_HEIGHT, 4210752);
        }
        GL11.glPopMatrix();
    }
}
