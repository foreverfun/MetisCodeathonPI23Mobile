package com.example.metiscodeathonpi23mobile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TrackedPath {
    public ArrayList<TrackedPoint> locationList = new ArrayList<TrackedPoint>();
    public LocalDate localDate = LocalDate.now(); // Create a date object
    public LocalTime localTime = LocalTime.now();
}
