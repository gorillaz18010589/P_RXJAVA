package com.example.p_rxjava.Task;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

    public static List<Task> crateTasksList(){
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("Take out the trash",true,3));
        taskList.add(new Task("Walk the dog",false,2));
        taskList.add(new Task("Make my bed",true,1));
        taskList.add(new Task("Unload the dishwasher",false,0));
        taskList.add(new Task("Make dinner",true,5));
        return taskList;
    }
}
