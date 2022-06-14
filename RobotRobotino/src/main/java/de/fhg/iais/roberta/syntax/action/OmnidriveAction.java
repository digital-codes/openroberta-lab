package de.fhg.iais.roberta.syntax.action;

import de.fhg.iais.roberta.blockly.generated.Hide;
import de.fhg.iais.roberta.syntax.*;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.NepoField;
import de.fhg.iais.roberta.transformer.NepoHide;
import de.fhg.iais.roberta.transformer.NepoPhrase;
import de.fhg.iais.roberta.transformer.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.*;


@NepoPhrase(containerType = "MOTOR_OMNIDRIVE_ACTION")
public class OmnidriveAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    @NepoValue(name = BlocklyConstants.X, type = BlocklyType.NUMBER)
    public final Expr<V> xVel;
    @NepoValue(name = BlocklyConstants.Y, type = BlocklyType.NUMBER)
    public final Expr<V> yVel;
    @NepoValue(name = "THETA", type = BlocklyType.NUMBER)
    public final Expr<V> thetaVel;
    @NepoValue(name = BlocklyConstants.DISTANCE, type = BlocklyType.NUMBER)
    public final Expr<V> distance;
    @NepoField(name = BlocklyConstants.ACTORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;

    @NepoHide
    public final Hide hide;

    public OmnidriveAction(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, Expr<V> xVel, Expr<V> yVel, Expr<V> thetaVel, Expr<V> distance, String port, Hide hide) {
        super(kind, properties, comment);
        Assert.nonEmptyString(port);

        this.hide = hide;
        this.xVel = xVel;
        this.yVel = yVel;
        this.thetaVel = thetaVel;
        this.distance = distance;
        this.port = port;

        setReadOnly();
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }
}
