/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.leagueRobot;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;


/**
Start with the front color sensor inline with the middle of the first stone
 */

@TeleOp(name="FantanaEX", group="Linear Opmode")

public class TeleopTester extends LinearOpMode
{


    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime lastLoop = new ElapsedTime();
    private ElapsedTime loopTime = new ElapsedTime();

    boolean shouldLiftMove = true; // if this variable is true, the lift will move to the position dictated by the gamepads.
    //if the variable is false, the lift will not move to the position and will instead stay at a position dictated by the right gamestick

    boolean grabFound = false;
    boolean grabStone = false;

    boolean shouldHoldHeight = false;
    boolean actionTaken = false;

    int holdPosition;

    public DcMotorEx leftLift = null;
    public DcMotorEx rightLift = null;



    @Override
    public void runOpMode()
    {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drive vroom = new drive(this,telemetry,hardwareMap);
        color see = new color(this,telemetry,hardwareMap);
        lift ellie = new lift(this,telemetry,hardwareMap);
        revIMU gyro = new revIMU(this,telemetry,hardwareMap);
        found pull = new found(this,telemetry,hardwareMap);
        grabber grabby = new grabber(this,telemetry,hardwareMap);
        range scope = new range(this,telemetry,hardwareMap);
        claw pince = new claw(this,telemetry,hardwareMap);
        bulk reader = new bulk(this,telemetry,hardwareMap);
        leftLift = hardwareMap.get(DcMotorEx.class,"left_lift");
        rightLift = hardwareMap.get(DcMotorEx.class,"right_lift");
        leftLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        leftLift.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightLift.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftLift.setDirection(DcMotorEx.Direction.REVERSE);
        leftLift.setPower(0);
        rightLift.setPower(0);
        int liftLocation = 0; //height of lift: 0 = ground, 1 = first stone, 2 = second stone, 3 = third stone, 4 = fourth stone 5 = fifth stone or capstone

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        lastLoop.reset();
        loopTime.reset();
        while (opModeIsActive())
        {

            loopTime.reset();
//lift code
            if(lastLoop.milliseconds()>140){
                if(gamepad2.dpad_up){
                    if(liftLocation<ellie.maxHeight){ //makes sure that the lift cannot get past the maximum height which would likely break the lift
                        liftLocation++;
                        telemetry.addData("Going up", "");
                    }
                    shouldLiftMove = true;
                    shouldHoldHeight = false;
                    actionTaken = true;
                }
                else if(gamepad2.dpad_down){
                    if(liftLocation>0){ //makes sure that the lift cannot get into a negative position which would likely break the lift
                        liftLocation--;
                        telemetry.addData("Going down", "");
                    }
                    actionTaken = true;
                }
                else if(gamepad2.dpad_right){
                    liftLocation = ellie.maxHeight; //replace with max lift position
                    telemetry.addData("Max height", "");
                    actionTaken = true;
                }
                else if(gamepad2.dpad_left){
                    liftLocation = 0; //min lift position

                    telemetry.addData("Min height", "");
                    actionTaken = true;
                }
                if(gamepad2.a&&grabFound){
                    pull.releaseFound();
                    grabFound = false;
                }
                else if(gamepad2.a){
                    pull.grabFound();
                    grabFound = true;
                }
                if(actionTaken == true)
                {
                    actionTaken = false;
                    lastLoop.reset();
                }
            }

                    ellie.liftMove(liftLocation);
                    telemetry.addData("Lift moving to", liftLocation);


            if(gamepad2.y){
                pince.close();
                grabStone = false;
            }
            else{
                pince.release();
            }

            if(gamepad1.b){
                vroom.turn(90,3);
            }
            if(gamepad1.x){
                vroom.turn(17,3);
            }

            if(gamepad2.b) {
                grabby.grabSkystone(0);
                grabby.grabSkystone(1);
            }
            else{
                grabby.releaseSkystone();
            }
            //telemetry.addData("Loop time",loopTime.milliseconds());
            //telemetry.addData("degrees",gyro.getAngle());
                //drive code
                    vroom.driveT(1);

                /*
                telemetry.addData("Lift Left encoders: ",leftLift.getCurrentPosition());
                telemetry.addData("Lift Left power: ",leftLift.getPower());
                telemetry.addData("Lift Right encoders: ",rightLift.getCurrentPosition());
                telemetry.addData("Lift Right power: ",rightLift.getPower());
            telemetry.addData("Left direction: ",leftLift.getDirection());
            telemetry.addData("Right direction ",rightLift.getDirection());
                telemetry.addData("Grab stone?", grabStone);
                telemetry.addData("Grab foundation?", grabFound);
                 */


            telemetry.addData("left voltage", leftLift.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("right voltage", rightLift.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("left position", leftLift.getCurrentPosition());
            telemetry.addData("right position", rightLift.getCurrentPosition());

            telemetry.update();
            }

        }
    }
