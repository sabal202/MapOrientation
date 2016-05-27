package sabal.maporientation;

import java.io.IOException;

public interface Robot2WD {

    boolean Connect(String Name) throws IOException;
    void Disconnect() throws IOException;
    void MotorsPowerSet(byte PowerA, byte PowerB) throws IOException;
    void MotorsPowerOff() throws IOException;
    void MotorsPowerOn() throws IOException;
    void TurnTo(int endDir) throws IOException;
    void RideTo(String way) throws IOException;
}
