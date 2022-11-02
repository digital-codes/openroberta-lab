package de.fhg.iais.roberta.syntax.sensor.robotino;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoPhrase;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;

@NepoPhrase(category = "SENSOR", blocklyNames = {"robSensors_set_marker_threshold"}, name = "THRESHOLD_SET")
public final class CameraThreshold extends Sensor {
    @NepoField(name = BlocklyConstants.SENSORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String mode;
    @NepoValue(name = "VALUE", type = BlocklyType.NUMBER)
    public final Expr threshold;

    public CameraThreshold(BlocklyProperties properties, String mode, Expr threshold) {
        super(properties);
        Assert.nonEmptyString(mode);
        this.mode = mode;
        this.threshold = threshold;
        setReadOnly();
    }
    
}
