public class Maze
{
    private Wall[][] walls;

    public Maze(Wall[][] walls)
    {
        this.walls = walls;
    }

    public Wall[][] getWalls()
    {
        return walls;
    }
}