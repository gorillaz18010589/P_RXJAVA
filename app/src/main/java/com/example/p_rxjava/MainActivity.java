package com.example.p_rxjava;
//參考https://www.youtube.com/watch?v=ejQI3kGrplI&list=PLgCYzUzKIBE-8wE9Sv3yzYZlo70PBmFPz&index=3
//1.加入API
//2.建立Task的Bean
//3.創建DataSource->裡面寫靜態方法建立好5筆Task資料隨時取用
//4.Observable被觀察者設定
//5.訂閱觀察並觀察執行緒是誰在處理的log
//6.這邊加上filter過濾器用Predicate這個任務,當false時會被呼叫

/*Log -> 未加上.filter(new Predicate<Task>() 過濾器之前
2020-10-09 20:51:54.137 1137-1137/com.example.p_rxjava D/MainActivity: onSubscribe
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext Task:Take out the trash
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext Task:Walk the dog
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 20:51:54.212 1137-1137/com.example.p_rxjava D/MainActivity: onNext Task:Make my bed
2020-10-09 20:51:54.213 1137-1137/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 20:51:54.213 1137-1137/com.example.p_rxjava D/MainActivity: onNext Task:Unload the dishwasher
2020-10-09 20:51:54.213 1137-1137/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 20:51:54.213 1137-1137/com.example.p_rxjava D/MainActivity: onNext Task:Make dinner
2020-10-09 20:51:54.213 1137-1137/com.example.p_rxjava D/MainActivity: onComplete
* */

/*加上.filter(new Predicate<Task>() 過濾器之後,回來的task.isComplete()如果是false會被Predicate<Task>抓到,只有true的可以通過讓觀者的onNext抓到
2020-10-09 21:03:29.478 1415-1415/com.example.p_rxjava D/MainActivity: onSubscribe
2020-10-09 21:03:30.487 1415-1468/com.example.p_rxjava V/MainActivity: testRxCachedThreadScheduler-1
2020-10-09 21:03:30.488 1415-1415/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 21:03:30.489 1415-1415/com.example.p_rxjava D/MainActivity: onNext Task:Take out the trash
2020-10-09 21:03:31.491 1415-1468/com.example.p_rxjava V/MainActivity: testRxCachedThreadScheduler-1
2020-10-09 21:03:32.492 1415-1468/com.example.p_rxjava V/MainActivity: testRxCachedThreadScheduler-1
2020-10-09 21:03:32.492 1415-1415/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 21:03:32.492 1415-1415/com.example.p_rxjava D/MainActivity: onNext Task:Make my bed
2020-10-09 21:03:33.494 1415-1468/com.example.p_rxjava V/MainActivity: testRxCachedThreadScheduler-1
2020-10-09 21:03:34.495 1415-1468/com.example.p_rxjava V/MainActivity: testRxCachedThreadScheduler-1
2020-10-09 21:03:34.496 1415-1415/com.example.p_rxjava D/MainActivity: onNext -> 現在的執行緒名稱是：main
2020-10-09 21:03:34.496 1415-1415/com.example.p_rxjava D/MainActivity: onNext Task:Make dinner
2020-10-09 21:03:34.497 1415-1415/com.example.p_rxjava D/MainActivity: onComplete
*/
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.p_rxjava.Task.DataSource;
import com.example.p_rxjava.Task.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //4.Observable被觀察者設定
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.crateTasksList())//將Iterable序列轉換為ObservableSource(List<Task>)
                .subscribeOn(Schedulers.io())//指定Observable被觀察者在哪個調度器上被使用(Scheduler調度器)
                //6.這邊加上filter過濾器用Predicate這個任務,當false時會被呼叫
                .filter(new Predicate<Task>() {//新增過濾器(任務Task) ,這邊Predicate任務類是抓true/false如果false時會被呼叫攔截成功
                    @Override
                    public boolean test(Task task) throws Exception {
                        //這邊Predicate任務類是抓true/false如果false時會被呼叫攔截成功
                        Thread.sleep(1000);//每次睡一秒
                        Log.v(TAG, "test -> " + Thread.currentThread().getName());
                        return task.isComplete();//被觀察的類回傳的boolean值
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());//指定Observer觀察者在哪一個調度器上去觀察Observable

        //5.訂閱觀察並觀察執行緒是誰在處理的log
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe");
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG,"onNext -> 現在的執行緒名稱是：" + Thread.currentThread().getName());
                Log.d(TAG,"onNext ->Task:" + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"onError:" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
            }
        });

    }
}