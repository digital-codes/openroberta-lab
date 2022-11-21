package de.fhg.iais.roberta.visitor;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.spike.MotorDiffOnForAction;
import de.fhg.iais.roberta.syntax.action.spike.MotorOnAction;
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerReset;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.validate.DifferentialMotorValidatorAndCollectorVisitor;


public class SpikeValidatorAndCollectorVisitor extends DifferentialMotorValidatorAndCollectorVisitor {


    public SpikeValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitMainTask(MainTask mainTask) {
        requiredComponentVisited(mainTask, mainTask.variables);
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(timerSensor.getUserDefinedPort(), SC.TIMER, timerSensor.getMode()));
        return null;
    }

    @Override
    public Void visitTimerReset(TimerReset timerReset) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(timerReset.getUserDefinedPort(), SC.TIMER, timerReset.getMode()));
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction motorGetPowerAction) {
        addWarningToPhrase(motorGetPowerAction, "BLOCK_NOT_SUPPORTED");
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction motorSetPowerAction) {
        addWarningToPhrase(motorSetPowerAction, "BLOCK_NOT_SUPPORTED");
        return null;
    }

    public Void visitMotorDiffOnForAction(MotorDiffOnForAction motorDiffOnForAction) {
        requiredComponentVisited(motorDiffOnForAction, motorDiffOnForAction.distance, motorDiffOnForAction.power);
        ConfigurationComponent diffDrive = this.robotConfiguration.optConfigurationComponentByType("DIFFERENTIALDRIVE");
        String leftName = diffDrive.getComponentProperties().get("MOTOR_L");
        String rightName = diffDrive.getComponentProperties().get("MOTOR_R");
        //ConfigurationComponent leftMotor = getMotor(leftName);
        //ConfigurationComponent rightMotor = getMotor(rightName);
        usedHardwareBuilder.addUsedActor(new UsedActor(leftName, SC.MOTOR));
        usedHardwareBuilder.addUsedActor(new UsedActor(rightName, SC.MOTOR));
        return null;
    }

    private ConfigurationComponent getMotor(String portName) {
        for ( Map.Entry<String, ConfigurationComponent> entry : this.robotConfiguration.getConfigurationComponents().entrySet() ) {
            ConfigurationComponent component = entry.getValue();
            if ( component.userDefinedPortName.equals(portName) ) {
                return component;
            }
        }
        throw new DbcException("port " + portName + " is missing");
    }


    private ConfigurationComponent getSubComponent(String userDefinedPort) {
        for ( ConfigurationComponent component : this.robotConfiguration.getConfigurationComponentsValues() ) {
            try {
                for ( List<ConfigurationComponent> subComponents : component.getSubComponents().values() ) {
                    for ( ConfigurationComponent subComponent : subComponents ) {
                        if ( subComponent.userDefinedPortName.equals(userDefinedPort) ) {
                            return subComponent;
                        }
                    }
                }
            } catch ( UnsupportedOperationException e ) {
                continue;
            }
        }
        return null;
    }

    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        System.out.println("BEATE");
        return null;
    }
}
