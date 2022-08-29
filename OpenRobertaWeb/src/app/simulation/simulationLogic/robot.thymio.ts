import { SelectionListener } from 'robot.base';
import { Interpreter } from 'interpreter.interpreter';
import RobotEv3 from 'robot.ev3';
import { ThymioChassis } from './robot.actuators';
import { ThymioInfraredSensors, ThymioLineSensor } from 'robot.sensors';

export default class RobotThymio extends RobotEv3 {
    override readonly imgList = ['simpleBackgroundSmall', 'drawBackground', 'rescueBackground', 'mathBackground'];
    private lineSensor: ThymioLineSensor;
    private infraredSensors: ThymioInfraredSensors;

    constructor(id: number, configuration: object, interpreter: Interpreter, savedName: string, myListener: SelectionListener) {
        super(id, configuration, interpreter, savedName, myListener);
    }

    protected override configure(configuration: object): void {
        // due to no information from the configuration, track width and wheel diameter are fix:
        configuration['TRACKWIDTH'] = 9;
        configuration['WHEELDIAMETER'] = 4.3;

        this.chassis = new ThymioChassis(this.id, configuration, this.pose);
        this.lineSensor = new ThymioLineSensor({ x: 24, y: 0 });
        this.infraredSensors = new ThymioInfraredSensors();
        /*this.RGBLedLeft = new MbotRGBLed({ x: 20, y: -10 }, 2);
        this.RGBLedRight = new MbotRGBLed({ x: 20, y: 10 }, 1);
        this.display = new MbotDisplay(this.id, { x: 15, y: 50 });
        let sensors: object = configuration['SENSORS'];
        for (const c in sensors) {
            switch (sensors[c]) {
                case 'ULTRASONIC': {
                    let myUltraSensors = [];
                    let mbot = this;
                    Object.keys(this).forEach((x) => {
                        if (mbot[x] && mbot[x] instanceof DistanceSensor) {
                            myUltraSensors.push(mbot[x]);
                        }
                    });
                    const ord = myUltraSensors.length + 1;
                    const num = Object.keys(sensors).filter((type) => sensors[type] == 'ULTRASONIC').length;
                    let position: Pose = new Pose(this.chassis.geom.x + this.chassis.geom.w, 0, 0);
                    if (num == 3) {
                        if (ord == 1) {
                            position = new Pose(this.chassis.geom.h / 2, -this.chassis.geom.h / 2, -Math.PI / 4);
                        } else if (ord == 2) {
                            position = new Pose(this.chassis.geom.h / 2, this.chassis.geom.h / 2, Math.PI / 4);
                        }
                    } else if (num % 2 === 0) {
                        switch (ord) {
                            case 1:
                                position = new Pose(this.chassis.geom.x + this.chassis.geom.w, -this.chassis.geom.h / 2, -Math.PI / 4);
                                break;
                            case 2:
                                position = new Pose(this.chassis.geom.x + this.chassis.geom.w, this.chassis.geom.h / 2, Math.PI / 4);
                                break;
                            case 3:
                                position = new Pose(this.chassis.geom.x, -this.chassis.geom.h / 2, (-3 * Math.PI) / 4);
                                break;
                            case 4:
                                position = new Pose(this.chassis.geom.x, this.chassis.geom.h / 2, (3 * Math.PI) / 4);
                                break;
                        }
                    }
                    this[c] = new UltrasonicSensor(c, position.x, position.y, position.theta, 255, 'Ultra Sensor');
                    break;
                }
                case 'INFRARED': {
                    // only one is supported in the simulation
                    let myInfraredSensors = [];
                    let mbot = this;
                    Object.keys(this).forEach((x) => {
                        if (mbot[x] && mbot[x] instanceof MbotInfraredSensor) {
                            myInfraredSensors.push(mbot[x]);
                        }
                    });
                    if (myInfraredSensors.length == 0) {
                        this[c] = new MbotInfraredSensor(c, { x: 26, y: 0 });
                    }
                    break;
                }
            }
        }
        let myButton: TouchKey[] = [
            {
                name: 'center',
                value: false,
                port: 'center',
                touchColors: ['#000000ff'],
            },
        ];
        this.buttons = new MbotButton(myButton, this.id);*/
    }
}
