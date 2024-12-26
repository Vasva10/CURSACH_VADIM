package com.example.goodmarksman.objects;

import com.example.goodmarksman.enams.COLORS;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.Arrays;

public class Arrow {
//    Polygon arrow = null;
    private int ownerPort;
    private final int moveSpeed = 10;
    private final Double[] points = new Double[]{-30.0, 5.0, 0.0, 0.0, -30.0, -5.0};
    private boolean isShooting = false;
    private final int min_x = 38;
    private int max_x = -1;
    private int startY = 122;
    private int layoutX = min_x, layoutY = startY;
    private COLORS colorName = COLORS.NULL;

    public Arrow(Polygon arrow, int ownerPort) {
        layoutX = (int) arrow.getLayoutX();
        layoutY = (int) arrow.getLayoutY();
        this.ownerPort = ownerPort;
    }

    public Arrow(int ownerPort) {
        this.ownerPort = ownerPort;
    }

    public Arrow() {}

    public Arrow(Arrow a) {
        this.ownerPort = a.ownerPort;
        this.colorName = a.colorName;
        this.layoutX = a.layoutX;
        this.layoutY = a.layoutY;
        this.isShooting = a.isShooting;
    }

    public void nullify() {
        setX(min_x);
        setY(startY);
    }

    public void setColor(COLORS color) {
        this.colorName = color;
    }
    public COLORS getColorName() { return colorName; }

    public Polygon getPolygon() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(this.points);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(1);
        polygon.setFill(this.colorName.getValue());
        polygon.setLayoutX(this.layoutX);
        polygon.setLayoutY(this.layoutY);

        return polygon;
    }

    public double getY() { return this.layoutY; }
    public void setY(double y) { this.layoutY = (int)y; }
    public double getX() { return this.layoutX; }
    public void setX(double x) { this.layoutX = (int)x; }

    public void setMaxX(int maxX) { this.max_x = maxX; }
    public int getMaxX() { return this.max_x; }

    public int getMinX() { return this.min_x; }

    public void hit() {
        isShooting = false;
        this.layoutX = this.min_x;
    }

    public int getSpeed() { return this.moveSpeed; }

    public void setIsShooting(boolean bool) {
        this.isShooting = bool;
    }
    public boolean getIsShooting() { return this.isShooting; }

    public void setOwnerPort(int port) { this.ownerPort = port; }
    public int getOwnerPort() { return this.ownerPort; }

    @Override
    public String toString() {
        return "Arrow{" +
                "ownerPort=" + ownerPort +
                ", moveSpeed=" + moveSpeed +
                ", points=" + Arrays.toString(points) +
                ", isShooting=" + isShooting +
                ", min_x=" + min_x +
                ", max_x=" + max_x +
                ", layoutX=" + layoutX +
                ", layoutY=" + layoutY +
                ", colorName=" + colorName +
                '}';
    }
}
