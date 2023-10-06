package ir.ac.ut.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongGame extends SurfaceView implements Runnable, SensorEventListener {

    public static final Float FRACTION = 1F;
    // Variables for game objects and properties
    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private final boolean paused = true;
    private final Paint paint;

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor rotationSensor;
    public static Integer UPDATE_RATE_MS = 20;
    public static Float DELTA_IN_SECONDS = UPDATE_RATE_MS / 1000F;
    public static Float DESK_WIDTH = 0.5F;
    public static Float BALL_RADIUS = 20F;
    private Racket racket;
    private Ball ball;

    public PongGame(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        this.racket = new Racket();
        this.ball = new Ball(BALL_RADIUS);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        this.racket.setViewWidth(getWidth());
        this.racket.update();

        this.ball.setViewWidth(getWidth());
        this.ball.setViewHeight(getHeight());
        this.ball.update();
        this.ball.applyCollision(this.racket);
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            this.racket.draw(canvas, paint);
            this.ball.draw(canvas, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(UPDATE_RATE_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            Float currentRotation = event.values[2];
            this.racket.updateRotation(currentRotation);
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Float currentAcc = event.values[0];
            this.racket.updateAcc(currentAcc);

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_MOVE) {
            this.ball.reset();
            this.racket.reset();
        }

        return true;
    }
}