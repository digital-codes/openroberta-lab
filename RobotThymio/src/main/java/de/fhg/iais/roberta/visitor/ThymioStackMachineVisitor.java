package de.fhg.iais.roberta.visitor;

import java.util.List;

import org.json.JSONObject;

import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.inter.mode.action.ITurnDirection;
import de.fhg.iais.roberta.mode.action.DriveDirection;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.light.LedsOffAction;
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
import de.fhg.iais.roberta.syntax.action.thymio.LedButtonOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.LedCircleOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.LedProxHOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.LedProxVOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.LedSoundOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.LedTemperatureOnAction;
import de.fhg.iais.roberta.syntax.action.thymio.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.thymio.RecordStartAction;
import de.fhg.iais.roberta.syntax.action.thymio.RecordStopAction;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.thymio.TapSensor;
import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.MotorDuration;
import de.fhg.iais.roberta.visitor.lang.codegen.AbstractStackMachineVisitor;

public final class ThymioStackMachineVisitor extends AbstractStackMachineVisitor implements IThymioVisitor<Void> {
    public ThymioStackMachineVisitor(ConfigurationAst configuration, List<List<Phrase>> phrases) {
        super(configuration);
        Assert.isTrue(!phrases.isEmpty());
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction clearDisplayAction) {
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction motorGetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        motorOnAction.param.getSpeed().accept(this);
        MotorDuration duration = motorOnAction.param.getDuration();
        boolean speedOnly = !processOptionalDuration(duration);
        String port = motorOnAction.getUserDefinedPort();

        JSONObject o = makeNode(C.MOTOR_ON_ACTION).put(C.PORT, port.toLowerCase()).put(C.NAME, "mbot").put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, true));
            if ( duration.getType() == null ) {
                o.put(C.MOTOR_DURATION, C.TIME);

            } else {
                String durationType = duration.getType().toString().toLowerCase();
                o.put(C.MOTOR_DURATION, durationType);
            }
            add(o);
            return add(makeNode(C.MOTOR_STOP).put(C.PORT, port.toLowerCase()));
        }
    }

    @Override
    public Void visitInfraredSensor(InfraredSensor infraredSensor) {
        String mode = infraredSensor.getMode().toLowerCase();
        String slot = infraredSensor.getSlot().toLowerCase();
        if ( slot.equals("0") ) {
            slot = C.LEFT;
        } else if ( slot.equals("1") ) {
            slot = C.RIGHT;
        }
        JSONObject o = makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.INFRARED).put(C.MODE, mode).put(C.SLOT, slot);
        return add(o);
    }

    @Override
    public Void visitSoundSensor(SoundSensor soundSensor) {
        return add(makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.SOUND).put(C.MODE, C.VOLUME));
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction motorStopAction) {
        String port = motorStopAction.getUserDefinedPort();
        JSONObject o = makeNode(C.MOTOR_STOP).put(C.PORT, port.toLowerCase());
        return add(o);
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction playRecordingAction) {
        return null;
    }

    @Override
    public Void visitLedButtonOnAction(LedButtonOnAction ledButtonOnAction) {
        return null;
    }

    @Override
    public Void visitLedCircleOnAction(LedCircleOnAction ledCircleOnAction) {
        return null;
    }

    @Override
    public Void visitLedSoundOnAction(LedSoundOnAction ledSoundOnAction) {
        return null;
    }

    @Override
    public Void visitLedTemperatureOnAction(LedTemperatureOnAction ledTemperatureOnAction) {
        return null;
    }

    @Override
    public Void visitLedProxHOnAction(LedProxHOnAction ledProxHOnAction) {
        return null;
    }

    @Override
    public Void visitLedProxVOnAction(LedProxVOnAction ledProxVOnAction) {
        return null;
    }

    @Override
    public Void visitLedsOffAction(LedsOffAction ledsOffAction) {
        return null;
    }

    @Override
    public Void visitTapSensor(TapSensor tapSensor) {
        return add(makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.TOUCH));
    }

    @Override
    public Void visitRecordStartAction(RecordStartAction recordStartAction) {
        return null;
    }

    @Override
    public Void visitRecordStopAction(RecordStopAction recordStopAction) {
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction showTextAction) {
        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction driveAction) {
        driveAction.param.getSpeed().accept(this);
        boolean speedOnly = !processOptionalDuration(driveAction.param.getDuration());
        DriveDirection driveDirection = (DriveDirection) driveAction.direction;
        JSONObject o = makeNode(C.DRIVE_ACTION).put(C.DRIVE_DIRECTION, driveDirection).put(C.NAME, "mbot").put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, true));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot"));
        }
    }

    @Override
    public Void visitCurveAction(CurveAction curveAction) {
        curveAction.paramLeft.getSpeed().accept(this);
        curveAction.paramRight.getSpeed().accept(this);
        boolean speedOnly = !processOptionalDuration(curveAction.paramLeft.getDuration());
        DriveDirection driveDirection = (DriveDirection) curveAction.direction;

        JSONObject o = makeNode(C.CURVE_ACTION).put(C.DRIVE_DIRECTION, driveDirection).put(C.NAME, "mbot").put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, true));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot"));
        }
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction toneAction) {
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction playNoteAction) {
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        turnAction.param.getSpeed().accept(this);
        boolean speedOnly = !processOptionalDuration(turnAction.param.getDuration());
        ITurnDirection turnDirection = turnAction.direction;
        JSONObject o =
            makeNode(C.TURN_ACTION).put(C.TURN_DIRECTION, turnDirection.toString().toLowerCase()).put(C.NAME, "mbot").put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, true));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot"));
        }
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        JSONObject o = makeNode(C.STOP_DRIVE).put(C.NAME, "mbot");
        return add(o);
    }

    @Override
    public Void visitVolumeAction(VolumeAction volumeAction) {
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction playFileAction) {
        return null;
    }
}
