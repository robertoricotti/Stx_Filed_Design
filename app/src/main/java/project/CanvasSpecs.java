package project;

public class CanvasSpecs {
    public float offsetX;
    public float offsetY;
    public float mScaleFactor;
    public float scale;
    public float radius;

    public CanvasSpecs(float offsetX, float offsetY, float mScaleFactor, float scale, float radius) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.mScaleFactor = mScaleFactor;
        this.scale = scale;
        this.radius = radius;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getmScaleFactor() {
        return mScaleFactor;
    }

    public void setmScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
