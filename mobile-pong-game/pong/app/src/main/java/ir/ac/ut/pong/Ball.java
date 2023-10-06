package ir.ac.ut.pong;

import static ir.ac.ut.pong.PongGame.DELTA_IN_SECONDS;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Ball {
    private Integer VIEW_WIDTH;
    private Integer VIEW_HEIGHT;
    private Float x;
    private Float y;
    private Float speedX;
    private Float speedY;
    private Float G = 0F;

    private final Float RADIUS;

    public Ball(Float ballRadius) {
        this.RADIUS = ballRadius;
        init();
    }

    private void init() {
        this.x = 540F;
        this.y = 0F;
        this.speedX = 0F;
        this.speedY = 0F;
    }

    public void setViewWidth(int width) {
        this.VIEW_WIDTH = width;
    }

    public void setViewHeight(int height) {
        this.VIEW_HEIGHT = height;
        this.G = 2F / 3F * this.VIEW_HEIGHT / 1225;
    }

    public void update() {
        speedY += G;

        x += speedX;
        y += speedY;

        if (x - RADIUS < 0 || x + RADIUS > VIEW_WIDTH) {
            speedX = -speedX;
        }

        if (y - RADIUS < 0 || y + RADIUS > VIEW_HEIGHT) {
            speedY = -speedY;
        }
    }

    Boolean isCollided(PointF leftPoint, PointF rightPoint) {
        if (this.x > rightPoint.x || this.x < leftPoint.x)
            return false;
        float tempY = (leftPoint.y - rightPoint.y) / (leftPoint.x - rightPoint.x) * (x - leftPoint.x) + leftPoint.y;
        return this.y + Math.abs(speedY) >= tempY && this.y - Math.abs(speedY) <= tempY;
    }

    public void applyCollision(Racket racket) {
        RectF racketRect = racket.getRacketRect();

        PointF leftPoint = new PointF();
        PointF rightPoint = new PointF();
        leftPoint.x = racketRect.left + (float) (1F - Math.cos(Math.toRadians(racket.getRotation()))) * racketRect.width() / 2;
        rightPoint.x = racketRect.right - (float) (1F - Math.cos(Math.toRadians(racket.getRotation()))) * racketRect.width() / 2;

        leftPoint.y = racketRect.top + (float) (Math.sin(Math.toRadians(racket.getRotation()))) * racketRect.width() / 2;
        rightPoint.y = racketRect.top - (float) (Math.sin(Math.toRadians(racket.getRotation()))) * racketRect.width() / 2;
        if (isCollided(leftPoint, rightPoint)) {
            float tempSin = (float) Math.sin(2 * Math.toRadians(racket.getRotation()));
            float tempCos = (float) Math.cos(2 * Math.toRadians(racket.getRotation()));
            float newSpeedY = -1 * this.speedX * tempSin - this.speedY * tempCos;
            float newSpeedX = this.speedX * tempCos + this.speedY * tempSin;
            this.speedX = newSpeedX;
            this.speedY = newSpeedY;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, RADIUS, paint);
    }

    public void reset() {
        init();
    }
}
