package de.fhg.iais.roberta.visitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.bean.UsedHardwareBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.light.LedsOffAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayFileAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.action.sound.VolumeAction;
import de.fhg.iais.roberta.syntax.action.thymio.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.thymio.RedLedOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.YellowLedOnAction;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.Var;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.ColorSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.thymio.TapSensor;
import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.MotorDuration;
import de.fhg.iais.roberta.util.syntax.SC;

public final class ThymioAsebaVisitor extends AbstractAsebaVisitor implements IThymioVisitor<Void> {

    public ThymioAsebaVisitor(
        List<List<Phrase>> programPhrases, ClassToInstanceMap<IProjectBean> beans, ConfigurationAst configurationAst) {
        super(programPhrases, beans);
    }

    @Override
    public Void visitAccelerometerSensor(AccelerometerSensor accelerometerSensor) {
        this.sb.append("acc[").append(accelerometerSensor.getSlot()).append("]");
        return null;
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction clearDisplayAction) {
        return null;
    }

    @Override
    public Void visitColorConst(ColorConst colorConst) {
        this.sb.append("[").append(colorConst.getRedChannelInt()).append(", ").append(colorConst.getGreenChannelInt()).append(", ").append(colorConst.getBlueChannelInt()).append("]");
        return null;
    }

    @Override
    public Void visitColorSensor(ColorSensor colorSensor) {
        return null;
    }

    @Override
    public Void visitCurveAction(CurveAction curveAction) {
        String multiplier = curveAction.direction.toString().equals(SC.FOREWARD) ? "" : "-";
        move(curveAction.paramRight.getDuration(), curveAction.paramLeft.getSpeed(), curveAction.paramRight.getSpeed(), multiplier, multiplier);
        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction driveAction) {
        String multiplier = driveAction.direction.toString().equals(SC.FOREWARD) ? "" : "-";
        move(driveAction.param.getDuration(), driveAction.param.getSpeed(), driveAction.param.getSpeed(), multiplier, multiplier);
        return null;
    }

    @Override
    public Void visitInfraredSensor(InfraredSensor infraredSensor) {
        String mode = infraredSensor.getMode().toLowerCase();
        String sensorMode = "";
        switch ( mode ) {
            case C.DISTANCE:
                sensorMode = "horizontal";
                break;
            case C.LINE:
                sensorMode = "ground.reflected";
                break;
            case C.AMBIENTLIGHT:
                sensorMode = "ground.ambiant";
                break;
            default:
                throw new DbcException("Invalid infrared sensor mode!");
        }
        this.sb.append("prox.").append(sensorMode).append("[").append(infraredSensor.getSlot()).append("]");
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor keysSensor) {
        this.sb.append("button.").append(keysSensor.getUserDefinedPort().toLowerCase());
        return null;
    }

    @Override
    public Void visitLightAction(LightAction lightAction) {
        if ( lightAction.rgbLedColor.getClass().equals(ColorConst.class) ) {
            this.sb.append("call leds.").append(lightAction.port.toLowerCase()).append("(");
            ColorConst color = (ColorConst) lightAction.rgbLedColor;
            this.sb.append(color.getRedChannelInt()).append("/LED_REMAP, ").append(color.getGreenChannelInt()).append("/LED_REMAP, ").append(color.getBlueChannelInt()).append("/LED_REMAP)");
        } else if ( lightAction.rgbLedColor.getClass().equals(RgbColor.class) ) {
            this.sb.append("call leds.").append(lightAction.port.toLowerCase()).append("(");
            RgbColor color = (RgbColor) lightAction.rgbLedColor;
            color.R.accept(this);
            this.sb.append("/LED_REMAP, ");
            color.G.accept(this);
            this.sb.append("/LED_REMAP, ");
            color.B.accept(this);
            this.sb.append("/LED_REMAP)");
        } else if ( lightAction.rgbLedColor.getClass().equals(Var.class) ) {
            this.sb.append("call leds.").append(lightAction.port.toLowerCase()).append("(");
            Var color = (Var) lightAction.rgbLedColor;
            color.accept(this);
            this.sb.append("[0]/LED_REMAP, ");
            color.accept(this);
            this.sb.append("[1]/LED_REMAP, ");
            color.accept(this);
            this.sb.append("[2]/LED_REMAP)");
        } else {
            lightAction.rgbLedColor.accept(this);
            nlIndent();
            this.sb.append("call leds.").append(lightAction.port.toLowerCase()).append("(___r/LED_REMAP, ___g/LED_REMAP, ___b/LED_REMAP)");
        }
        return null;
    }

    @Override
    public Void visitLightSensor(LightSensor lightSensor) {
        this.sb.append("prox.ground.ambiant").append("[").append(lightSensor.getSlot()).append("]");
        return null;
    }

    @Override
    public Void visitMainTask(MainTask mainTask) {
        StmtList variables = mainTask.variables;
        variables.accept(this);
        if ( variables.sl.size() > 0 ) {
            nlIndent();
            nlIndent();
        }
        this.sb.append("timer.period[0] = 10");
        nlIndent();
        nlIndent();
        this.sb.append("onevent timer0");
        incrIndentation();
        nlIndent();
        this.getBean(UsedHardwareBean.class).getUserDefinedMethods().forEach(method -> {
            method.accept(this);
            nlIndent();
        });
        int first = this.funcStart.size() > 0 ? this.funcStart.size() - 1 + 1 : 0;
        this.sb.append(this.getIfElse()).append(" _state == ").append(first).append(" then");
        incrIndentation();
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        this.sb.append("callsub diffdrive_stop");
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction motorGetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        String motorSide = motorOnAction.port.toLowerCase();
        if ( motorOnAction.getDurationValue() != null ) {
            if ( !this.newState ) {
                this.stateCounter++;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
                nlIndent();
                this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
                incrIndentation();
                nlIndent();
            } else {
                newState = false;
            }
            this.sb.append("__time = 0");
            nlIndent();
            this.sb.append("motor.").append(motorSide).append(".target = MOTOR_MAX * ");
            motorOnAction.param.getSpeed().accept(this);
            nlIndent();
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            incrIndentation();
            nlIndent();
            this.sb.append("if __time == ");
            motorOnAction.getDurationValue().accept(this);
            this.sb.append("/timer.period[0] then");
            incrIndentation();
            nlIndent();
            this.sb.append("motor.").append(motorSide).append(".target = 0");
            nlIndent();
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("else");
            incrIndentation();
            nlIndent();
            this.sb.append("__time++");
            decrIndentation();
            nlIndent();
            this.sb.append("end");
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            this.newState = true;
            incrIndentation();
        } else {
            this.sb.append("motor.").append(motorSide).append(".target = MOTOR_MAX * ");
            motorOnAction.param.getSpeed().accept(this);
        }
        return null;
    }


    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction motorStopAction) {
        String motorSide = motorStopAction.port.toLowerCase();
        this.sb.append("motor.").append(motorSide).append(".target = 0");
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction playFileAction) {
        // TODO
        this.sb.append("call sound.system( ").append(playFileAction.fileName).append(" )");
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction playNoteAction) {
        if ( !this.newState ) {
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            incrIndentation();
            nlIndent();
        } else {
            newState = false;
        }
        this.sb.append("__time = 0");
        nlIndent();
        this.sb.append("call sound.freq(").append(playNoteAction.frequency.split("\\.")[0]).append(", ").append(playNoteAction.duration).append("/16)");
        nlIndent();
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        this.sb.append("if __time == ").append(playNoteAction.duration).append("/timer.period[0] then");
        incrIndentation();
        nlIndent();
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("else");
        incrIndentation();
        nlIndent();
        this.sb.append("__time++");
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        this.stateCounter++;
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        this.newState = true;
        incrIndentation();
        return null;
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction playRecordingAction) {
        return null;
    }

    @Override
    public Void visitRedLedOnAction(RedLedOnAction redLedOnAction) {
        this.sb.append("call leds.buttons(");
        redLedOnAction.led1.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        redLedOnAction.led2.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        redLedOnAction.led3.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        redLedOnAction.led4.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP)");
        return null;
    }

    @Override
    public Void visitRgbColor(RgbColor rgbColor) {
        this.sb.append("[");
        rgbColor.R.accept(this);
        this.sb.append(", ");
        rgbColor.G.accept(this);
        this.sb.append(", ");
        rgbColor.B.accept(this);
        this.sb.append("]");
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction showTextAction) {
        return null;
    }

    @Override
    public Void visitSoundSensor(SoundSensor soundSensor) {
        this.sb.append("mic.intensity");
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        switch ( timerSensor.getMode() ) {
            case SC.DEFAULT:
            case SC.VALUE:
                this.sb.append("((cyberpi.timer.get() - _timer").append(timerSensor.getUserDefinedPort()).append(")*1000)");
                break;
            case SC.RESET:
                this.sb.append("_timer").append(timerSensor.getUserDefinedPort()).append(" = cyberpi.timer.get()");
                break;
            default:
                throw new DbcException("Invalid Time Mode!");
        }
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction toneAction) {
        if ( !this.newState ) {
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            incrIndentation();
            nlIndent();
        } else {
            newState = false;
        }
        this.sb.append("__time = 0");
        nlIndent();
        this.sb.append("call sound.freq(");
        toneAction.frequency.accept(this);
        this.sb.append(", ");
        toneAction.duration.accept(this);
        this.sb.append("/16)");
        this.stateCounter++;
        nlIndent();
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        this.sb.append("if __time == ");
        toneAction.duration.accept(this);
        this.sb.append("/timer.period[0] then");
        incrIndentation();
        nlIndent();
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("else");
        incrIndentation();
        nlIndent();
        this.sb.append("__time++");
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        this.stateCounter++;
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        this.newState = true;
        incrIndentation();
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        String multiplierRight = turnAction.direction.toString().equals(SC.RIGHT) ? "-" : "";
        String multiplierLeft = turnAction.direction.toString().equals(SC.LEFT) ? "-" : "";
        move(turnAction.param.getDuration(), turnAction.param.getSpeed(), turnAction.param.getSpeed(), multiplierRight, multiplierLeft);
        return null;
    }

    private void move(MotorDuration duration, Expr speedLeft, Expr speedRight, String multLeft, String multRight) {
        if ( duration != null ) {
            if ( !this.newState ) {
                this.stateCounter++;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
                nlIndent();
                this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
                incrIndentation();
                nlIndent();
            } else {
                newState = false;
            }
            this.sb.append("__time = 0");
            nlIndent();
            this.sb.append("motor.left.target = MOTOR_MAX * ").append(multLeft);
            speedLeft.accept(this);
            nlIndent();
            this.sb.append("motor.right.target = MOTOR_MAX * ").append(multRight);
            speedRight.accept(this);
            nlIndent();
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            incrIndentation();
            nlIndent();
            this.sb.append("if __time == ");
            duration.getValue().accept(this);
            this.sb.append("/timer.period[0] then");
            incrIndentation();
            nlIndent();
            this.sb.append("callsub diffdrive_stop");
            nlIndent();
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("else");
            incrIndentation();
            nlIndent();
            this.sb.append("__time++");
            decrIndentation();
            nlIndent();
            this.sb.append("end");
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            this.newState = true;
            incrIndentation();
        } else {
            this.sb.append("motor.left.target = ").append(multLeft);
            speedLeft.accept(this);
            this.sb.append(" * MOTOR_MAX");
            nlIndent();
            this.sb.append("motor.right.target = ").append(multRight);
            speedRight.accept(this);
            this.sb.append(" * MOTOR_MAX");
        }
    }

    @Override
    public Void visitVolumeAction(VolumeAction volumeAction) {
        return null;
    }

    @Override
    public Void visitWaitTimeStmt(WaitTimeStmt waitTimeStmt) {
        if ( !this.newState ) {
            this.stateCounter++;
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
            incrIndentation();
            nlIndent();
        } else {
            newState = false;
        }
        this.sb.append("__time = 0");
        nlIndent();
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        this.sb.append("if __time == ");
        waitTimeStmt.time.accept(this);
        this.sb.append("/timer.period[0] then");
        incrIndentation();
        nlIndent();
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("else");
        incrIndentation();
        nlIndent();
        this.sb.append("__time++");
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        this.newState = true;
        incrIndentation();
        return null;
    }

    @Override
    public Void visitYellowLedOnAction(YellowLedOnAction yellowLedOnAction) {
        this.sb.append("call leds.circle(");
        yellowLedOnAction.led1.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led2.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led3.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led4.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led5.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led6.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led7.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP, ");
        yellowLedOnAction.led8.accept(this);
        this.sb.append("/COLOR_INTENSITY_REMAP)");
        return null;
    }

    @Override
    public Void visitLedsOffAction(LedsOffAction ledsOffAction) {
        this.sb.append("call leds.").append(ledsOffAction.port.toLowerCase()).append("(0, 0, 0)");
        return null;
    }

    @Override
    public Void visitTapSensor(TapSensor tapSensor) {
        this.sb.append("(abs(acc[0]) > THRESHOLD or abs(acc[1]) > THRESHOLD or abs(acc[2]) > THRESHOLD)");
        return null;
    }

    @Override
    protected void generateProgramPrefix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        nlIndent();
        this.sb.append("<!DOCTYPE aesl-source>");
        nlIndent();
        this.sb.append("<network>");
        nlIndent();
        this.sb.append("<constant value=\"5\" name=\"MOTOR_MAX\"/>");
        nlIndent();
        this.sb.append("<constant value=\"8\" name=\"LED_REMAP\"/>");
        nlIndent();
        this.sb.append("<constant value=\"3\" name=\"COLOR_INTENSITY_REMAP\"/>");
        nlIndent();
        this.sb.append("<constant value=\"20\" name=\"THRESHOLD\"/>");
        nlIndent();
        this.sb.append("<!--node thymio-II-->");
        nlIndent();
        this.sb.append("<node name=\"thymio-II\">");
        nlIndent();
        this.myMethods = new ArrayList();
        this.getBean(UsedHardwareBean.class).getUserDefinedMethods().forEach(method -> this.myMethods.add(method.getMethodName()));
        this.myMethods.forEach(m -> {
            this.funcStart.add(this.stateCounter);
            this.stateCounter++;
        });
        appendRobotVariables();
        generateVariablesForUsage(this.programPhrases);
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        nlIndent();
        this.sb.append("timer.period[0] = 0");
        // TODO stop all
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        decrIndentation();
        nlIndent();
        if ( !withWrapping ) {
            return;
        }
        generateUserDefinedMethodsForThymio();
        if ( !this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().isEmpty() ) {
            String helperMethodImpls = this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodDefinitions(this.getBean(CodeGeneratorSetupBean.class).getUsedMethods());
            this.sb.append(helperMethodImpls);
        }
        nlIndent();
        this.sb.append("</node>");
        nlIndent();
        this.sb.append("</network>");
        nlIndent();
    }

    void generateUserDefinedMethodsForThymio() {
        this.getBean(UsedHardwareBean.class).getUserDefinedMethods().forEach(method -> {
            String methodName = method.getMethodName();
            int funcIndex = this.myMethods.indexOf(methodName);
            nlIndent();
            this.sb.append("sub ").append(methodName);
            incrIndentation();
            nlIndent();
            this.sb.append("_state = ").append(this.funcStart.get(funcIndex));
            decrIndentation();
            nlIndent();
        });
    }

    private void appendRobotVariables() {
        nlIndent();
        this.sb.append("var _result");
        nlIndent();
        this.sb.append("var _state = ").append(this.stateCounter);
        nlIndent();
        if ( this.stateCounter > 0 ) {
            this.sb.append("var _return_state");
            nlIndent();
        }
        this.sb.append("var __time = 0");
    }

    private void generateVariablesForUsage(List<Phrase> exprList) {
        List<String> listVariablesWithoutDuplicates = new ArrayList<>(
            new LinkedHashSet<>(this.getBean(UsedHardwareBean.class).getDeclaredVariables()));
        for ( String global : this.getBean(UsedHardwareBean.class).getMarkedVariablesAsGlobal() ) {
            listVariablesWithoutDuplicates.remove(global);
        }
        listVariablesWithoutDuplicates.forEach(var -> {
            nlIndent();
            this.sb.append("var ___").append(var);
            if ( var.equals("color") ) {
                this.sb.append("[3]");
            } else if ( var.equals("true") ) {
                this.sb.append(" = 1");
            }
        });
    }
}
