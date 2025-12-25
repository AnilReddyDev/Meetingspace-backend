package com.meetingspace.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;
    private int floor;
    private String status = "ACTIVE";

    @ManyToMany
    @JoinTable(
            name="room_amenities",
            joinColumns=@JoinColumn(name="room_id"),
            inverseJoinColumns=@JoinColumn(name="amenity_id")
    )
    private Set<Amenity> amenities;

    public Long getId(){ return id; }
    public int getCapacity(){ return capacity; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setAmenities(Set<Amenity> amenities) {
        this.amenities = amenities;
    }
}
