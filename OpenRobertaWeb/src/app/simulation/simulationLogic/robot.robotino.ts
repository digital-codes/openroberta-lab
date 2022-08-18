import { RobotBaseMobile } from 'robot.base.mobile';
import { Keys, OdometrySensor, RobotinoInfraredSensor, RobotinoTouchSensor, Timer } from 'robot.sensors';
import { RobotinoChassis, StatusLed, TTS, WebAudio } from './robot.actuators';
import { RobotBase, SelectionListener } from 'robot.base';
import { Interpreter } from 'interpreter.interpreter';

export default class RobotRobotino extends RobotBaseMobile {
    chassis: RobotinoChassis;
    led: StatusLed;
    volume: number = 0.5;
    tts: TTS = new TTS();
    webAudio: WebAudio = new WebAudio();
    override timer: Timer = new Timer(5);
    buttons: Keys;
    override readonly imgList = ['squareBig', 'blank'];
    private infraredSensor: RobotinoInfraredSensor;
    private robotinoTouchSensor: RobotinoTouchSensor;
    private odometrySensor: OdometrySensor;

    constructor(id: number, configuration: object, interpreter: Interpreter, savedName: string, myListener: SelectionListener) {
        super(id, configuration, interpreter, savedName, myListener);
        this.mouse = {
            x: 0,
            y: 0,
            rx: 0,
            ry: 0,
            r: 70,
        };
        this.configure(configuration);
    }

    override updateActions(robot: RobotBase, dt, interpreterRunning: boolean): void {
        super.updateActions(robot, dt, interpreterRunning);
        let volume = this.interpreter.getRobotBehaviour().getActionState('volume', true);
        if (volume || volume === 0) {
            this.volume = volume / 100.0;
        }
    }

    override reset() {
        super.reset();
        this.volume = 0.5;
    }

    // this method might go up to BaseMobileRobots as soon as the configuration has detailed information about the sensors geometry and location on the robot
    protected configure(configuration: object): void {
        this.chassis = new RobotinoChassis(this.id, this.pose);
        this.robotinoTouchSensor = new RobotinoTouchSensor();
        this.infraredSensor = new RobotinoInfraredSensor();
        this.odometrySensor = new OdometrySensor();
    }
}
