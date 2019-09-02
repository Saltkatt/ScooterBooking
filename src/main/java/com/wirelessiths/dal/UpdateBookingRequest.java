package com.wirelessiths.dal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@JsonAutoDetect
public class UpdateBookingRequest {


    private String scooterId;
    private String bookingId;
    private String userId;
    private String startTime;
    private String endTime;
    private String date;


    public UpdateBookingRequest() {
    }

    public Optional<String> getScooterId() {
        return Optional.ofNullable(scooterId);
    }

    public void setScooterId(String scooterId) {
        this.scooterId = scooterId;
    }

    public Optional<String>  getBookingId() {
        return Optional.ofNullable(bookingId);
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Optional<String> getUserId() {
        return Optional.ofNullable(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Optional<String> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Optional<String> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Optional<String> getDate() {
        return Optional.ofNullable(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBookingRequest that = (UpdateBookingRequest) o;
        return Objects.equals(scooterId, that.scooterId) &&
                Objects.equals(bookingId, that.bookingId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scooterId, bookingId, userId, startTime, endTime, date);
    }

    @Override
    public String toString() {
        return "UpdateBookingRequest{" +
                "scooterId='" + scooterId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", date=" + date +
                '}';
    }
}
