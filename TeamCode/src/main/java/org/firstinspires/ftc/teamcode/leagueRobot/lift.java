package org.firstinspires.ftc.teamcode.leagueRobot;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Disabled
public class lift
{


    LinearOpMode opModeObj;
public int ticksPerHeight =100;
public int maxHeight = 5;




//variables for taking the derivative of error
public double lasterror;
public int error;

public double nowtime, thentime;

//kp = .009, kd = 0;

public double kp = .009;
public double kd = 0;
    private ElapsedTime runtime = new ElapsedTime();
    public DcMotor leftLift = null;
    public DcMotor rightLift = null;
    
    public lift(LinearOpMode opmode, Telemetry telemetry, HardwareMap hardwareMap){
        leftLift = hardwareMap.get(DcMotor.class,"left_lift");
        rightLift = hardwareMap.get(DcMotor.class,"right_lift");
        leftLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       // leftLift.setDirection(DcMotorSimple.Direction.REVERSE);

        leftLift.setPower(0);
        rightLift.setPower(0);
        opModeObj = opmode;
    }

public void liftMove(int height){
if(height == 0){
    motorPower(leftLift,0);
    motorPower(rightLift,0);
}
else if(height!=0){
    int goalPos = (height-1)*ticksPerHeight+35;
    motorPower(leftLift,goalPos);
    motorPower(rightLift,goalPos);
}

}

public double posDif(double currPos, double endPos){
        return Math.abs(currPos-endPos);
}

public void setLiftPower(double power){ //sets power for both lift motors, not recommended.
        leftLift.setPower(Range.clip(power,-.25,1));
        rightLift.setPower(Range.clip(power,-.25,1));
}

public void motorPower(DcMotor liftMotor, int goal)
{
    nowtime = runtime.seconds();
 error = -liftMotor.getCurrentPosition()+goal;
    double output = kp*error + kd*(error-lasterror)/(nowtime-thentime);

    //store these variables for the next loop
    lasterror = error;
    thentime = nowtime;
    liftMotor.setPower(Range.clip(output,-.3,1));
}
}