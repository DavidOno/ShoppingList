package de.db.shoppinglist.utility;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility to create Toasts globally.
 * Reason: All interactions with a database may result in a failure,
 * but passing the necessary information to notify the user as parameter every time,
 * makes the code less readable and cumbersome.
 * Since it is relevant for all database-interactions,
 * I think it's an appropriate and justified solution to make this functionality global.
 * It's considered to be thread-safe.
 */
public class ToastUtility {

    private static ToastUtility instance;
    private ReentrantLock lock = new ReentrantLock();
    private MutableLiveData<Boolean> isToastNew = new MutableLiveData<>(false);
    private String message;

    private ToastUtility(){
        //empty constructor
    }

    public static ToastUtility getInstance(){
        if(instance == null){
            instance = new ToastUtility();
        }
        return instance;
    }

    /**
     * Toast is getting prepared.
     * Since it's called from an asynchronous environment, it's made thread-safe.
     * Therefore at the beginning a lock is acquired.
     * This lock is only release iff {@link #getMessage()} is called.
     * @param message The message to display via Toast.
     */
    public void prepareToast(String message){
        lock.lock();
        isToastNew.setValue(true);
        this.message = message;
    }

    public LiveData<Boolean> getNewToast(){
        return isToastNew;
    }

    /**
     * Returns the Message.
     * Here the lock, which was acquired in {@link #prepareToast(String)}, is released.
     * @return Returns the message, which should be displayed via Toast
     */
    public String getMessage(){
        isToastNew.setValue(false);
        lock.unlock();
        return message;
    }
}
