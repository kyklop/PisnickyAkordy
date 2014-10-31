package cz.pisnickyakordy.android.model;

import java.util.ArrayList;

public class Interpreter {
	private String name, ordername, image;
	private int id, favourite, songcount;

	public Interpreter() {
	}

	public Interpreter(int id, String name, String ordername, String image, int favourite, int songcount) {
        this.id = id;
		this.name = name;
		this.image = image;
		this.ordername = ordername;
		this.favourite = favourite;
		this.songcount = songcount;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getOrdername() {
        return ordername;
    }

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getFavourite() {
		return favourite;
	}

	public void setFavourite(int favourite) {
		this.favourite = favourite;
	}

	public int getSongcount() {
		return songcount;
	}

	public void setSongcount(int songcount) {
		this.songcount = songcount;
	}
}
