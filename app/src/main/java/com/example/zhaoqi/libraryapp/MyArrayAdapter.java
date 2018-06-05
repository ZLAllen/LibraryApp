package com.example.zhaoqi.libraryapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zhaoqi on 5/14/18.
 */

public class MyArrayAdapter extends ArrayAdapter<Book> {
    private final Context context;
    private final ArrayList<Book> books;
    private Location mLastKnownLocation;
    private Location chapinlocation, libraryLocation;

    public MyArrayAdapter(Context context, ArrayList<Book> books, Location mLastKnownLocation)
    {
        super(context, -1);
        this.context = context;
        this.books = books;
        this.mLastKnownLocation = mLastKnownLocation;
        chapinlocation = new Location("");
        chapinlocation.setLatitude(40.907745);
        chapinlocation.setLongitude(-73.109688);
        libraryLocation = new Location("");
        libraryLocation.setLatitude(40.915450);
        libraryLocation.setLongitude(-73.122714);
    }

    public void setmLastKnownLocation(Location mLastKnownLocation){
        this.mLastKnownLocation = mLastKnownLocation;
        this.reload();
    }

    private void reload(){
        ArrayList<Book> temp = new ArrayList<>(books);
        books.clear();
        books.addAll(temp);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return books.size();
    }

    @Override
    public Book getItem(int position) {
        // TODO Auto-generated method stub
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }



    @Override
    public void add(Book book)
    {
        books.add(book);
        this.notifyDataSetChanged();

    }


    @Override
    public void remove(Book book){
        for(Book b: books){
            if(b.getISBN().equals(book.getISBN())){
                books.remove(b);
                break;
            }
        }
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout, parent, false);
        TextView firstline = (TextView)rowView.findViewById(R.id.firstLine);
        TextView secondline = (TextView)rowView.findViewById((R.id.secondLine));
        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon2);
        Book book = getItem(position);
        String returnMessage = "";
        boolean due = false;
        try{
            System.out.println("date: " +  book.getDueDay());
            Date date = (new SimpleDateFormat("yyyy/MM/dd")).parse(book.getDueDay());
            Date today = new Date();
            long diff = date.getTime() - today.getTime();
            System.out.println("Diff: " + diff/(1000*60*60*24));
            if(diff < 0 || (diff/(1000*60*60*24) <= 3)){
                due = true;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(mLastKnownLocation.distanceTo(chapinlocation));
        if(book.getLocation() < 1){
            if(mLastKnownLocation != null && mLastKnownLocation.distanceTo(chapinlocation) < 1000 && due){
                returnMessage = "(return now)";
            }
        }else{
            if(mLastKnownLocation != null && mLastKnownLocation.distanceTo(libraryLocation) < 1000 && due){
                returnMessage = "(return now)";
            }
        }
        String line1 = String.format("%s by %s", book.getTitle(), book.getAuthor());
        String line2 = String.format("Due %s %s", book.getDueDay(), returnMessage);
        if(!returnMessage.equals("")){
            secondline.setTextColor(context.getResources().getColor(R.color.demo_color));
        }
        firstline.setText(line1);
        secondline.setText(line2);
        new DownloadImage(imageView).execute(book.getCover());
        return rowView;
    }

}
