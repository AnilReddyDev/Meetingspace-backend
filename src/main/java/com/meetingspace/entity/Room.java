package com.meetingspace.entity;

import jakarta.persistence.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int floor;
    // Consider using lower-camel for fields and a proper enum for status
    private String status;

    private String name;
    private int capacity;

    @ManyToMany
    @JoinTable(
        name = "room_amenities",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;

    // Inverse (non-owning) side pointing to Booking.room
    @OneToMany(
        mappedBy = "room",
        cascade = CascadeType.ALL,      // optional; include only if you want cascading
        orphanRemoval = false,          // set true if deleting from collection should delete booking
        fetch = FetchType.LAZY
    )
    // If you serialize Room -> Bookings, use Managed/Back or Ignore properties to avoid recursion
    // @JsonManagedReference  // use this together with @JsonBackReference on Booking.room (instead of @JsonIgnore)
    @JsonIgnoreProperties({"room", "user"}) // keeps bookings but strips heavy links
    private Set<Booking> bookings = new java.util.HashSet<>();

    // Convenience helpers to keep both sides in sync
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setRoom(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setRoom(null);
    }

    // Getters/Setters
    public Long getId(){ return id; }
    public void setId(Long id) { this.id = id; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity(){ return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Set<Amenity> getAmenities() { return amenities; }
    public void setAmenities(Set<Amenity> amenities) { this.amenities = amenities; }

    public Set<Booking> getBookings() { return bookings; }
    public void setBookings(Set<Booking> bookings) { this.bookings = bookings; }
}
