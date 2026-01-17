package com.meetingspace.dto;


import java.util.List;

public class AdminRoomsDashboardResponse {
 private String date;
 private int totalRooms;
 private int totalBookingsToday; // actually "for date"
 private Double avgUtilization;  // optional, can be null if you prefer FE compute
 private List<RoomSummary> rooms;

 public static class RoomSummary {
     public Long roomId;
     public String name;
     public int capacity;
     public int confirmedBookings;
     public int cancelledBookings;
     public int totalBookings;
     public int utilizationPercentage;
 }

 // getters/setters...

 public String getDate() { return date; }
 public void setDate(String date) { this.date = date; }

 public int getTotalRooms() { return totalRooms; }
 public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

 public int getTotalBookingsToday() { return totalBookingsToday; }
 public void setTotalBookingsToday(int totalBookingsToday) { this.totalBookingsToday = totalBookingsToday; }

 public Double getAvgUtilization() { return avgUtilization; }
 public void setAvgUtilization(Double avgUtilization) { this.avgUtilization = avgUtilization; }

 public List<RoomSummary> getRooms() { return rooms; }
 public void setRooms(List<RoomSummary> rooms) { this.rooms = rooms; }
}

