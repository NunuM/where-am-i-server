package me.nunum.whereami.model.request;

public class UpdateTask {

    private boolean isFinish;

    public UpdateTask() {
        this(false);
    }

    public UpdateTask(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    @Override
    public String toString() {
        return "UpdateTask{" +
                "isFinish=" + isFinish +
                '}';
    }
}
