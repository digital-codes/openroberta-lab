package de.fhg.iais.roberta.syntax.action.spike;

import de.fhg.iais.roberta.blockly.generated.Hide;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.transformer.forClass.NepoPhrase;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoHide;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;

@NepoPhrase(category = "ACTOR", blocklyNames = {"actions_motor_stop"}, name = "MOTOR_STOP_ACTION")
public final class MotorStopAction extends Action {
    @NepoField(name = BlocklyConstants.MOTORPORT)
    public final String port;
    @NepoHide
    public final Hide hide;

    public MotorStopAction(
        BlocklyProperties properties,
        String port,
        Hide hide) {
        super(properties);
        this.port = port;
        this.hide = hide;
        setReadOnly();
    }
}