package com.example.listapp.list.model;

public class List {
	
	private String name;
	private ListObject[] items;
	
	public List(){
		name = "";
		items = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ListObject[] getItems() {
		return items;
	}

	public void setItems(ListObject[] items) {
		this.items = items;
	}
	
	

}
