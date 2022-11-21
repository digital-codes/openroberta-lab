package de.fhg.iais.roberta.visitor;

import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.spike.MotorDiffOnForAction;
import de.fhg.iais.roberta.syntax.action.spike.MotorOnAction;
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerReset;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.visitor.lang.codegen.prog.AbstractPythonVisitor;

/**
 * This class is implementing {@link IVisitor}. All methods are implemented and they append a human-readable Python code representation of a phrase to a
 * StringBuilder. <b>This representation is correct Python code.</b> <br>
 */
public final class SpikePythonVisitor extends AbstractPythonVisitor {

    private final ConfigurationAst configurationAst;
    private String rightMotorPort;

    /**
     * initialize the Python code generator visitor.
     *
     * @param programPhrases to generate the code from
     */
    public SpikePythonVisitor(
        List<List<Phrase>> programPhrases, ClassToInstanceMap<IProjectBean> beans, ConfigurationAst configurationAst) {
        super(programPhrases, beans);
        this.configurationAst = configurationAst;
    }


    @Override
    protected void generateProgramPrefix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        this.src.add("from spike import PrimeHub, LightMatrix, Button, StatusLight, ForceSensor, MotionSensor, Speaker, ColorSensor, App, DistanceSensor, Motor, MotorPair").nlI();
        this.src.add("from spike.control import wait_for_seconds, wait_until, Timer").nlI();
        this.src.add("from math import *").nlI().nlI();
        this.src.add("hub = PrimeHub()").nlI();

        if ( !this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().isEmpty() ) {
            String helperMethodImpls =
                this.getBean(CodeGeneratorSetupBean.class)
                    .getHelperMethodGenerator()
                    .getHelperMethodDefinitions(this.getBean(CodeGeneratorSetupBean.class).getUsedMethods());
            this.sb.append(helperMethodImpls);
        }
    }

    @Override
    public Void visitMainTask(MainTask mainTask) {
        StmtList variables = mainTask.variables;
        variables.accept(this);
        generateUserDefinedMethods();
        nlIndent();
        this.sb.append("def run():");
        incrIndentation();
        if ( !this.usedGlobalVarInFunctions.isEmpty() ) {
            nlIndent();
            this.sb.append("global ").append(String.join(", ", this.usedGlobalVarInFunctions));
        } else {
            addPassIfProgramIsEmpty();
        }
        return null;
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        decrIndentation(); // everything is still indented from main program
        nlIndent();
        nlIndent();
        this.sb.append("def main():");
        incrIndentation();
        nlIndent();
        this.sb.append("try:");
        incrIndentation();
        nlIndent();
        this.sb.append("run()");
        decrIndentation();
        nlIndent();
        this.sb.append("except Exception as e:");
        incrIndentation();
        nlIndent();
        this.sb.append("hub.light_matrix.show_image('SAD')");
        decrIndentation();
        //TODO finally close open ports
        decrIndentation();
        nlIndent();
        nlIndent();

        this.sb.append("main()");
    }

    private String getPortFromConfig(String name) {
        ConfigurationComponent block = configurationAst.getConfigurationComponent(name);
        return block.getComponentProperties().get("PORT1");
    }

    @Override
    public Void visitWaitStmt(WaitStmt waitStmt) {
        this.sb.append("while True:");
        incrIndentation();
        visitStmtList(waitStmt.statements);
        decrIndentation();
        return null;
    }

    @Override
    public Void visitWaitTimeStmt(WaitTimeStmt waitTimeStmt) {
        this.sb.append("time.sleep(");
        waitTimeStmt.time.accept(this);
        this.sb.append("/1000)");
        return null;
    }

    @Override
    public Void visitColorConst(ColorConst colorConst) {
        this.sb.append("(").append(colorConst.getRedChannelInt()).append(", ").append(colorConst.getGreenChannelInt()).append(", ").append(colorConst.getBlueChannelInt()).append(")");
        return null;
    }

    @Override
    public Void visitConnectConst(ConnectConst connectConst) {
        return null;
    }

    @Override
    public Void visitRgbColor(RgbColor rgbColor) {
        this.sb.append("(");
        rgbColor.R.accept(this);
        this.sb.append(", ");
        rgbColor.G.accept(this);
        this.sb.append(", ");
        rgbColor.B.accept(this);
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        return null;
    }

    @Override
    public Void visitTimerReset(TimerReset timerReset) {
        return null;
    }

    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        //TODO
        return null;
    }

    public Void visitMotorDiffOnForAction(MotorDiffOnForAction motorDiffOnForAction) {
        //TODO
        return null;
    }
}