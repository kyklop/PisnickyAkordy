package cz.pisnickyakordy.android.model;

public class Song {
	private String name, image;
	private int id, favourite;

	public Song() {
	}

	public Song(int id, String name, String image, int favourite) {
        this.id = id;
		this.name = name;
		this.image = image;
		this.favourite = favourite;
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
}
