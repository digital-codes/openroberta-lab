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

@NepoPhrase(category = "ACTOR", blocklyNames = {"actions_motor_on"}, name = "MOTOR_ON_ACTION")
public final class MotorOnAction extends Action {
    @NepoField(name = BlocklyConstants.MOTORPORT)
    public final String port;
    @NepoValue(name = BlocklyConstants.POWER, type = BlocklyType.NUMBER)
    public final Expr power;
    @NepoHide
    public final Hide hide;

    public MotorOnAction(
        BlocklyProperties properties,
        String port,
        Expr power,
        Hide hide) {
        super(properties);
        this.port = port;
        this.power = power;
        this.hide = hide;
        setReadOnly();
    }
}