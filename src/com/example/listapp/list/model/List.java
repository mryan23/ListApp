package com.example.listapp.list.model;

import java.util.ArrayList;



public class List {
	
	private String name;
	private ArrayList<ListObject> items;
	private int id;
	private String user;
	
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
		if(items==null)
			return null;
		ListObject[] result = new ListObject[items.size()];
		for(int i = 0; i < items.size();i++)
			result[i]=items.get(i);
		return result;
	}

	public void setItems(ListObject[] it) {
		items = new ArrayList<ListObject>();
		for(int i = 0; i < it.length;i++)
			items.add(it[i]);
	}
	public void addItem(ListObject obj){
		if(items==null)
			items = new ArrayList<ListObject>();
		items.add(obj);
	}
	public int getId(){
		return id;
	}
	public void setId(int i){
		id = i;
	}
	public String getUser(){
		return user;
	}
	public void setUser(String us){
		user = us;
	}
	

}