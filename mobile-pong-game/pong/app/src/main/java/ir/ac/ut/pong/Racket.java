package ir.ac.ut.pong;

import static ir.ac.ut.pong.PongGame.DELTA_IN_SECONDS;
import static ir.ac.ut.pong.PongGame.DESK_WIDTH;
import static ir.ac.ut.pong.PongGame.FRACTION;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Racket {
    private Float speed;
    private Float acc;
    private Float rotation;

    private Float position;

    private Float racketWidth;
    private final RectF racketRect;
    private Integer VIEW_WIDTH;
    public static final Float ASCENDING_ACC_SENSITIVITY_FACTOR = 10F;
    public static final Float DESCENDING_ACC_SENSITIVITY_FACTOR = 0.1F;
    public static final Float ACC_HIGH_FILTER_VALUE = 0.6F;
    public static final Float SPEED_HIGH_FILTER_VALUE = 0.6F;

    public Float getRotation() {
        return rotation;
    }

    public Racket() {
        this.racketRect = new RectF();
        init();
    }

    private void init() {
        this.speed = 0F;
        this.acc = 0F;
        this.rotation = 0F;
        this.position = 540F;
        this.racketWidth = 0F;
    }

    public RectF getRacketRect() {
        return this.racketRect;
    }

    public void updateAcc(Float currentAcc) {
        if (Math.abs(currentAcc) < ACC_HIGH_FILTER_VALUE)
            this.acc = 0F;
        else {
            if (Math.signum(this.speed) != Math.signum(currentAcc))
                this.acc = currentAcc  * DESCENDING_ACC_SENSITIVITY_FACTOR;
            else
                this.acc = currentAcc  * ASCENDING_ACC_SENSITIVITY_FACTOR;
        }
    }

    public void updateRotation(Float currentRotation) {
        this.rotation = (float) Math.toDegrees(currentRotation) * -2;
    }

    public void update() {
        this.position += (this.speed * DELTA_IN_SECONDS) * VIEW_WIDTH / DESK_WIDTH;
        this.speed += this.acc * DELTA_IN_SECONDS;
        this.speed -= Math.signum(this.speed) * FRACTION;
        if (Math.abs(this.speed) < SPEED_HIGH_FILTER_VALUE)
            this.speed = 0F;

        if (this.position + this.racketWidth / 2 < 0) {
            this.position = -1 * this.racketWidth / 2;
            this.speed = 0F;
        }

        if (this.position - this.racketWidth / 2 > VIEW_WIDTH) {
            this.speed = 0F;
            this.position = VIEW_WIDTH - this.racketWidth / 2;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        this.racketWidth = canvas.getWidth() / 3F;
        this.racketRect.left = this.position - this.racketWidth / 2;
        this.racketRect.top = canvas.getHeight() - (canvas.getHeight() / 4);
        this.racketRect.right = this.position + this.racketWidth / 2;
        this.racketRect.bottom = this.racketRect.top + 10;

        canvas.save();
        canvas.rotate(this.rotation, this.racketRect.centerX(), this.racketRect.centerY());
        canvas.drawRect(this.racketRect, paint);
        canvas.restore();
    }

    public void setViewWidth(int width) {
        this.VIEW_WIDTH = width;
    }

    public void reset() {
        init();
    }
}
