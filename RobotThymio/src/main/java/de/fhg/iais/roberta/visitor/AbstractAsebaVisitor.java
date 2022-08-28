package de.fhg.iais.roberta.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.antlr.v4.runtime.misc.OrderedHashSet;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.inter.mode.general.IMode;
import de.fhg.iais.roberta.mode.general.IndexLocation;
import static de.fhg.iais.roberta.mode.general.ListElementOperations.GET;
import static de.fhg.iais.roberta.mode.general.ListElementOperations.GET_REMOVE;
import static de.fhg.iais.roberta.mode.general.ListElementOperations.REMOVE;
import static de.fhg.iais.roberta.mode.general.ListElementOperations.SET;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.serial.SerialWriteAction;
import de.fhg.iais.roberta.syntax.lang.expr.Binary;
import de.fhg.iais.roberta.syntax.lang.expr.BoolConst;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyExpr;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyList;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.ExprList;
import de.fhg.iais.roberta.syntax.lang.expr.ListCreate;
import de.fhg.iais.roberta.syntax.lang.expr.MathConst;
import de.fhg.iais.roberta.syntax.lang.expr.NullConst;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.Unary;
import de.fhg.iais.roberta.syntax.lang.expr.VarDeclaration;
import de.fhg.iais.roberta.syntax.lang.functions.GetSubFunct;
import de.fhg.iais.roberta.syntax.lang.functions.IndexOfFunct;
import de.fhg.iais.roberta.syntax.lang.functions.LengthOfIsEmptyFunct;
import de.fhg.iais.roberta.syntax.lang.functions.ListGetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.ListRepeat;
import de.fhg.iais.roberta.syntax.lang.functions.ListSetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.MathCastCharFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathCastStringFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathConstrainFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathNumPropFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathOnListFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathPowerFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomFloatFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathSingleFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextCharCastNumberFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextJoinFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextPrintFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextStringCastNumberFunct;
import de.fhg.iais.roberta.syntax.lang.methods.MethodCall;
import de.fhg.iais.roberta.syntax.lang.methods.MethodIfReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodVoid;
import de.fhg.iais.roberta.syntax.lang.stmt.AssertStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.DebugAction;
import de.fhg.iais.roberta.syntax.lang.stmt.IfStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtFlowCon;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtTextComment;
import de.fhg.iais.roberta.syntax.lang.stmt.TernaryExpr;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.FunctionNames;
import de.fhg.iais.roberta.visitor.lang.codegen.AbstractLanguageVisitor;

public abstract class AbstractAsebaVisitor extends AbstractLanguageVisitor {
    protected Set<String> usedGlobalVarInFunctions = new OrderedHashSet<>();
    protected int stateCounter = 0;
    protected int loopCounter = -1;
    List<Integer> loopsStart = new ArrayList();
    List<Integer> loopsBody = new ArrayList();
    List<Integer> loopsEnd = new ArrayList();
    List<Integer> funcStart = new ArrayList();
    protected int funcCounter = -1;

    protected boolean ifOnce = false;

    protected ArrayList myMethods;


    protected AbstractAsebaVisitor(List<List<Phrase>> programPhrases, ClassToInstanceMap<IProjectBean> beans) {
        super(programPhrases, beans);
    }

    protected static Map<Binary.Op, String> binaryOpSymbols() {
        return Collections
            .unmodifiableMap(
                Stream
                    .of(
                        AbstractLanguageVisitor.entry(Binary.Op.ADD, "+"),
                        AbstractLanguageVisitor.entry(Binary.Op.MINUS, "-"),
                        AbstractLanguageVisitor.entry(Binary.Op.MULTIPLY, "*"),
                        AbstractLanguageVisitor.entry(Binary.Op.DIVIDE, "/"),
                        AbstractLanguageVisitor.entry(Binary.Op.MOD, "%"),
                        AbstractLanguageVisitor.entry(Binary.Op.EQ, "=="),
                        AbstractLanguageVisitor.entry(Binary.Op.NEQ, "!="),
                        AbstractLanguageVisitor.entry(Binary.Op.LT, "<"),
                        AbstractLanguageVisitor.entry(Binary.Op.LTE, "<="),
                        AbstractLanguageVisitor.entry(Binary.Op.GT, ">"),
                        AbstractLanguageVisitor.entry(Binary.Op.GTE, ">="),
                        AbstractLanguageVisitor.entry(Binary.Op.AND, "and"),
                        AbstractLanguageVisitor.entry(Binary.Op.OR, "or"),
                        AbstractLanguageVisitor.entry(Binary.Op.MATH_CHANGE, "+="),
                        AbstractLanguageVisitor.entry(Binary.Op.TEXT_APPEND, "+="),
                        AbstractLanguageVisitor.entry(Binary.Op.IN, "in"),
                        AbstractLanguageVisitor.entry(Binary.Op.ASSIGNMENT, "="),
                        AbstractLanguageVisitor.entry(Binary.Op.ADD_ASSIGNMENT, "+="),
                        AbstractLanguageVisitor.entry(Binary.Op.MINUS_ASSIGNMENT, "-="),
                        AbstractLanguageVisitor.entry(Binary.Op.MULTIPLY_ASSIGNMENT, "*="),
                        AbstractLanguageVisitor.entry(Binary.Op.DIVIDE_ASSIGNMENT, "/="),
                        AbstractLanguageVisitor.entry(Binary.Op.MOD_ASSIGNMENT, "%=")

                    )
                    .collect(AbstractLanguageVisitor.entriesToMap()));
    }

    protected static Map<Unary.Op, String> unaryOpSymbols() {
        return Collections
            .unmodifiableMap(
                Stream
                    .of(
                        AbstractLanguageVisitor.entry(Unary.Op.PLUS, "+"),
                        AbstractLanguageVisitor.entry(Unary.Op.NEG, "-"),
                        AbstractLanguageVisitor.entry(Unary.Op.NOT, "not"),
                        AbstractLanguageVisitor.entry(Unary.Op.POSTFIX_INCREMENTS, "++"),
                        AbstractLanguageVisitor.entry(Unary.Op.PREFIX_INCREMENTS, "++")

                    )
                    .collect(AbstractLanguageVisitor.entriesToMap()));
    }

    @Override
    public String getEnumCode(IMode value) {
        return "'" + value.toString().toLowerCase() + "'";
    }

    @Override
    public String getEnumCode(String value) {
        return "'" + value.toLowerCase() + "'";
    }

    @Override
    public Void visitAssertStmt(AssertStmt assertStmt) {
        this.sb.append("if not ");
        assertStmt.asserts.accept(this);
        this.sb.append(":");
        incrIndentation();
        nlIndent();
        this.sb.append("print(\"Assertion failed: \", \"").append(assertStmt.msg).append("\", ");
        ((Binary) assertStmt.asserts).left.accept(this);
        this.sb.append(", \"").append(((Binary) assertStmt.asserts).op.toString()).append("\", ");
        ((Binary) assertStmt.asserts).getRight().accept(this);
        this.sb.append(")");
        decrIndentation();
        return null;
    }

    @Override
    public Void visitBinary(Binary binary) {
        //this.sb.append("( ");
        try {
            VarDeclaration variablePart = (VarDeclaration) binary.left;
            this.sb.append(variablePart.getCodeSafeName());
        } catch ( ClassCastException e ) {
            generateSubExpr(this.sb, false, binary.left, binary);
        }
        Binary.Op op = binary.op;
        String sym = getBinaryOperatorSymbol(op);
        switch ( op.toString() ) {
            case "==":
                break;
            case "AND":
            case "OR":
                this.sb.append(" == 1 ").append(sym).append(" 1 == ");
                break;
            default:
                this.sb.append(' ').append(sym).append(' ');
                break;
        }
        generateCodeRightExpression(binary, op);
        //this.sb.append(" )");
        return null;
    }

    @Override
    public Void visitBoolConst(BoolConst boolConst) {
        this.sb.append(boolConst.value ? "1" : "0");
        return null;
    }

    @Override
    public Void visitConnectConst(ConnectConst connectConst) {
        return null;
    }

    @Override
    public Void visitDebugAction(DebugAction debugAction) {
        this.sb.append("print(");
        debugAction.value.accept(this);
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitEmptyExpr(EmptyExpr emptyExpr) {
        switch ( emptyExpr.getDefVal() ) {
            case BOOLEAN:
                this.sb.append("1");
                break;
            case NUMBER_INT:
                this.sb.append("0");
                break;
            case ARRAY:
                this.sb.append("[0]");
                break;
            case NULL:
                break;
            default:
                this.sb.append("[[EmptyExpr [defVal=" + emptyExpr.getDefVal() + "]]]");
                break;
        }
        return null;
    }

    @Override
    public Void visitEmptyList(EmptyList emptyList) {
        this.sb.append("[]");
        return null;
    }

    @Override
    public Void visitGetSubFunct(GetSubFunct getSubFunct) {
        if ( getSubFunct.functName == FunctionNames.GET_SUBLIST ) {
            getSubFunct.param.get(0).accept(this);
            this.sb.append("[");
            switch ( (IndexLocation) getSubFunct.strParam.get(0) ) {
                case FIRST:
                    this.sb.append("0:");
                    break;
                case FROM_END:
                    this.sb.append("-1 -");
                    getSubFunct.param.get(1).accept(this);
                    this.sb.append(":");
                    break;
                case FROM_START:
                    getSubFunct.param.get(1).accept(this);
                    this.sb.append(":");
                    break;
                default:
                    break;
            }
            switch ( (IndexLocation) getSubFunct.strParam.get(1) ) {
                case LAST:
                    // append nothing
                    break;
                case FROM_END:
                    this.sb.append("-1 -");
                    try {
                        getSubFunct.param.get(2).accept(this);
                    } catch ( IndexOutOfBoundsException e ) { // means that our start index does not have a variable
                        getSubFunct.param.get(1).accept(this);
                    }
                    break;
                case FROM_START:
                    try {
                        getSubFunct.param.get(2).accept(this);
                    } catch ( IndexOutOfBoundsException e ) { // means that our start index does not have a variable
                        getSubFunct.param.get(1).accept(this);
                    }
                    break;
                default:
                    break;
            }
            this.sb.append("]");
        }
        return null;
    }

    @Override
    public Void visitIndexOfFunct(IndexOfFunct indexOfFunct) {
        switch ( (IndexLocation) indexOfFunct.location ) {
            case FIRST:
                indexOfFunct.param.get(0).accept(this);
                this.sb.append(".index(");
                indexOfFunct.param.get(1).accept(this);
                this.sb.append(")");
                break;
            case LAST:
                this.sb.append("(len(");
                indexOfFunct.param.get(0).accept(this);
                this.sb.append(") - 1) - ");
                indexOfFunct.param.get(0).accept(this);
                this.sb.append("[::-1].index(");
                indexOfFunct.param.get(1).accept(this);
                this.sb.append(")");
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public Void visitLengthOfIsEmptyFunct(LengthOfIsEmptyFunct lengthOfIsEmptyFunct) {
        switch ( lengthOfIsEmptyFunct.functName ) {
            case LIST_LENGTH:
                this.sb.append("len( ");
                lengthOfIsEmptyFunct.param.get(0).accept(this);
                this.sb.append(")");
                break;

            case LIST_IS_EMPTY:
                this.sb.append("not ");
                lengthOfIsEmptyFunct.param.get(0).accept(this);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public Void visitListCreate(ListCreate listCreate) {
        if ( listCreate.typeVar.toString().equals("COLOR") ) {
            this.sb.append("var ___r[] = ");
            listCreate.exprList.el.get(0).accept(this);
            nlIndent();
            this.sb.append("var ___g[] = ");
            listCreate.exprList.el.get(1).accept(this);
            nlIndent();
            this.sb.append("var ___b[] = ");
            listCreate.exprList.el.get(2).accept(this);
            return null;
        }
        this.sb.append("[");
        listCreate.exprList.accept(this);
        this.sb.append("]");
        return null;
    }

    @Override
    public Void visitListGetIndex(ListGetIndex listGetIndex) {
        if ( listGetIndex.param.get(0).getVarType().toString().equals("ARRAY_COLOUR") ) {
            if ( listGetIndex.mode == GET ) {
                this.sb.append("___r = ");
                listGetIndex.param.get(0).accept(this);
                this.sb.append("_r");
                this.sb.append("[");
                listGetIndex.param.get(1).accept(this);
                this.sb.append("]");
                nlIndent();
                this.sb.append("___g = ");
                listGetIndex.param.get(0).accept(this);
                this.sb.append("_g");
                this.sb.append("[");
                listGetIndex.param.get(1).accept(this);
                this.sb.append("]");
                nlIndent();
                this.sb.append("___b = ");
                listGetIndex.param.get(0).accept(this);
                this.sb.append("_b");
                this.sb.append("[");
                listGetIndex.param.get(1).accept(this);
                this.sb.append("]");
            }
            return null;
        }
        listGetIndex.param.get(0).accept(this);
        if ( listGetIndex.mode == GET ) {
            this.sb.append("[");
        } else if ( listGetIndex.mode == REMOVE || listGetIndex.mode == GET_REMOVE ) {
            this.sb.append(".pop(");
        }

        switch ( (IndexLocation) listGetIndex.location ) {
            case RANDOM: // backwards compatibility
                // TODO?
            case FIRST:
                this.sb.append("0");
                break;
            case FROM_END:
                this.sb.append("-1 -"); // TODO should be correct but how is it handled on other robots?
                listGetIndex.param.get(1).accept(this);
                break;
            case FROM_START:
                listGetIndex.param.get(1).accept(this);
                break;
            case LAST:
                this.sb.append("-1");
                break;
            default:
                break;
        }
        if ( listGetIndex.mode == GET ) {
            this.sb.append("]");
        } else if ( listGetIndex.mode == REMOVE || listGetIndex.mode == GET_REMOVE ) {
            this.sb.append(")");
        }
        return null;
    }

    @Override
    public Void visitListRepeat(ListRepeat listRepeat) {
        this.sb.append("[");
        listRepeat.param.get(1).accept(this);
        this.sb.append("] = [");
        //TODO init list with right number of entries...
        listRepeat.param.get(0).accept(this);
        this.sb.append("]");
        return null;
    }

    @Override
    public Void visitListSetIndex(ListSetIndex listSetIndex) {
        if ( listSetIndex.mode != SET ) {
            return null;
        }
        if ( listSetIndex.param.get(0).getVarType().toString().equals("ARRAY_COLOUR") ) {
            //listSetIndex.param.get(0).accept(this);
            this.sb.append("_color = ");
            listSetIndex.param.get(1).accept(this);
            nlIndent();
            listSetIndex.param.get(0).accept(this);
            this.sb.append("___r");
            addSetIndex(listSetIndex);
            this.sb.append("_color[0]");
            nlIndent();
            listSetIndex.param.get(0).accept(this);
            this.sb.append("___g");
            addSetIndex(listSetIndex);
            this.sb.append("_color[1]");
            nlIndent();
            listSetIndex.param.get(0).accept(this);
            this.sb.append("___b");
            addSetIndex(listSetIndex);
            this.sb.append("_color[2]");
            return null;
        }
        listSetIndex.param.get(0).accept(this);
        addSetIndex(listSetIndex);
        listSetIndex.param.get(1).accept(this);
        return null;
    }


    private void addSetIndex(ListSetIndex listSetIndex) {
        this.sb.append("[");
        switch ( (IndexLocation) listSetIndex.location ) {
            case FIRST:
                this.sb.append("0");
                break;
            case FROM_END:
            case FROM_START:
                listSetIndex.param.get(2).accept(this);
                break;
            default:
                break;
        }
        this.sb.append("] = ");
    }

    @Override
    public Void visitMathCastCharFunct(MathCastCharFunct mathCastCharFunct) {
        throw new DbcException("Characters not supported.");
    }

    @Override
    public Void visitMathCastStringFunct(MathCastStringFunct mathCastStringFunct) {
        throw new DbcException("Strings not supported.");
    }

    @Override
    public Void visitMathConst(MathConst mathConst) {
        throw new DbcException("No floats in Aseba");
    }

    @Override
    public Void visitMathConstrainFunct(MathConstrainFunct mathConstrainFunct) {
        throw new DbcException("Function not supported");
    }

    @Override
    public Void visitMathNumPropFunct(MathNumPropFunct mathNumPropFunct) {
        switch ( mathNumPropFunct.functName ) {
            case EVEN:
                this.sb.append("(");
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" % 2) == 0");
                break;
            case ODD:
                this.sb.append("(");
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" % 2) == 1");
                break;
            case WHOLE:
                this.sb.append("(");
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" % 1) == 0");
                break;
            case POSITIVE:
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" > 0");
                break;
            case NEGATIVE:
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" < 0");
                break;
            case DIVISIBLE_BY:
                this.sb.append("(");
                mathNumPropFunct.param.get(0).accept(this);
                this.sb.append(" % ");
                mathNumPropFunct.param.get(1).accept(this);
                this.sb.append(") == 0");
                break;
            case PRIME:
                throw new DbcException("Statement not supported by Aseba!");
            default:
                break;
        }
        return null;
    }

    @Override
    public Void visitMathOnListFunct(MathOnListFunct mathOnListFunct) {
        throw new DbcException("Statement not yet supported");
    }

    @Override
    public Void visitMathPowerFunct(MathPowerFunct mathPowerFunct) {
        throw new DbcException("Statement not supported by Aseba!");
    }

    @Override
    public Void visitMathRandomFloatFunct(MathRandomFloatFunct mathRandomFloatFunct) {
        throw new DbcException("Floats not supported by Aseba!");
    }

    @Override
    public Void visitMathRandomIntFunct(MathRandomIntFunct mathRandomIntFunct) {
        this.sb.append("math.rand(");
        mathRandomIntFunct.param.get(0).accept(this);
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitMathSingleFunct(MathSingleFunct mathSingleFunct) {
        switch ( mathSingleFunct.functName ) {
            case ROOT:
                this.sb.append("call math.sqrt( _result, ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(" )");
                nlIndent();
                break;
            case SIN:
                this.sb.append("call math.sin( _result, ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(" )");
                nlIndent();
                break;
            case COS:
                this.sb.append("call math.cos( _result, ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(" )");
                nlIndent();
                break;
            case SQUARE:
                this.sb.append("call math.mul( _result, ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(", ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(" )");
                nlIndent();
                break;
            case ABS:
                this.sb.append("abs( ");
                mathSingleFunct.param.get(0).accept(this);
                this.sb.append(" )");
                nlIndent();
                break;
            default:
                throw new DbcException("Statement not supported by Aseba!");
        }
        return null;
    }

    @Override
    public Void visitMethodCall(MethodCall methodCall) {
        this.stateCounter++;
        this.sb.append("_return_state = ").append(this.stateCounter);
        nlIndent();
        this.sb.append("callsub ").append(methodCall.getMethodName());
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        return null;
    }

    @Override
    public Void visitMethodIfReturn(MethodIfReturn methodIfReturn) {
        this.sb.append("if ");
        methodIfReturn.oraCondition.accept(this);
        if ( !methodIfReturn.oraReturnValue.getKind().hasName("EMPTY_EXPR") ) {
            this.sb.append(": return ");
            methodIfReturn.oraReturnValue.accept(this);
        } else {
            this.sb.append(": return None");
        }
        return null;
    }

    @Override
    public Void visitMethodReturn(MethodReturn methodReturn) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public Void visitMethodVoid(MethodVoid methodVoid) {
        this.funcCounter++;
        this.sb.append(this.getIfElse()).append(" _state == ").append(this.funcStart.get(this.funcCounter)).append(" then");
        incrIndentation();
        methodVoid.body.accept(this);
        nlIndent();
        this.sb.append("_state = _return_state");
        decrIndentation();
        return null;
    }

    String getIfElse() {
        String ifElse = "";
        if ( this.ifOnce ) {
            ifElse = "elseif";
        } else {
            ifElse = "if";
            this.ifOnce = true;
        }
        return ifElse;
    }

    @Override
    public Void visitNullConst(NullConst nullConst) {
        throw new DbcException("No NULL in Aseba");
    }

    @Override
    public Void visitRepeatStmt(RepeatStmt repeatStmt) {
        loopCounter++;
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        switch ( repeatStmt.mode.toString() ) {
            case "FOREVER":
            case "UNTIL":
            case "WHILE":
            case "WAIT":
                generateCodeFromStmtCondition("if", repeatStmt.expr);
                break;
            case "TIMES":
            case "FOR":
                generateCodeFromStmtConditionFor("if", repeatStmt.expr);
                break;
            default:
                throw new DbcException("Invalid Repeat Statement!");
        }
        this.loopsStart.add(this.stateCounter);
        incrIndentation();
        nlIndent();
        this.stateCounter++;
        this.loopsBody.add(this.stateCounter);
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("else");
        incrIndentation();
        nlIndent();
        this.stateCounter++;
        this.loopsEnd.add(this.stateCounter);
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.loopsBody.get(this.loopCounter)).append(" then");
        incrIndentation();
        repeatStmt.list.accept(this);
        nlIndent();
        if ( repeatStmt.mode.equals(RepeatStmt.Mode.TIMES) || repeatStmt.mode.equals(RepeatStmt.Mode.FOR) ) {
            ((ExprList) repeatStmt.expr).get().get(0).accept(this);
            this.sb.append(" += ");
            ((ExprList) repeatStmt.expr).get().get(3).accept(this);
            nlIndent();
        }
        this.sb.append("_state = ").append(this.loopsStart.get(this.loopCounter));
        decrIndentation();
        nlIndent();
        this.stateCounter++;
        this.sb.append("elseif _state == ").append(this.loopsEnd.get(this.loopCounter)).append(" then");
        incrIndentation();
        loopCounter--;
        return null;
    }

    @Override
    public Void visitSerialWriteAction(SerialWriteAction serialWriteAction) {
        return null;
    }

    @Override
    public Void visitStmtFlowCon(StmtFlowCon stmtFlowCon) {
        if ( stmtFlowCon.flow.name().toLowerCase().equals(C.BREAK) ) {
            this.sb.append("_state = ").append(this.loopsEnd.get(this.loopCounter));
        } else if ( stmtFlowCon.flow.name().toLowerCase().equals(C.CONTINUE) ) {
            this.sb.append("_state = ").append(this.loopsStart.get(this.loopCounter));
        } else {
            throw new DbcException("Invalid flow control statement!");
        }
        decrIndentation();
        nlIndent();
        this.stateCounter++;
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        return null;
    }

    @Override
    public Void visitStmtTextComment(StmtTextComment stmtTextComment) {
        this.sb.append("# " + stmtTextComment.textComment.replace("\n", " "));
        return null;
    }

    @Override
    public Void visitTextCharCastNumberFunct(TextCharCastNumberFunct textCharCastNumberFunct) {
        this.sb.append("ord(");
        textCharCastNumberFunct.param.get(0).accept(this);
        this.sb.append("[");
        textCharCastNumberFunct.param.get(1).accept(this);
        this.sb.append("])");
        return null;
    }

    @Override
    public Void visitTextJoinFunct(TextJoinFunct textJoinFunct) {
        this.sb.append("\"\".join(str(arg) for arg in [");
        textJoinFunct.param.accept(this);
        this.sb.append("])");
        return null;
    }

    @Override
    public Void visitTextPrintFunct(TextPrintFunct textPrintFunct) {
        this.sb.append("print(");
        textPrintFunct.param.get(0).accept(this);
        this.sb.append(")");
        return null;
    }

    @Override
    public Void visitTextStringCastNumberFunct(TextStringCastNumberFunct textStringCastNumberFunct) {
        throw new DbcException("Floats not supported in Aseba!");
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        return null;
    }

    @Override
    public Void visitUnary(Unary unary) {
        Unary.Op op = unary.op;
        String sym = getUnaryOperatorSymbol(op);
        if ( op == Unary.Op.POSTFIX_INCREMENTS ) {
            generateExprCode(unary, this.sb);
            src.add(sym);
        } else {
            src.add(sym, " ( ");
            if ( unary.expr.getKind().getName().equals("BOOL_CONST") ) {
                src.add("___true == ");
            }
            generateExprCode(unary, this.sb);
            if ( unary.expr.getKind().getName().equals("VAR") ) {
                src.add(" == 1");
            }
            src.add(" ) ");
        }
        return null;
    }

    @Override
    public Void visitVarDeclaration(VarDeclaration var) {
        // TODO add here color var!!!
        this.sb.append("var ").append(var.getCodeSafeName());
        if ( !var.value.getKind().hasName("EMPTY_EXPR") ) {
            if ( var.value.getKind().getName().equals("LIST_CREATE") ) {
                if ( ((ListCreate) var.value).typeVar.toString().equals("COLOR") ) {
                    int listSize = ((ListCreate) var.value).exprList.get().size();
                    this.sb.append("_r[] = [");
                    for ( int i = 0; i < listSize; i++ ) {
                        if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("COLOR_CONST") ) {
                            this.sb.append(((ColorConst) ((ListCreate) var.value).exprList.get().get(i)).getRedChannelInt());
                        } else if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("RGB_COLOR") ) {
                            ((RgbColor) ((ListCreate) var.value).exprList.get().get(i)).R.accept(this);
                        }
                        if ( i != listSize - 1 ) {
                            this.sb.append(", ");
                        }
                    }
                    this.sb.append("]");
                    nlIndent();
                    this.sb.append("var ").append(var.getCodeSafeName());
                    this.sb.append("_g[] = [");
                    for ( int i = 0; i < listSize; i++ ) {
                        if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("COLOR_CONST") ) {
                            this.sb.append(((ColorConst) ((ListCreate) var.value).exprList.get().get(i)).getGreenChannelInt());
                        } else if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("RGB_COLOR") ) {
                            ((RgbColor) ((ListCreate) var.value).exprList.get().get(i)).G.accept(this);
                        }
                        if ( i != listSize - 1 ) {
                            this.sb.append(", ");
                        }
                    }
                    this.sb.append("]");
                    nlIndent();
                    this.sb.append("var ").append(var.getCodeSafeName());
                    this.sb.append("_b[] = [");
                    for ( int i = 0; i < listSize; i++ ) {
                        if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("COLOR_CONST") ) {
                            this.sb.append(((ColorConst) ((ListCreate) var.value).exprList.get().get(i)).getBlueChannelInt());
                        } else if ( ((ListCreate) var.value).exprList.get().get(i).getKind().hasName("RGB_COLOR") ) {
                            ((RgbColor) ((ListCreate) var.value).exprList.get().get(i)).B.accept(this);
                        }
                        if ( i != listSize - 1 ) {
                            this.sb.append(", ");
                        }
                    }
                    this.sb.append("]");
                    return null;
                } else {
                    this.sb.append("[]");
                }
            }
            if ( var.value.getKind().getName().equals("FUNCTION_EXPR") ) {
                var.value.accept(this);
                return null;
            }
            this.sb.append(" = ");
            if ( var.value.getKind().hasName("EXPR_LIST") ) {
                ExprList list = (ExprList) var.value;
                if ( list.get().size() == 2 ) {
                    list.get().get(1).accept(this);
                } else {
                    list.get().get(0).accept(this);
                }
            } else {
                var.value.accept(this);
            }
        } else {
            this.sb.append(" = 0");
        }
        return null;
    }

    @Override
    public Void visitWaitStmt(WaitStmt waitStmt) {
        //loopCounter++;
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        int stmtSize = waitStmt.statements.get().size();
        int statesSize = stmtSize + 1;
        int[] states = new int[statesSize];
        int i = 0;
        for ( ; i < stmtSize; i++ ) {
            if ( i == 0 ) {
                generateCodeFromStmtCondition("if", ((RepeatStmt) waitStmt.statements.get().get(i)).expr);
                incrIndentation();
                nlIndent();
                this.stateCounter++;
                states[i] = this.stateCounter;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
            } else {
                nlIndent();
                generateCodeFromStmtCondition("elseif", ((RepeatStmt) waitStmt.statements.get().get(i)).expr);
                incrIndentation();
                nlIndent();
                this.stateCounter++;
                states[i] = this.stateCounter;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
            }
        }
        nlIndent();
        this.stateCounter++;
        states[i] = this.stateCounter;
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        for ( int j = 0; j < stmtSize; j++ ) {
            this.sb.append("elseif _state == ").append(states[j]).append(" then");
            incrIndentation();
            StmtList then = ((RepeatStmt) waitStmt.statements.get().get(j)).list;
            if ( !then.get().isEmpty() ) {
                then.accept(this);
            }
            nlIndent();
            this.sb.append("_state = ").append(states[statesSize - 1]);
            decrIndentation();
            nlIndent();
        }
        this.sb.append("elseif _state == ").append(states[statesSize - 1]).append(" then");
        incrIndentation();
        //loopCounter--;
        return null;
    }

    @Override
    public Void visitWaitTimeStmt(WaitTimeStmt waitTimeStmt) {
        return null;
    }

    @Override
    protected void generateCodeFromTernary(TernaryExpr ternaryExpr) {
        throw new DbcException("No Ternary Expressions in Aseba!");
    }

    @Override
    protected void generateCodeFromIfElse(IfStmt ifStmt) {
        int stmtSize = ifStmt.expr.size();
        int statesSize = stmtSize + 1 + (ifStmt.elseList.get().isEmpty() ? 0 : 1);
        int[] states = new int[statesSize];
        int i = 0;
        for ( ; i < stmtSize; i++ ) {
            if ( i == 0 ) {
                generateCodeFromStmtCondition("if", ifStmt.expr.get(i));
                incrIndentation();
                nlIndent();
                this.stateCounter++;
                states[i] = this.stateCounter;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
            } else {
                nlIndent();
                generateCodeFromStmtCondition("elseif", ifStmt.expr.get(i));
                incrIndentation();
                nlIndent();
                this.stateCounter++;
                states[i] = this.stateCounter;
                this.sb.append("_state = ").append(this.stateCounter);
                decrIndentation();
            }
        }
        nlIndent();
        this.sb.append("else");
        incrIndentation();
        nlIndent();
        this.stateCounter++;
        states[i] = this.stateCounter;
        i++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        if ( !ifStmt.elseList.get().isEmpty() ) {
            this.stateCounter++;
            states[i] = this.stateCounter;
        }
        nlIndent();
        this.sb.append("end");
        decrIndentation();
        nlIndent();
        for ( int j = 0; j < stmtSize; j++ ) {
            this.sb.append("elseif _state == ").append(states[j]).append(" then");
            incrIndentation();
            StmtList then = ifStmt.thenList.get(j);
            if ( !then.get().isEmpty() ) {
                then.accept(this);
            }
            nlIndent();
            this.sb.append("_state = ").append(states[statesSize - 1]);
            decrIndentation();
            nlIndent();
        }
        if ( !ifStmt.elseList.get().isEmpty() ) {
            this.sb.append("elseif _state == ").append(states[statesSize - 2]).append(" then");
            incrIndentation();
            ifStmt.elseList.accept(this);
            nlIndent();
            this.sb.append("_state = ").append(states[statesSize - 1]);
            decrIndentation();
            nlIndent();
        }
        this.sb.append("elseif _state == ").append(states[statesSize - 1]).append(" then");
        incrIndentation();
    }

    @Override
    protected void generateCodeFromElse(IfStmt ifStmt) {
        // nothing to do, integrated in generateCodeFromIfElse()
    }

    @Override
    protected String getLanguageVarTypeFromBlocklyType(BlocklyType type) {
        return "";
    }

    protected void generateCodeFromStmtCondition(String stmtType, Expr expr) {
        this.sb.append(stmtType).append(whitespace());
        expr.accept(this);
        if ( expr.getKind().getName().equals("BOOL_CONST") || expr.getKind().getName().equals("VAR") ) {
            this.sb.append(" == ___true");
        }
        this.sb.append(" then");
    }

    protected void generateCodeFromStmtConditionFor(String stmtType, Expr expr) {
        ExprList expressions = (ExprList) expr;
        expressions.get().get(0).accept(this);
        this.sb.append(" = ");
        expressions.get().get(1).accept(this);
        nlIndent();
        this.stateCounter++;
        this.sb.append("_state = ").append(this.stateCounter);
        decrIndentation();
        nlIndent();
        this.sb.append("elseif _state == ").append(this.stateCounter).append(" then");
        incrIndentation();
        nlIndent();
        this.sb.append(stmtType).append(whitespace());
        expressions.get().get(0).accept(this);
        this.sb.append(whitespace()).append("< ");
        expressions.get().get(2).accept(this);
        this.sb.append(" then");
    }

    @Override
    protected String getBinaryOperatorSymbol(Binary.Op op) {
        return AbstractAsebaVisitor.binaryOpSymbols().get(op);
    }

    @Override
    protected String getUnaryOperatorSymbol(Unary.Op op) {
        return AbstractAsebaVisitor.unaryOpSymbols().get(op);
    }

    private void generateCodeRightExpression(Binary binary, Binary.Op op) {
        if ( op == Binary.Op.TEXT_APPEND ) {
            generateSubExpr(this.sb, false, binary.getRight(), binary);
        } else {
            generateSubExpr(this.sb, parenthesesCheck(binary), binary.getRight(), binary);
        }
    }
}
