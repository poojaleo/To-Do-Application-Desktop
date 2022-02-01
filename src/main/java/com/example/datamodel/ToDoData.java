package com.example.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    // Singleton class
    // creates only one instance
    // static

    private static ToDoData instance = new ToDoData();
    private static String filename = "ToDoListItems.txt";
    private ObservableList<ToDoItem> toDoItems; // We use Observable List for performance reasons
    private DateTimeFormatter formatter;

    public static ToDoData getInstance() {
        return instance;
    }

    // private constructor, so no other class can instantiate a new class
    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void addToDoItem(ToDoItem item) {
        toDoItems.add(item);
    }

    //Once data is loaded once, you do not need it again
    /*public void setToDoItems(List<ToDoItem> toDoItems) {
        this.toDoItems = toDoItems;
    }*/

    public void loadToDoItems() throws IOException {
        toDoItems = FXCollections.observableArrayList(); // Use FXCollections and not collections
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");  // tab character
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];
                LocalDate dueDate = LocalDate.parse(dateString, formatter);
                ToDoItem item = new ToDoItem(shortDescription, details, dueDate);
                toDoItems.add(item);
            }
        } finally {
            if(br!=null)
                br.close();
        }
    }

    public void storeToDoItems() throws IOException {
        // save data
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<ToDoItem> itemIterator = toDoItems.iterator();
            while(itemIterator.hasNext()) {
                ToDoItem item = itemIterator.next();
                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(),
                        item.getDueDate().format(formatter)));
                bw.newLine();
            }

        } finally {
            if(bw != null)
                bw.close();
        }
    }

    public void deleteToDoItem(ToDoItem item) {
        toDoItems.remove(item);
    }
}
