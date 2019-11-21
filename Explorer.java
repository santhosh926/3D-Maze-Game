public class Explorer
{
	private Location loc;
    private String facing;
    private int key;

	public Explorer(int r, int c)
	{
        loc = new Location(r,c);
        facing = "D";
    }
    
    public Location move(String[][] maze)
    {
        if(key == 37)
        {
            switch(facing)
            {
                case "U": facing = "L";
                    break;
                case "L": facing = "D";
                    break;
                case "R": facing = "U";
                    break;
                case "D": facing = "R";
            }
        }

        else if(key == 39)
        {
            switch(facing)
            {
                case "U": facing = "R";
                    break;
                case "L": facing = "U";
                    break;
                case "R": facing = "D";
                    break;
                case "D": facing = "L";
            } 
        }

        else if(key == 38)
        {
            int x = loc.getX();
            int y = loc.getY();

            switch(facing)
            {
                case "U": loc.setY((y - 10));
                    break;
                case "L": loc.setX((x - 10));
                    break;
                case "R": loc.setX((x + 10));
                    break;
                case "D": loc.setY((y + 10));
            } 
        }

        return loc;

    }

    public void setX(int x) {
        loc.setX(x);
    }

    public void setY(int y) {
        loc.setY(y);
    }

    public Location getLocation()
    {
        return loc;
    }

    public void setKey(int key)
    {
        this.key = key;
    }

    public String getFacing()
    {
        return facing;
    }
    
    public int getX()
    {
        return loc.getX();
    }

    public int getY()
    {
        return loc.getY();
    }

}