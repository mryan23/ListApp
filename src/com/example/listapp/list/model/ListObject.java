package com.example.listapp.list.model;

public class ListObject {
	private String name;
	private boolean completed;
	private boolean deleted;
	private ListObject[] items;

	public ListObject(){
		name = "";
		completed = false;
		deleted = false;
		items = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public ListObject[] getItems() {
		return items;
	}

	public void setItems(ListObject[] items) {
		this.items = items;
	}

	
}
