package de.fhg.iais.roberta.components.arduino;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.fhg.iais.roberta.components.Actor;
import de.fhg.iais.roberta.components.ActorType;
import de.fhg.iais.roberta.components.Configuration;
import de.fhg.iais.roberta.components.Sensor;
import de.fhg.iais.roberta.inter.mode.action.IActorPort;
import de.fhg.iais.roberta.inter.mode.sensor.ISensorPort;
import de.fhg.iais.roberta.mode.action.MotorSide;
import de.fhg.iais.roberta.mode.actors.arduino.botnroll.ActorPort;
import de.fhg.iais.roberta.mode.sensors.arduino.botnroll.SensorPort;
import de.fhg.iais.roberta.util.Pair;
import de.fhg.iais.roberta.util.dbc.DbcException;

public class MbotConfiguration extends Configuration {

    public MbotConfiguration(Map<IActorPort, Actor> actors, Map<ISensorPort, Sensor> sensors, double wheelDiameterCM, double trackWidthCM) {
        super(actors, sensors, wheelDiameterCM, trackWidthCM);

    }

    /**
     * This method returns the port on which the left motor is connected. If there is no left motor connected throws and {@link DbcException} exception.
     *
     * @return port on which the left motor is connected
     */
    @Override
    public IActorPort getLeftMotorPort() {
        return getMotorPortOnSide(MotorSide.LEFT);
    }

    @Override
    public Actor getLeftMotor() {
        IActorPort port = getLeftMotorPort();
        if ( port != null ) {
            return getActorOnPort(port);
        }
        return null;
    }

    /**
     * This method returns the port on which the right motor is connected. If there is no right motor connected throws and {@link DbcException} exception.
     *
     * @return port on which the left motor is connected
     */
    @Override
    public IActorPort getRightMotorPort() {
        return getMotorPortOnSide(MotorSide.RIGHT);
    }

    @Override
    public Actor getRightMotor() {
        IActorPort port = getRightMotorPort();
        if ( port != null ) {
            return getActorOnPort(port);
        }
        return null;

    }

    @Override
    public int getNumberOfRightMotors() {
        int right = 0;
        for ( Actor actor : this.actors.values() ) {
            if ( actor.getMotorSide() == MotorSide.RIGHT ) {
                right++;
            }
        }
        return right;
    }

    @Override
    public int getNumberOfLeftMotors() {
        int leftMotors = 0;
        for ( Actor actor : this.actors.values() ) {
            if ( actor.getMotorSide() == MotorSide.LEFT ) {
                leftMotors++;
            }
        }
        return leftMotors;
    }

    /**
     * @return text which defines the brick configuration
     */
    @Override
    public String generateText(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("robot mbot ").append(name).append(" {\n");
        generateSensors(sb);
        generateActors(sb);
        sb.append("}");
        return sb.toString();
    }

    private void generateSensors(StringBuilder sb) {
        if ( !this.sensors.isEmpty() ) {
            sb.append("  sensor port {\n");
            for ( ISensorPort port : this.sensors.keySet() ) {
                sb.append("    ").append(port.getPortNumber()).append(": ");
                String sensor = this.sensors.get(port).getType().toString();
                sb.append(sensor.toLowerCase()).append(";\n");
            }
            sb.append("  }\n");
        }
    }

    private void generateActors(StringBuilder sb) {
        if ( !this.actors.isEmpty() ) {
            sb.append("  actor port {\n");
            for ( IActorPort port : this.actors.keySet() ) {
                sb.append("    ").append(port.toString()).append(": ");
                Actor actor = this.actors.get(port);
                if ( actor.getName() == ActorType.GEARED_MOTOR ) {
                    sb.append("geared");
                }
                sb.append(" motor, ");
                if ( actor.isRegulated() ) {
                    sb.append("regulated");
                } else {
                    sb.append("unregulated");
                }
                sb.append(", ");
                String rotationDirection = actor.getRotationDirection().toString().toLowerCase();
                sb.append(rotationDirection.equals("foreward") ? "forward" : rotationDirection); // TODO: remove this hack; rename FOIREWARD to FORWARD (be careful!)
                MotorSide motorSide = (MotorSide) actor.getMotorSide();
                if ( motorSide != MotorSide.NONE ) {
                    sb.append(", ").append(motorSide.getText());

                }
                sb.append(";\n");
            }
            sb.append("  }\n");
        }
    }

    /**
     * This class is a builder of {@link Configuration}
     */
    public static class Builder extends Configuration.Builder<Builder> {
        private final Map<IActorPort, Actor> actorMapping = new TreeMap<>();
        private final Map<ISensorPort, Sensor> sensorMapping = new TreeMap<>();

        private double wheelDiameter;
        private double trackWidth;

        /**
         * Add actor to the {@link Configuration}
         *
         * @param port on which the component is connected
         * @param actor we want to connect
         * @return
         */
        @Override
        public Builder addActor(IActorPort port, Actor actor) {
            this.actorMapping.put(port, actor);
            return this;
        }

        /**
         * Client must provide list of {@link Pair} ({@link ActorPort} and {@link Actor})
         *
         * @param actors we want to connect to the brick configuration
         * @return
         */
        @Override
        public Builder addActors(List<Pair<IActorPort, Actor>> actors) {
            for ( Pair<IActorPort, Actor> pair : actors ) {
                this.actorMapping.put(pair.getFirst(), pair.getSecond());
            }
            return this;
        }

        /**
         * Add sensor to the {@link Configuration}
         *
         * @param port on which the component is connected
         * @param component we want to connect
         * @return
         */

        @Override
        public Builder addSensor(ISensorPort port, Sensor sensor) {
            this.sensorMapping.put(port, sensor);
            return this;
        }

        /**
         * Client must provide list of {@link Pair} ({@link SensorPort} and {@link Sensor})
         *
         * @param sensors we want to connect to the brick configuration
         * @return
         */
        @Override
        public Builder addSensors(List<Pair<ISensorPort, Sensor>> sensors) {
            for ( Pair<ISensorPort, Sensor> pair : sensors ) {
                this.sensorMapping.put(pair.getFirst(), pair.getSecond());
            }
            return this;
        }

        /**
         * Set the wheel diameter
         *
         * @param wheelDiameter in cm
         * @return
         */
        @Override
        public Builder setWheelDiameter(double wheelDiameter) {
            this.wheelDiameter = wheelDiameter;
            return this;
        }

        /**
         * Set the track width
         *
         * @param trackWidth in cm
         * @return
         */
        @Override
        public Builder setTrackWidth(double trackWidth) {
            this.trackWidth = trackWidth;
            return this;
        }

        @Override
        public Configuration build() {
            return new MbotConfiguration(this.actorMapping, this.sensorMapping, this.wheelDiameter, this.trackWidth);
        }

        @Override
        public String toString() {
            return "Builder [actorMapping="
                + this.actorMapping
                + ", sensorMapping="
                + this.sensorMapping
                + ", wheelDiameter="
                + this.wheelDiameter
                + ", trackWidth="
                + this.trackWidth
                + "]";
        }

    }

}
