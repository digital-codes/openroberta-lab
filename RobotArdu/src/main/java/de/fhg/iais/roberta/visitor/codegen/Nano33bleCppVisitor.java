package de.fhg.iais.roberta.visitor.codegen;

import java.util.List;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.Var;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkAddClassifyData;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkAddRawData;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkAddTrainingsData;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkClassify;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkInitClassifyData;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkInitRawData;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkSetup;
import de.fhg.iais.roberta.syntax.neuralnetwork.NeuralNetworkTrain;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Apds9960ColorSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Apds9960DistanceSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Apds9960GestureSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Hts221HumiditySensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Hts221TemperatureSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Lps22hbPressureSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Lsm9ds1AccSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Lsm9ds1GyroSensor;
import de.fhg.iais.roberta.syntax.sensors.arduino.nano33blesense.Lsm9ds1MagneticFieldSensor;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.visitor.IVisitor;
import de.fhg.iais.roberta.visitor.hardware.INano33BleSensorVisitor;

/**
 * This class is implementing {@link IVisitor}. All methods are implemented and they append a human-readable C representation of a phrase to a StringBuilder.
 * <b>This class generates C++ code for the Arduino Nano 33 BLE.</b> <br>
 */
public class Nano33bleCppVisitor extends ArduinoCppVisitor implements INano33BleSensorVisitor<Void> {
    /**
     * Initialize the C++ code generator visitor.
     *
     * @param phrases to generate the code from
     */
    public Nano33bleCppVisitor(List<List<Phrase>> phrases, ConfigurationAst brickConfiguration, ClassToInstanceMap<IProjectBean> beans) {
        super(phrases, brickConfiguration, beans);
    }

    @Override
    public Void visitLsm9ds1AccSensor(Lsm9ds1AccSensor sensor) {
        this.sb
            .append("(IMU.accelerationAvailable()?(IMU.readAcceleration(xAsFloat,yAsFloat,zAsFloat),")
            .append("___" + phrase2varValue(sensor.x))
            .append(" = (double) xAsFloat,")
            .append("___" + phrase2varValue(sensor.y))
            .append(" = (double) yAsFloat,")
            .append("___" + phrase2varValue(sensor.z))
            .append(" = (double) zAsFloat,1) : 0)");
        return null;
    }

    @Override
    public Void visitLsm9ds1GyroSensor(Lsm9ds1GyroSensor sensor) {
        this.sb
            .append("(IMU.gyroscopeAvailable()?(IMU.readGyroscope(xAsFloat,yAsFloat,zAsFloat),")
            .append("___" + phrase2varValue(sensor.x))
            .append(" = (double) xAsFloat,")
            .append("___" + phrase2varValue(sensor.y))
            .append(" = (double) yAsFloat,")
            .append("___" + phrase2varValue(sensor.z))
            .append(" = (double) zAsFloat,1) : 0)");
        return null;
    }

    @Override
    public Void visitLsm9ds1MagneticFieldSensor(Lsm9ds1MagneticFieldSensor sensor) {
        this.sb
            .append("(IMU.magneticFieldAvailable()?(IMU.readMagneticField(xAsFloat,yAsFloat,zAsFloat),")
            .append("___" + phrase2varValue(sensor.x))
            .append(" = (double) xAsFloat,")
            .append("___" + phrase2varValue(sensor.y))
            .append(" = (double) yAsFloat,")
            .append("___" + phrase2varValue(sensor.z))
            .append(" = (double) zAsFloat,1) : 0)");
        return null;
    }

    @Override
    public Void visitApds9960DistanceSensor(Apds9960DistanceSensor sensor) {
        this.sb
            .append("(APDS.proximityAvailable()?(___") //
            .append(phrase2varValue(sensor.distance))
            .append(" = (double) APDS.readProximity(),1) : 0)");
        return null;
    }

    @Override
    public Void visitApds9960GestureSensor(Apds9960GestureSensor sensor) {
        this.sb
            .append("(APDS.gestureAvailable()?(___") //
            .append(phrase2varValue(sensor.gesture))
            .append(" = (double) APDS.readGesture(),1) : 0)");
        return null;
    }

    @Override
    public Void visitApds9960ColorSensor(Apds9960ColorSensor sensor) {
        this.sb
            .append("(APDS.colorAvailable()?(APDS.readColor(rAsInt,gAsInt,bAsInt),")
            .append("___" + phrase2varValue(sensor.r))
            .append(" = (double) rAsInt,")
            .append("___" + phrase2varValue(sensor.g))
            .append(" = (double) gAsInt,")
            .append("___" + phrase2varValue(sensor.b))
            .append(" = (double) bAsInt,1) : 0)");
        return null;
    }

    @Override
    public Void visitLps22hbPressureSensor(Lps22hbPressureSensor sensor) {
        this.sb
            .append("(___") //
            .append(phrase2varValue(sensor.pressure))
            .append(" = (double) BARO.readPressure(),1)");
        return null;
    }

    @Override
    public Void visitHts221TemperatureSensor(Hts221TemperatureSensor sensor) {
        this.sb
            .append("(___") //
            .append(phrase2varValue(sensor.temperature))
            .append(" = (double) HTS.readTemperature(),1)");
        return null;
    }

    @Override
    public Void visitHts221HumiditySensor(Hts221HumiditySensor sensor) {
        this.sb
            .append("(___") //
            .append(phrase2varValue(sensor.humidity))
            .append(" = (double) HTS.readHumidity(),1)");
        return null;
    }

    @Override
    public Void visitNeuralNetworkSetup(NeuralNetworkSetup nn) {
        this.sb.append("// visitNeuralNetworkSetup");
        return null;
    }

    @Override
    public Void visitNeuralNetworkInitRawData(NeuralNetworkInitRawData nn) {
        String dataSetStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_DATASET");
        String inputNeuronsStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_NUMBER_INPUT_NEURONS");
        this.sb.append("currentData = 0;\n    currentDataSet = 0;\n");
        this.sb.append("    for (int i = 0; i <" + dataSetStr + "; i++){\n        for (int j = 0; j <" + inputNeuronsStr + "; j++){\n            input_data[i][j] = 0.0;\n        }\n        target_data[i] = 0.0;\n     }");
        return null;
    }

    @Override
    public Void visitNeuralNetworkAddRawData(NeuralNetworkAddRawData nn) {
        this.sb.append("addTrainingData(" + nn.getValueNN(nn.rawData) + ");");
        return null;
    }

    @Override
    public Void visitNeuralNetworkAddTrainingsData(NeuralNetworkAddTrainingsData nn) {
        this.sb.append("addTargetData(" + nn.getValueNNTrain(nn.classNumber) + ");");
        return null;
    }

    @Override
    public Void visitNeuralNetworkTrain(NeuralNetworkTrain nn) {
        String dataSetStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_DATASET");
        String numberLayersStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_FNN_LAYERS");
        int numberLayers = Integer.parseInt(numberLayersStr);
        this.sb.append("uint16_t input_shape[] = {" + dataSetStr + ", (uint16_t)FNN_structure[0]};\n")
            .append("    aitensor_t input_tensor = AITENSOR_2D_F32(input_shape, input_data);\n    uint16_t target_shape[] = {" + dataSetStr + ", (uint16_t)FNN_structure[" + (numberLayers - 1) + "]};\n")
            .append("    aitensor_t target_tensor = AITENSOR_2D_F32(target_shape, target_data);\n    float output_train_data[" + dataSetStr + "];\n")
            .append("    uint16_t output_train_shape[] = {" + dataSetStr + ", (uint16_t)FNN_structure[" + (numberLayers - 1) + "]};\n")
            .append("    aitensor_t output_train_tensor = AITENSOR_2D_F32(output_train_shape, output_train_data);\n")
            .append("    error = AIFES_E_training_fnn_f32(&input_tensor,&target_tensor,&FNN,&FNN_TRAIN,&FNN_INIT_WEIGHTS,&output_train_tensor);\n")
            .append("    if (error != 0){\n        Serial.println(F(\"ERROR while train!\"));\n    }");
        return null;
    }


    @Override
    public Void visitNeuralNetworkAddClassifyData(NeuralNetworkAddClassifyData nn) {
        this.sb.append("addClassifyData(" + nn.getValueNNClassify(nn.classNumber) + ");");
        return null;
    }

    @Override
    public Void visitNeuralNetworkInitClassifyData(NeuralNetworkInitClassifyData nn) {
        String inputNeuronsStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_NUMBER_INPUT_NEURONS");
        this.sb.append("for (int i = 0; i < " + inputNeuronsStr + "; i++){\n        classify_data[i] = 0.0;\n    }");
        return null;
    }

    @Override
    public Void visitNeuralNetworkClassify(NeuralNetworkClassify nn) {
        String numberLayersStr = this.configuration.getConfigurationComponent("Aifes").getProperty("AIFES_FNN_LAYERS");
        int numberLayers = Integer.parseInt(numberLayersStr);
        this.sb.append("uint16_t classify_shape[] = {1, (uint16_t)FNN_structure[0]};\n    aitensor_t classify_tensor = AITENSOR_2D_F32(classify_shape, classify_data);\n")
            .append("    float output_classify_data[1];\n    uint16_t output_classify_shape[] = {1 , (uint16_t)FNN_structure[" + (numberLayers - 1) + "]};\n")
            .append("    aitensor_t output_classify_tensor = AITENSOR_2D_F32(output_classify_shape, output_classify_data);\n")
            .append("    error = AIFES_E_inference_fnn_f32(&classify_tensor,&FNN,&output_classify_tensor);\n    if (error != 0){\n        Serial.println(F(\"ERROR while classifiy!\"));\n    }\n")
            .append("    Serial.println(output_classify_data[0], 5);");//TODO: Löschen
        return null;
    }

    private String phrase2varValue(Expr phrase) {
        if ( phrase instanceof Var ) {
            return ((Var) phrase).name;
        } else {
            throw new DbcException("Phrase MUST be a Var: " + phrase);
        }
    }
}