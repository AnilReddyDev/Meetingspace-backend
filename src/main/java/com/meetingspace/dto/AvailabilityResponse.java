package com.meetingspace.dto;

import java.util.List;

public class AvailabilityResponse {
    public List<Slot> availability;

    public static class Slot {
        public String start;
        public String end;
        public String status;
    }
}
