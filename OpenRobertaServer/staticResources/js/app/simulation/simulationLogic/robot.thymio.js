var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
define(["require", "exports", "robot.ev3", "./robot.actuators", "robot.sensors", "jquery"], function (require, exports, robot_ev3_1, robot_actuators_1, robot_sensors_1, $) {
    Object.defineProperty(exports, "__esModule", { value: true });
    var RobotThymio = /** @class */ (function (_super) {
        __extends(RobotThymio, _super);
        function RobotThymio(id, configuration, interpreter, savedName, myListener) {
            var _this = _super.call(this, id, configuration, interpreter, savedName, myListener) || this;
            _this.imgList = ['simpleBackgroundSmall', 'drawBackground', 'rescueBackground', 'mathBackground'];
            _this.webAudio = new robot_actuators_1.WebAudio();
            return _this;
        }
        RobotThymio.prototype.configure = function (configuration) {
            // due to no information from the configuration, track width and wheel diameter are fix:
            configuration['TRACKWIDTH'] = 9;
            configuration['WHEELDIAMETER'] = 4.3;
            this.chassis = new robot_actuators_1.ThymioChassis(this.id, configuration, this.pose);
            this.lineSensor = new robot_sensors_1.ThymioLineSensor({ x: 24, y: 0 });
            this.infraredSensors = new robot_sensors_1.ThymioInfraredSensors();
            this.tapSensor = new robot_sensors_1.TapSensor();
            this.soundSensor = new robot_sensors_1.VolumeMeterSensor(this);
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
            }*/
            var myButtons = [
                {
                    name: 'forward',
                    value: false
                },
                {
                    name: 'backward',
                    value: false
                },
                {
                    name: 'left',
                    value: false
                },
                {
                    name: 'right',
                    value: false
                },
                {
                    name: 'center',
                    value: false
                }
            ];
            this.buttons = new robot_sensors_1.EV3Keys(myButtons, this.id);
            var thymio = this;
            for (var property in this['buttons']['keys']) {
                var $property = $('#' + this['buttons']['keys'][property].name + thymio.id);
                $property.on('mousedown touchstart', function () {
                    thymio['buttons']['keys'][this.id.replace(/\d+$/, '')]['value'] = true;
                });
                $property.on('mouseup touchend', function () {
                    thymio['buttons']['keys'][this.id.replace(/\d+$/, '')]['value'] = false;
                });
            }
        };
        return RobotThymio;
    }(robot_ev3_1.default));
    exports.default = RobotThymio;
});
