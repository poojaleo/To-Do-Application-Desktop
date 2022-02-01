package com.example.datamodel;

import java.time.LocalDate;

public class ToDoItem {
    private String shortDescription;
    private String details;
    private LocalDate dueDate;

    public ToDoItem(String shortDescription, String details, LocalDate dueDate) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.dueDate = dueDate;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Not needed now because of cell factory
    @Override
    public String toString() {
        return shortDescription;
    }
}
