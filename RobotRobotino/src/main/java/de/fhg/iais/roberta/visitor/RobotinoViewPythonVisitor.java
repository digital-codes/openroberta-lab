package de.fhg.iais.roberta.visitor;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.bean.UsedHardwareBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.constants.RobotinoConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidriveDistanceAction;
import de.fhg.iais.roberta.syntax.actor.robotino.OmnidrivePositionAction;
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.DetectMarkSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.InfraredSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.PinGetValueSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TouchSensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.CameraSensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.MarkerInformation;
import de.fhg.iais.roberta.syntax.sensor.robotino.OdometrySensor;
import de.fhg.iais.roberta.syntax.sensor.robotino.OdometrySensorReset;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.lang.codegen.prog.AbstractPythonVisitor;

/**
 * This class is implementing {@link IVisitor}. All methods are implemented and they append a human-readable Python code representation of a phrase to a
 * StringBuilder. <b>This representation is correct Python code.</b> <br>
 */
public final class RobotinoViewPythonVisitor extends AbstractPythonVisitor implements IRobotinoVisitor<Void> {

    private final ConfigurationAst configurationAst;

    /**
     * initialize the Python code generator visitor.
     *
     * @param programPhrases to generate the code from
     */
    public RobotinoViewPythonVisitor(
        List<List<Phrase>> programPhrases, ClassToInstanceMap<IProjectBean> beans, ConfigurationAst configurationAst) {
        super(programPhrases, beans);
        this.configurationAst = configurationAst;
    }

    @Override
    protected void generateProgramPrefix(boolean withWrapping) {
        this.sb.append("#!/usr/bin/env python3");
        nlIndent();
        this.sb.append("import math, random, time, requests, threading, sys, io");
        nlIndent();
        generateVariables();
        nlIndent();
        generateTimerVariables();
        nlIndent();
        if ( !this.getBean(CodeGeneratorSetupBean.class).getUsedMethods().isEmpty() ) {
            String helperMethodImpls =
                this.getBean(CodeGeneratorSetupBean.class)
                    .getHelperMethodGenerator()
                    .getHelperMethodDefinitions(this.getBean(CodeGeneratorSetupBean.class).getUsedMethods());
            this.sb.append(helperMethodImpls);
        }
    }

    private void generateVariables() {
        this.sb.append("sys.stdout = io.StringIO()\n" +
            "sys.stderr = io.StringIO()");
        nlIndent();
        this.sb.append("ROBOTINOIP = \"127.0.0.1:80\"");
        nlIndent();
        this.sb.append("PARAMS = {'sid':'robertaProgram'}");
        nlIndent();
        this.sb.append("_maxSpeed = 1");
        nlIndent();
        generateOptionalVariables();
    }

    private void generateOptionalVariables() {
        if ( this.getBean(UsedHardwareBean.class).isActorUsed(SC.DIGITAL_PIN) ) {
            this.sb.append("_digitalPinValues = [0 for i in range(8)]");
            nlIndent();
        }
        if ( this.getBean(UsedHardwareBean.class).isActorUsed(RobotinoConstants.OMNIDRIVE) ) {
            this.sb.append("_speed = [0, 0, 0]");
            nlIndent();
        }
    }

    private ConfigurationComponent getOmnidrive() {
        for ( ConfigurationComponent component : this.configurationAst.getConfigurationComponents().values() ) {
            if ( component.componentType.equals(RobotinoConstants.OMNIDRIVE) ) {
                return component;
            }
        }
        return null;
    }

    @Override
    public Void visitMainTask(MainTask mainTask) {
        StmtList variables = mainTask.variables;
        variables.accept(this);
        generateUserDefinedMethods();
        nlIndent();
        this.sb.append("def run(RV):");
        incrIndentation();
        generateGlobalVariables();
        this.sb.append("time.sleep(1)");
        nlIndent();

        return null;
    }

    private void generateGlobalVariables() {
        //add usermade global variables
        if ( !this.usedGlobalVarInFunctions.isEmpty() ) {
            nlIndent();
            this.sb.append("global ").append(String.join(", ", this.usedGlobalVarInFunctions));
        }
        nlIndent();
    }

    @Override
    protected void generateProgramSuffix(boolean withWrapping) {
        if ( !withWrapping ) {
            return;
        }
        decrIndentation(); // everything is still indented from main program
        nlIndent();
        nlIndent();
        appendViewMethods();
        //generateFinally();
        decrIndentation();
        nlIndent();
    }

    private void appendViewMethods() {
        this.sb.append("def start(RV):");
        incrIndentation();
        nlIndent();
        this.sb.append("motorDaemon2 = threading.Thread(target=run, daemon=True, args=(RV,), name='mainProgram')\n" +
            "    motorDaemon2.start()\n" +
            "    print('start')");
        decrIndentation();
        nlIndent();
        nlIndent();
        this.sb.append("def step(RV):");
        incrIndentation();
        nlIndent();
        this.sb.append("print('step')");
        if ( this.getBean(UsedHardwareBean.class).isActorUsed(RobotinoConstants.OMNIDRIVE) ) {
            nlIndent();
            this.sb.append(this.getBean(CodeGeneratorSetupBean.class).
                    getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.POSTVEL))
                .append("()");
            nlIndent();
        }
        decrIndentation();
        nlIndent();
        nlIndent();
        this.sb.append("def stop(RV):");
        incrIndentation();
        nlIndent();
        this.sb.append("print('stop')");
        decrIndentation();
        nlIndent();
        nlIndent();
        this.sb.append("def cleanup(RV):");
        incrIndentation();
        nlIndent();
        this.sb.append("print('cleanup')");
        decrIndentation();
        nlIndent();
        nlIndent();
    }

    private void generateFinally() {
        if ( this.getBean(UsedHardwareBean.class).isActorUsed(RobotinoConstants.OMNIDRIVE) || this.getBean(UsedHardwareBean.class).isActorUsed(SC.DIGITAL_PIN) ) {
            this.sb.append("finally:");
        }
        incrIndentation();
        incrIndentation();

        nlIndent();
        if ( this.getBean(UsedHardwareBean.class).isActorUsed(RobotinoConstants.OMNIDRIVE) ) {
            this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.OMNIDRIVESPEED));
            this.sb.append("(0,0,0)");
            nlIndent();
        }
        decrIndentation();
    }

    @Override
    public Void visitConnectConst(ConnectConst connectConst) {
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        switch ( timerSensor.getMode() ) {
            case SC.DEFAULT:
            case SC.VALUE:
                this.sb.append("((time.time() - _timer").append(timerSensor.getUserDefinedPort()).append(")/1000)");
                break;
            case SC.RESET:
                this.sb.append("_timer").append(timerSensor.getUserDefinedPort()).append(" = time.time()");
                break;
            default:
                throw new DbcException("Invalid Time Mode!");
        }
        return null;
    }

    private void generateTimerVariables() {
        this.getBean(UsedHardwareBean.class)
            .getUsedSensors()
            .stream()
            .filter(usedSensor -> usedSensor.getType().equals(SC.TIMER))
            .collect(Collectors.groupingBy(UsedSensor::getPort))
            .keySet()
            .forEach(port -> {
                this.usedGlobalVarInFunctions.add("_timer" + port);
                this.sb.append("_timer").append(port).append(" = time.time()");
                nlIndent();
            });
    }

    @Override
    public Void visitWaitStmt(WaitStmt waitStmt) {
        this.sb.append("while True:");
        incrIndentation();
        visitStmtList(waitStmt.statements);
        nlIndent();
        this.sb.append("time.sleep(0.2)");
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
    public Void visitOmnidriveAction(OmnidriveAction omnidriveAction) {
        this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.OMNIDRIVESPEED));
        this.sb.append("(");
        omnidriveAction.xVel.accept(this);
        this.sb.append(", ");
        omnidriveAction.yVel.accept(this);
        this.sb.append(", ");
        omnidriveAction.thetaVel.accept(this);
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.OMNIDRIVESPEED));
        this.sb.append("(0, 0, 0)");
        return null;
    }

    @Override
    public Void visitOmnidriveDistanceAction(OmnidriveDistanceAction omnidriveDistanceAction) {
        //TODO with View but similar to ROS solution
        return null;
    }

    @Override
    public Void visitOmnidrivePositionAction(OmnidrivePositionAction omnidrivePositionAction) {
        //TODO with View
        return null;
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        //TODO with View
        return null;
    }

    @Override
    public Void visitMarkerInformation(MarkerInformation markerInformation) {
        return null;
    }

    @Override
    public Void visitDetectMarkSensor(DetectMarkSensor detectMarkSensor) {
        return null;
    }

    @Override
    public Void visitCameraSensor(CameraSensor cameraSensor) {
        return null;
    }

    @Override
    public Void visitOdometrySensor(OdometrySensor odometrySensor) {
        this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.GETODOMETRY));
        if ( odometrySensor.getSlot().equals("THETA") ) {
            this.sb.append("('rot')")
                .append(" * 180 / math.pi)");
        } else {
            this.sb.append("('")
                .append(odometrySensor.getSlot().toLowerCase())
                .append("') * 100");
        }
        return null;
    }

    @Override
    public Void visitOdometrySensorReset(OdometrySensorReset odometrySensorReset) {
        //TODO with View
        return null;
    }

    @Override
    public Void visitPinGetValueSensor(PinGetValueSensor pinGetValueSensor) {
        if ( pinGetValueSensor.getMode().equals(SC.DIGITAL) ) {
            this.sb.append(this.getBean(CodeGeneratorSetupBean.class).
                getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.GETDIGITALPIN)).append("()");
        } else if ( pinGetValueSensor.getMode().equals(SC.ANALOG) ) {
            this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().
                getHelperMethodName(RobotinoMethods.GETANALOGPIN)).append("()");
        }
        return null;
    }

    @Override
    public Void visitTouchSensor(TouchSensor touchSensor) {
        this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.ISBUMPED))
            .append("()");
        return null;
    }

    @Override
    public Void visitInfraredSensor(InfraredSensor infraredSensor) {
        String port = infraredSensor.getUserDefinedPort();
        this.sb.append("_getDistance(")
            .append(port)
            .append(")");
        return null;
    }

    @Override
    public Void visitPinWriteValueAction(PinWriteValueAction pinWriteValueAction) {
        this.sb.append(this.getBean(CodeGeneratorSetupBean.class).getHelperMethodGenerator().getHelperMethodName(RobotinoMethods.SETDIGITALPIN))
            .append("(")
            .append(configurationAst.getConfigurationComponent(pinWriteValueAction.port).getComponentProperties().get("INPUT"))
            .append(", ");
        pinWriteValueAction.value.accept(this);
        this.sb.append(")");
        return null;
    }
}