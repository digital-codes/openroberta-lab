package de.fhg.iais.roberta.syntax.sensor.robotino;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(name = "MARKER_INFORMATION", category = "SENSOR", blocklyNames = {"robSensors_get_markerinf"}, blocklyType = BlocklyType.ARRAY_NUMBER)
public final class MarkerInformation extends Sensor {

    @NepoField(name = "MODE")
    public final String mode;

    @NepoValue(name = "VALUE", type = BlocklyType.NUMBER)
    public final Expr markerId;

    public MarkerInformation(BlocklyProperties properties, String mode, Expr markerId) {
        super(properties);
        this.mode = mode;
        this.markerId = markerId;
        setReadOnly();
    }
}