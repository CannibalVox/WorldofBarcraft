package net.technicpack.barcraft.gui;

import net.minecraft.client.renderer.Tessellator;

public class NineSquareRenderer {
    private float u;
    private float v;
    private float width;
    private float height;
    private float leftU;
    private float rightU;
    private float topV;
    private float bottomV;
    private float texWidth;
    private float texHeight;

    public NineSquareRenderer(float u, float v, float width, float height, float leftU, float rightU, float topV, float bottomV, float texWidth, float texHeight) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.leftU = leftU;
        this.rightU = rightU;
        this.topV = topV;
        this.bottomV = bottomV;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    //Render a gray-bordered box in the content of the GUI
    public void draw(Tessellator tessellator, double x, double y, double z, double width, double height) {
        drawImage(tessellator, x, y, z, leftU, topV, u, v, u+leftU, v+topV, texWidth, texHeight);
        drawImage(tessellator, x + leftU, y, z, width - leftU - rightU, topV, u+leftU, v, u+this.width-rightU, v+topV, texWidth, texHeight);
        drawImage(tessellator, x + width - rightU, y, z, rightU, topV, u+this.width-rightU, v, u+this.width, v+topV, texWidth, texHeight);

        drawImage(tessellator, x, y + topV, z, leftU, height - topV - bottomV, u, v+topV, u+leftU, v+this.height-bottomV, texWidth, texHeight);
        drawImage(tessellator, x + leftU, y + topV, z, width - leftU - rightU, height - topV - bottomV, u+leftU, v+topV, u+this.width-rightU, v+this.height-bottomV, texWidth, texHeight);
        drawImage(tessellator, x + width - rightU, y + topV, z, rightU, height - topV - bottomV, u+this.width-rightU, v+topV, u+this.width, v+this.height-bottomV, texWidth, texHeight);

        drawImage(tessellator, x, y + height - bottomV, z, leftU, bottomV, u, v+this.height-bottomV, u+leftU, v+this.height, texWidth, texHeight);
        drawImage(tessellator, x + leftU, y + height - bottomV, z, width - leftU - rightU, bottomV, u+leftU, v+this.height-bottomV, u+this.width-rightU, v+this.height, texWidth, texHeight);
        drawImage(tessellator, x + width - rightU, y + height - bottomV, z, rightU, bottomV, u+this.width-rightU, v+this.height-bottomV, u+this.width, v+this.height, texWidth, texHeight);
    }

    //Draw GUI art without forcing a 256x256 art size
    private void drawImage(Tessellator tessellator, double x, double y, double z, double width, double height, double u, double v, double maxU, double maxV, double texWidth, double texHeight) {
        tessellator.addVertexWithUV(x, y + height, z, u / texWidth, maxV / texHeight);
        tessellator.addVertexWithUV(x + width, y + height, z, maxU / texWidth, maxV / texHeight);
        tessellator.addVertexWithUV(x + width, y, z, maxU / texWidth, v / texHeight);
        tessellator.addVertexWithUV(x, y, z, u / texWidth, v / texHeight);
    }
}
