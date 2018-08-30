package co.astrnt.surveyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceErrorDao implements Parcelable {
    public static final int TYPE_DEVICE = 0;
    public static final int TYPE_NETWORK = 1;
    public static final Creator<DeviceErrorDao> CREATOR = new Creator<DeviceErrorDao>() {
        @Override
        public DeviceErrorDao createFromParcel(Parcel source) {
            return new DeviceErrorDao(source);
        }

        @Override
        public DeviceErrorDao[] newArray(int size) {
            return new DeviceErrorDao[size];
        }
    };
    private boolean memoryOk;
    private boolean cameraOk;
    private boolean soundOk;
    private boolean networkOk;
    private int type;

    public DeviceErrorDao(boolean memoryOk, boolean cameraOk, boolean soundOk, boolean networkOk, int type) {
        this.memoryOk = memoryOk;
        this.cameraOk = cameraOk;
        this.soundOk = soundOk;
        this.networkOk = networkOk;
        this.type = type;
    }

    protected DeviceErrorDao(Parcel in) {
        this.memoryOk = in.readByte() != 0;
        this.cameraOk = in.readByte() != 0;
        this.soundOk = in.readByte() != 0;
        this.networkOk = in.readByte() != 0;
        this.type = in.readInt();
    }

    public boolean isNetworkOk() {
        return networkOk;
    }

    public void setNetworkOk(boolean networkOk) {
        this.networkOk = networkOk;
    }

    public boolean isMemoryOk() {
        return memoryOk;
    }

    public void setMemoryOk(boolean memoryOk) {
        this.memoryOk = memoryOk;
    }

    public boolean isSoundOk() {
        return soundOk;
    }

    public void setSoundOk(boolean soundOk) {
        this.soundOk = soundOk;
    }

    public boolean isCameraOk() {
        return cameraOk;
    }

    public void setCameraOk(boolean cameraOk) {
        this.cameraOk = cameraOk;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.memoryOk ? (byte) 1 : (byte) 0);
        dest.writeByte(this.cameraOk ? (byte) 1 : (byte) 0);
        dest.writeByte(this.soundOk ? (byte) 1 : (byte) 0);
        dest.writeByte(this.networkOk ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }
}
