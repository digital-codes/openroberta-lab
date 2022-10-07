package de.fhg.iais.roberta.visitor.validate;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveDistanceAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidrivePositionAction;
import de.fhg.iais.roberta.syntax.sensor.generic.PinGetValueSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TouchSensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.OdometrySensor;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.RobotinoMethods;

public class RobotinoViewValidatorAndCollectorVisitor extends RobotinoValidatorAndCollectorVisitor {

    public RobotinoViewValidatorAndCollectorVisitor(
        ConfigurationAst robotConfiguration,
        ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitTouchSensor(TouchSensor touchSensor) {
        usedMethodBuilder.addUsedMethod(RobotinoMethods.ISBUMPED);
        return super.visitTouchSensor(touchSensor);

    }

    @Override
    public Void visitPinGetValueSensor(PinGetValueSensor pinGetValueSensor) {
        if ( pinGetValueSensor.getMode().equals(SC.ANALOG) ) {
            usedMethodBuilder.addUsedMethod(RobotinoMethods.GETANALOGPIN);
        } else if ( pinGetValueSensor.getMode().equals(SC.DIGITAL) ) {
            usedMethodBuilder.addUsedMethod(RobotinoMethods.GETDIGITALPIN);
        }
        return super.visitPinGetValueSensor(pinGetValueSensor);
    }

    @Override
    public Void visitOdometrySensor(OdometrySensor odometrySensor) {
        usedMethodBuilder.addUsedMethod(RobotinoMethods.GETODOMETRY);
        return super.visitOdometrySensor(odometrySensor);
    }

    @Override
    public Void visitOmnidriveAction(OmnidriveAction omnidriveAction) {
        addMotorMethods();
        return super.visitOmnidriveAction(omnidriveAction);
    }

    @Override
    public Void visitOmnidriveDistanceAction(OmnidriveDistanceAction omnidriveDistanceAction) {
        addMotorMethods();
        usedMethodBuilder.addUsedMethod(RobotinoMethods.DRIVEFORDISTANCE);
        usedMethodBuilder.addUsedMethod(RobotinoMethods.DRIVETOPOSITION);
        return super.visitOmnidriveDistanceAction(omnidriveDistanceAction);
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        addMotorMethods();
        usedMethodBuilder.addUsedMethod(RobotinoMethods.TURNFORDEGREES);
        return super.visitTurnAction(turnAction);
    }

    @Override
    public Void visitOmnidrivePositionAction(OmnidrivePositionAction omnidrivePositionAction) {
        addMotorMethods();
        usedMethodBuilder.addUsedMethod(RobotinoMethods.DRIVETOPOSITION);
        return super.visitOmnidrivePositionAction(omnidrivePositionAction);
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        addMotorMethods();
        return super.visitMotorDriveStopAction(stopAction);
    }

    private void addMotorMethods() {
        usedMethodBuilder.addUsedMethod(RobotinoMethods.OMNIDRIVESPEED);
        usedMethodBuilder.addUsedMethod(RobotinoMethods.POSTVEL);
    }
}
