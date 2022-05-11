package de.fhg.iais.roberta.visitor.validate;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.constants.RobotinoConstants;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveDistanceAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidrivePositionAction;
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.sensor.generic.DetectMarkSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.PinGetValueSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TouchSensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.CameraSensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.MarkerInformation;
import de.fhg.iais.roberta.syntax.sensor.robotino.OdometrySensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.OdometrySensorReset;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.IRobotinoVisitor;
import de.fhg.iais.roberta.visitor.RobotinoMethods;


public abstract class RobotinoValidatorAndCollectorVisitor extends MotorValidatorAndCollectorVisitor implements IRobotinoVisitor<Void> {

    public RobotinoValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(timerSensor.getUserDefinedPort(), SC.TIMER, timerSensor.getMode()));
        return null;
    }

    @Override
    public Void visitTouchSensor(TouchSensor touchSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(touchSensor.getUserDefinedPort(), SC.TOUCH, touchSensor.getMode()));
        return null;
    }

    @Override
    public Void visitOmnidriveAction(OmnidriveAction omnidriveAction) {

        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(RobotinoConstants.OMNIDRIVE), RobotinoConstants.OMNIDRIVE));
        requiredComponentVisited(omnidriveAction, omnidriveAction.xVel, omnidriveAction.yVel);
        requiredComponentVisited(omnidriveAction, omnidriveAction.thetaVel);
        return null;
    }

    @Override
    public Void visitOmnidriveDistanceAction(OmnidriveDistanceAction omnidriveDistanceAction) {
        requiredComponentVisited(omnidriveDistanceAction, omnidriveDistanceAction.xVel, omnidriveDistanceAction.yVel);
        requiredComponentVisited(omnidriveDistanceAction, omnidriveDistanceAction.distance);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(getConfigPort(RobotinoConstants.ODOMETRY), RobotinoConstants.ODOMETRY, null));
        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(RobotinoConstants.OMNIDRIVE), RobotinoConstants.OMNIDRIVE));

        checkIfBothZeroSpeed(omnidriveDistanceAction);
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        requiredComponentVisited(turnAction, turnAction.param.getSpeed(), turnAction.param.getDuration().getValue());

        usedHardwareBuilder.addUsedSensor(new UsedSensor(getConfigPort(RobotinoConstants.ODOMETRY), RobotinoConstants.ODOMETRY, null));
        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(RobotinoConstants.OMNIDRIVE), RobotinoConstants.OMNIDRIVE));
        return null;
    }


    @Override
    public Void visitOmnidrivePositionAction(OmnidrivePositionAction omnidrivePositionAction) {
        requiredComponentVisited(omnidrivePositionAction, omnidrivePositionAction.x, omnidrivePositionAction.y, omnidrivePositionAction.power);

        usedHardwareBuilder.addUsedSensor(new UsedSensor(getConfigPort(RobotinoConstants.ODOMETRY), RobotinoConstants.ODOMETRY, null));
        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(RobotinoConstants.OMNIDRIVE), RobotinoConstants.OMNIDRIVE));

        checkForZeroSpeed(omnidrivePositionAction, omnidrivePositionAction.power);
        return null;
    }

    private void checkIfBothZeroSpeed(OmnidriveDistanceAction omnidriveDistanceAction) {
        if ( omnidriveDistanceAction.xVel.getKind().hasName("NUM_CONST") && omnidriveDistanceAction.yVel.getKind().hasName("NUM_CONST") ) {

            if ( Math.abs(Double.parseDouble(((NumConst) omnidriveDistanceAction.xVel).value)) < DOUBLE_EPS && Math.abs(Double.parseDouble(((NumConst) omnidriveDistanceAction.yVel).value)) < DOUBLE_EPS ) {
                addWarningToPhrase(omnidriveDistanceAction, "MOTOR_SPEED_0");
            }
        }
    }

    @Override
    public Void visitInfraredSensor(InfraredSensor infraredSensor) {
        usedMethodBuilder.addUsedMethod(RobotinoMethods.GETDISTANCE);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(infraredSensor.getUserDefinedPort(), SC.INFRARED, infraredSensor.getMode()));
        return null;
    }

    @Override
    public Void visitOdometrySensor(OdometrySensor odometrySensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(odometrySensor.getUserDefinedPort(), RobotinoConstants.ODOMETRY, odometrySensor.getSlot()));
        return null;
    }

    @Override
    public Void visitOdometrySensorReset(OdometrySensorReset odometrySensorReset) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(odometrySensorReset.getUserDefinedPort(), RobotinoConstants.ODOMETRY, odometrySensorReset.slot));
        return null;
    }

    @Override
    public Void visitPinWriteValueAction(PinWriteValueAction pinWriteValueAction) {
        requiredComponentVisited(pinWriteValueAction, pinWriteValueAction.value);

        ConfigurationComponent usedConfigurationBlock = this.robotConfiguration.optConfigurationComponent(pinWriteValueAction.port);
        if ( usedConfigurationBlock == null ) {
            addErrorToPhrase(pinWriteValueAction, "CONFIGURATION_ERROR_ACTOR_MISSING");
        }

        usedMethodBuilder.addUsedMethod(RobotinoMethods.SETDIGITALPIN);
        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(pinWriteValueAction.port), SC.DIGITAL_PIN));
        return null;
    }

    @Override
    public Void visitPinGetValueSensor(PinGetValueSensor pinGetValueSensor) {
        ConfigurationComponent usedConfigurationBlock = this.robotConfiguration.optConfigurationComponent(pinGetValueSensor.getUserDefinedPort());
        if ( usedConfigurationBlock == null ) {
            addErrorToPhrase(pinGetValueSensor, "CONFIGURATION_ERROR_SENSOR_MISSING");
        }

        if ( pinGetValueSensor.getMode().equals(SC.ANALOG) ) {
            usedHardwareBuilder.addUsedSensor(new UsedSensor(pinGetValueSensor.getUserDefinedPort(), SC.ANALOG_INPUT, pinGetValueSensor.getMode()));
        } else if ( pinGetValueSensor.getMode().equals(SC.DIGITAL) ) {
            usedHardwareBuilder.addUsedSensor(new UsedSensor(pinGetValueSensor.getUserDefinedPort(), SC.DIGITAL_INPUT, pinGetValueSensor.getMode()));
        }
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor(getConfigPort(RobotinoConstants.OMNIDRIVE), RobotinoConstants.OMNIDRIVE));
        return null;
    }

    private String getConfigPort(String name) {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.componentType.equals(name) ) {
                return component.userDefinedPortName;
            }
        }
        return "";
    }

    @Override
    public Void visitMarkerInformation(MarkerInformation markerInformation) {
        return null;
    }

    @Override
    public Void visitDetectMarkSensor(DetectMarkSensor detectMarkSensor) {
        return null;
    }

    @Override
    public Void visitCameraSensor(CameraSensor cameraSensor) {
        return null;
    }
}
