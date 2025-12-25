package com.meetingspace.dto;

import java.util.List;

public class RoomRequest {
    public String name;
    public int capacity;
    public int floor;
    public List<Long> amenityIds;
}
