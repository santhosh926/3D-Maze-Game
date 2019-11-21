import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Font;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

public class MazeProgram extends JPanel implements KeyListener, MouseListener
{
	JFrame frame;
	static String [][] maze = new String[22][73];
	static Wall [][] walls = new Wall[22][73];
	int x = 10, y = 0;
	Explorer player;
	BufferedImage image;
	Graphics2D g2d;
	Maze mazeObj;
	Timer timer;
	TimerTask task;
	int secondsElapsed;
	Location warpLocation = new Location(90, 90);
	int moves;

	ArrayList<Polygon> leftWalls;
	ArrayList<Polygon> rightWalls;
	ArrayList<Polygon> frontWalls;
	ArrayList<Polygon> ceilings;
	ArrayList<Polygon> floors;

	public MazeProgram()
	{
		frame=new JFrame();
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200,1000);
		frame.setVisible(true);
		frame.addKeyListener(this);

		timer = new Timer();
		task = new TimerTask()
		{
			@Override
			public void run()
			{
				secondsElapsed++;
				repaint();
				System.out.println(30-secondsElapsed);
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);

		player = new Explorer(x, y);
		leftWalls = new ArrayList<Polygon>();
		rightWalls = new ArrayList<Polygon>();
		frontWalls = new ArrayList<Polygon>();
		ceilings = new ArrayList<Polygon>();
		floors = new ArrayList<Polygon>();
		moves = 0;
		setBoard();
		playSound();
		this.addMouseListener(this); //in case you need mouse clicking
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		g.setColor(Color.BLACK);	//this will set the background color
		g.fillRect(0,0,1200,1000);  //since the screen size is 1000x800
										//it will fill the whole visible part
										//of the screen with a black rectangle
			//drawBoard here!
			if(!gameOver() && !gameWon())
			{
				Font font = new Font("TimesRoman", Font.BOLD, 20);
				g.setFont(font);

				g.setColor(Color.GREEN);
				g.drawString("GREEN WALLS = TAKE YOUR TIME", 780, 250);
				
				g.setColor(Color.BLUE);
				g.drawString("BLUE WALLS = PICK UP THE PACE", 780, 280);	

				g.setColor(Color.RED);
				g.drawString("RED WALLS = HURRY!!!", 780, 310);	

				g.setColor(Color.WHITE);
				g.drawString("TIME LEFT: "+(30-secondsElapsed), 780, 50);

				for(int i = 0; i < 22; i++)
				{
					for(int j = 0; j < 73; j++)
					{
						Wall wall = walls[i][j];
						if(wall != null && wall.getType().equals("#"))
						{
							g.setColor(Color.WHITE);
							g.drawRect(j*10, i*10, 10, 10);
						}
						else
						{
							g.setColor(Color.BLACK);
							g.fillRect(j*10, i*10, 10, 10);
						}
					}
				}

				g.setColor(Color.RED);
				g.fillOval(x, y, 10, 10);

				g.drawImage(image, 770, 75, 150, 150, null);
				setWalls();
				for(Polygon p: rightWalls)
				{
					GradientPaint paint = new GradientPaint(750, 520, getColor(), 600, 520, Color.BLACK);
					g2d.setPaint(paint);
					g2d.fillPolygon(p);
					g2d.setPaint(Color.BLACK);
					g2d.drawPolygon(p);
				}

				for(Polygon p: leftWalls)
				{
					GradientPaint paint = new GradientPaint(50, 520, getColor(), 200, 520, Color.BLACK);
					g2d.setPaint(paint);
					g2d.fillPolygon(p);
					g2d.setPaint(Color.BLACK);
					g2d.drawPolygon(p);
				}

				for(Polygon p: ceilings)
				{
					GradientPaint paint = new GradientPaint(400, 230, getColor(), 400, 380, Color.BLACK);
					g2d.setPaint(paint);
					g2d.fillPolygon(p);
					g2d.setPaint(Color.BLACK);
					g2d.drawPolygon(p);
				}

				for(Polygon p: floors)
				{
					GradientPaint paint = new GradientPaint(400, 800, getColor(), 400, 650, Color.BLACK);
					g2d.setPaint(paint);
					g2d.fillPolygon(p);
					g2d.setPaint(Color.BLACK);
					g2d.drawPolygon(p);
				}

				for(Polygon p: frontWalls)
				{
					g.setColor(getColor());
					g.fillPolygon(p);
					g.setColor(Color.BLACK);
					g.drawPolygon(p);
				}
			}
			else if(gameWon())
			{
				g.setColor(Color.WHITE);
				Font font = new Font("TimesRoman", Font.BOLD, 40);
				g.setFont(font);
				g.drawString("CONGRATS! YOU BEAT THE MAZE!", 175, 400);
				g.drawString("FINISHED WITH " + moves + " MOVES!", 175, 500);
			}
			else
			{
				g.setColor(Color.WHITE);
				Font font = new Font("TimesRoman", Font.BOLD, 40);
				g.setFont(font);
				g.drawString("TIME'S UP! GAME OVER...", 175, 400);
			}

		}

		public void playSound() {
			try {
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Rocky Theme song.wav"));
				Clip clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				clip.start();
			} catch(Exception ex) {
				System.out.println("Error with playing sound.");
				ex.printStackTrace();
			}
		}

		public boolean gameOver()
		{
			return secondsElapsed >= 30;
		}

		public boolean gameWon()
		{
			return (player.getX()/10)==72 && (player.getY()/10==20);
		}

		public Color getColor()
		{
			if(secondsElapsed <= 10)
				return Color.GREEN;
			else if(secondsElapsed <= 20)
				return Color.BLUE;
			return Color.RED;
		}

		public void setBoard()
		{
			//choose your maze design

			//pre-fill maze array here

			File name = new File("Design.txt");
			int r = 0;
			try
			{
				BufferedReader input = new BufferedReader(new FileReader(name));
				String text;
				while( (text=input.readLine())!= null)
				{
					//System.out.println(text);
					for(int c = 0; c < 73; c++)
					{
						maze[r][c] = "" + text.charAt(c);
					}
					r++;
				}

				for(int i = 0; i < 22; i++)
				{
					for(int j = 0; j < 73; j++)
					{
						if(maze[i][j].equals("#"))
							walls[i][j] = new Wall(i, j, "#");

						else
							walls[i][j] = new Wall(i, j, " ");
					}
				}

				mazeObj = new Maze(walls);
				setWalls();

			}
			catch (IOException io)
			{
				System.err.println("File error");
			}
		}

		public void setWalls()
		{
			leftWalls.clear(); rightWalls.clear(); floors.clear(); ceilings.clear(); frontWalls.clear();
			int currentRow = player.getY()/10;
			int currentCol = player.getX()/10;
			setRightWalls(currentRow, currentCol);
			setLeftWalls(currentRow, currentCol);
			setFrontWalls(currentRow, currentCol);
			setCeilings();
			setFloors();
		}
		
		public void setRightWalls(int currentRow, int currentCol) {

			for (int d = 0; d < 3; d++) {
				int[] x = { 700 - 50 * d, 750 - 50 * d, 750 - 50 * d, 700 - 50 * d };
				int[] y = { 280 + 50 * d, 230 + 50 * d, 800 - 50 * d, 750 - 50 * d };
				
				switch (player.getFacing()) {

					case "R":
							if ((currentRow+1 >= 22 || currentCol+d >= 73) || maze[currentRow+1][currentCol+d].equals("#"))
								rightWalls.add(new Polygon(x, y, 4));
					break;
			
					case "L":
							if ((currentRow-1 < 0 || currentCol-d < 0) || maze[currentRow-1][currentCol-d].equals("#"))
								rightWalls.add(new Polygon(x, y, 4));
					break;
				
					case "U":
							if ((currentRow-d < 0 || currentCol+1 >= 73) || maze[currentRow-d][currentCol+1].equals("#"))
								rightWalls.add(new Polygon(x, y, 4));
					break;
				
					case "D":
							if ((currentRow+d >= 22 || currentCol-1 < 0) || maze[currentRow+d][currentCol-1].equals("#"))
								rightWalls.add(new Polygon(x, y, 4));
					break;
					
				}
			}
		}

		public void setLeftWalls(int currentRow, int currentCol) {

			for (int d = 0; d < 3; d++) {
				int[] x = { 50 + 50 * d, 100 + 50 * d, 100 + 50 * d, 50 + 50 * d };
				int[] y = { 230 + 50 * d, 280 + 50 * d, 750 - 50 * d, 800 - 50 * d };
				
				switch (player.getFacing()) {

					case "R":
							if ((currentRow-1 < 0 || currentCol+d >= 73) || maze[currentRow-1][currentCol+d].equals("#"))
								leftWalls.add(new Polygon(x, y, 4));
						
					break;
				
					case "L":
							if ((currentRow+1 >= 22 || currentCol-d < 0) || maze[currentRow+1][currentCol-d].equals("#"))
								leftWalls.add(new Polygon(x, y, 4));
					break;
				
					case "U":
							if ((currentRow-d < 0 || currentCol-1 < 0) || maze[currentRow-d][currentCol-1].equals("#"))
								leftWalls.add(new Polygon(x, y, 4));
					break;
				
					case "D":
							if ((currentRow+d >= 22 || currentCol+1 >= 73) || maze[currentRow+d][currentCol+1].equals("#"))
								leftWalls.add(new Polygon(x, y, 4));
					break;
					
				}
			}
		}

		public void setFrontWalls(int currentRow, int currentCol)
		{
			for(int d = 3; d > 0; d--)
			{
				int[] x = { 50 + 50 * d, 750 - 50 * d, 750 - 50 * d, 50 + 50 * d };
				int[] y = { 230 + 50 * d, 230 + 50 * d, 800 - 50 * d, 800 - 50 * d };
				
				switch (player.getFacing()) {

					case "R":
							if ((currentCol+d >= 73) || maze[currentRow][currentCol+d].equals("#"))
								frontWalls.add(new Polygon(x, y, 4));
					break;
			
					case "L":
							if ((currentCol-d < 0) || maze[currentRow][currentCol-d].equals("#"))
								frontWalls.add(new Polygon(x, y, 4));
					break;
				
					case "U":
							if ((currentRow-d < 0) || maze[currentRow-d][currentCol].equals("#"))
								frontWalls.add(new Polygon(x, y, 4));
					break;
				
					case "D":
							if ((currentRow+d >= 22) || maze[currentRow+d][currentCol].equals("#"))
								frontWalls.add(new Polygon(x, y, 4));
					break;
					
				}
			}
		}

		public void setCeilings()
		{
			for(int i = 0; i < 3; i++)
			{
				int[] x = { 750 - (50 * i), 700 - (50 * i), 100 + (50 * i), 50 + (50 * i) };
				int[] y = { 230 + (50 * i), 280 + (50 * i), 280 + (50 * i), 230 + (50 * i) };
				ceilings.add(new Polygon(x, y, 4));
			}
		}

		public void setFloors()
		{
			for(int i = 0; i < 3; i++)
			{
				int[] x = { 750 - (50 * i), 700 - (50 * i), 100 + (50 * i), 50 + (50 * i) };
				int[] y = { 800 - (50 * i), 750 - (50 * i), 750 - (50 * i), 800 - (50 * i) };
				floors.add(new Polygon(x, y, 4));
			}
		}	

		public void keyPressed(KeyEvent e)
		{
			if(gameOver() || gameWon()) return;
				
			moves++;
				if(e.getKeyCode() == 37)
					player.setKey(37);
				else if(e.getKeyCode() == 38)
					player.setKey(38);
				else if(e.getKeyCode() == 39)
					player.setKey(39);
				else
					return;

			int tempx = x;
			int tempy = y;

			Location loc = player.move(maze);
			x = loc.getX();
			y = loc.getY();
			
			System.out.println(player.getFacing());

			try
			{
				switch(player.getFacing())
				{
					case "U": image = ImageIO.read(new File("n_compass.png"));
						break;
					case "D": image = ImageIO.read(new File("s_compass.png"));
						break;
					case "R": image = ImageIO.read(new File("e_compass.png"));
						break;
					case "L": image = ImageIO.read(new File("w_compass.png"));
						break;
				}

			}
			catch(IOException ioe) {}

			if((y/10 < 0 || y/10 >= 22) || (x/10 < 0 || x/10 >= 73) || maze[y/10][x/10].equals("#"))
			{
				player.setX(tempx);
				player.setY(tempy);
				x = tempx;
				y = tempy;
			}
			if(player.getLocation() == warpLocation)
			{
				player.setX(10);
				player.setY(0);
				x = 10;
				y = 0;
			}
			System.out.println(x + " " +  y);

			repaint();

		}

		public void keyReleased(KeyEvent e)
		{
		}
		public void keyTyped(KeyEvent e)
		{
		}
		public void mouseClicked(MouseEvent e)
		{
			int x=e.getX();
			int y=e.getY();
			System.out.println(x+","+y);
		}
		public void mousePressed(MouseEvent e)
		{
		}
		public void mouseReleased(MouseEvent e)
		{
		}
		public void mouseEntered(MouseEvent e)
		{
		}
		public void mouseExited(MouseEvent e)
		{
		}
		public static void main(String args[])
		{
			MazeProgram app = new MazeProgram();
		}
}