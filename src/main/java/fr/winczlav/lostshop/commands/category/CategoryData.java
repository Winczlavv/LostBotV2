package fr.winczlav.lostshop.commands.category;

public class CategoryData {

    private final String name;
    private final int r;
    private final int g;
    private final int b;

    public CategoryData(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getName() { return name; }
    public int getR() { return r; }
    public int getG() { return g; }
    public int getB() { return b; }

}
