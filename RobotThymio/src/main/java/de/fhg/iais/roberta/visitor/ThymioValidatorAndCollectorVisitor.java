package de.fhg.iais.roberta.visitor;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
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
import de.fhg.iais.roberta.syntax.action.thymio.RecordStartAction;
import de.fhg.iais.roberta.syntax.action.thymio.RecordStopAction;
import de.fhg.iais.roberta.syntax.action.thymio.RedLedOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.YellowLedOnAction;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.VarDeclaration;
import de.fhg.iais.roberta.syntax.lang.functions.MathConstrainFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathNumPropFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathOnListFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomFloatFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathSingleFunct;
import de.fhg.iais.roberta.syntax.lang.stmt.IfStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TemperatureSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.thymio.TapSensor;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.validate.CommonNepoValidatorAndCollectorVisitor;


public class ThymioValidatorAndCollectorVisitor extends CommonNepoValidatorAndCollectorVisitor implements IThymioVisitor<Void> {
    private static final Map<String, String> SENSOR_COMPONENT_TYPE_MAP = new HashMap<String, String>() {{
        put("SOUND_RECORD", SC.SOUND);
//        put("QUAD_COLOR_SENSING", ThymioConstants.MBUILD_QUADRGB);
        put("GYRO_AXIS_RESET", SC.GYRO);
    }};

    public ThymioValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitRgbColor(RgbColor rgbColor) {
        super.visitRgbColor(rgbColor);
        addColorVariables();
        return null;
    }

    @Override
    public Void visitColorConst(ColorConst colorConst) {
        super.visitColorConst(colorConst);
        addColorVariables();
        return null;
    }

    @Override
    public Void visitRepeatStmt(RepeatStmt repeatStmt) {
        super.visitRepeatStmt(repeatStmt);
        if ( repeatStmt.mode == RepeatStmt.Mode.FOREVER ) {
            usedHardwareBuilder.addDeclaredVariable("true");
        }
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt ifStmt) {
        super.visitIfStmt(ifStmt);
        usedHardwareBuilder.addDeclaredVariable("true");
        return null;
    }

    @Override
    public Void visitVarDeclaration(VarDeclaration varDeclaration) {
        super.visitVarDeclaration(varDeclaration);
        usedHardwareBuilder.addMarkedVariableAsGlobal(varDeclaration.name);
        return null;
    }

    private void addColorVariables() {
        usedHardwareBuilder.addDeclaredVariable("r");
        usedHardwareBuilder.addDeclaredVariable("g");
        usedHardwareBuilder.addDeclaredVariable("b");
        usedHardwareBuilder.addDeclaredVariable("color");
    }

    @Override
    public Void visitAccelerometerSensor(AccelerometerSensor accelerometerSensor) {
        return null;
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction clearDisplayAction) {
        return null;
    }

    @Override
    public Void visitCurveAction(CurveAction curveAction) {
        requiredComponentVisited(curveAction, curveAction.paramLeft.getSpeed(), curveAction.paramRight.getSpeed());
        if ( curveAction.paramRight.getDuration() != null ) {
            requiredComponentVisited(curveAction, curveAction.paramRight.getDuration().getValue());
            usedMethodBuilder.addUsedMethod(ThymioMethods.STOP);
        }
        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction driveAction) {
        requiredComponentVisited(driveAction, driveAction.param.getSpeed());
        if ( driveAction.param.getDuration() != null ) {
            requiredComponentVisited(driveAction, driveAction.param.getDuration().getValue());
            usedMethodBuilder.addUsedMethod(ThymioMethods.STOP);
        }
        return null;
    }

    @Override
    public Void visitInfraredSensor(InfraredSensor infraredSensor) {
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor keysSensor) {
        return null;
    }

    @Override
    public Void visitLightAction(LightAction lightAction) {
        requiredComponentVisited(lightAction, lightAction.rgbLedColor);
        usedHardwareBuilder.addUsedActor(new UsedActor(BlocklyConstants.EMPTY_PORT, SC.RGBLED));
        return null;
    }

    @Override
    public Void visitLightSensor(LightSensor lightSensor) {
        return null;
    }

    @Override
    public Void visitMainTask(MainTask mainTask) {
        requiredComponentVisited(mainTask, mainTask.variables);
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        usedMethodBuilder.addUsedMethod(ThymioMethods.STOP);
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction motorGetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        requiredComponentVisited(motorOnAction, motorOnAction.param.getSpeed());
        if ( motorOnAction.param.getDuration() != null ) {
            requiredComponentVisited(motorOnAction, motorOnAction.param.getDuration().getValue());
        }
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction motorStopAction) {
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction playFileAction) {
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction playNoteAction) {
        return null;
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction playRecordingAction) {
        requiredComponentVisited(playRecordingAction, playRecordingAction.filename);
        return null;
    }

    @Override
    public Void visitRedLedOnAction(RedLedOnAction redLedOnAction) {
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction showTextAction) {
        return null;
    }

    @Override
    public Void visitSoundSensor(SoundSensor soundSensor) {
        return null;
    }

    @Override
    public Void visitTemperatureSensor(TemperatureSensor temperatureSensor) {
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction toneAction) {
        requiredComponentVisited(toneAction, toneAction.duration, toneAction.frequency);
        usedHardwareBuilder.addUsedActor(new UsedActor(toneAction.port, SC.BUZZER));
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        requiredComponentVisited(turnAction, turnAction.param.getSpeed());
        if ( turnAction.param.getDuration() != null ) {
            requiredComponentVisited(turnAction, turnAction.param.getDuration().getValue());
            usedMethodBuilder.addUsedMethod(ThymioMethods.STOP);
        }
        return null;
    }

    @Override
    public Void visitVolumeAction(VolumeAction volumeAction) {
        return null;
    }

    @Override
    public Void visitYellowLedOnAction(YellowLedOnAction yellowLedOnAction) {
        return null;
    }


    public Void visitLedsOffAction(LedsOffAction ledsOffAction) {
        return null;
    }

    @Override
    public Void visitTapSensor(TapSensor tapSensor) {
        return null;
    }

    @Override
    public Void visitRecordStartAction(RecordStartAction recordStartAction) {
        requiredComponentVisited(recordStartAction, recordStartAction.filename);
        return null;
    }

    @Override
    public Void visitRecordStopAction(RecordStopAction recordStopAction) {
        return null;
    }

    @Override
    public Void visitMathSingleFunct(MathSingleFunct mathSingleFunct) {
        super.visitMathSingleFunct(mathSingleFunct);
        switch ( mathSingleFunct.functName ) {
            case ROOT:
            case SIN:
            case COS:
            case SQUARE:
            case ABS:
                break;
            default:
                addErrorToPhrase(mathSingleFunct, "BLOCK_NOT_SUPPORTED");
        }
        return null;
    }

    @Override
    public Void visitMathNumPropFunct(MathNumPropFunct mathNumPropFunct) {
        super.visitMathNumPropFunct(mathNumPropFunct);
        switch ( mathNumPropFunct.functName ) {
            case EVEN:
            case ODD:
            case WHOLE:
            case POSITIVE:
            case NEGATIVE:
            case DIVISIBLE_BY:
                break;
            case PRIME:
                addErrorToPhrase(mathNumPropFunct, "BLOCK_NOT_SUPPORTED");
                break;
            default:
                throw new DbcException("Statement not supported by Aseba!");
        }
        return null;
    }

    @Override
    public Void visitMathOnListFunct(MathOnListFunct mathOnListFunct) {
        throw new DbcException("Block not supported by Aseba!");
    }

    public Void visitMathConstrainFunct(MathConstrainFunct mathConstrainFunct) {
        throw new DbcException("Block not supported by Aseba!");
    }

    public Void visitMathRandomFloatFunct(MathRandomFloatFunct mathRandomFloatFunct) {
        throw new DbcException("Block not supported by Aseba!");
    }

    public Void visitMathRandomIntFunct(MathRandomIntFunct mathRandomIntFunct) {
        throw new DbcException("Block not supported by Aseba!");
    }
}
