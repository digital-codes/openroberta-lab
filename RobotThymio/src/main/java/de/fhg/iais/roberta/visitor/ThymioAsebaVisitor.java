package de.fhg.iais.roberta.visitor;

import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.Category;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.constants.ThymioConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
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
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.ExprList;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.Stmt;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.ColorSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.MotorDuration;
import de.fhg.iais.roberta.util.syntax.SC;

public final class ThymioAsebaVisitor extends AbstractAsebaVisitor implements IThymioVisitor<Void> {

    private final ConfigurationAst configurationAst;

    public ThymioAsebaVisitor(
        List<List<Phrase>> programPhrases, ClassToInstanceMap<IProjectBean> beans, ConfigurationAst configurationAst) {
        super(programPhrases, beans);
        this.configurationAst = configurationAst;
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
        String mode = infraredSensor.getMode().equals("DISTANCE") ? "horizontal" : "ground.reflected";
        this.sb.append("prox.").append(mode).append("[").append(infraredSensor.getSlot()).append("]");
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor keysSensor) {
//        incrIndentation();
//        nlIndent();
//        this.sb.append("if button.").append(keysSensor.getUserDefinedPort().toLowerCase());
//        this.sb.append(" == 1 then");
//        incrIndentation();
//        nlIndent();
//        this.sb.append("_state = ").append(this.stateCounter);
//        decrIndentation();
//        nlIndent();
//        this.sb.append("else");
//        incrIndentation();
//        nlIndent();
//        this.sb.append("timer.period[0] = 10");
//        decrIndentation();
//        nlIndent();
//        this.sb.append("end");
        this.sb.append("button.").append(keysSensor.getUserDefinedPort().toLowerCase());
        return null;
    }

    @Override
    public Void visitLightAction(LightAction lightAction) {
        this.sb.append("call leds.").append(lightAction.port.toLowerCase()).append("( ");
        if ( lightAction.rgbLedColor.getClass().equals(ColorConst.class) ) {
            ColorConst color = (ColorConst) lightAction.rgbLedColor;
            this.sb
                .append(color.getRedChannelInt()).append("/LED_REMAP, ")
                .append(color.getGreenChannelInt()).append("/LED_REMAP, ")
                .append(color.getBlueChannelInt()).append("/LED_REMAP )");
        } else if ( lightAction.rgbLedColor.getClass().equals(RgbColor.class) ) {
            RgbColor color = (RgbColor) lightAction.rgbLedColor;
            color.R.accept(this);
            this.sb.append("/LED_REMAP, ");
            color.G.accept(this);
            this.sb.append("/LED_REMAP, ");
            color.B.accept(this);
            this.sb.append("/LED_REMAP )");
        } else {
            this.sb.append("_r");
            lightAction.rgbLedColor.accept(this);
            this.sb.append("/LED_REMAP, ").append("_g");
            lightAction.rgbLedColor.accept(this);
            this.sb.append("/LED_REMAP, ").append("_b");
            lightAction.rgbLedColor.accept(this);
            this.sb.append("/LED_REMAP )");
        }
//        nlIndent();
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
        nlIndent();
        this.programPhrases
            .stream()
            .filter(phrase -> phrase.getKind().getCategory() != Category.METHOD || phrase.getKind().hasName("METHOD_CALL"))
            .forEach(p -> this.noOfStates++);
        this.noOfStates--;
        this.programPhrases
            .stream()
            .filter(phrase -> phrase.getKind().hasName("REPEAT_STMT"))
            .forEach(p -> {
                this.noOfLoopStates = ((RepeatStmt) p).list.get().size();
            });
        this.noOfLoopStates--;
        this.sb.append("timer.period[0] = 10");
        nlIndent();
        nlIndent();
        this.sb.append("onevent timer0");
        incrIndentation();
        nlIndent();
        this.sb.append("if _state == 0 then");
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
            this.sb.append("_state = ").append(this.stateCounter);
            decrIndentation();
            nlIndent();
            this.stateCounter++;
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
            ;
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
        incrIndentation();
        nlIndent();
        String stateVar = this.isFunc ? "_funcstate" : this.isLoop ? "_loopstate" : "_state";
        int counter = this.isFunc ? this.funcCounter : this.isLoop ? this.loopCounter : this.stateCounter;
        String ifElseif = counter == 0 ? "if " : "elseif ";
        this.sb.append(ifElseif).append(stateVar).append(" == ").append(counter).append(" then");
        incrIndentation();
        nlIndent();
        this.sb.append("call sound.system( ").append(playFileAction.fileName).append(" )");
        nlIndent();
        this.sb.append("timer.period[0] = 1000");
        nlIndent();
        this.sb.append(stateVar).append("++");
        if ( isFunc ) {
            this.funcCounter++;
        } else if ( isLoop ) {
            this.loopCounter++;
        } else {
            this.stateCounter++;
        }
        decrIndentation();
        nlIndent();
        if ( this.noOfStates == counter ) {
            this.sb.append("end");
            decrIndentation();
        }
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
        this.sb.append("call sound.freq(").append(
            playNoteAction.frequency).append(", ").append(playNoteAction.duration).append("/16)");
        nlIndent();
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.stateCounter++;
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
        this.sb.append("(");
        rgbColor.R.accept(this);
        this.sb.append(", ");
        rgbColor.G.accept(this);
        this.sb.append(", ");
        rgbColor.B.accept(this);
        this.sb.append(")");
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
        nlIndent();
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.stateCounter++;
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
                newState = true;
            }
            this.sb.append("__time = 0");
            nlIndent();
            this.sb.append("speedL = ").append(multLeft);
            speedLeft.accept(this);
            this.sb.append(" * MOTOR_MAX");
            nlIndent();
            this.sb.append("speedR = ").append(multRight);
            speedLeft.accept(this);
            this.sb.append(" * MOTOR_MAX");
            nlIndent();
            this.sb.append("callsub diffdrive");
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
    public Void visitWaitStmt(WaitStmt waitStmt) {
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        String stateVar = this.isFunc ? "_funcstate" : this.isLoop ? "_loopstate" : "_state";
        int counter = this.isFunc ? this.funcCounter : this.isLoop ? this.loopCounter : this.stateCounter;
        String ifElseif = counter == 0 ? "if " : "elseif ";
        this.sb.append(ifElseif).append(stateVar).append(" == ").append(counter).append(" then");
        incrIndentation();
        visitStmtList(waitStmt.statements);
//        decrIndentation();
        if ( isFunc ) {
            this.funcCounter++;
        } else if ( isLoop ) {
            this.loopCounter++;
        } else {
            this.stateCounter++;
        }
//        decrIndentation();
//        nlIndent();
        if ( this.noOfStates == counter + 1 ) {
            this.sb.append("end");
//            decrIndentation();
        }
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
        this.sb.append("<!--node thymio-II-->");
        nlIndent();
        this.sb.append("<node name=\"thymio-II\">");
        nlIndent();
        nlIndent();
        appendRobotVariables();
        nlIndent();
        generateVariablesForUsage(this.programPhrases);
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        nlIndent();
        this.sb.append("timer.period[0] = 0");
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        decrIndentation();
        nlIndent();
        if ( !withWrapping ) {
            return;
        }
        generateUserDefinedMethods();
        if ( !this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().isEmpty() ) {
            String helperMethodImpls =
                this.getBean(CodeGeneratorSetupBean.class)
                    .getHelperMethodGenerator()
                    .getHelperMethodDefinitions(this.getBean(CodeGeneratorSetupBean.class).getUsedMethods());
            this.sb.append(helperMethodImpls);
        }
        nlIndent();
        this.sb.append("</node>");
        nlIndent();
        this.sb.append("</network>");
        nlIndent();
    }

    private void appendRobotVariables() {
//        ConfigurationComponent diffDrive = getDiffDrive();
//        if ( diffDrive != null ) {
//            nlIndent();
//            double circumference = 6.5 * Math.PI;
//            double trackWidth = 11.5;
//            this.sb.append("var _trackWidth = ");
//            this.sb.append((int) Math.floor(trackWidth));
//            nlIndent();
//            this.sb.append("var _circumference = ");
//            this.sb.append((int) Math.floor(circumference));
        nlIndent();
        this.sb.append("var _result # to store potential results from function calls");
        nlIndent();
        this.sb.append("var _state = 0");
        nlIndent();
        this.sb.append("var _loopstate = 0");
        nlIndent();
        this.sb.append("var _funcstate = 0");
        nlIndent();
        this.sb.append("var __time = 0");
//        }
    }

    private ConfigurationComponent getDiffDrive() {
        for ( ConfigurationComponent component : this.configurationAst.getConfigurationComponents().values() ) {
            if ( component.componentType.equals(ThymioConstants.DIFFERENTIALDRIVE) ) {
                return component;
            }
        }
        return null;
    }

    private void generateVariablesForUsage(List<Phrase> exprList) {
        boolean isAppended = false;
        boolean isAppendedDrive = false;
        for ( Phrase e : exprList ) {
            if ( e.getKind().hasName("CURVE_ACTION", "DRIVE_ACTION", "TURN_ACTION") ) {
                if ( !isAppendedDrive ) {
                    this.sb.append("var speedL");
                    nlIndent();
                    this.sb.append("var speedR");
                    isAppendedDrive = true;
                }
            }
            if ( e.getKind().getName().equals("REPEAT_STMT") ) {
                RepeatStmt stmt = (RepeatStmt) e;
                isAppended = generateVariablesForLoops(isAppended, stmt);
                for ( Stmt s : stmt.list.get() ) {
                    if ( s.getKind().getName().equals("REPEAT_STMT") ) {
                        isAppended = generateVariablesForLoops(isAppended, (RepeatStmt) s);
                    }
                }
            }
            if ( e.getKind().getName().equals("IF_STMT") ) {
                if ( !isAppended ) {
                    this.sb.append("var ___true = 1");
                    isAppended = true;
                    nlIndent();
                }
            }
        }
        if ( this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().contains(ThymioMethods.DIFFDRIVE) ||
            this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().contains(ThymioMethods.STOP) ) {
            if ( !isAppendedDrive ) {
                this.sb.append("var speedL");
                nlIndent();
                this.sb.append("var speedR");
                isAppendedDrive = true;
            }
        }
    }

    private boolean generateVariablesForLoops(boolean isAppended, RepeatStmt stmt) {
        switch ( stmt.mode ) {
            case FOREVER:
            case UNTIL:
            case WHILE:
                if ( !isAppended ) {
                    this.sb.append("var ___true = 1");
                    isAppended = true;
                    nlIndent();
                }
                break;
            case TIMES:
            case FOR:
//                        List<Stmt> s = stmt.getList().get();
//                for ( Stmt s : stmt.getList().get() ) {
//                    LoggerFactory.getLogger(ThymioAsebaVisitor.class).info(s.toString());
                ExprList expr = (ExprList) stmt.expr;
                this.sb.append("var ");
                expr.get().get(0).accept(this);
                this.sb.append(" = 0");
                nlIndent();
//                }
                break;
            case WAIT:
                this.sb.append(stmt.getKind()).append(stmt.mode).append(stmt.expr);
//                        generateCodeFromStmtCondition("if", repeatStmt.getExpr());
                break;
            case FOR_EACH:
//                        generateCodeFromStmtCondition("for", repeatStmt.getExpr());
                break;
            default:
                throw new DbcException("Invalid Repeat Statement!");
        }
        return isAppended;
    }
}
