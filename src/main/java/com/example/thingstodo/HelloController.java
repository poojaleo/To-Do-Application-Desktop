package com.example.thingstodo;

import com.example.datamodel.ToDoData;
import com.example.datamodel.ToDoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HelloController {
    private List<ToDoItem> toDoItems;

    @FXML
    private ListView todoListView;
    @FXML
    private TextArea toDoDetails;
    @FXML
    private Label dueDateLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList; // because we want to pass this to sorted list
    private Predicate<ToDoItem> displayAllItems;
    private Predicate<ToDoItem> displayTodaysItems;

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        // Event Handler
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToDoItem item = (ToDoItem) todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue) {
                if(newValue != null) {
                    ToDoItem item = (ToDoItem) todoListView.getSelectionModel().getSelectedItem();
                    toDoDetails.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("EE, MMMM d, yyyy");
                    dueDateLabel.setText(df.format(item.getDueDate()));
                }
            }
        });

        displayAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };

        displayTodaysItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                if(item.getDueDate().equals(LocalDate.now()))
                    return true;
                else
                    return false;
            }
        };

        filteredList = new FilteredList<>(ToDoData.getInstance().getToDoItems(), displayAllItems);
        // Sort by Due Date after filter
        SortedList<ToDoItem> sortedList = new SortedList<>(filteredList, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
                return o1.getDueDate().compareTo(o2.getDueDate());
            }
        });

        //todoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
        // Data binding
        todoListView.setItems(sortedList);
        // single selection
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    // override
                    // This allows us to update (in this case paint) every cell based on conditions
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty)
                            setText(null);
                        else {
                            setText(item.getShortDescription());
                            if(item.getDueDate().compareTo(LocalDate.now()) < 1)
                                setTextFill(Color.RED);
                            else if(item.getDueDate().equals(LocalDate.now().plusDays(1)))
                                setTextFill(Color.ORANGE);
                        }

                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // dialog window in focus, cannot interact with main window
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new ToDo Item");
        dialog.setHeaderText("Use this dialog to create new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("toDoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException exception) {
            System.out.println("Couldn't load the dialog");
            exception.printStackTrace();
            return;
        }

        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem itemCreated = controller.processResults();
            // todoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
            todoListView.getSelectionModel().select(itemCreated);
        }
    }

    @FXML
    public void showDetailsWhenClicked() {
        ToDoItem item = (ToDoItem) todoListView.getSelectionModel().getSelectedItem();
        toDoDetails.setText(item.getDetails());
        dueDateLabel.setText(item.getDueDate().toString());
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        // delete item if delete key is pressed
        ToDoItem selectedItem = (ToDoItem) todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    public void deleteItem(ToDoItem item) {
        // Confirmation Dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDo Item");
        alert.setHeaderText("Delete Item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)) {
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }
    @FXML
    public void handleFilterButton() {
        ToDoItem selectedItem = (ToDoItem) todoListView.getSelectionModel().getSelectedItem();
        // First filter and then sort!!
        if(filterToggleButton.isSelected()) {
            // show only today's item
            filteredList.setPredicate(displayTodaysItems);
            if(filteredList.isEmpty()) {
                toDoDetails.clear();
                dueDateLabel.setText("");
            } else if(filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }

        } else {
            // show all items, default case
            filteredList.setPredicate(displayAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

}