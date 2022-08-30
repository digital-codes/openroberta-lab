package de.fhg.iais.roberta.visitor;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.action.thymio.RecordStartAction;
import de.fhg.iais.roberta.syntax.action.thymio.RecordStopAction;
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TemperatureSensor;

public class ThymioSimValidatorAndCollectorVisitor extends ThymioValidatorAndCollectorVisitor {
    public ThymioSimValidatorAndCollectorVisitor(ConfigurationAst brickConfiguration, ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders) {
        super(brickConfiguration, beanBuilders);
    }

    @Override
    public Void visitRecordStartAction(RecordStartAction recordStartAction) {
        addWarningToPhrase(recordStartAction, "SIM_BLOCK_NOT_SUPPORTED");
        return null;
    }

    @Override
    public Void visitRecordStopAction(RecordStopAction recordStopAction) {
        addWarningToPhrase(recordStopAction, "SIM_BLOCK_NOT_SUPPORTED");
        return null;
    }

    public Void visitAccelerometerSensor(AccelerometerSensor accelerometerSensor) {
        addErrorToPhrase(accelerometerSensor, "SIM_BLOCK_NOT_SUPPORTED");
        return null;
    }

    public Void visitTemperatureSensor(TemperatureSensor temperatureSensor) {
        addErrorToPhrase(temperatureSensor, "SIM_BLOCK_NOT_SUPPORTED");
        return null;
    }
}
