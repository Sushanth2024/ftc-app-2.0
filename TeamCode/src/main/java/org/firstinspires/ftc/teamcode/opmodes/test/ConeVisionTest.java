package org.firstinspires.ftc.teamcode.opmodes.test;

import static org.opencv.core.CvType.CV_8UC4;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.ImageFormat;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
import org.firstinspires.ftc.teamcode.opmodes.util.FTCDVS;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.vision.ConeInfoDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

//@Disabled
@TeleOp(name="ConeVisionTest")
public class ConeVisionTest extends LoggingOpMode {
    private Webcam camera;
    private Webcam.SimpleFrameHandler frameHandler;
    private Mat cvFrame;
    private volatile boolean serverFrameUsed = true;
    private Bitmap serverFrameCopy;
    private String result = "Nothing";

    private final Logger log = new Logger("Cone Vision Test");

    static
    {
        OpenCVLoader.initDebug();
    }

    @Override
    public void init() {
        super.init();
        camera = Webcam.forSerial("3522DE6F");
        if (camera == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
        frameHandler = new Webcam.SimpleFrameHandler();
        camera.open(ImageFormat.YUY2, 1920, 1080, 30, frameHandler);
        cvFrame = new Mat(1920, 1080, CV_8UC4);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

    }

    @Override
    public void loop() {
        if (frameHandler.newFrameAvailable) {
            frameHandler.newFrameAvailable = false;
            Utils.bitmapToMat(frameHandler.currFramebuffer, cvFrame);
            if (serverFrameUsed) {
                if (serverFrameCopy != null) serverFrameCopy.recycle();
                serverFrameCopy = frameHandler.currFramebuffer.copy(Bitmap.Config.ARGB_8888, false);
                serverFrameUsed = false;
            }



            camera.requestNewFrame();
        }

        telemetry.addData("Detected", result);

        telemetry.update();
    }
}
