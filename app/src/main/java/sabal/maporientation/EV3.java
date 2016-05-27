package sabal.maporientation;

import java.io.IOException;

public class EV3 extends Connector {

    public void MotorsPowerOn() throws IOException {
        byte motor1[] = {0x08, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA6, 0x00, 0x02};
        Outstream.write(motor1);
        byte motor2[] = {0x08, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA6, 0x00, 0x04};
        Outstream.write(motor2);
    }


    @Override
    public void TurnTo(int endDir) throws IOException {
        //Получить значение с компаса
//        int curDir = MainActivity.direction;
//        int dD = endDir - curDir;
//        while (dD < -5 || dD > 5) {
//            if (dD < -5) {
//                MotorsPowerOn();
//                MotorsPowerSet();
//            }
//        }
    }

    @Override
    public void RideTo(String way) throws IOException {

    }

    public void MotorsPowerOff() throws IOException {
        byte motor1[] = {0x09, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA3, 0x00, 0x02, 0x01};
        Outstream.write(motor1);
        byte motor2[] = {0x09, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA3, 0x00, 0x04, 0x01};
        Outstream.write(motor2);
    }

    public void MotorsPowerSet(byte PowerA, byte PowerB) throws IOException {
        byte motor1[] = {0x0a, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA4, 0x00, 0x02, (byte) 0x81,(byte) PowerA};
        Outstream.write(motor1);
        byte motor2[] = {0x0a, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xA4, 0x00, 0x04, (byte) 0x81,(byte) PowerB};
        Outstream.write(motor2);
    }
}

