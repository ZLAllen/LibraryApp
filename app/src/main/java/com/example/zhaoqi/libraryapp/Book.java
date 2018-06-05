package com.example.zhaoqi.libraryapp;

/**
 * Created by Zhaoqi on 5/13/18.
 */

public class Book {
    private String title;
    private String author;
    private String isbn;
    private int location;
    private int status;
    private String holder;
    private String dueDay;
    private String cover;

    public Book(){

    }


    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getISBN(){
        return this.isbn;
    }

    public void setISBN(String isbn){
        this.isbn = isbn;
    }

    public int getLocation(){
        return this.location;
    }

    public void setLocation(int location){
        this.location = location;
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getHolder(){
        return this.holder;
    }

    public void setHolder(String holder){
        this.holder = holder;
    }

    public String getDueDay(){ return this.dueDay;}

    public void setDueDay(String dueDay){this.dueDay = dueDay;}

    public String getCover(){return this.cover;}

    public void setCover(String cover){this.cover = cover;}

}
