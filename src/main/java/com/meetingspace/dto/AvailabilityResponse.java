package com.meetingspace.dto;

import java.util.List;

public class AvailabilityResponse {
    private List<Slot> availability;

    public static class Slot {
        private String start;
        private String end;
        private String status;
		public String getStart() {
			return start;
		}
		public void setStart(String start) {
			this.start = start;
		}
		public String getEnd() {
			return end;
		}
		public void setEnd(String end) {
			this.end = end;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
        
    }

	public List<Slot> getAvailability() {
		return availability;
	}

	public void setAvailability(List<Slot> availability) {
		this.availability = availability;
	}
    
}
