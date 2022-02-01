package com.example.thingstodo;

import com.example.datamodel.ToDoData;
import com.example.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker dueDate;

    public ToDoItem processResults() {
        String description = shortDescription.getText().trim();
        String longDetails = details.getText().trim();
        LocalDate date = dueDate.getValue();

        ToDoItem item = new ToDoItem(description, longDetails, date);

        ToDoData.getInstance().addToDoItem(item);
        return item;
    }
}
