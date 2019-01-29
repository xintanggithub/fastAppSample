package android.util;

/**
 * Created by PC on 2017/7/20.
 */


public abstract class Singleton<T> {
    private T mInstance;

    public Singleton() {
    }

    protected abstract T create();

    public T get() {
        synchronized(this) {
            if(this.mInstance == null) {
                this.mInstance = this.create();
            }

            return this.mInstance;
        }
    }
}
