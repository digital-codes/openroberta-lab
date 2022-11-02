package de.fhg.iais.roberta.syntax.sensor.robotino;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(name = "COLOUR_BLOB", category = "SENSOR", blocklyNames = {"robSensors_get_colourBlob"}, blocklyType = BlocklyType.ARRAY_NUMBER)
public final class ColourBlob extends Sensor {

    @NepoField(name = "MODE")
    public final String mode;

    @NepoValue(name = "MIN_HUE", type = BlocklyType.NUMBER)
    public final Expr minHue;
    @NepoValue(name = "MAX_HUE", type = BlocklyType.NUMBER)
    public final Expr maxHue;
    @NepoValue(name = "MIN_SAT", type = BlocklyType.NUMBER)
    public final Expr minSat;
    @NepoValue(name = "MAX_SAT", type = BlocklyType.NUMBER)
    public final Expr maxSat;
    @NepoValue(name = "MIN_VAL", type = BlocklyType.NUMBER)
    public final Expr minVal;
    @NepoValue(name = "MAX_VAL", type = BlocklyType.NUMBER)
    public final Expr maxVal;

    public ColourBlob(BlocklyProperties properties, String mode, Expr minHue, Expr maxHue, Expr minSat, Expr maxSat, Expr minVal, Expr maxVal) {
        super(properties);
        this.mode = mode;
        this.minHue = minHue;
        this.maxHue = maxHue;
        this.minSat = minSat;
        this.maxSat = maxSat;
        this.minVal = minVal;
        this.maxVal = maxVal;
        setReadOnly();
    }
}