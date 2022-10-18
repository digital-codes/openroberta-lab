package de.fhg.iais.roberta.syntax.action.spike;

import de.fhg.iais.roberta.blockly.generated.Hide;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoPhrase;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoHide;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;

@NepoPhrase(category = "ACTOR", blocklyNames = {"actions_motor_on_for"}, name = "MOTOR_ON_FOR_ACTION")
public final class MotorOnForAction extends Action {
    @NepoField(name = BlocklyConstants.MOTORPORT)
    public final String port;
    @NepoValue(name = BlocklyConstants.POWER, type = BlocklyType.NUMBER)
    public final Expr power;
    @NepoField(name = "UNIT")
    public final String unit;
    @NepoValue(name = BlocklyConstants.VALUE, type = BlocklyType.NUMBER)
    public final Expr value;
    @NepoHide
    public final Hide hide;

    public MotorOnForAction(
        BlocklyProperties properties,
        String port,
        Expr power,
        String unit,
        Expr value,
        Hide hide) {
        super(properties);
        this.port = port;
        this.power = power;
        this.unit = unit;
        this.value = value;
        this.hide = hide;
        setReadOnly();
    }
}